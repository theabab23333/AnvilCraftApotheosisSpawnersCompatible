package dev.anvilcraft.addon.aasc.mixin;

import dev.dubhe.anvilcraft.anvil.HitSpawnerBehavior;
import dev.dubhe.anvilcraft.mixin.accessor.BaseSpawnerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HitSpawnerBehavior.class)
public class HitSpawnerBehaviorMixin {
    @Inject(method = "spawnEntities", at = @At("HEAD"))
    private void spawnEntities(
        SpawnData spawnData,
        ServerLevel serverLevel,
        BlockPos pos,
        RandomSource randomSource,
        BaseSpawnerAccessor accessor,
        CallbackInfo ci
    ) {

    }
}
