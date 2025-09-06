package ru.minced.mixin.render;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.render.CameraModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.RotationController;
import ru.minced.client.util.rotation.rotation.RotationPlan;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract void setPos(Vec3d pos);
    
    @Shadow
    private Vec3d pos;

    @Final
    @Shadow
    private Vector3f horizontalPlane;

    @Final
    @Shadow
    private Vector3f verticalPlane;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    private void injectQuickPerspectiveSwap(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        RotationController rotationController = RotationController.INSTANCE;
        RotationPlan rotationPlan = rotationController.getCurrentRotationPlan();
        Angle previousAngle = rotationController.getPreviousAngle();
        Angle currentAngle = rotationController.getCurrentAngle();

        boolean shouldModifyRotation = rotationPlan != null && rotationPlan.isChangeLook();

        if (currentAngle == null || previousAngle == null || !shouldModifyRotation) {
            return;
        }

        this.setRotation(
                MathHelper.lerp(tickDelta, previousAngle.getYaw(), currentAngle.getYaw()),
                MathHelper.lerp(tickDelta, previousAngle.getPitch(), currentAngle.getPitch())
        );
    }
    
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    private void hookFreeCamModifiedPosition(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        Minced.getInstance().getModuleManager().getFreeCamModule().applyCameraPosition();
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void injectHorizontalOffset(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (thirdPerson) {
            CameraModule module = Minced.getInstance().getModuleManager().getCameraModule();
            if (module.isState() && module.getHorizontalOffset() != 0) {
                float offset = module.getHorizontalOffset();

                Vector3f rightVector = new Vector3f();
                horizontalPlane.cross(verticalPlane, rightVector);
                rightVector.normalize();

                Vec3d newPos = this.pos.add(
                    rightVector.x * offset,
                    rightVector.y * offset, 
                    rightVector.z * offset
                );
                
                this.setPos(newPos);
            }
        }
    }

    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDistance(float distance) {
        CameraModule module = Minced.getInstance().getModuleManager().getCameraModule();
        return module.isState() ? module.getDistance() : distance;
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(float distance, CallbackInfoReturnable<Float> cir) {
        CameraModule module = Minced.getInstance().getModuleManager().getCameraModule();
        if (module.isState() && module.getNoClip()) {
            cir.setReturnValue(distance);
        }
    }
}
