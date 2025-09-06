package ru.minced.client.feature.module.impl.miscellaneous;

import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.container.EventClickSlot;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;

public class ItemScrollerModule extends Module implements IMinecraft {
    
    private boolean pauseListening = false;
    
    public ItemScrollerModule() {
        super("Item Scroller", "Quickly throws all items of the same type", Category.Miscellaneous);

        SliderSetting delay = new SliderSetting("Delay (ticks)", 1.0f, 0.0f, 20.0f, 1.0f);

        addSettings(delay);
    }
    
    @EventHandler
    public void onClickSlot(EventClickSlot event) {
        if ((isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT))
                && (isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL))
                && event.getSlotActionType() == SlotActionType.THROW
                && !pauseListening) {

            assert mc.player != null;
            Item copy = mc.player.currentScreenHandler.slots.get(event.getSlot()).getStack().getItem();
            pauseListening = true;
            
            for (int i = 0; i < mc.player.currentScreenHandler.slots.size(); ++i) {
                if (mc.player.currentScreenHandler.slots.get(i).getStack().getItem() == copy) {
                    assert mc.interactionManager != null;
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, mc.player
                    );
                }
            }
            
            pauseListening = false;
        }
    }

    public static boolean isKeyPressed(int button) {
        if (button == -1)
            return false;

        if (button < 10)
            return false;

        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), button);
    }
}