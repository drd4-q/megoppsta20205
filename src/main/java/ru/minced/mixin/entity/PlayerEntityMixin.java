package ru.minced.mixin.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.player.EventKeepSprint;
import ru.minced.client.core.event.impl.player.EventPushWater;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.RotationController;
import ru.minced.client.util.rotation.rotation.RotationPlan;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attackHook(CallbackInfo callbackInfo) {
        EventManager.post(new EventKeepSprint());
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float hookFixRotation(PlayerEntity entity) {
        RotationController rotationController = RotationController.INSTANCE;
        Angle angle = rotationController.getCurrentAngle();
        RotationPlan configurable = rotationController.getCurrentRotationPlan();

        if (configurable == null || angle == null) {
            return entity.getYaw();
        }

        return angle.getYaw();
    }

    @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true)
    public void isPushedByFluids(CallbackInfoReturnable<Boolean> infoReturnable) {
        if ((Object) this instanceof ClientPlayerEntity) {
            EventPushWater event = new EventPushWater();
            EventManager.post(event);
            if (event.isStopped()) {
                infoReturnable.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getBlockInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getBlockInteractionRangeHook(CallbackInfoReturnable<Double> cir) {
        if (Minced.getInstance().getModuleManager().getReachModule().isState()) {
                cir.setReturnValue((double) Minced.getInstance().getModuleManager().getReachModule().blocksRange.get());
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("HEAD"), cancellable = true)
    public void getEntityInteractionRangeHook(CallbackInfoReturnable<Double> cir) {
        if (Minced.getInstance().getModuleManager().getReachModule().isState()) {
                cir.setReturnValue((double) Minced.getInstance().getModuleManager().getReachModule().entityRange.get());
        }
    }
}