package ru.minced.client.feature.module.impl.miscellaneous;

import net.minecraft.item.Items;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.keyboard.EventKey;
import ru.minced.client.core.event.impl.player.MovementInputEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BindSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.player.InventoryHandler;
import ru.minced.client.util.player.MovingUtil;

public class ElytraHelperModule extends Module implements IMinecraft {

    public final BindSetting elytraSwap = new BindSetting("Elytra swap", 0);
    public final SliderSetting tickSetting = new SliderSetting("Tick", 1, 0, 2, 1);

    int swapTicks;
    boolean swapping;

    public ElytraHelperModule() {
        super("Elytra Helper", "elytraHelper", Category.Miscellaneous);
        addSettings(elytraSwap, tickSetting);
    }

    @EventHandler
    public void onKey(EventKey keyEvent) {
        if (IMinecraft.nullCheck()) return;

        if (keyEvent.getAction() == 1 && keyEvent.getKey() == elytraSwap.getKey()) {
            swapping = true;
        }
    };

    @EventHandler
    public void onInput(MovementInputEvent movementInputEvent) {
        if (IMinecraft.nullCheck()) return;

        if (swapping) {
            if (swapTicks > tickSetting.getValue()) {
                elytraSwap();
                swapTicks = 0;
                swapping = false;
            }

            movementInputEvent.setDirectionalInput(MovingUtil.DirectionalInput.NONE);
            movementInputEvent.setJumping(false);
            swapTicks++;
        }
    };

    private void elytraSwap() {
        assert mc.player != null;
        boolean elytra = mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA;
        int slot = elytra ? InventoryHandler.findChestplate(0, 35) : InventoryHandler.findItem(Items.ELYTRA);
        if (slot == -1) {
            logInfo("Elytra Helper", elytra ? "Нагрудник не найден" : "Элитра не найдена");
            return;
        }

        InventoryHandler.armorSwap(slot, 6);
        logInfo("Elytra Helper", elytra ? "Надет нагрудник" : "Надета элитра");
    }
}
