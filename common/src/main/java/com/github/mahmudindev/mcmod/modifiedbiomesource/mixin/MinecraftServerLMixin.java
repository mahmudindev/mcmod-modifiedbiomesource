package com.github.mahmudindev.mcmod.modifiedbiomesource.mixin;

import com.github.mahmudindev.mcmod.modifiedbiomesource.core.ModifiedMultiNoiseBiomeSource;
import com.mojang.datafixers.DataFixer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(value = MinecraftServer.class, priority = 750)
public abstract class MinecraftServerLMixin {
    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initLast(
            Thread thread,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            WorldStem worldStem,
            Proxy proxy,
            DataFixer dataFixer,
            Services services,
            ChunkProgressListenerFactory chunkProgressListenerFactory,
            CallbackInfo ci
    ) {
        RegistryAccess.Frozen registryAccess = this.registryAccess();
        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

        levelStems.forEach(v -> {
            ChunkGenerator chunkGenerator = v.generator();
            BiomeSource biomeSource = chunkGenerator.getBiomeSource();

            if (biomeSource instanceof ModifiedMultiNoiseBiomeSource biomeSourceX) {
                biomeSourceX.initializeParameters();
            }
        });
    }
}
