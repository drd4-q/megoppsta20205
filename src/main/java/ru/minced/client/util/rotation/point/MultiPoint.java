package ru.minced.client.util.rotation.point;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.AngleUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class MultiPoint implements IMinecraft {
    private Random random = new SecureRandom();
    private int pointDensity = 15;
    private double expandFactor = -0.05F;

    @NonFinal
    private Vec3d offset = Vec3d.ZERO;

    public Vec3d computeVector(LivingEntity entity, float maxDistance, Angle initialAngle, Vec3d velocity) {
        List<Vec3d> candidatePoints = generateMultiPoints(entity);
        List<Vec3d> filteredPoints = filterPointsByDistance(candidatePoints, maxDistance);
        List<Vec3d> accessiblePoints = filterAccessiblePoints(filteredPoints, entity);

        if (accessiblePoints.size() < 3) {
            List<Vec3d> visiblePoints = filterVisiblePoints(filteredPoints, entity);
            if (!visiblePoints.isEmpty()) {
                accessiblePoints = visiblePoints;
            }
        }

        if (accessiblePoints.isEmpty()) {
            accessiblePoints = filteredPoints;
        }

        Vec3d bestVector = findBestVector(accessiblePoints, initialAngle, entity);
        updateOffset(velocity);

        return (bestVector == null ? entity.getEyePos() : bestVector).add(offset);
    }

    private List<Vec3d> generateMultiPoints(LivingEntity entity) {
        Box entityBox = entity.getBoundingBox().expand(expandFactor);
        List<Vec3d> points = new ArrayList<>();

        double stepX = entityBox.getLengthX() / pointDensity;
        double stepY = entityBox.getLengthY() / pointDensity;
        double stepZ = entityBox.getLengthZ() / pointDensity;

        for (double x = entityBox.minX; x <= entityBox.maxX; x += stepX) {
            for (double y = entityBox.minY; y <= entityBox.maxY; y += stepY) {
                for (double z = entityBox.minZ; z <= entityBox.maxZ; z += stepZ) {
                    points.add(new Vec3d(x, y, z));
                }
            }
        }

        double headY = entityBox.maxY - (entityBox.getLengthY() * 0.2);
        for (double x = entityBox.minX; x <= entityBox.maxX; x += stepX / 2) {
            for (double z = entityBox.minZ; z <= entityBox.maxZ; z += stepZ / 2) {
                points.add(new Vec3d(x, headY, z));
                points.add(new Vec3d(x, entityBox.maxY, z));
            }
        }

        Vec3d center = entityBox.getCenter();
        for (double x = center.x - stepX; x <= center.x + stepX; x += stepX / 3) {
            for (double y = center.y - stepY; y <= center.y + stepY; y += stepY / 3) {
                for (double z = center.z - stepZ; z <= center.z + stepZ; z += stepZ / 3) {
                    points.add(new Vec3d(x, y, z));
                }
            }
        }

        generateSurfacePoints(points, entityBox);

        return points;
    }

    private void generateSurfacePoints(List<Vec3d> points, Box box) {
        double xStep = box.getLengthX() / (pointDensity * 1.5);
        double yStep = box.getLengthY() / (pointDensity * 1.5);
        double zStep = box.getLengthZ() / (pointDensity * 1.5);

        for (double x = box.minX; x <= box.maxX; x += xStep) {
            for (double z = box.minZ; z <= box.maxZ; z += zStep) {
                points.add(new Vec3d(x, box.minY, z));
                points.add(new Vec3d(x, box.maxY, z));
            }
        }

        for (double y = box.minY; y <= box.maxY; y += yStep) {
            for (double z = box.minZ; z <= box.maxZ; z += zStep) {
                points.add(new Vec3d(box.minX, y, z));
                points.add(new Vec3d(box.maxX, y, z));
            }
        }

        for (double x = box.minX; x <= box.maxX; x += xStep) {
            for (double y = box.minY; y <= box.maxY; y += yStep) {
                points.add(new Vec3d(x, y, box.minZ));
                points.add(new Vec3d(x, y, box.maxZ));
            }
        }

        points.add(new Vec3d(box.minX, box.minY, box.minZ));
        points.add(new Vec3d(box.maxX, box.minY, box.minZ));
        points.add(new Vec3d(box.minX, box.maxY, box.minZ));
        points.add(new Vec3d(box.maxX, box.maxY, box.minZ));
        points.add(new Vec3d(box.minX, box.minY, box.maxZ));
        points.add(new Vec3d(box.maxX, box.minY, box.maxZ));
        points.add(new Vec3d(box.minX, box.maxY, box.maxZ));
        points.add(new Vec3d(box.maxX, box.maxY, box.maxZ));

        points.add(new Vec3d(box.getCenter().x, box.minY, box.getCenter().z));
        points.add(new Vec3d(box.getCenter().x, box.maxY, box.getCenter().z));
        points.add(new Vec3d(box.minX, box.getCenter().y, box.getCenter().z));
        points.add(new Vec3d(box.maxX, box.getCenter().y, box.getCenter().z));
        points.add(new Vec3d(box.getCenter().x, box.getCenter().y, box.minZ));
        points.add(new Vec3d(box.getCenter().x, box.getCenter().y, box.maxZ));
    }

    private List<Vec3d> filterPointsByDistance(List<Vec3d> points, float maxDistance) {
        Vec3d playerEyePos = mc.player.getEyePos();
        return points.stream()
                .filter(point -> isWithinDistance(playerEyePos, point, maxDistance))
                .collect(Collectors.toList());
    }

    private boolean isWithinDistance(Vec3d startPoint, Vec3d endPoint, float maxDistance) {
        return startPoint.distanceTo(endPoint) < maxDistance;
    }

    private List<Vec3d> filterAccessiblePoints(List<Vec3d> points, LivingEntity targetEntity) {
        Vec3d playerEyePos = mc.player.getEyePos();

        return points.stream()
                .filter(point -> isPointFullyAccessible(playerEyePos, point, targetEntity))
                .collect(Collectors.toList());
    }

    private List<Vec3d> filterVisiblePoints(List<Vec3d> points, LivingEntity targetEntity) {
        Vec3d playerEyePos = mc.player.getEyePos();

        return points.stream()
                .filter(point -> isPointVisible(playerEyePos, point, targetEntity))
                .collect(Collectors.toList());
    }

    private boolean isPointFullyAccessible(Vec3d start, Vec3d end, LivingEntity targetEntity) {
        if (IMinecraft.nullCheck()) return false;

        HitResult blockHitResult = mc.world.raycast(
                new net.minecraft.world.RaycastContext(
                        start,
                        end,
                        net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                        net.minecraft.world.RaycastContext.FluidHandling.NONE,
                        mc.player
                )
        );

        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            double blockHitDist = start.distanceTo(blockHitResult.getPos());
            double targetDist = start.distanceTo(end);

            if (blockHitDist < targetDist - 0.1) {
                return false;
            }
        }

        Box box = mc.player.getBoundingBox().stretch(end.subtract(start)).expand(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(
                mc.player,
                start,
                end,
                box,
                (entity) -> entity == targetEntity,
                start.squaredDistanceTo(end)
        );

        return entityHitResult != null && entityHitResult.getEntity() == targetEntity;
    }

    private boolean isPointVisible(Vec3d start, Vec3d end, LivingEntity targetEntity) {
        if (mc.world == null) return false;

        HitResult hitResult = mc.world.raycast(
                new net.minecraft.world.RaycastContext(
                        start,
                        end,
                        net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                        net.minecraft.world.RaycastContext.FluidHandling.NONE,
                        mc.player
                )
        );

        if (hitResult.getType() == HitResult.Type.MISS) {
            return true;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            double hitDist = start.distanceTo(hitResult.getPos());
            double targetDist = start.distanceTo(end);

            return hitDist >= targetDist - 0.1;
        }

        return false;
    }

    private Vec3d findBestVector(List<Vec3d> candidatePoints, Angle initialAngle, LivingEntity entity) {
        if (candidatePoints.isEmpty()) {
            return null;
        }

        Vec3d playerEyePos = mc.player.getEyePos();
        Vec3d entityCenter = entity.getBoundingBox().getCenter();
        Vec3d directionToEntity = entityCenter.subtract(playerEyePos).normalize();

        return candidatePoints.stream()
                .min(Comparator.comparing(point -> {
                    double rotationDiff = calculateRotationDifference(playerEyePos, point, initialAngle);

                    Box entityBox = entity.getBoundingBox();
                    double distanceFromBorder = getDistanceFromBorder(point, entityBox);

                    Vec3d pointDirection = point.subtract(playerEyePos).normalize();
                    double dotProduct = pointDirection.dotProduct(directionToEntity);

                    return rotationDiff * 0.4 - distanceFromBorder * 0.4 - dotProduct * 0.2;
                }))
                .orElse(null);
    }

    private double getDistanceFromBorder(Vec3d point, Box box) {
        double dx = Math.min(Math.abs(point.x - box.minX), Math.abs(point.x - box.maxX));
        double dy = Math.min(Math.abs(point.y - box.minY), Math.abs(point.y - box.maxY));
        double dz = Math.min(Math.abs(point.z - box.minZ), Math.abs(point.z - box.maxZ));

        return Math.min(Math.min(dx, dy), dz);
    }

    private double calculateRotationDifference(Vec3d startPoint, Vec3d endPoint, Angle initialAngle) {
        Angle targetAngle = AngleUtil.fromVec3d(endPoint.subtract(startPoint));
        Angle delta = AngleUtil.calculateDelta(initialAngle, targetAngle);
        return Math.hypot(delta.getYaw(), delta.getPitch());
    }

    private void updateOffset(Vec3d velocity) {
        offset = offset.add(
                random.nextGaussian() * 0.01,
                random.nextGaussian() * 0.01,
                random.nextGaussian() * 0.01
        ).multiply(velocity).multiply(0.1);

        double maxOffset = 0.05;
        offset = new Vec3d(
                Math.max(-maxOffset, Math.min(maxOffset, offset.x)),
                Math.max(-maxOffset, Math.min(maxOffset, offset.y)),
                Math.max(-maxOffset, Math.min(maxOffset, offset.z))
        );
    }
} 