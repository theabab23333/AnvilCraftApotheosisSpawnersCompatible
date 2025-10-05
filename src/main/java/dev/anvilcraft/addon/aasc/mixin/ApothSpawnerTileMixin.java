package dev.anvilcraft.addon.aasc.mixin;

import dev.anvilcraft.addon.aasc.AASCSpawnerStats;
import dev.shadowsoffire.apothic_spawners.block.ApothSpawnerTile;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ApothSpawnerTile.SpawnerLogicExt.class)
public abstract class ApothSpawnerTileMixin extends BaseSpawner {
    @Shadow protected abstract <T> T getStatValue(SpawnerStat<T> stat);

    @Inject(method = "isActivated", at = @At("HEAD"), cancellable = true)
    protected void isActivated(
        Level level,
        BlockPos pos,
        CallbackInfoReturnable<Boolean> cir) {
        if (this.getStatValue(AASCSpawnerStats.ANVIL_CONTROL)) cir.cancel();
    }
}
