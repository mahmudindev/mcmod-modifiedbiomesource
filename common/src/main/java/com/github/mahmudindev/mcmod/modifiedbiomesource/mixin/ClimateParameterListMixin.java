package com.github.mahmudindev.mcmod.modifiedbiomesource.mixin;

import com.github.mahmudindev.mcmod.modifiedbiomesource.base.IClimateParameterList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(Climate.ParameterList.class)
public abstract class ClimateParameterListMixin<T> implements IClimateParameterList<T> {
    @Shadow @Final @Mutable private List<Pair<Climate.ParameterPoint, T>> values;
    @Shadow @Final @Mutable private Climate.RTree<T> index;
    @Unique
    private boolean modified;

    @Override
    public void modify(Predicate<T> canGenerateBiome, T fallbackBiome) {
        if (this.modified) {
            return;
        }

        List<Pair<Climate.ParameterPoint, T>> values = this.values.stream().filter(v -> {
            return canGenerateBiome.test(v.getSecond());
        }).collect(Collectors.toList());

        if (fallbackBiome != null) {
            values.add(new Pair<>(
                    Climate.parameters(0, 0, 0, 0, 0, 0, 0),
                    fallbackBiome
            ));
        }

        this.values = values;
        this.index = Climate.RTree.create(values);
        this.modified = true;
    }
}
