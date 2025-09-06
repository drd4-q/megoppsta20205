package ru.minced.client.feature.module.impl.fight;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventAttack;
import ru.minced.client.feature.module.Category;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.player.InventoryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.minced.client.feature.module.Module;
import net.minecraft.item.SwordItem;

public class MaceKillModule extends Module {

    public MaceKillModule() {
        super("Mace Kill", "pizda", Category.Player);
    }

    @EventHandler
    private void onAttack(EventAttack eventAttack) {
        if (IMinecraft.nullCheck()) return;

        assert mc.player != null;
        ItemStack held = mc.player.getMainHandStack();
        Item heldItem = held.getItem();

        if (heldItem instanceof SwordItem) {
            int maceSlot = InventoryHandler.findItemInHotbar(Items.MACE);
            if (maceSlot == -1) {
                logWarn("MaceKill", "Булавa не найдена в хотбаре");
                return;
            }

            int prevSlot = mc.player.getInventory().selectedSlot;

            logInfo("MaceKill", "Свапнул на булаву");
            mc.player.getInventory().setSelectedSlot(maceSlot);

            logInfo("MaceKill", "Вернул меч");
            mc.execute(() -> mc.player.getInventory().setSelectedSlot(prevSlot));
        }
    }
}
