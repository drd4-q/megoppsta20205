package ru.minced.client.feature.module.impl.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.keyboard.EventKey;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BindSetting;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.player.InventoryHandler;

public class ClickPearlModule extends Module implements IMinecraft {
    private final BindSetting throwKey = new BindSetting("Throw", GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
    private final SliderSetting delay = new SliderSetting("Delay [ticks]", 1, 0, 20, 1);
    private final BooleanSetting syncWithClickFriend = new BooleanSetting("Sync with ClickFriend", true);

    private int previousSlot = -1;
    private boolean waitingToRestore = false;
    private boolean waitingToThrow = false;
    private long throwTime = 0;
    private boolean offhandPearl = false;

    public ClickPearlModule() {
        super("Click Pearl", "Бросает эндер-жемчуг по бинду", Category.Player);
        addSettings(throwKey, delay, syncWithClickFriend);
    }

    @EventHandler
    public void onUpdate(EventTick event) {
        if (mc.player == null || mc.world == null) return;

        if (waitingToThrow && System.currentTimeMillis() >= throwTime) {
            assert mc.interactionManager != null;
            Hand hand = offhandPearl ? Hand.OFF_HAND : Hand.MAIN_HAND;
            mc.interactionManager.interactItem(mc.player, hand);

            mc.player.swingHand(hand);
            
            waitingToThrow = false;
            waitingToRestore = true;
        }

        if (waitingToRestore && !waitingToThrow) {
            if (!offhandPearl && previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
                previousSlot = -1;
            }
            offhandPearl = false;
            waitingToRestore = false;
        }
    }

    @EventHandler
    public void onKey(EventKey event) {
        if (IMinecraft.nullCheck()) return;

        if (event.getAction() == 1 && event.getKey() == throwKey.getKey()) {
            if (syncWithClickFriend.isState() && shouldSkipForFriend()) {
                return;
            }

            throwPearl();
        }
    }

    private boolean shouldSkipForFriend() {
        ClickFriendModule clickFriendModule = Minced.getInstance().getModuleManager().getClickFriendModule();

        if (clickFriendModule == null || !clickFriendModule.isState()) {
            return false;
        }

        if (clickFriendModule.getFriendKey().getKey() != throwKey.getKey()) {
            return false;
        }

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) mc.crosshairTarget).getEntity() instanceof PlayerEntity;
        }

        return false;
    }

    private void throwPearl() {
        int pearlSlot = InventoryHandler.findItemInHotbarByClass(EnderPearlItem.class);

        if (pearlSlot != -1) {
            assert mc.player != null;
            ItemStack stack;
            
            if (pearlSlot == 40) {
                stack = mc.player.getOffHandStack();
                offhandPearl = true;
            } else {
                stack = mc.player.getInventory().getStack(pearlSlot);
                offhandPearl = false;
            }

            if (InventoryHandler.isItemOnCooldown(stack)) {
                logWarn("ClickPearl", "Эндер жемчуг на кулдауне");
                return;
            }
            
            if (!offhandPearl) {
                previousSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = pearlSlot;
            }

            int delayTicks = (int) delay.get();
            if (delayTicks > 0) {
                waitingToThrow = true;
                throwTime = System.currentTimeMillis() + (delayTicks * 50L);
            } else {
                assert mc.interactionManager != null;
                Hand hand = offhandPearl ? Hand.OFF_HAND : Hand.MAIN_HAND;
                mc.interactionManager.interactItem(mc.player, hand);

                mc.player.swingHand(hand);
                
                waitingToRestore = true;
            }
        } else {
            logError("ClickPearl", "Эндер жемчуг не найден");
        }
    }

    @Override
    public void onDisabled() {
        if (waitingToRestore && !offhandPearl && previousSlot != -1) {
            assert mc.player != null;
            mc.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
            waitingToRestore = false;
        }
        waitingToThrow = false;
        offhandPearl = false;
        super.onDisabled();
    }
}