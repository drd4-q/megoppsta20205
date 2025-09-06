package ru.minced.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.player.EventBox;
import ru.minced.client.core.event.impl.player.PlayerVelocityStrafeEvent;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private Box boundingBox;

    @Shadow
    protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookVelocity(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            PlayerVelocityStrafeEvent event = new PlayerVelocityStrafeEvent(movementInput, speed, yaw, EntityMixin.movementInputToVelocity(movementInput, speed, yaw));
            EventManager.post(event);
            return event.getVelocity();
        }
        return EntityMixin.movementInputToVelocity(movementInput, speed, yaw);
    }

    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    public final void getBoundingBox(CallbackInfoReturnable<Box> cir) {
        Entity entity = (Entity) (Object) this;

        PlayerEntity localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer != null && entity.getId() == localPlayer.getId()) {
            return;
        }

        if (entity instanceof PlayerEntity && entity != localPlayer) {
            EventBox event = new EventBox(boundingBox, entity);
            EventManager.post(event);
            if (event.isStopped()) {
                cir.setReturnValue(event.getChangedBox());
            }
        }
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    public void isInvisibleToHook(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (Minced.getInstance().getModuleManager().getTrueSightModule().isState()) {
            Entity entity = (Entity)(Object)this;
            if (entity instanceof PlayerEntity) {
                cir.setReturnValue(false);
            }
        }
    }

    @ModifyExpressionValue(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isControlledByPlayer()Z"))
    private boolean fixFallDistanceCalculation(boolean original) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            return false;
        }
        return original;
    }
}