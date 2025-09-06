package ru.minced.mixin.entity;

import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.movement.LiquidWalkModule;
import ru.minced.client.util.IMinecraft;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.player.EventPushPlayer;
import ru.minced.client.core.event.impl.player.NoSlowEvent;
import ru.minced.client.core.event.impl.render.EventSwingDuration;
import ru.minced.client.util.rotation.rotation.RotationController;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements IMinecraft {

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addVelocityInternal(Lnet/minecraft/util/math/Vec3d;)V"))
    private void hookFixRotation(LivingEntity instance, Vec3d velocity) {
        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if ((Object) this != MinecraftClient.getInstance().player) {
            instance.addVelocityInternal(velocity);
            return;
        }

        if (configurable == null || !configurable.isMoveCorrection() || rotation == null) {
            instance.addVelocityInternal(velocity);
            return;
        }

        float yaw = rotation.getYaw() * 0.017453292F;
        instance.addVelocityInternal(new Vec3d(-MathHelper.sin(yaw) * 0.2F, 0.0, MathHelper.cos(yaw) * 0.2F));
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float hookModifyFallFlyingPitch(LivingEntity instance) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return instance.getPitch();
        }

        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if (rotation == null || configurable == null || !configurable.isMoveCorrection() || configurable.isChangeLook()) {
            return instance.getPitch();
        }

        return rotation.getPitch();
    }

    @Redirect(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookModifyFallFlyingRotationVector(LivingEntity original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original.getRotationVector();
        }

        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if (rotation == null || configurable == null || !configurable.isMoveCorrection() || configurable.isChangeLook()) {
            return original.getRotationVector();
        }

        return rotation.toVector();
    }

    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void onIsUsingItem(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientPlayerEntity) {
            NoSlowEvent event = new NoSlowEvent();
            EventManager.post(event);
            if (event.isStopped()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getHandSwingDuration", at = @At("RETURN"), cancellable = true)
    private void hookGetHandSwingDuration(CallbackInfoReturnable<Integer> info) {
        int animation = info.getReturnValue();
        EventSwingDuration event = new EventSwingDuration(animation);
        EventManager.post(event);
        info.setReturnValue(event.getDuration());
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> infoReturnable) {
        if ((Object) this instanceof ClientPlayerEntity) {
            EventPushPlayer event = new EventPushPlayer();
            EventManager.post(event);
            if (event.isStopped()) {
                infoReturnable.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getMovementSpeed*", at = @At("RETURN"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientPlayerEntity) {
            NoSlowEvent event = new NoSlowEvent();
            EventManager.post(event);
            if (event.isStopped()) {
                cir.setReturnValue(cir.getReturnValue() * 5.0F);
            }
        }
    }

    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private boolean setSprintingHook(boolean sprinting) {
        LiquidWalkModule liquidWalkModule = Minced.getInstance().getModuleManager().getLiquidWalkModule();
        if (mc.player != null && mc.world != null && liquidWalkModule.isState() && liquidWalkModule.sprint.isState()) {
            if (mc.player.isTouchingWater() || mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos().add(0, -0.5, 0))).getBlock() instanceof FluidBlock)
                return true;
        }
        return sprinting;
    }
}