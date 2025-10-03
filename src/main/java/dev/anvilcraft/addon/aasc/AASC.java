package dev.anvilcraft.addon.aasc;

import com.tterrag.registrate.Registrate;
import dev.anvilcraft.addon.aasc.data.ModDatagen;
import net.neoforged.fml.common.Mod;

@Mod(AASC.MOD_ID)
public class AASC {
    public static final String MOD_ID = "aasc";
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public AASC() {
        ModDatagen.init();
    }
}
