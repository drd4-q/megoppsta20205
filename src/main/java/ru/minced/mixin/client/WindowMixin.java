package ru.minced.mixin.client;

import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.render.EventGlfwInit;
import ru.minced.client.core.event.impl.render.EventSwapBuffers;
import ru.minced.client.core.manager.os.OSDetector;
import ru.minced.client.core.manager.os.WindowsThemeManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow @Final
    private long handle;

    @Inject(method = "setPhase", at = @At("HEAD"))
    private void onSetPhase(String phase, CallbackInfo ci) {
        if (phase.equals("Post startup")) {
            EventManager.post(new EventGlfwInit(handle));
            if (OSDetector.isWindows()) {
                WindowsThemeManager.setTitleBarTheme(handle);
            }
        }
    }

    @Inject(method = "swapBuffers", at=@At("HEAD"))
    private void onSwapBuffers(TracyFrameCapturer capturer, CallbackInfo ci) {
        EventManager.post(new EventSwapBuffers());
    }
}