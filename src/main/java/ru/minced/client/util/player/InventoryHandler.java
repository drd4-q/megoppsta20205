package ru.minced.client.util.player;

import lombok.experimental.UtilityClass;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

import static ru.minced.client.util.IMinecraft.mc;
import static ru.minced.client.util.IMinecraft.nullCheck;

@UtilityClass
public class InventoryHandler {

    public static void armorSwap(int slot, int targetSlot) {
        if (slot == -1 || targetSlot == -1) return;
        if (mc.player == null || mc.interactionManager == null) return;

        int syncId = mc.player.playerScreenHandler.syncId;

        clickSlot(syncId, indexToSlot(slot));
        clickSlot(syncId, targetSlot);
        clickSlot(syncId, indexToSlot(slot));
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CloseHandledScreenC2SPacket(0));
    }

    private static void clickSlot(int syncId, int slot) {
        assert mc.interactionManager != null;
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    public static int indexToSlot(int index) {
        if (index >= 0 && index <= 8) return 36 + index;
        return index;
    }

    public static int findChestplate(int start, int end) {
        int bestSlot = -1;
        int bestPriority = -1;

        for (int i = end; i >= start; i--) {
            assert mc.player != null;
            ItemStack stack = mc.player.getInventory().getStack(i);
            Item item = stack.getItem();
            int priority = -1;

            if (item == Items.LEATHER_CHESTPLATE) priority = 1;
            else if (item == Items.CHAINMAIL_CHESTPLATE) priority = 2;
            else if (item == Items.GOLDEN_CHESTPLATE) priority = 3;
            else if (item == Items.IRON_CHESTPLATE) priority = 4;
            else if (item == Items.DIAMOND_CHESTPLATE) priority = 5;
            else if (item == Items.NETHERITE_CHESTPLATE) priority = 6;

            if (priority > bestPriority) {
                bestPriority = priority;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    public static int findItemInHotbar(Item item) {
        if (mc.player == null) return -1;

        ItemStack offHandStack = mc.player.getOffHandStack();
        if (!offHandStack.isEmpty() && offHandStack.getItem() == item) {
            return 40;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static int getCurrentSlot() {
        if (mc.player == null) return -1;
        return mc.player.getInventory().selectedSlot;
    }

    public static void setCurrentSlot(int slot) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (slot >= 0 && slot < 9) {
            mc.player.getInventory().selectedSlot = slot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    public static ItemStack getItemInSlot(int slot) {
        if (mc.player == null) return ItemStack.EMPTY;
        if (slot >= 0 && slot < 36) {
            return mc.player.getInventory().getStack(slot);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isItemOnCooldown(ItemStack itemStack) {
        if (mc.player == null || itemStack.isEmpty()) return false;
        return mc.player.getItemCooldownManager().isCoolingDown(itemStack);
    }
    
    public static boolean isItemOnCooldown(int slot) {
        if (slot < 0 || slot >= 36) return false;
        ItemStack itemStack = getItemInSlot(slot);
        return isItemOnCooldown(itemStack);
    }

    public static <T extends Item> int findItemByClass(Class<T> itemClass) {
        if (mc.player == null) return -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }

    public static <T extends Item> int findItemInHotbarByClass(Class<T> itemClass) {
        if (mc.player == null) return -1;

        ItemStack offHandStack = mc.player.getOffHandStack();
        if (!offHandStack.isEmpty() && itemClass.isInstance(offHandStack.getItem())) {
            return 40;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }

    public static <T extends Item> int findItemInInventoryByClass(Class<T> itemClass) {
        if (mc.player == null) return -1;

        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }

    public static int findItem(Item item) {
        ClientPlayerEntity localPlayer = mc.player;

        int slot = -1;

        if (localPlayer == null) {
            return slot;
        }

        for (int i = 0; i < 36; i++) {
            ItemStack stack = localPlayer.getInventory().getStack(i);

            if (stack.getItem() == item) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }
    
    public static int findItemByName(String itemName) {
        if (mc.player == null) return -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getName().getString().toLowerCase().contains(itemName.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public static void swap(int fromSlot, int toSlot) {
        if (nullCheck() || mc.interactionManager == null) return;

        assert mc.player != null;
        int syncId = mc.player.currentScreenHandler.syncId;

        mc.interactionManager.clickSlot(syncId, fromSlot, toSlot, SlotActionType.SWAP, mc.player);
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CloseHandledScreenC2SPacket(0));
    }

    public static void moveItem(int one, int two, boolean swap) {
        assert mc.interactionManager != null;
        mc.interactionManager.clickSlot(0, one, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, two, 0, SlotActionType.PICKUP, mc.player);
        if (swap) {
            mc.interactionManager.clickSlot(0, one, 0, SlotActionType.PICKUP, mc.player);
        }
    }
    
    public static int findEmptySlot() {
        if (mc.player == null) return -1;
        
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
