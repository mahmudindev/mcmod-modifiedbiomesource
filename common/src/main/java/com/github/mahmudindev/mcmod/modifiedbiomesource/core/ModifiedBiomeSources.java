package com.github.mahmudindev.mcmod.modifiedbiomesource.core;

import com.github.mahmudindev.mcmod.orenocommons.registry.UnifiedRegistry;
import net.minecraft.core.registries.Registries;

public class ModifiedBiomeSources {
    public static void bootstrap() {
        UnifiedRegistry.registerEntry(
                Registries.BIOME_SOURCE,
                ModifiedMultiNoiseBiomeSource.ID,
                () -> ModifiedMultiNoiseBiomeSource.CODEC
        );
        UnifiedRegistry.registerEntry(
                Registries.BIOME_SOURCE,
                ModifiedTheEndBiomeSource.ID,
                () -> ModifiedTheEndBiomeSource.CODEC
        );
    }
}
