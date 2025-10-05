package dev.anvilcraft.addon.aasc;

import dev.shadowsoffire.apothic_spawners.ApothicSpawners;
import dev.shadowsoffire.apothic_spawners.stats.BooleanStat;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStat;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStats;
import net.minecraft.core.Registry;

public class AASCSpawnerStats {
    public static final SpawnerStat<Boolean> ANVIL_CONTROL = new BooleanStat(false);

    public static void register() {
        Registry.register(
            SpawnerStats.REGISTRY,
            ApothicSpawners.loc("anvil_control"),
            ANVIL_CONTROL
        );
    }
}
