package com.github.mahmudindev.mcmod.modifiedbiomesource.base;

import java.util.function.Predicate;

public interface IClimateParameterList<T> {
    void modify(Predicate<T> canGenerateBiome, T fallbackBiome);
}
