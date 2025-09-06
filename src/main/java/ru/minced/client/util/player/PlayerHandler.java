package ru.minced.client.util.player;

import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;

@UtilityClass
public class PlayerHandler implements IMinecraft {

    public static float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0D / mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED) * 20.0D);
    }

    public static @NotNull Vec3d getRotationVector(float yaw, float pitch) {
        return new Vec3d(MathHelper.sin(-pitch * 0.017453292F) * MathHelper.cos(yaw * 0.017453292F), -MathHelper.sin(yaw * 0.017453292F), MathHelper.cos(-pitch * 0.017453292F) * MathHelper.cos(yaw * 0.017453292F));
    }

    public static boolean isPvp() {
        return !getBossBarText().isEmpty() && (getBossBarText().toLowerCase().contains("pvp") || getBossBarText().toLowerCase().contains("пвп"));
    }

    public static String getPvpTimer() {
        return getBossBarText().isEmpty() ? "" : getBossBarText().replaceAll("\\D", "");
    }

    public static String getBossBarText() {
        for (ClientBossBar bossBar : mc.inGameHud.getBossBarHud().bossBars.values()) {
            if (bossBar == null) continue;
            return bossBar.getName().getString();
        }

        return "";
    }

    public static HitResult rayTrace(double dst, float yaw, float pitch) {
        Vec3d vec3d = mc.player.getCameraPosVec(MathUtil.getTickDelta());
        Vec3d vec3d2 = getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
    }

    public static float squaredDistanceFromEyes(@NotNull Vec3d vec) {
        if (mc.player == null) return 0;

        double d0 = vec.x - mc.player.getX();
        double d1 = vec.z - mc.player.getZ();
        double d2 = vec.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        return (float) (d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static float[] calcAngle(Vec3d to) {
        if (to == null) return null;
        double difX = to.x - mc.player.getEyePos().x;
        double difY = (to.y - mc.player.getEyePos().y) * -1.0;
        double difZ = to.z - mc.player.getEyePos().z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static Vec3d getPoint(Entity target) {
        double hitboxSize = target.getBoundingBox().maxY - target.getBoundingBox().minY;
        double additional = hitboxSize/2;
        Vec3d pos = getBestPoint(mc.player.getEyePos(), target);
        pos = new Vec3d(pos.x, target.getPos().add(0, additional + ((int) mc.player.getY() > (int) pos.y ? 0.5f : 0), 0).y, pos.z);

        return pos;
    }
    public static Vec3d getBestPoint(Vec3d pos, Entity entity) {
        if (entity == null) return new Vec3d(0.0D, 0.0D, 0.0D);

        double safePoint = 0;

        return new Vec3d(
                MathHelper.clamp(pos.x,
                        entity.getBoundingBox().minX + safePoint,
                        entity.getBoundingBox().maxX - safePoint),

                MathHelper.clamp(pos.y,
                        entity.getBoundingBox().minY + safePoint,
                        entity.getBoundingBox().maxY - safePoint),

                MathHelper.clamp(pos.z,
                        entity.getBoundingBox().minZ + safePoint,
                        entity.getBoundingBox().maxZ - safePoint)
        );
    }
}