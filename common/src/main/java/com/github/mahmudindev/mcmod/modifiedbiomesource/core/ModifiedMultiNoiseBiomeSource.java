package com.github.mahmudindev.mcmod.modifiedbiomesource.core;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;
import com.github.mahmudindev.mcmod.modifiedbiomesource.base.IClimateParameterList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModifiedMultiNoiseBiomeSource extends BiomeSource implements IModifiedBiomeSource {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(
            ModifiedBiomeSource.MOD_ID,
            String.format("%s_%s", Identifier.DEFAULT_NAMESPACE, "multi_noise")
    );
    public static final MapCodec<ModifiedMultiNoiseBiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.mapEither(
                    Climate.ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("biomes"),
                    MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable())
            ).forGetter(v -> v.parameters),
            Codec.BOOL.optionalFieldOf("mod_support").forGetter(v -> v.modSupport),
            RegistryCodecs.holderSet(Registries.BIOME).optionalFieldOf("allows").forGetter(v -> v.allows),
            RegistryCodecs.holderSet(Registries.BIOME).optionalFieldOf("denies").forGetter(v -> v.denies),
            Biome.CODEC.optionalFieldOf("fallback").forGetter(v -> v.fallback)
    ).apply(i, ModifiedMultiNoiseBiomeSource::new));

    private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;
    private final Optional<Boolean> modSupport;
    private final Optional<HolderSet<Biome>> allows;
    private final Optional<HolderSet<Biome>> denies;
    private final Optional<Holder<Biome>> fallback;

    public ModifiedMultiNoiseBiomeSource(
            Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters,
            Optional<Boolean> modSupport,
            Optional<HolderSet<Biome>> allows,
            Optional<HolderSet<Biome>> denies,
            Optional<Holder<Biome>> fallback
    ) {
        this.parameters = parameters;
        this.modSupport = modSupport;
        this.allows = allows;
        this.denies = denies;
        this.fallback = fallback;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public boolean isModSupported() {
        return this.modSupport.orElse(true);
    }

    @Override
    public boolean canGenerate(Holder<Biome> biome) {
        return this.allows.map(biomes -> {
            return biomes.contains(biome);
        }).orElse(true) && !this.denies.map(biomes -> {
            return biomes.contains(biome);
        }).orElse(false);
    }

    @Override
    public Holder<Biome> getFallback() {
        return this.fallback.orElse(null);
    }

    public void initializeParameters() {
        Climate.ParameterList<Holder<Biome>> parameters = this.parameters.map(
                parameterList -> parameterList,
                holder -> holder.value().parameters()
        );

        parameters = ((IClimateParameterList<Holder<Biome>>) parameters).clone();

        this.parameters = Either.left(parameters);
    }

    public Climate.ParameterList<Holder<Biome>> getParameters() {
        Climate.ParameterList<Holder<Biome>> parameters = this.parameters.map(
                parameterList -> parameterList,
                holder -> holder.value().parameters()
        );

        if (((IClimateParameterList<Holder<Biome>>) parameters).isChanged()) {
            return parameters;
        }

        parameters = ((IClimateParameterList<Holder<Biome>>) parameters).clone();

        List<Pair<Climate.ParameterPoint, Holder<Biome>>> values = parameters
                .values()
                .stream()
                .filter(v -> this.canGenerate(v.getSecond()))
                .collect(Collectors.toList());

        Holder<Biome> fallback = getFallback();
        if (fallback != null) {
            values.add(new Pair<>(
                    Climate.parameters(0, 0, 0, 0, 0, 0, 0),
                    fallback
            ));
        }

        ((IClimateParameterList<Holder<Biome>>) parameters).change(values);

        this.parameters = Either.left(parameters);

        return parameters;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.getParameters().values().stream().map(Pair::getSecond);
    }

    public Holder<Biome> getNoiseBiome(Climate.TargetPoint target) {
        return this.getParameters().findValue(target);
    }

    @Override
    public BiomeResolver createResolver(Climate.Sampler sampler) {
        return (quartX, quartY, quartZ) -> this.getNoiseBiome(sampler.sample(quartX, quartY, quartZ));
    }

    @Override
    public BiomeResolver createResolverForChunk(Climate.Sampler sampler, int minQuartX, int minQuartY, int minQuartZ, int quartSizeX, int quartSizeY, int quartSizeZ) {
        DensityVolume volume = new DensityVolume(quartSizeX, quartSizeY, quartSizeZ, QuartPos.toBlock(minQuartX), QuartPos.toBlock(minQuartY), QuartPos.toBlock(minQuartZ), 4, 4, 4);
        DensityBuffer temperature = DensityBuffer.createUnpooled(volume.size());
        DensityBuffer vegetation = DensityBuffer.createUnpooled(volume.size());
        DensityBuffer continents = DensityBuffer.createUnpooled(volume.size());
        DensityBuffer erosion = DensityBuffer.createUnpooled(volume.size());
        DensityBuffer depth = DensityBuffer.createUnpooled(volume.size());
        DensityBuffer ridges = DensityBuffer.createUnpooled(volume.size());
        sampler.temperature().sampleVolume(temperature, volume);
        sampler.humidity().sampleVolume(vegetation, volume);
        sampler.continentalness().sampleVolume(continents, volume);
        sampler.erosion().sampleVolume(erosion, volume);
        sampler.depth().sampleVolume(depth, volume);
        sampler.weirdness().sampleVolume(ridges, volume);
        return (quartX, quartY, quartZ) -> {
            int index = volume.indexUnchecked(quartX - minQuartX, quartY - minQuartY, quartZ - minQuartZ);
            return this.getNoiseBiome(Climate.target(temperature.get(index), vegetation.get(index), continents.get(index), erosion.get(index), depth.get(index), ridges.get(index)));
        };
    }
}
