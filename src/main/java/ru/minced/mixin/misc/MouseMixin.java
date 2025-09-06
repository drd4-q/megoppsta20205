package ru.minced.mixin.misc;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.keyboard.EventKey;
import ru.minced.client.util.IMinecraft;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == mc.getWindow().getHandle()) {
            if (action == 1) {
                if (button != 0 && !IMinecraft.nullCheck() && mc.currentScreen == null) {
                    EventKey eventKey = new EventKey(1, button);
                    EventManager.post(eventKey);
                }
            }
        }
    }
}