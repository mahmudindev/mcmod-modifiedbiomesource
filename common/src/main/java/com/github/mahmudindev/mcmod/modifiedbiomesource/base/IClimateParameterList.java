package com.github.mahmudindev.mcmod.modifiedbiomesource.base;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.biome.Climate;

import java.util.Optional;
import java.util.function.Predicate;

public interface IClimateParameterList {
    <T> Climate.ParameterList<T> modify(
            Predicate<T> filter,
            Optional<Pair<Climate.ParameterPoint, T>> fallback
    );
}
