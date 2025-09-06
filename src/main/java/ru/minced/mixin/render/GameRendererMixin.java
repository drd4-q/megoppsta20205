package ru.minced.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.core.event.impl.render.PerspectiveEvent;
import ru.minced.client.feature.module.impl.render.NoRenderModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.Renderer3D;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.RaytracingUtil;
import ru.minced.client.util.rotation.rotation.RotationController;

import static ru.minced.client.util.IMinecraft.mc;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow private float zoom;
    @Shadow private float zoomX;
    @Shadow private float zoomY;
    @Shadow private float viewDistance;
    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult hookRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        if (instance != client.player) return instance.raycast(maxDistance, tickDelta, includeFluids);

        Angle currentAngle = RotationController.INSTANCE.getCurrentAngle();
        Angle angle = currentAngle != null ? currentAngle : new Angle(instance.getYaw(tickDelta), instance.getPitch(tickDelta));

        return RaytracingUtil.raycast(maxDistance, angle, includeFluids);
    }

    @Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookRotationVector(Entity instance, float tickDelta) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();

        return angle != null ? angle.toVector() : instance.getRotationVec(tickDelta);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void render3dHook(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if (IMinecraft.nullCheck()) return;

        Camera camera = mc.gameRenderer.getCamera();
        MatrixStack matrixStack = new MatrixStack();
        RenderSystem.getModelViewStack().pushMatrix().mul(matrixStack.peek().getPositionMatrix());
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));

        MathUtil.lastProjMat.set(RenderSystem.getProjectionMatrix());
        MathUtil.lastModMat.set(RenderSystem.getModelViewMatrix());
        MathUtil.lastWorldSpaceMatrix.set(matrixStack.peek().getPositionMatrix());

        Renderer3D.onRender3D(matrixStack);

        EventWorld eventWorld = new EventWorld(matrixStack, renderTickCounter.getTickDelta(false));
        EventManager.post(eventWorld);

        RenderSystem.getModelViewStack().popMatrix();
    }

    @Inject(method = "findCrosshairTarget", at = @At("HEAD"), cancellable = true)
    private void findCrosshairTargetHook(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta, CallbackInfoReturnable<HitResult> cir) {
        if (Minced.getInstance().getModuleManager().getNoEntityTraceModule().isState()) {
            double d = Math.max(blockInteractionRange, entityInteractionRange);
            Vec3d vec3d = camera.getCameraPosVec(tickDelta);
            HitResult hitResult = camera.raycast(d, tickDelta, false);
            cir.setReturnValue(ensureTargetInRangeCustom(hitResult, vec3d, blockInteractionRange));
        }
    }

    @Unique
    private HitResult ensureTargetInRangeCustom(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrixHook(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
        if (Minced.getInstance().getModuleManager().getAspectModule().isState()) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.peek().getPositionMatrix().identity();
            if (zoom != 1.0f) {
                matrixStack.translate(zoomX, -zoomY, 0.0f);
                matrixStack.scale(zoom, zoom, 1.0f);
            }
            matrixStack.peek().getPositionMatrix().mul(new Matrix4f().setPerspective((float) (fovDegrees * 0.01745329238474369), Minced.getInstance().getModuleManager().getAspectModule().getRatio().get(), 0.05f, viewDistance * 4.0f));
            cir.setReturnValue(matrixStack.peek().getPositionMatrix());
        }
    }


    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void onTiltViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
        if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("HurtCam")) {
            ci.cancel();
        }
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if (IMinecraft.nullCheck()) return;

        NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
        if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("Totem")) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;"))
    private Perspective hookPerspective(Perspective original) {
        PerspectiveEvent event = new PerspectiveEvent(original);
        EventManager.post(event);
        return event.getPerspective();
    }
}