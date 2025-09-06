package ru.minced.client.feature.module.impl.player;

import lombok.Getter;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.MovementInputEvent;
import ru.minced.client.core.event.impl.render.PerspectiveEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.player.MovingUtil.DirectionalInput;

public class FreeCamModule extends Module {

    @Getter private Vec3d originalPosition;
    @Getter private Vec3d cameraPosition;

    private final SliderSetting speed = new SliderSetting("Speed", 0.5f, 0.1f, 5.0f, 0.1f);

    public FreeCamModule(){
        super("Free Cam", "Allows you to move camera independently from player", Category.Player);
        addSettings(speed);
    }

    public void applyCameraPosition() {
        if (!isState() || IMinecraft.nullCheck() || originalPosition == null) return;

        updateCameraPosition();

        if (cameraPosition != null) {
            Camera camera = mc.gameRenderer.getCamera();
            camera.setPos(cameraPosition);
        }
    }

    private void updateCameraPosition() {
        if (IMinecraft.nullCheck() || originalPosition == null) return;

        double speedValue = speed.getValue();
        Vec3d movement = Vec3d.ZERO;

        assert mc.player != null;
        Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw());
        Vec3d right = Vec3d.fromPolar(0, mc.player.getYaw() - 90);

        boolean movingForward = mc.options.forwardKey.isPressed();
        boolean movingBackward = mc.options.backKey.isPressed();
        boolean movingRight = mc.options.rightKey.isPressed();
        boolean movingLeft = mc.options.leftKey.isPressed();
        boolean movingUp = mc.options.jumpKey.isPressed();
        boolean movingDown = mc.options.sneakKey.isPressed();

        float forwardMovement = DirectionalInput.getMovementMultiplier(movingForward, movingBackward);
        float sidewaysMovement = DirectionalInput.getMovementMultiplier(movingRight, movingLeft);

        if (forwardMovement != 0) {
            movement = movement.add(forward.multiply(forwardMovement));
        }
        if (sidewaysMovement != 0) {
            movement = movement.add(right.multiply(-sidewaysMovement));
        }

        float verticalMovement = DirectionalInput.getMovementMultiplier(movingUp, movingDown);
        if (verticalMovement != 0) {
            movement = movement.add(0, verticalMovement, 0);
        }

        if (movement.lengthSquared() > 0) {
            movement = movement.normalize().multiply(speedValue * 0.1);
            cameraPosition = cameraPosition.add(movement);
        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        if (IMinecraft.nullCheck()) return;

        assert mc.player != null;
        originalPosition = mc.player.getEyePos();
        cameraPosition = originalPosition;
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        originalPosition = null;
        cameraPosition = null;
    }

    @EventHandler
    private void onMovementInput(MovementInputEvent event) {
        if (cameraPosition == null) return;
        
        double speedValue = speed.getValue();
        Vec3d velocity = Vec3d.ZERO;

        float yAxisMovement = 0;
        if (event.isJumping()) {
            yAxisMovement = 1.0f;
        } else if (event.isSneaking()) {
            yAxisMovement = -1.0f;
        }

        DirectionalInput input = event.getDirectionalInput();
        if (input != null) {
            float forwardMovement = DirectionalInput.getMovementMultiplier(input.isForwards(), input.isBackwards());
            float sidewaysMovement = DirectionalInput.getMovementMultiplier(input.isRight(), input.isLeft());

            if (forwardMovement != 0 || sidewaysMovement != 0) {
                assert mc.player != null;
                Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw());
                Vec3d right = Vec3d.fromPolar(0, mc.player.getYaw() - 90);

                if (forwardMovement != 0) {
                    velocity = velocity.add(forward.multiply(forwardMovement));
                }
                if (sidewaysMovement != 0) {
                    velocity = velocity.add(right.multiply(sidewaysMovement));
                }
            }
        }

        if (yAxisMovement != 0) {
            velocity = velocity.add(0, yAxisMovement, 0);
        }

        if (velocity.lengthSquared() > 0) {
            velocity = velocity.normalize().multiply(speedValue * 0.1);
            cameraPosition = cameraPosition.add(velocity);
        }

        event.setDirectionalInput(DirectionalInput.NONE);
        event.setJumping(false);
        event.setSneaking(false);
    }

    @EventHandler
    private void onPerspective(PerspectiveEvent event) {
        event.setPerspective(Perspective.FIRST_PERSON);
    }

    public boolean shouldRenderPlayerModel(LivingEntity entity) {
        if (!isState() || IMinecraft.nullCheck()) {
            return entity.isSleeping();
        }

        assert mc.player != null;
        if (entity != mc.player) {
            return entity.isSleeping();
        }

        return true;
    }
}