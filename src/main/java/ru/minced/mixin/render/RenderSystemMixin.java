package ru.minced.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.world.AmbienceModule;
import java.awt.Color;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {

    @Inject(method = "clearColor", at = @At(value = "HEAD"), cancellable = true)
    private static void injectFog(float red, float green, float blue, float alpha, CallbackInfo ci) {
        AmbienceModule ambienceModule = Minced.getInstance().getModuleManager().getAmbienceModule();
        if (ambienceModule.isState() && ambienceModule.getCustomFogSetting().isState()) {
            Color themeColor = Minced.getInstance().getThemeManager().getColorRate();
            GlStateManager._clearColor(
                    themeColor.getRed() / 255f,
                    themeColor.getGreen() / 255f,
                    themeColor.getBlue() / 255f,
                    themeColor.getAlpha() / 255f
            );
            ci.cancel();
        }
    }
}