package ru.minced.client.util.rotation.rotation;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.hypot;
import static java.lang.Math.toDegrees;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class AngleUtil {
    public static Angle fromVec2f(Vec2f vector2f) {
        return new Angle(vector2f.y, vector2f.x);
    }

    public static Angle fromVec3d(Vec3d vector) {
        return new Angle(
                (float) wrapDegrees(toDegrees(Math.atan2(vector.z, vector.x)) - 90),
                (float) wrapDegrees(toDegrees(-Math.atan2(vector.y, hypot(vector.x, vector.z))))
        );
    }
    public static Angle calculateDelta(Angle start, Angle end) {
        float deltaYaw = MathHelper.wrapDegrees(end.getYaw() - start.getYaw());
        float deltaPitch = MathHelper.wrapDegrees(end.getPitch() - start.getPitch());
        return new Angle(deltaYaw, deltaPitch);
    }

    public static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float f1 = -yaw * 0.017453292F;
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vec3d(f3 * f4, -f5, f2 * f4);
    }
}
