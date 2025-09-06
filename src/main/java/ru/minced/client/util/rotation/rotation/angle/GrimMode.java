package ru.minced.client.util.rotation.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.AngleUtil;

public class GrimMode extends AngleMode {
    public GrimMode() {
        super("ReallyWorld");
    }

    public float previousYawDelta;

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);
        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        float straightLineYaw = Math.abs(yawDelta / rotationDifference) * 180;
        float straightLinePitch = Math.abs(pitchDelta / rotationDifference) * 180;

        return new Angle(
                currentAngle.getYaw() + Math.min(Math.max(yawDelta, -straightLineYaw), straightLineYaw),
                currentAngle.getPitch() + Math.min(Math.max(pitchDelta, -straightLinePitch), straightLinePitch)
        );
    }
    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.05F, 0.0F, 0.05F);
    }
}
