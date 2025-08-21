package com.github.mahmudindev.mcmod.modifiedbiomesource.core;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSourceExpectPlatform;
import net.minecraft.core.registries.Registries;

public class ModifiedBiomeSources {
    public static void bootstrap() {
        ModifiedBiomeSourceExpectPlatform.registerRegistryEntry(
                Registries.BIOME_SOURCE,
                ModifiedMultiNoiseBiomeSource.ID,
                () -> ModifiedMultiNoiseBiomeSource.CODEC
        );
        ModifiedBiomeSourceExpectPlatform.registerRegistryEntry(
                Registries.BIOME_SOURCE,
                ModifiedTheEndBiomeSource.ID,
                () -> ModifiedTheEndBiomeSource.CODEC
        );
    }
}
