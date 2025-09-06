package ru.minced.client.feature.module.impl.fight;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.keyboard.EventKey;
import ru.minced.client.core.event.impl.player.MovementInputEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BindSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.player.InventoryHandler;
import ru.minced.client.util.player.MovingUtil;

public class BindSwapModule extends Module implements IMinecraft {

    final ModeSetting firstItemSetting = new ModeSetting("First item", "Shield", "Sphere", "Totem", "GApple", "Torch");
    final ModeSetting secondItemSetting = new ModeSetting("Second item", "Shield", "Sphere", "Totem", "GApple", "Dirt");
    final SliderSetting tickSetting = new SliderSetting("Tick", 1, -1, 2, 1);
    final BindSetting bindSetting = new BindSetting("Item use key", 0);

    int swapTicks;
    boolean swapping;

    public BindSwapModule() {
        super("Bind Swap", "Увеличивает яркость", Category.Fight);
        addSettings(firstItemSetting, secondItemSetting, tickSetting, bindSetting);
    }

    @EventHandler
    public void onKey(EventKey eventKey) {
        if (IMinecraft.nullCheck()) return;

        if (eventKey.getAction() == 1 && eventKey.getKey() == bindSetting.getKey()) {
            swapping = true;
        }
    }

    @EventHandler
    public void onInput(MovementInputEvent event) {
        if (IMinecraft.nullCheck()) return;

        if (swapping) {
            if (swapTicks > tickSetting.getValue()) {
                swap();
                swapTicks = 0;
                swapping = false;
            }

            event.setDirectionalInput(MovingUtil.DirectionalInput.NONE);
            swapTicks++;
        }
    }

    private void swap() {
        ClientPlayerEntity localPlayer = mc.player;

        if (localPlayer == null) {
            return;
        }

        Item swapFromItem = getItemByName(firstItemSetting.getSelected());
        Item swapToItem = getItemByName(secondItemSetting.getSelected());

        Item itemToSwap = computeItem(localPlayer, swapFromItem, swapToItem);

        if (itemToSwap == null) {
            return;
        }

        int slot = InventoryHandler.findItem(itemToSwap);

        if (slot == -1) {
            logError("BindSwap", "Предмет не найден");
            return;
        }

        InventoryHandler.swap(slot, 40);
    }

    private Item computeItem(ClientPlayerEntity localPlayer, Item firstItem, Item secondItem) {
        return localPlayer.getOffHandStack().getItem() == firstItem ? secondItem : firstItem;
    }

    private Item getItemByName(String name) {
        return switch (name) {
            case "Shield" -> Items.SHIELD;
            case "Sphere" -> Items.PLAYER_HEAD;
            case "Totem" -> Items.TOTEM_OF_UNDYING;
            case "GApple" -> Items.GOLDEN_APPLE;
            case "Dirt" -> Items.DIRT;
            case "Torch" -> Items.TORCH;
            default -> null;
        };
    }
}
