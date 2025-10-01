package com.github.mahmudindev.mcmod.modifiedbiomesource.base;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.biome.Climate;

import java.util.List;

public interface IClimateParameterList<T> extends Cloneable {
    void change(List<Pair<Climate.ParameterPoint, T>> values);

    boolean isChanged();

    Climate.ParameterList<T> clone();
}
