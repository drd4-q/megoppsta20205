package ru.minced.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.world.AmbienceModule;

import java.awt.*;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @ModifyReturnValue(method = "applyFog", at = @At("RETURN"))
    private static Fog modifyFog(Fog original, Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return original;

        AmbienceModule ambienceModule = Minced.getInstance().getModuleManager().getAmbienceModule();

        if (ambienceModule.isState() && ambienceModule.getCustomFogSetting().isState()) {
            Color themeColor = Minced.getInstance().getThemeManager().getColorRate();
            float fogDistance = ambienceModule.getFogDistanceSetting().getValue();
            FogShape fogShape = FogShape.valueOf(ambienceModule.getFogShapeSetting().getSelected());

            return new Fog(
                    0.0f,
                    fogDistance,
                    fogShape,
                    themeColor.getRed() / 255.0f,
                    themeColor.getGreen() / 255.0f,
                    themeColor.getBlue() / 255.0f,
                    themeColor.getAlpha() / 255.0f
            );
        }

        return original;
    }
}
