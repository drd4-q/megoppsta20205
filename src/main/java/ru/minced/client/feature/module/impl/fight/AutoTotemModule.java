package ru.minced.client.feature.module.impl.fight;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.player.MovingUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.minced.client.core.event.impl.player.MovementInputEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.player.InventoryHandler;

public class AutoTotemModule extends Module {

    final SliderSetting healthSetting = new SliderSetting("Здоровье", 3.5f, 0.5f, 20.0f, 0.5f);
    //final MultiBoxSetting checkSetting = new MultiBoxSetting("Учитывать", this, "Падение", "Бабах");
    final SliderSetting tickSetting = new SliderSetting("Тик", 1, 0, 2, 1);

    public AutoTotemModule() {
        super("Auto Totem","Automatically move totem to off-hand", Category.Fight);
        addSettings(healthSetting, tickSetting);
    }

    int swapTicks;
    boolean swapping;
    Item prevItem;

    @EventHandler
    public void onTick(EventTick eventTick) {
        if (IMinecraft.nullCheck()) return;

        float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        ItemStack offhandStack = mc.player.getOffHandStack();
        Item offhandItem = offhandStack.getItem();

        if (health <= healthSetting.getValue()) {
            if (offhandItem != Items.TOTEM_OF_UNDYING) {
                prevItem = offhandItem;
                int totemSlot = InventoryHandler.findItem(Items.TOTEM_OF_UNDYING);
                if (totemSlot != -1) {
                    swapping = true;
                }
            }
        } else {
            if (offhandItem == Items.TOTEM_OF_UNDYING && prevItem != null && prevItem != Items.AIR) {
                int prevSlot = InventoryHandler.findItem(prevItem);
                if (prevSlot != -1) {
                    swapping = true;
                }
            }
        }
    };

    @EventHandler
    public void onInput(MovementInputEvent movementInputEvent) {
        if (IMinecraft.nullCheck()) return;

        if (swapping) {
            if (swapTicks > tickSetting.getValue()) {
                equip();
                swapTicks = 0;
                swapping = false;
            }

            movementInputEvent.setDirectionalInput(MovingUtil.DirectionalInput.NONE);
            movementInputEvent.setJumping(false);
            swapTicks++;
        }
    };

    private void equip() {
        ClientPlayerEntity localPlayer = mc.player;
        if (localPlayer == null) return;

        float health = localPlayer.getHealth() + localPlayer.getAbsorptionAmount();
        ItemStack offhandStack = localPlayer.getOffHandStack();

        if (health <= healthSetting.getValue()) {
            int totemSlot = InventoryHandler.findItem(Items.TOTEM_OF_UNDYING);
            if (totemSlot != -1 && offhandStack.getItem() != Items.TOTEM_OF_UNDYING) {
                InventoryHandler.swap(totemSlot, 40);
            }
        } else {
            if (prevItem != null && offhandStack.getItem() == Items.TOTEM_OF_UNDYING) {
                int prevSlot = InventoryHandler.findItem(prevItem);
                if (prevSlot != -1) {
                    InventoryHandler.swap(prevSlot, 40);
                }
            }
        }
    }
}
