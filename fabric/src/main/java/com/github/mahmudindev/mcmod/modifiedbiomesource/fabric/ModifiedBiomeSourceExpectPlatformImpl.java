package com.github.mahmudindev.mcmod.modifiedbiomesource.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class ModifiedBiomeSourceExpectPlatformImpl {
    public static <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            Identifier identifier,
            Supplier<? extends V> supplier
    ) {
        Registry<?> registry = BuiltInRegistries.REGISTRY.getValue(resourceKey.identifier());
        if (registry == null) {
            throw new RuntimeException("Registry entry may not exist");
        }

        //noinspection unchecked
        Registry<T> registryX = (Registry<T>) registry;

        V registered = Registry.register(registryX, identifier, supplier.get());

        return () -> registered;
    }
}
