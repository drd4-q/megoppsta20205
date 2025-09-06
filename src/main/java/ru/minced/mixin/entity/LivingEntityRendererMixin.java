package ru.minced.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ru.minced.client.util.render.ColorUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.feature.module.impl.render.AntiAimModule;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import ru.minced.client.util.rotation.rotation.RotationController;
import ru.minced.client.util.rotation.rotation.RotationPlan;
import ru.minced.client.util.math.MathUtil;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Shadow
    protected abstract void scale(S state, MatrixStack matrices);
    @Shadow
    public abstract Identifier getTexture(S state);
    @Unique
    private float prevHeadYaw = 0f;
    @Unique
    private float prevHeadPitch = 0f;

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void injectTrueSight(EntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, @Local(argsOnly = true) S livingEntityRenderState) {
        var trueSightModule = Minced.getInstance().getModuleManager().getTrueSightModule();
        if (trueSightModule.isState() && livingEntityRenderState.invisible) {
            color = ColorUtils.setAlpha(color, trueSightModule.getOpacity().get());
        }
        original.call(instance, matrixStack, vertexConsumer, light, overlay, color);
    }

    @ModifyReturnValue(method = "getRenderLayer", at = @At("RETURN"))
    private RenderLayer injectTrueSight(RenderLayer original, S state, boolean showBody, boolean translucent, boolean showOutline) {
        if (Minced.getInstance().getModuleManager().getTrueSightModule().isState() && state.invisible && !showBody && !translucent && !showOutline) {
            state.invisible = false;
            return RenderLayer.getItemEntityTranslucentCull(this.getTexture(state));
        }
        return original;
    }


    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;clampBodyYaw(Lnet/minecraft/entity/LivingEntity;FF)F"))
    private float bodyYaw(float original, LivingEntity entity, S state, float tickDelta) {
        if (entity == mc.player) {
            AntiAimModule antiAimModule = Minced.getInstance().getModuleManager().getAntiAimModule();
            if (antiAimModule != null && antiAimModule.isState() && antiAimModule.shouldAffectBody()) {
                return antiAimModule.getLocalYaw();
            }

            RotationPlan activePlan = RotationController.INSTANCE.getCurrentRotationPlan();
            if (activePlan != null) {
                float targetYaw = RotationController.INSTANCE.getServerAngle().getYaw();
                float interpolatedYaw = MathUtil.interpolateFloat(prevHeadYaw, targetYaw, 0.08f);
                prevHeadYaw = interpolatedYaw;
                return interpolatedYaw;
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F"))
    private float headYaw(float original, LivingEntity entity, S state, float tickDelta) {
        if (entity == mc.player) {
            AntiAimModule antiAimModule = Minced.getInstance().getModuleManager().getAntiAimModule();
            if (antiAimModule != null && antiAimModule.isState() && antiAimModule.shouldAffectHead()) {
                return antiAimModule.getLocalYaw();
            }

            RotationPlan activePlan = RotationController.INSTANCE.getCurrentRotationPlan();
            if (activePlan != null) {
                float targetYaw = RotationController.INSTANCE.getServerAngle().getYaw();
                float interpolatedYaw = MathUtil.interpolateFloat(prevHeadYaw, targetYaw, 0.08f);
                prevHeadYaw = interpolatedYaw;
                return interpolatedYaw;
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getLerpedPitch(F)F"))
    private float pitch(float original, LivingEntity entity, S state, float tickDelta) {
        if (entity == mc.player) {
            AntiAimModule antiAimModule = (AntiAimModule) ModuleManager.modules.stream()
                    .filter(module -> module instanceof AntiAimModule)
                    .findFirst()
                    .orElse(null);

            if (antiAimModule != null && antiAimModule.isState() && antiAimModule.shouldAffectHead()) {
                return antiAimModule.getLocalPitch();
            }

            RotationPlan activePlan = RotationController.INSTANCE.getCurrentRotationPlan();
            if (activePlan != null) {
                float targetPitch = RotationController.INSTANCE.getServerAngle().getPitch();
                float interpolatedPitch = MathUtil.interpolateFloat(prevHeadPitch, targetPitch, 0.08f);
                prevHeadPitch = interpolatedPitch;
                return interpolatedPitch;
            }
        }
        return original;
    }
}