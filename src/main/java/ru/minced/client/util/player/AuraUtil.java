package ru.minced.client.util.player;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import ru.minced.client.util.IMinecraft;

@UtilityClass
public class AuraUtil implements IMinecraft {
    public Vector4f calculateRotationFromCamera(LivingEntity target) {
        Vec3d vec = PlayerHandler.getPoint(target).subtract(mc.player.getEyePos());

        float rawYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90F);
        float rawPitch = (float) (-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.z, 2)))));
        float yawDelta = MathHelper.wrapDegrees(rawYaw - mc.player.getYaw());
        float pitchDelta = rawPitch - mc.player.getPitch();

        return new Vector4f(rawYaw, rawPitch, yawDelta, pitchDelta);
    }

    public double calculateFOVFromCamera(LivingEntity target) {
        Vector4f rotation = calculateRotationFromCamera(target);
        float yawDelta = rotation.z;
        float pitchDelta = rotation.w;

        return Math.sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta);
    }
}
