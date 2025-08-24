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
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import java.util.Optional;
import java.util.stream.Stream;

public class ModifiedMultiNoiseBiomeSource extends BiomeSource implements IModifiedBiomeSource {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            ModifiedBiomeSource.MOD_ID,
            String.format("%s_%s", ResourceLocation.DEFAULT_NAMESPACE, "multi_noise")
    );
    public static final MapCodec<ModifiedMultiNoiseBiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.mapEither(
                    Climate.ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("biomes"),
                    MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable())
            ).forGetter(v -> v.parameters),
            Codec.BOOL.optionalFieldOf("mod_support").forGetter(v -> v.modSupport),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("allows").forGetter(v -> v.allows),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("denies").forGetter(v -> v.denies),
            Biome.CODEC.optionalFieldOf("fallback").forGetter(v -> v.fallback)
    ).apply(i, ModifiedMultiNoiseBiomeSource::new));

    private final Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;
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

    public Climate.ParameterList<Holder<Biome>> getParameters() {
        Climate.ParameterList<Holder<Biome>> parameters = this.parameters.map(
                parameterList -> parameterList,
                holder -> holder.value().parameters()
        );

        ((IClimateParameterList<Holder<Biome>>) parameters).modify(
                this::canGenerate,
                this.getFallback()
        );

        return parameters;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.getParameters().values().stream().map(Pair::getSecond);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        return this.getParameters().findValue(sampler.sample(i, j, k));
    }
}
