package com.github.mahmudindev.mcmod.modifiedbiomesource;

import com.github.mahmudindev.mcmod.modifiedbiomesource.core.ModifiedBiomeSources;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class ModifiedBiomeSource {
    public static final String MOD_ID = "modifiedbiomesource";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ModifiedBiomeSources.bootstrap();
    }
}
