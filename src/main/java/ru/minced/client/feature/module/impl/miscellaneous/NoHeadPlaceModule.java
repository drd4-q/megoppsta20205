package ru.minced.client.feature.module.impl.miscellaneous;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventInteractBlock;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;

public class NoHeadPlaceModule extends Module {

    public NoHeadPlaceModule() {
        super("No Head Place", "Prohibits the placement of heads", Category.Miscellaneous);
    }

    @EventHandler
    public void onInteractBlock(EventInteractBlock event) {
        ItemStack itemStack = event.getItemStack();

        if (itemStack.getItem() == Items.PLAYER_HEAD
            || itemStack.getItem() == Items.CREEPER_HEAD
            || itemStack.getItem() == Items.DRAGON_HEAD
            || itemStack.getItem() == Items.PIGLIN_HEAD
            || itemStack.getItem() == Items.ZOMBIE_HEAD) {
            event.stop();
        }
    }
} 