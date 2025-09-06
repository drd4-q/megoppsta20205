package ru.minced.client.core.manager.gps;

public class RadarLogic {

    public static class Vec3 {
        public float x, y, z;

        public Vec3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3 add(Vec3 other) {
            return new Vec3(x + other.x, y + other.y, z + other.z);
        }

        public Vec3 sub(Vec3 other) {
            return new Vec3(x - other.x, y - other.y, z - other.z);
        }

        public Vec3 div(float value) {
            return new Vec3(x / value, y / value, z / value);
        }
    }

    public static Vec3 worldToRadar(
            float yawDegrees,
            Vec3 targetPos,
            Vec3 localPos,
            float posX, float posY,
            Vec3 size,
            boolean[] outOfBounds) {

        boolean flag = false;
        double yawRad = Math.toRadians(yawDegrees);
        float cosYaw = (float) Math.cos(yawRad);
        float sinYaw = (float) Math.sin(yawRad);

        float deltaX = targetPos.x - localPos.x;
        float deltaY = targetPos.y - localPos.y;

        Vec3 radarPos = new Vec3(
                (deltaY * cosYaw - deltaX * sinYaw) / 150f,
                (deltaX * cosYaw + deltaY * sinYaw) / 150f,
                0
        );

        Vec3 transformedPos = new Vec3(
                radarPos.x + posX + size.x / 2f,
                -radarPos.y + posY + size.y / 2f,
                0
        );

        if (transformedPos.x > posX + size.x) {
            transformedPos.x = posX + size.x;
        } else if (transformedPos.x < posX) {
            transformedPos.x = posX;
        }

        if (transformedPos.y > posY + size.y) {
            transformedPos.y = posY + size.y;
        } else if (transformedPos.y < posY) {
            transformedPos.y = posY;
        }

        if (transformedPos.y == posY || transformedPos.x == posX) {
            flag = true;
        }

        outOfBounds[0] = flag;
        return transformedPos;
    }

    public static void calculateVectorAngles(Vec3 forward, Vec3 angles) {
        if (forward.x == 0f && forward.y == 0f) {
            angles.x = forward.z > 0f ? -90f : 90f;
            angles.y = 0f;
        } else {
            angles.x = (float) Math.toDegrees(Math.atan2(
                    -forward.z,
                    Math.sqrt(forward.x * forward.x + forward.y * forward.y + forward.z * forward.z)
            ));
            angles.y = (float) Math.toDegrees(Math.atan2(forward.y, forward.x));
        }
        angles.z = 0f;
    }

    public static void processTarget(
            Vec3 localPos,
            Vec3 targetPos,
            float localYaw,
            float screenX, float screenY) {

        boolean[] outOfBounds = new boolean[1];
        Vec3 radarPos = worldToRadar(localYaw, targetPos, localPos,
                0f, 0f,
                new Vec3(screenX, screenY, 0f),
                outOfBounds);

        Vec3 forward = new Vec3(
                screenX / 2f - radarPos.x,
                screenY / 2f - radarPos.y,
                0f
        );

        Vec3 angles = new Vec3(0, 0, 0);
        calculateVectorAngles(forward, angles);

        float angleYawRad = (float) Math.toRadians(angles.y + 180f);

        float newPointX = (screenX / 2f) + (55 / 2f * 8f * (float) Math.cos(angleYawRad));
        float newPointY = (screenY / 2f) + (55 / 2f * 8f * (float) Math.sin(angleYawRad));

        drawArrowPlaceholder(newPointX, newPointY, angles.y);
    }

    public static void drawArrowPlaceholder(float x, float y, float rotation) {
        System.out.printf("Отрисовать стрелку на (%.2f, %.2f) с углом %.2f°\n", x, y, rotation);
    }

    public static void main(String[] args) {
        Vec3 localPos = new Vec3(100, 100, 0);
        Vec3 targetPos = new Vec3(300, 250, 0);
        processTarget(localPos, targetPos, 45f, 1920, 1080);
    }
}
