package com.github.mahmudindev.mcmod.modifiedbiomesource.neoforge;

import com.github.mahmudindev.mcmod.modifiedbiomesource.ModifiedBiomeSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ModifiedBiomeSource.MOD_ID)
public final class ModifiedBiomeSourceNeoForge {
    public static IEventBus EVENT_BUS;

    public ModifiedBiomeSourceNeoForge(IEventBus eventBus) {
        EVENT_BUS = eventBus;

        // Run our common setup.
        ModifiedBiomeSource.init();
    }
}
