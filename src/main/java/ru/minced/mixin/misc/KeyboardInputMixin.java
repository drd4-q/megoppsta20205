package ru.minced.mixin.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.player.MovementInputEvent;
import ru.minced.client.core.event.impl.player.RotatedMovementInputEvent;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.RotationController;
import ru.minced.client.util.rotation.rotation.RotationPlan;
import ru.minced.client.util.player.MovingUtil;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends InputMixin {

    @Inject(method = "tick", at = @At("RETURN"), allow = 1)
    private void injectMovementInputEvent(CallbackInfo ci) {
        updateBooleanFields();

        var event = new MovementInputEvent(
                new MovingUtil.DirectionalInput(
                        playerInput.forward(),
                        playerInput.backward(),
                        playerInput.left(),
                        playerInput.right()),
                playerInput.jump(),
                playerInput.sneak()
        );

        EventManager.post(event);

        var directionalInput = event.getDirectionalInput();

        if (directionalInput != null) {
            boolean forward = directionalInput.isForwards();
            boolean back = directionalInput.isBackwards();
            boolean left = directionalInput.isLeft();
            boolean right = directionalInput.isRight();

            setPlayerInput(
                    forward,
                    back,
                    left,
                    right,
                    event.isJumping(),
                    event.isSneaking(),
                    playerInput.sprint()
            );

            this.movementForward = MovingUtil.DirectionalInput.getMovementMultiplier(forward, back);
            this.movementSideways = MovingUtil.DirectionalInput.getMovementMultiplier(left, right);

            fixStrafeMovement();
        }
    }

    @Unique
    private void fixStrafeMovement() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        RotationController rotationController = RotationController.INSTANCE;
        Angle angle = rotationController.getCurrentAngle();
        RotationPlan configurable = rotationController.getCurrentRotationPlan();

        float z = this.movementForward;
        float x = this.movementSideways;

        final RotatedMovementInputEvent MoveInputEvent;

        if (configurable == null || angle == null || player == null
                || !(configurable.isMoveCorrection() && configurable.isFreeCorrection())) {
            MoveInputEvent = new RotatedMovementInputEvent(z, x);
            EventManager.post(MoveInputEvent);
        } else {
            float deltaYaw = player.getYaw() - angle.getYaw();

            float newX = x * MathHelper.cos(deltaYaw * 0.017453292f) - z * MathHelper.sin(deltaYaw * 0.017453292f);
            float newZ = z * MathHelper.cos(deltaYaw * 0.017453292f) + x * MathHelper.sin(deltaYaw * 0.017453292f);

            MoveInputEvent = new RotatedMovementInputEvent(Math.round(newZ), Math.round(newX));
            EventManager.post(MoveInputEvent);
        }

        this.movementSideways = MoveInputEvent.getSideways();
        this.movementForward = MoveInputEvent.getForward();
    }
}