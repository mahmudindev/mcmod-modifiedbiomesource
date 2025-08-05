package com.github.mahmudindev.mcmod.modifiedbiomesource.fabric;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;
import net.fabricmc.api.ModInitializer;

public final class ModifiedBiomeSourceFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ModifiedBiomeSource.init();
    }
}
