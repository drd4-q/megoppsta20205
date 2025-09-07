package ru.minced.client.util.rotation.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.AngleUtil;

public class HybridHolyInstantMode extends AngleMode {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public HybridHolyInstantMode() {
        super("HolyworldInstant");
    }

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        // разница углов
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);
        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(yawDelta, pitchDelta);

        // --- Instant ---
        if (rotationDifference > 120) {
            return targetAngle;
        }

        // --- HolyWorld ---
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

        float newYaw = currentAngle.getYaw() +
                MathHelper.clamp(yawDelta * baseSpeed, -maxYaw, maxYaw);
        float newPitch = currentAngle.getPitch() +
                MathHelper.clamp(pitchDelta * baseSpeed, -maxPitch, maxPitch);

        newPitch = MathHelper.clamp(newPitch, -89.0f, 89.0f);

        // фиксим GCD
        newYaw = fixWithGCD(newYaw);
        newPitch = fixWithGCD(newPitch);

        return new Angle(newYaw, newPitch);
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.05F, 0.0F, 0.05F);
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
// пнкпавол
    private float getGCD() {
        float f1 = (float) (mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2);
        return f1 * f1 * f1 * 8;
    }
}
