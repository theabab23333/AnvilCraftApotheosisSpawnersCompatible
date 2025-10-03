package dev.anvilcraft.addon.aasc.data;

import dev.anvilcraft.addon.aasc.AASC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AASC.MOD_ID)
public class ModDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {}

    /**
     * 初始化生成器
     */
    public static void init() {
//        REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
    }
}
