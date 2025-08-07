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

public class ModifiedMultiNoiseBiomeSource extends BiomeSource {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
            ModifiedBiomeSource.MOD_ID,
            String.format("%s_%s", ResourceLocation.DEFAULT_NAMESPACE, "multi_noise")
    );
    public static final MapCodec<ModifiedMultiNoiseBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.mapEither(
                    Climate.ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("biomes"),
                    MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable())
            ).forGetter(modifiedMultiNoiseBiomeSource -> modifiedMultiNoiseBiomeSource.parameters),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("allows").forGetter(modifiedMultiNoiseBiomeSource -> {
                return modifiedMultiNoiseBiomeSource.allows;
            }),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("denies").forGetter(modifiedMultiNoiseBiomeSource -> {
                return modifiedMultiNoiseBiomeSource.denies;
            }),
            Codec.pair(
                    Climate.ParameterPoint.CODEC.fieldOf("parameters").codec(),
                    Biome.CODEC.fieldOf("biome").codec()
            ).optionalFieldOf("fallback").forGetter(modifiedMultiNoiseBiomeSource -> modifiedMultiNoiseBiomeSource.fallback)
    ).apply(instance, ModifiedMultiNoiseBiomeSource::new));

    private final Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;
    private final Optional<HolderSet<Biome>> allows;
    private final Optional<HolderSet<Biome>> denies;
    private final Optional<Pair<Climate.ParameterPoint, Holder<Biome>>> fallback;

    public ModifiedMultiNoiseBiomeSource(
            Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters,
            Optional<HolderSet<Biome>> allows,
            Optional<HolderSet<Biome>> denies,
            Optional<Pair<Climate.ParameterPoint, Holder<Biome>>> fallback
    ) {
        this.parameters = parameters;
        this.allows = allows;
        this.denies = denies;
        this.fallback = fallback;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    public Climate.ParameterList<Holder<Biome>> getParameters() {
        return ((IClimateParameterList) this.parameters.map(
                parameterList -> parameterList,
                holder -> holder.value().parameters()
        )).modify(biome -> this.allows.map(biomes -> {
            return biomes.contains(biome);
        }).orElse(true) && !this.denies.map(biomes -> {
            return biomes.contains(biome);
        }).orElse(false), this.fallback);
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
