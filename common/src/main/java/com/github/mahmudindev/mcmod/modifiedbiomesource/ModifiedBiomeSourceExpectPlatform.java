package com.github.mahmudindev.mcmod.modifiedbiomesource;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class ModifiedBiomeSourceExpectPlatform {
    @ExpectPlatform
    public static <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            Identifier identifier,
            Supplier<? extends V> supplier
    ) {
        return () -> null;
    }
}
