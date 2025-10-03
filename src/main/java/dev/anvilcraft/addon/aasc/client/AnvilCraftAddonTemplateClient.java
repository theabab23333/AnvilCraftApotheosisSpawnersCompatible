package dev.anvilcraft.addon.aasc.client;

import dev.anvilcraft.addon.aasc.AnvilCraftAddonTemplate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(value = AnvilCraftAddonTemplate.MOD_ID, dist = Dist.CLIENT)
public class AnvilCraftAddonTemplateClient {
    public AnvilCraftAddonTemplateClient(@NotNull IEventBus modBus, @NotNull ModContainer container) {
    }
}
