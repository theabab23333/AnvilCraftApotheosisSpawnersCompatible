package dev.anvilcraft.addon.aasc.init;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.world.item.Item;

import static dev.anvilcraft.addon.aasc.AnvilCraftAddonTemplate.REGISTRATE;

public class AddonItems {
    static {
        AnvilCraft.REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final ItemEntry<Item> EXAMPLE_ITEM = REGISTRATE
        .item("example_item", Item::new)
        .register();

    public static void register() {
    }
}
