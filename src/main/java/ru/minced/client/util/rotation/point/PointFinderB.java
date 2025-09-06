package ru.minced.client.util.rotation.point;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.LivingEntity;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class PointFinderB implements IMinecraft {
    private Random random = new SecureRandom();
    @NonFinal
    private Vec3d offset = Vec3d.ZERO;

    public Vec3d computeVector(LivingEntity entity, float maxDistance, Angle initialAngle, Vec3d velocity) {
        List<Vec3d> candidatePoints = generateCandidatePoints(entity, maxDistance);

        Vec3d bestVector = findBestVector(candidatePoints, initialAngle);

        updateOffset(velocity);

        return (bestVector == null ? entity.getEyePos() : bestVector).add(offset);
    }

    private List<Vec3d> generateCandidatePoints(LivingEntity entity, float maxDistance) {
        Box entityBox = entity.getBoundingBox().expand(-0.18F);
        double headHeight = entityBox.getLengthY() * 0.15F;
        double headY = entityBox.maxY - headHeight;

        Box headBox = new Box(
            entityBox.minX, 
            headY, 
            entityBox.minZ, 
            entityBox.maxX, 
            entityBox.maxY, 
            entityBox.maxZ
        );
        
        double stepY = headBox.getLengthY() / 4.0F;
        double stepX = headBox.getLengthX() / 3.0F;
        double stepZ = headBox.getLengthZ() / 3.0F;
        
        List<Vec3d> points = new ArrayList<>();

        for (double y = headBox.minY; y <= headBox.maxY; y += stepY) {
            for (double x = headBox.minX; x <= headBox.maxX; x += stepX) {
                for (double z = headBox.minZ; z <= headBox.maxZ; z += stepZ) {
                    Vec3d point = new Vec3d(x, y, z);
                    assert mc.player != null;
                    if (isWithinDistance(mc.player.getEyePos(), point, maxDistance)) {
                        points.add(point);
                    }
                }
            }
        }
        
        return points;
    }

    private boolean isWithinDistance(Vec3d startPoint, Vec3d endPoint, float maxDistance) {
        return startPoint.distanceTo(endPoint) < maxDistance;
    }

    private Vec3d findBestVector(List<Vec3d> candidatePoints, Angle initialAngle) {
        if (candidatePoints.isEmpty()) {
            return null;
        }

        assert mc.player != null;
        Vec3d playerEyePos = mc.player.getEyePos();

        return candidatePoints.stream().min(Comparator.comparing(point -> calculateRotationDifference(playerEyePos, point, initialAngle)))
                .orElse(null);
    }

    private double calculateRotationDifference(Vec3d startPoint, Vec3d endPoint, Angle initialAngle) {
        Angle targetAngle = AngleUtil.fromVec3d(endPoint.subtract(startPoint));
        Angle delta = AngleUtil.calculateDelta(initialAngle, targetAngle);
        return Math.hypot(delta.getYaw(), delta.getPitch());
    }

    private void updateOffset(Vec3d velocity) {
        offset = offset.add(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).multiply(velocity);
    }
}






































