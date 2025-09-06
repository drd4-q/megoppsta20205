package ru.minced.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import ru.minced.client.core.event.impl.player.*;
import ru.minced.client.core.event.impl.player.*;
import ru.minced.client.util.IMinecraft;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.feature.module.impl.player.FreeCamModule;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.RotationController;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(value = ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow public abstract float getPitch(float tickDelta);
    @Shadow public abstract float getYaw(float tickDelta);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (mc.player != null && mc.world != null) {
            EventTick eventTick = new EventTick();
            EventManager.post(eventTick);
            if (eventTick.isStopped()) {
                ci.cancel();
            }
        }
    }

    @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float hookSilentRotationYaw(float original) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (angle == null) {
            return original;
        }

        return angle.getYaw();
    }

    @ModifyExpressionValue(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float hookSilentRotationPitch(float original) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (angle == null) {
            return original;
        }

        return angle.getPitch();
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "HEAD"), cancellable = true)
    private void preMotion(CallbackInfo info) {
        MotionEvent eventMotion = new MotionEvent(getX(), getY(), getZ(), getYaw(1), getPitch(1), isOnGround());
        EventManager.post(eventMotion);

        if (eventMotion.isStopped()) {
            info.cancel();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"), cancellable = true)
    private void postMotion(CallbackInfo info) {
        EventPostMotion eventPostMotion = new EventPostMotion();
        EventManager.post(eventPostMotion);

        if (eventPostMotion.isStopped()) {
            info.cancel();
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double z, CallbackInfo callbackInfo) {
        EventPushBlock event = new EventPushBlock();
        EventManager.post(event);
        if (event.isStopped()) {
            callbackInfo.cancel();
        }
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"))
    private boolean hookFreeCamPreventCreativeFly(boolean original) {
        FreeCamModule freeCamModule = Minced.getInstance().getModuleManager().getFreeCamModule();
        return !freeCamModule.isState() && original;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    public void onMoveHook(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if(IMinecraft.nullCheck()) return;
        EventMove event = new EventMove(movement.x, movement.y, movement.z);
        EventManager.post(event);
        if (event.isStopped()) {
            super.move(movementType, new Vec3d(event.getX(), event.getY(), event.getZ()));
            ci.cancel();
        }
    }
}
