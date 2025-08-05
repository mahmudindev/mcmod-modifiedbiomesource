package com.github.mahmudindev.mcmod.modifiedbiomesource.forge;

import net.minecraftforge.fml.common.Mod;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;

@Mod(ModifiedBiomeSource.MOD_ID)
public final class ModifiedBiomeSourceForge {
    public ModifiedBiomeSourceForge() {
        // Run our common setup.
        ModifiedBiomeSource.init();
    }
}
