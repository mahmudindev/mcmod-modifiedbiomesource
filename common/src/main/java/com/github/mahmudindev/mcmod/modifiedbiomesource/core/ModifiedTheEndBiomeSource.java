package com.github.mahmudindev.mcmod.modifiedbiomesource.core;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.*;

import java.util.Optional;
import java.util.stream.Stream;

public class ModifiedTheEndBiomeSource extends BiomeSource implements IModifiedBiomeSource {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(
            ModifiedBiomeSource.MOD_ID,
            String.format("%s_%s", Identifier.DEFAULT_NAMESPACE, "the_end")
    );
    public static final MapCodec<ModifiedTheEndBiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            RegistryOps.retrieveElement(Biomes.THE_END),
            RegistryOps.retrieveElement(Biomes.END_HIGHLANDS),
            RegistryOps.retrieveElement(Biomes.END_MIDLANDS),
            RegistryOps.retrieveElement(Biomes.SMALL_END_ISLANDS),
            RegistryOps.retrieveElement(Biomes.END_BARRENS),
            Codec.BOOL.optionalFieldOf("mod_support").forGetter(v -> v.modSupport),
            RegistryCodecs.holderSet(Registries.BIOME).optionalFieldOf("allows").forGetter(v -> v.allows),
            RegistryCodecs.holderSet(Registries.BIOME).optionalFieldOf("denies").forGetter(v -> v.denies),
            Biome.CODEC.optionalFieldOf("fallback").forGetter(v -> v.fallback)
    ).apply(i, i.stable(ModifiedTheEndBiomeSource::new)));

    private final Holder<Biome> biomeTheEnd;
    private final Holder<Biome> biomeEndHighlands;
    private final Holder<Biome> biomeEndMidlands;
    private final Holder<Biome> biomeSmallEndIslands;
    private final Holder<Biome> biomeEndBarrens;
    private final Optional<Boolean> modSupport;
    private final Optional<HolderSet<Biome>> allows;
    private final Optional<HolderSet<Biome>> denies;
    private final Optional<Holder<Biome>> fallback;

    public ModifiedTheEndBiomeSource(
            Holder<Biome> biomeTheEnd,
            Holder<Biome> biomeEndHighlands,
            Holder<Biome> biomeEndMidlands,
            Holder<Biome> biomeSmallEndIslands,
            Holder<Biome> biomeEndBarrens,
            Optional<Boolean> modSupport,
            Optional<HolderSet<Biome>> allows,
            Optional<HolderSet<Biome>> denies,
            Optional<Holder<Biome>> fallback
    ) {
        this.biomeTheEnd = biomeTheEnd;
        this.biomeEndHighlands = biomeEndHighlands;
        this.biomeEndMidlands = biomeEndMidlands;
        this.biomeSmallEndIslands = biomeSmallEndIslands;
        this.biomeEndBarrens = biomeEndBarrens;
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

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        Holder<Biome> fallback = this.getFallback();

        return Stream.concat(
                Stream.of(
                        this.biomeTheEnd,
                        this.biomeEndHighlands,
                        this.biomeEndMidlands,
                        this.biomeSmallEndIslands,
                        this.biomeEndBarrens
                ).filter(this::canGenerate),
                fallback != null ? Stream.of(fallback) : Stream.empty()
        );
    }

    @Override
    public BiomeResolver createResolver(Climate.Sampler sampler) {
        return (quartX, quartY, quartZ) -> this.getNoiseBiome(quartX, quartY, quartZ, sampler);
    }

    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        int blockX = QuartPos.toBlock(quartX);
        int blockY = QuartPos.toBlock(quartY);
        int blockZ = QuartPos.toBlock(quartZ);
        int chunkX = SectionPos.blockToSectionCoord(blockX);
        int chunkZ = SectionPos.blockToSectionCoord(blockZ);

        if ((long)chunkX * (long)chunkX + (long)chunkZ * (long)chunkZ <= 4096L) {
            if (this.canGenerate(this.biomeTheEnd)) {
                return this.biomeTheEnd;
            }
        }

        int weirdBlockX = (SectionPos.blockToSectionCoord(blockX) * 2 + 1) * 8;
        int weirdBlockZ = (SectionPos.blockToSectionCoord(blockZ) * 2 + 1) * 8;
        double d = sampler.erosion().sampleValue(weirdBlockX, blockY, weirdBlockZ);

        if (d > 0.25) {
            if (this.canGenerate(this.biomeEndHighlands)) {
                return this.biomeEndHighlands;
            }
        }

        if (d >= -0.0625) {
            if (this.canGenerate(this.biomeEndMidlands)) {
                return this.biomeEndMidlands;
            }
        }

        if (d < -0.21875) {
            if (this.canGenerate(this.biomeSmallEndIslands)) {
                return this.biomeSmallEndIslands;
            }
        }

        if (this.canGenerate(this.biomeEndBarrens)) {
            return this.biomeEndBarrens;
        }

        return this.getFallback();
    }
}
