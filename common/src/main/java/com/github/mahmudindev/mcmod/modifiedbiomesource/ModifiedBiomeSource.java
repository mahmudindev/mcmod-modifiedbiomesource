package com.github.mahmudindev.mcmod.modifiedbiomesource;

import com.github.mahmudindev.mcmod.modifiedbiomesource.core.ModifiedBiomeSources;

public final class ModifiedBiomeSource {
    public static final String MOD_ID = "modifiedbiomesource";

    public static void init() {
        ModifiedBiomeSources.bootstrap();
    }
}
