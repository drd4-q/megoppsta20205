package ru.minced.client.util.rotation.rotation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import ru.minced.client.util.IMinecraft;

import java.util.function.Predicate;

public class RaytracingUtil implements IMinecraft {
    public static BlockHitResult raycast(double range, Angle angle, boolean includeFluids) {
        Entity entity = mc.cameraEntity;

        if (entity == null) {
            return null;
        }

        Vec3d start = entity.getCameraPosVec(1.0F);
        Vec3d rotationVec = angle.toVector();
        Vec3d end = start.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);

        World world = mc.world;
        if (world == null) {
            return null;
        }

        RaycastContext.FluidHandling fluidHandling = includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE;
        RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, fluidHandling, entity);

        return world.raycast(context);
    }

    public static EntityHitResult raytraceEntity(double range, Angle angle, Predicate<Entity> filter) {
        Entity entity = mc.cameraEntity;
        if (entity == null) return null;

        Vec3d cameraVec = entity.getCameraPosVec(1.0F);
        Vec3d rotationVec = angle.toVector();

        Vec3d vec3d3 = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = entity.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);

        return ProjectileUtil.raycast(
                entity,
                cameraVec,
                vec3d3,
                box,
                (e) -> !e.isSpectator() && filter.test(e),
                range * range
        );
    }

    public static boolean isEntityBehindWall(Entity targetEntity) {
        assert mc.player != null;
        Vec3d playerPos = mc.player.getCameraPosVec(1.0F);
        Vec3d targetPos = targetEntity.getPos();

        RaycastContext context = new RaycastContext(
                playerPos,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        );

        assert mc.world != null;
        BlockHitResult hitResult = mc.world.raycast(context);
        return hitResult.getType() == HitResult.Type.BLOCK;
    }
}
