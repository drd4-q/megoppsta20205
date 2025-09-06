package ru.minced.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.VertexConsumerProvider;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.render.NoRenderModule;
import ru.minced.client.util.IMinecraft;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void onRenderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (IMinecraft.nullCheck()) return;

        NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
        if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("Fire")) {
            ci.cancel();
        }
    }
    
    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void onRenderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (IMinecraft.nullCheck()) return;

        NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
        if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("Water")) {
            ci.cancel();
        }
    }
}
