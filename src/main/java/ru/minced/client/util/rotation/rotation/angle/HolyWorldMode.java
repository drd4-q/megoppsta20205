package ru.minced.client.util.rotation.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.AngleUtil;

import java.util.Random;

public class HolyWorldMode extends AngleMode {
    private final Random random = new Random();

    public HolyWorldMode() {
        super("HolyWorld");
    }

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);
        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        float baseSpeed;
        if (rotationDifference > 90) {
            baseSpeed = 4.3f;
        } else if (rotationDifference > 45) {
            baseSpeed = 4.4f;
        } else {
            baseSpeed = 4.5f;
        }

        float maxRotationSpeed = Math.min(180.0f, rotationDifference * 0.95f);
        float maxYaw = Math.abs(yawDelta / rotationDifference) * maxRotationSpeed;
        float maxPitch = Math.abs(pitchDelta / rotationDifference) * maxRotationSpeed;

        float yawSpeed = baseSpeed;
        float pitchSpeed = baseSpeed;

        float newYaw = currentAngle.getYaw() + 
            Math.min(Math.max(yawDelta * yawSpeed, -maxYaw), maxYaw);
        float newPitch = currentAngle.getPitch() + 
            Math.min(Math.max(pitchDelta * pitchSpeed, -maxPitch), maxPitch);

        newPitch = MathHelper.clamp(newPitch, -89.0f, 89.0f);

        newYaw = fixWithGCD(newYaw);
        newPitch = fixWithGCD(newPitch);

        return new Angle(newYaw, newPitch);
    }

    private float fixWithGCD(float rotation) {
        float gcd = getGCDValue();
        rotation = getSensitivity(rotation);
        rotation -= rotation % gcd;
        return rotation;
    }

    private float getSensitivity(float rotation) {
        return getDeltaMouse(rotation) * getGCDValue();
    }

    private float getDeltaMouse(float delta) {
        return Math.round(delta / getGCDValue());
    }

    private float getGCDValue() {
        return (float) (getGCD() * 0.15);
    }

    private float getGCD() {
        float f1 = (float) (mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2);
        return f1 * f1 * f1 * 8;
    }

    @Override
    public Vec3d randomValue() {
        float offset = 0.015f;
        return new Vec3d(
                offset * (random.nextBoolean() ? 1 : -1),
                offset * (random.nextBoolean() ? 1 : -1) * 0.5f,
                offset * (random.nextBoolean() ? 1 : -1)
        );
    }
}
