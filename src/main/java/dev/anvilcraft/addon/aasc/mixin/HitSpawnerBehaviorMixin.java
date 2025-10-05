package dev.anvilcraft.addon.aasc.mixin;

import dev.anvilcraft.addon.aasc.AASCSpawnerStats;
import dev.dubhe.anvilcraft.anvil.HitSpawnerBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.mixin.accessor.BaseSpawnerAccessor;
import dev.shadowsoffire.apothic_spawners.block.ApothSpawnerTile;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStat;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStats;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HitSpawnerBehavior.class)
public class HitSpawnerBehaviorMixin {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    public void handle(
        Level level,
        BlockPos pos,
        BlockState hitBlockState,
        float fallDistance,
        AnvilEvent.OnLand event,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (level instanceof ServerLevel serverLevel) {
            if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity blockEntity) {
                RandomSource randomSource = serverLevel.getRandom();
                BaseSpawner spawner = blockEntity.getSpawner();
                BaseSpawnerAccessor accessor = (BaseSpawnerAccessor) spawner;
                SpawnData spawnData = accessor.invokeGetOrCreateNextSpawnData(level, randomSource, pos);
                spawnEntities(spawnData, serverLevel, pos, randomSource, accessor, cir);
            }
        }
        cir.setReturnValue(false);
    }

    @Inject(method = "spawnEntities", at = @At("HEAD"), cancellable = true)
    private void spawnEntities(
        SpawnData spawnData,
        ServerLevel serverLevel,
        BlockPos pos,
        RandomSource randomSource,
        BaseSpawnerAccessor accessor,
        CallbackInfo ci
    ) {
        ApothSpawnerTile apothSpawnerTile = (ApothSpawnerTile) serverLevel.getBlockEntity(pos);
        if (aasc$getStatValue(AASCSpawnerStats.ANVIL_CONTROL, apothSpawnerTile)) {
            ApothSpawnerTile.SpawnerLogicExt spawnerLogicExt = (ApothSpawnerTile.SpawnerLogicExt) apothSpawnerTile.getSpawner();
            for (int i = 0; i < spawnerLogicExt.spawnCount; ++i) {
                CompoundTag compoundTag = spawnData.getEntityToSpawn();
                Optional<EntityType<?>> optional = EntityType.by(compoundTag);
                if (optional.isEmpty()) return;

                ListTag listTag = compoundTag.getList("Pos", 6);
                int size = listTag.size();
                double x;
                double y;
                double z;
                if (size >= 1) {
                    x = listTag.getDouble(0);
                } else {
                    x = (double) pos.getX()
                        + (randomSource.nextDouble() - randomSource.nextDouble()) * accessor.getSpawnRange()
                        + 0.5;
                }
                if (size >= 2) {
                    y = listTag.getDouble(1);
                } else {
                    y = pos.getY() + randomSource.nextInt(3) - 1;
                }
                if (size >= 3) {
                    z = listTag.getDouble(2);
                } else {
                    z = (double) pos.getZ()
                        + (randomSource.nextDouble() - randomSource.nextDouble()) * accessor.getSpawnRange()
                        + 0.5;
                }
                if (serverLevel.noCollision(optional.get().getSpawnAABB(x, y, z))) {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    if (spawnData.getCustomSpawnRules().isPresent()) {
                        if (!optional.get().getCategory().isFriendly()
                            && serverLevel.getDifficulty() == Difficulty.PEACEFUL
                        ) {
                            continue;
                        }

                        SpawnData.CustomSpawnRules customSpawnRules =
                            spawnData.getCustomSpawnRules().get();
                        if (!customSpawnRules
                            .blockLightLimit()
                            .isValueInRange(serverLevel.getBrightness(LightLayer.BLOCK, blockPos))
                            || !customSpawnRules
                            .skyLightLimit()
                            .isValueInRange(serverLevel.getBrightness(LightLayer.SKY, blockPos))
                        ) {
                            continue;
                        }
                    } else if (!SpawnPlacements.checkSpawnRules(
                        optional.get(), serverLevel, MobSpawnType.SPAWNER, blockPos, serverLevel.getRandom())
                    ) {
                        continue;
                    }
                    Entity entity = EntityType.loadEntityRecursive(compoundTag, serverLevel, it -> {
                        it.moveTo(x, y, z, it.getYRot(), it.getXRot());
                        return it;
                    });
                    if (entity == null) {
                        return;
                    }

                    entity.getSelfAndPassengers().forEach(selfOrPassenger -> {
                        if (this.aasc$getStatValue(SpawnerStats.NO_AI, apothSpawnerTile) && selfOrPassenger instanceof Mob mob) {
                            mob.setNoAi(true);
                            mob.getPersistentData().putBoolean("apotheosis:movable", true);
                        }

                        if (this.aasc$getStatValue(SpawnerStats.YOUTHFUL, apothSpawnerTile) && selfOrPassenger instanceof Mob mob) {
                            mob.setBaby(true);
                        }

                        if (this.aasc$getStatValue(SpawnerStats.SILENT, apothSpawnerTile)) {
                            selfOrPassenger.setSilent(true);
                        }

                        if (this.aasc$getStatValue(SpawnerStats.INITIAL_HEALTH, apothSpawnerTile) != 1 && selfOrPassenger instanceof LivingEntity living) {
                            living.setHealth(living.getHealth() * this.aasc$getStatValue(SpawnerStats.INITIAL_HEALTH, apothSpawnerTile));
                        }

                        if (this.aasc$getStatValue(SpawnerStats.BURNING, apothSpawnerTile) && !selfOrPassenger.fireImmune()) {
                            selfOrPassenger.setRemainingFireTicks(Integer.MAX_VALUE);
                        }

                        if (this.aasc$getStatValue(SpawnerStats.ECHOING, apothSpawnerTile) > 0) {
                            selfOrPassenger.getPersistentData().putInt(SpawnerStats.ECHOING.getId().toString(), this.aasc$getStatValue(SpawnerStats.ECHOING, apothSpawnerTile));
                        }
                    });

                    AABB boundingBox = new AABB(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        pos.getX() + 1,
                        pos.getY() + 1,
                        pos.getZ() + 1
                    );
                    int k = serverLevel
                        .getEntitiesOfClass(entity.getClass(), boundingBox.inflate(accessor.getSpawnRange()))
                        .size();
                    if (k >= accessor.getMaxNearbyEntities()) {
                        return;
                    }

                    entity.moveTo(
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        randomSource.nextFloat() * 360.0F,
                        0.0F
                    );
                    if (entity instanceof Mob mob) {
                        if (spawnData.getCustomSpawnRules().isEmpty()
                            && !mob.checkSpawnRules(serverLevel, MobSpawnType.SPAWNER)
                            || !mob.checkSpawnObstruction(serverLevel)
                        ) {
                            continue;
                        }

                        if (spawnData.getEntityToSpawn().size() == 1
                            && spawnData.getEntityToSpawn().contains("id", 8)
                        ) {
                            EventHooks.finalizeMobSpawn(
                                mob,
                                serverLevel,
                                serverLevel.getCurrentDifficultyAt(entity.blockPosition()),
                                MobSpawnType.SPAWNER,
                                null
                            );
                        }
                    }

                    if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                        return;
                    }

                    serverLevel.levelEvent(2004, pos, 0);
                    serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
                    if (entity instanceof Mob) {
                        ((Mob) entity).spawnAnim();
                    }
                }
            }
        } else ci.cancel();
    }

    @Unique
    private <T> T aasc$getStatValue(SpawnerStat<T> stat, ApothSpawnerTile spawnerTile) {
        return stat.getValue(spawnerTile);
    }
}
