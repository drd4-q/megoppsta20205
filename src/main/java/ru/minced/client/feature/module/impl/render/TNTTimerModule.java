package ru.minced.client.feature.module.impl.render;

import ru.minced.client.util.rotation.rotation.AngleUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TNTTimerModule extends Module implements IMinecraft {

    private static final float TEXT_SIZE = 20.0f;

    private final Map<Integer, Vec3d> lastProjectedPositions = new HashMap<>();
    private final Map<Integer, Vec3d> currentProjectedPositions = new HashMap<>();
    private final Map<Integer, Float> lastDistances = new HashMap<>();

    private static final float scale = 0.4f;
    
    public TNTTimerModule() {
        super("TNT Timer", "Показывает время до взрыва TNT", Category.Visuals);
    }

    @EventHandler
    public void onRender2D(EventRender event) {
        if (IMinecraft.nullCheck()) return;
        
        MatrixStack stack = event.getStack();

        lastProjectedPositions.keySet().removeIf(id -> {
            assert mc.world != null;
            return mc.world.getEntityById(id) == null;
        });
        currentProjectedPositions.keySet().removeIf(id -> {
            assert mc.world != null;
            return mc.world.getEntityById(id) == null;
        });
        lastDistances.keySet().removeIf(id -> {
            assert mc.world != null;
            return mc.world.getEntityById(id) == null;
        });
        
        float partialTicks = MathUtil.getTickDelta();

        assert mc.world != null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof TntEntity tntEntity) {
                int entityId = tntEntity.getId();

                double tPosX = MathUtil.interpolate(tntEntity.prevX, tntEntity.getX(), partialTicks);
                double tPosY = MathUtil.interpolate(tntEntity.prevY, tntEntity.getY(), partialTicks);
                double tPosZ = MathUtil.interpolate(tntEntity.prevZ, tntEntity.getZ(), partialTicks);

                Vec3d pos = new Vec3d(tPosX, tPosY + 0.5, tPosZ);
                
                Vec3d eyePos = mc.gameRenderer.getCamera().getPos();
                Vec3d toTarget = pos.subtract(eyePos);
                double distance = toTarget.length();

                lastDistances.put(entityId, (float)distance);
                
                float yaw = mc.gameRenderer.getCamera().getYaw();
                float pitch = mc.gameRenderer.getCamera().getPitch();
                Vec3d cameraDir = AngleUtil.getVectorForRotation(pitch, yaw);
                double dotProduct = cameraDir.dotProduct(toTarget.normalize());

                if (dotProduct <= 0.0) {
                    continue;
                }
                
                Vec3d projectedPos = MathUtil.projectCoordinates(pos);
                
                if (!isValidProjection(projectedPos)) {
                    continue;
                }
                
                int screenWidth = mc.getWindow().getScaledWidth();
                int screenHeight = mc.getWindow().getScaledHeight();

                if (projectedPos.x < 0 || projectedPos.x > screenWidth || projectedPos.y < 0 || projectedPos.y > screenHeight) {
                    continue;
                }

                currentProjectedPositions.put(entityId, projectedPos);

                Vec3d lastProjected = lastProjectedPositions.getOrDefault(entityId, projectedPos);
                Vec3d interpolatedProjection = new Vec3d(
                        MathUtil.interpolate(lastProjected.x, projectedPos.x, 0.5),
                        MathUtil.interpolate(lastProjected.y, projectedPos.y, 0.5),
                        0
                );

                lastProjectedPositions.put(entityId, interpolatedProjection);
                
                int fuseTicks = tntEntity.getFuse();
                double time = fuseTicks / 20.0;
                String text = String.format("%.1f", time);
                
                float textWidth = Fonts.MEDIUM.getWidth(text, TEXT_SIZE * scale);

                float backgroundWidth = textWidth + 6.0f * 1.5f;
                float backgroundHeight = 14.0f;

                double bgX = interpolatedProjection.x - backgroundWidth / 2;
                double bgY = interpolatedProjection.y - backgroundHeight / 2;
                
                Matrix4f matrix = stack.peek().getPositionMatrix();

                DrawHelper.drawRect(stack, (float)bgX, (float)bgY, backgroundWidth, backgroundHeight, 5.0f, new Color(0, 0, 0, 160));

                float textX = (float)bgX + (backgroundWidth - textWidth) / 2 - 0.5f;
                float textY = MathUtil.centerY((float)bgY, backgroundHeight, TEXT_SIZE * scale);
                
                DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(TEXT_SIZE * scale), text, textX, textY, Color.WHITE);
            }
        }
    }
    
    private boolean isValidProjection(Vec3d projection) {
        return projection != null &&
                !Double.isInfinite(projection.x) && !Double.isInfinite(projection.y) &&
                !Double.isNaN(projection.x) && !Double.isNaN(projection.y) &&
                projection.x != Double.MAX_VALUE && projection.y != Double.MAX_VALUE;
    }
} 