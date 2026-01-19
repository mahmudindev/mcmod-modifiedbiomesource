package com.github.mahmudindev.mcmod.modifiedbiomesource.core;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.DensityFunction;

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
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("allows").forGetter(v -> v.allows),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("denies").forGetter(v -> v.denies),
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
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        int l = QuartPos.toBlock(i);
        int m = QuartPos.toBlock(j);
        int n = QuartPos.toBlock(k);
        int o = SectionPos.blockToSectionCoord(l);
        int p = SectionPos.blockToSectionCoord(n);

        if ((long) o * (long) o + (long) p * (long) p <= 4096L) {
            if (this.canGenerate(this.biomeTheEnd)) {
                return this.biomeTheEnd;
            }
        }

        int q = (SectionPos.blockToSectionCoord(l) * 2 + 1) * 8;
        int r = (SectionPos.blockToSectionCoord(n) * 2 + 1) * 8;
        double d = sampler.erosion().compute(new DensityFunction.SinglePointContext(q, m, r));

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
