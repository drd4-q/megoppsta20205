package ru.minced.client.util.other;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import ru.minced.client.util.IMinecraft;

import java.util.Optional;
import java.util.function.Predicate;


@UtilityClass
public class MouseUtil implements IMinecraft {
    public static Entity getMouseOver(Entity target,
                                      float yaw,
                                      float pitch,
                                      double distance) {
        HitResult objectMouseOver;
        Entity entity = mc.getCameraEntity();

        if (entity != null && mc.world != null) {
            objectMouseOver = null;
            boolean flag = distance > 3;

            Vec3d startVec = entity.getCameraPosVec(1);
            Vec3d directionVec = getVectorForRotation(pitch, yaw);
            Vec3d endVec = startVec.add(
                    directionVec.x * distance,
                    directionVec.y * distance,
                    directionVec.z * distance
            );

            Box axisalignedbb = target.getBoundingBox().expand(target.getTargetingMargin());

            EntityHitResult entityraytraceresult = rayTraceEntities(entity,
                    startVec,
                    endVec,
                    axisalignedbb,
                    (p_lambda$getMouseOver$0_0_) ->
                            !p_lambda$getMouseOver$0_0_.isSpectator()
                                    && p_lambda$getMouseOver$0_0_.canHit(), distance
            );

            if (entityraytraceresult != null) {
                if (flag && startVec.distanceTo(startVec) > distance) {
                    objectMouseOver = BlockHitResult.createMissed(startVec, null, new BlockPos((int)startVec.x, (int)startVec.y, (int)startVec.z));
                }
                if ((distance < distance || objectMouseOver == null)) {
                    objectMouseOver = entityraytraceresult;
                }
            }
            if (objectMouseOver == null) {
                return null;
            }
            if (objectMouseOver instanceof EntityHitResult obj) {
                return obj.getEntity();
            }
        }
        return null;
    }

    public static EntityHitResult rayTraceEntities(Entity shooter,
                                                   Vec3d startVec,
                                                   Vec3d endVec,
                                                   Box boundingBox,
                                                   Predicate<Entity> filter,
                                                   double distance) {
        World world = shooter.getWorld();
        double closestDistance = distance;
        Entity entity = null;
        Vec3d closestHitVec = null;

        for (Entity entity1 : world.getOtherEntities(shooter, boundingBox, filter)) {
            Box axisalignedbb = entity1.getBoundingBox().expand(entity1.getTargetingMargin());
            Optional<Vec3d> optional = axisalignedbb.raycast(startVec, endVec);

            if (axisalignedbb.contains(startVec)) {
                if (closestDistance >= 0.0D) {
                    entity = entity1;
                    closestHitVec = startVec;
                    closestDistance = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3d vector3d1 = optional.get();
                double d3 = startVec.distanceTo(optional.get());

                if (d3 < closestDistance || closestDistance == 0.0D) {
                    boolean flag1 = false;

                    if (!flag1 && entity1.getRootVehicle() == shooter.getRootVehicle()) {
                        if (closestDistance == 0.0D) {
                            entity = entity1;
                            closestHitVec = vector3d1;
                        }
                    } else {
                        entity = entity1;
                        closestHitVec = vector3d1;
                        closestDistance = d3;
                    }
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity, closestHitVec);
    }

    public static HitResult rayTrace(double rayTraceDistance,
                                     float yaw,
                                     float pitch,
                                     Entity entity) {
        Vec3d startVec = mc.player.getCameraPosVec(1.0F);
        Vec3d directionVec = getVectorForRotation(pitch, yaw);
        Vec3d endVec = startVec.add(
                directionVec.x * rayTraceDistance,
                directionVec.y * rayTraceDistance,
                directionVec.z * rayTraceDistance
        );

        return mc.world.raycast(new RaycastContext(
                startVec,
                endVec,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                entity)
        );
    }

    public static HitResult rayTraceResult(double rayTraceDistance,
                                           float yaw,
                                           float pitch,
                                           Entity entity) {

        HitResult object = null;

        if (entity != null && mc.world != null) {
            float partialTicks = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
            double distance = rayTraceDistance;
            object = rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vec3d vector3d = entity.getCameraPosVec(partialTicks);
            boolean flag = false;
            double d1 = distance;

            if (mc.interactionManager.getCurrentGameMode().isCreative()) {
                d1 = 6.0D;
                distance = d1;
            } else {
                if (distance > 3.0D) {
                    flag = true;
                }

                distance = distance;
            }

            d1 = d1 * d1;

            if (object != null) {
                d1 = object.getPos().squaredDistanceTo(vector3d);
            }

            Vec3d vector3d1 = getVectorForRotation(pitch, yaw);
            Vec3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0F;
            Box axisalignedbb = entity.getBoundingBox().stretch(vector3d1.multiply(distance)).expand(1.0D, 1.0D, 1.0D);
            EntityHitResult entityraytraceresult = ProjectileUtil.raycast(entity, vector3d, vector3d2, axisalignedbb, (p_lambda$getMouseOver$0_0_) ->
            {
                return !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canHit();
            }, d1);

            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vec3d vector3d3 = entityraytraceresult.getPos();
                double d2 = vector3d.squaredDistanceTo(vector3d3);

                if (flag && d2 > 9.0D) {
                    object = BlockHitResult.createMissed(vector3d3, net.minecraft.util.math.Direction.getFacing(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos((int)vector3d3.x, (int)vector3d3.y, (int)vector3d3.z));
                } else if (d2 < d1 || object == null) {
                    object = entityraytraceresult;
                }
            }
        }
        return object;
    }

    public static boolean rayTraceWithBlock(double rayTraceDistance,
                                            float yaw,
                                            float pitch,
                                            Entity entity, Entity target) {

        HitResult object = null;

        if (entity != null && mc.world != null) {
            float partialTicks = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
            double distance = rayTraceDistance;
            object = rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vec3d vector3d = entity.getCameraPosVec(partialTicks);
            boolean flag = false;
            double d1 = distance;

            if (mc.interactionManager.getCurrentGameMode().isCreative()) {
                d1 = 6.0D;
                distance = d1;
            } else {
                if (distance > 3.0D) {
                    flag = true;
                }

                distance = distance;
            }

            d1 = d1 * d1;

            if (object != null) {
                d1 = object.getPos().squaredDistanceTo(vector3d);
            }

            Vec3d vector3d1 = getVectorForRotation(pitch, yaw);
            Vec3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0F;
            Box axisalignedbb = entity.getBoundingBox().stretch(vector3d1.multiply(distance)).expand(1.0D, 1.0D, 1.0D);
            EntityHitResult entityraytraceresult = ProjectileUtil.raycast(entity, vector3d, vector3d2, axisalignedbb, (p_lambda$getMouseOver$0_0_) ->
            {
                return !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canHit();
            }, d1);

            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vec3d vector3d3 = entityraytraceresult.getPos();
                double d2 = vector3d.squaredDistanceTo(vector3d3);

                if (flag && d2 > 9.0D) {
                    object = BlockHitResult.createMissed(vector3d3, net.minecraft.util.math.Direction.getFacing(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos((int)vector3d3.x, (int)vector3d3.y, (int)vector3d3.z));
                } else if (d2 < d1 || object == null) {
                    object = entityraytraceresult;
                }
            }
        }
        if (object instanceof EntityHitResult) {
            return ((EntityHitResult) object).getEntity().getId() == target.getId();
        }
        return false;
    }

    public static Vec3d getVectorForRotation(float pitch, float yaw) {
        float yawRadians = -yaw * ((float) Math.PI / 180) - (float) Math.PI;
        float pitchRadians = -pitch * ((float) Math.PI / 180);

        float cosYaw = MathHelper.cos(yawRadians);
        float sinYaw = MathHelper.sin(yawRadians);
        float cosPitch = -MathHelper.cos(pitchRadians);
        float sinPitch = MathHelper.sin(pitchRadians);

        return new Vec3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }
}