package com.github.mahmudindev.mcmod.modifiedbiomesource.mixin;

import com.github.mahmudindev.mcmod.modifiedbiomesource.base.IClimateParameterList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(Climate.ParameterList.class)
public abstract class ClimateParameterListMixin<T> implements IClimateParameterList<T> {
    @Shadow @Final @Mutable private List<Pair<Climate.ParameterPoint, T>> values;
    @Shadow @Final @Mutable private Climate.RTree<T> index;
    @Unique
    private boolean changed;

    @Override
    public void change(List<Pair<Climate.ParameterPoint, T>> values) {
        this.values = values;
        this.index = Climate.RTree.create(this.values);
        this.changed = true;
    }

    @Override
    public boolean isChanged() {
        return this.changed;
    }

    @Override
    public Climate.ParameterList<T> clone() {
        try {
            return (Climate.ParameterList<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
