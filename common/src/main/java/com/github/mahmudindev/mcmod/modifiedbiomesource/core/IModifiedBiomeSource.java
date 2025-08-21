package com.github.mahmudindev.mcmod.modifiedbiomesource.core;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface IModifiedBiomeSource {
    boolean isModSupported();

    boolean canGenerate(Holder<Biome> biome);

    Holder<Biome> getFallback();
}
