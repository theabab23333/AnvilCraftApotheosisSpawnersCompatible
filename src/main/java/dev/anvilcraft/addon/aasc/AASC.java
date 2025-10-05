package dev.anvilcraft.addon.aasc;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

@Mod(AASC.MOD_ID)
public class AASC {
    public static final String MOD_ID = "aasc";

    public AASC() {
        AASCSpawnerStats.register();
    }
}
