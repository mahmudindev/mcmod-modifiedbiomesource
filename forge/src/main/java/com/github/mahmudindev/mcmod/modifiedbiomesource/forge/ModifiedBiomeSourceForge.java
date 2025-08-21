package com.github.mahmudindev.mcmod.modifiedbiomesource.forge;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;
import net.minecraftforge.fml.common.Mod;

@Mod(ModifiedBiomeSource.MOD_ID)
public final class ModifiedBiomeSourceForge {
    public ModifiedBiomeSourceForge() {
        // Run our common setup.
        ModifiedBiomeSource.init();
    }
}
