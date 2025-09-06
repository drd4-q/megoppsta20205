package ru.minced.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow
    private ItemStack offHand;

    @Shadow
    private MinecraftClient client;

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private void hookRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var viewModelModule = Minced.getInstance().getModuleManager().getViewModelModule();
        if (viewModelModule.isState()) {
            if (Hand.MAIN_HAND == hand) {
                matrices.translate(viewModelModule.mainX.get(), viewModelModule.mainY.get(), viewModelModule.mainZ.get());
            }
            if (Hand.OFF_HAND == hand) {
                matrices.translate(viewModelModule.offhandX.get(), viewModelModule.offhandY.get(), viewModelModule.offhandZ.get());
            }
        }
    }

    @Inject(method="swingArm", at=@At("HEAD"), cancellable = true)
    private void swingArm(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm, CallbackInfo ci) {
        var viewModelModule = Minced.getInstance().getModuleManager().getViewModelModule();
        if (viewModelModule.isState() && arm == client.player.getMainArm()) {
            if (viewModelModule.onlyWithAura.isState()) {
                var auraModule = Minced.getInstance().getModuleManager().getAttackAuraModule();
                if (!auraModule.isState() || auraModule.getTarget() == null) {
                    return;
                }
            }

            ci.cancel();
            viewModelModule.mode.getSelected().apply(swingProgress, equipProgress, matrices, armX, arm);
        }
    }
}