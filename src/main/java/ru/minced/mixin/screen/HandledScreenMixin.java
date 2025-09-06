package ru.minced.mixin.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.container.EventInventory;
import ru.minced.client.util.math.StopWatch;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Unique
    private final StopWatch stopWatch = new StopWatch();

    @Shadow
    protected abstract void onMouseClick(Slot slotIn, int slotId, int mouseButton, SlotActionType type);

    @Shadow
    protected abstract boolean isPointOverSlot(Slot slotIn, double mouseX, double mouseY);

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        if (((Object) this) instanceof InventoryScreen) {
            EventManager.post(new EventInventory());
        }
    }
}