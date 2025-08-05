package com.github.mahmudindev.mcmod.modifiedbiomesource.mixin;

import com.github.mahmudindev.mcmod.modifiedbiomesource.base.IClimateParameterList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(Climate.ParameterList.class)
public abstract class ClimateParameterListMixin implements IClimateParameterList {
    @Unique private Climate.ParameterList<?> modified;

    @Shadow
    public abstract List<Pair<Climate.ParameterPoint, ?>> values();

    @Override
    public <T> Climate.ParameterList<T> modify(
            Predicate<T> filter,
            Optional<Pair<Climate.ParameterPoint, T>> fallback
    ) {
        if (this.modified == null) {
            List<Pair<Climate.ParameterPoint, ?>> list = this.values().stream().filter(v -> {
                return filter.test((T) v.getSecond());
            }).collect(Collectors.toList());

            fallback.ifPresent(list::add);

            this.modified = new Climate.ParameterList(list);
        }

        return (Climate.ParameterList<T>) this.modified;
    }
}
