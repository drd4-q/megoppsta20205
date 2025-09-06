package ru.minced.client.feature.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.Renderer3D;
import ru.minced.client.util.render.ScaleUtil;
import ru.minced.client.util.render.font.Fonts;
import ru.minced.client.util.rotation.rotation.AngleUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PearlPredictionModule extends Module implements IMinecraft {

    private static final float TEXT_SIZE = 24.0f;
    
    private final List<PearlPoint> pearlPoints = new ArrayList<>();
    
    public PearlPredictionModule() {
        super("Pearl Prediction", "Shows where ender pearls will land", Category.Visuals);
        addSettings();
    }

    @EventHandler
    public void onRender2D(EventRender event) {
        if (IMinecraft.nullCheck()) return;
        
        for (PearlPoint pearlPoint : pearlPoints) {
            Vec3d pos = pearlPoint.position;
            
            Vec3d eyePos = mc.gameRenderer.getCamera().getPos();
            Vec3d toTarget = pos.subtract(eyePos);
            
            float yaw = mc.gameRenderer.getCamera().getYaw();
            float pitch = mc.gameRenderer.getCamera().getPitch();
            Vec3d cameraDir = AngleUtil.getVectorForRotation(pitch, yaw);
            double dotProduct = cameraDir.dotProduct(toTarget.normalize());

            if (dotProduct <= 0.0) {
                continue;
            }
            
            Vec3d projected = MathUtil.projectCoordinates(pos);
            
            if (!isValidProjection(projected)) {
                continue;
            }
            
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();

            if (projected.x < 0 || projected.x > screenWidth || projected.y < 0 || projected.y > screenHeight) {
                continue;
            }

            ScaleUtil.fixScale(event.getContext(), (originalScale) -> {
                MatrixStack stack = event.getStack();
                double scaledX = projected.x * originalScale;
                double scaledY = projected.y * originalScale;
                
                double time = pearlPoint.ticks * 50 / 1000.0;
                String text = String.format("%.1f", time);
                
                float scale = 0.6f;
                float textWidth = Fonts.MEDIUM.getWidth(text, TEXT_SIZE * scale);
                float scaledPadding = 4.0f * scale;
                
                float itemSize = 10.0f * scale;
                float backgroundWidth = textWidth + scaledPadding * 4.0f + itemSize + 4f;
                float backgroundHeight = TEXT_SIZE * scale + scaledPadding * 2;
                
                float verticalOffset = 0;
                
                double bgX = scaledX - backgroundWidth / 2;
                double bgY = scaledY + verticalOffset;
                
                Matrix4f matrix = stack.peek().getPositionMatrix();
                
                Color bgColor = new Color(0, 0, 0, 160);
                DrawHelper.drawRect(stack, (float)bgX, (float)bgY, backgroundWidth, backgroundHeight, 10.0f * scale, bgColor);
                
                stack.push();
                float itemY = (float)bgY + (backgroundHeight - itemSize) / 2 - 2;
                stack.translate(bgX + scaledPadding, itemY, 0);
                stack.scale(0.5F, 0.5F, 1.0F);
                event.getContext().drawItem(new ItemStack(Items.ENDER_PEARL), 0, 0);
                stack.pop();
                
                float textX = (float)bgX + scaledPadding + itemSize + scaledPadding * 2.0f + 2;
                float textY = MathUtil.centerY((float)bgY, backgroundHeight, TEXT_SIZE * scale + 0.5f);
                
                DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(TEXT_SIZE * scale), text, textX, (float)bgY, backgroundHeight, Color.WHITE);
            });
        }
    }
    
    private boolean isValidProjection(Vec3d projection) {
        return projection != null &&
                !Double.isInfinite(projection.x) && !Double.isInfinite(projection.y) &&
                !Double.isNaN(projection.x) && !Double.isNaN(projection.y) &&
                projection.x != Double.MAX_VALUE && projection.y != Double.MAX_VALUE;
    }

    @EventHandler
    public void onRender3D(EventWorld event) {
        if (IMinecraft.nullCheck()) return;
        
        MatrixStack stack = event.getStack();
        Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
        stack.push();
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        RenderSystem.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
        
        float lineWidthValue = 1.5f;
        RenderSystem.lineWidth(lineWidthValue);

        pearlPoints.clear();
        
        boolean hasEnderPearls = false;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EnderPearlEntity) {
                hasEnderPearls = true;
                break;
            }
        }
        
        if (hasEnderPearls) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof EnderPearlEntity enderPearlEntity) {
                    Vec3d motion = enderPearlEntity.getVelocity();
                    Vec3d pos = enderPearlEntity.getPos();
                    Vec3d prevPos;
                    int ticks = 0;
    
                    for (int i = 0; i < 150; i++) {
                        prevPos = pos;
                        pos = pos.add(motion);
    
                        motion = getNextMotion(enderPearlEntity, prevPos, motion);
    
                        HitResult hitResult = mc.world.raycast(
                                new RaycastContext(prevPos, pos,
                                        RaycastContext.ShapeType.COLLIDER,
                                        RaycastContext.FluidHandling.NONE,
                                        enderPearlEntity)
                        );
    
                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            pos = hitResult.getPos();
                        }
    
                        float alpha = i / 25.0f;
                        Color color = new Color(255, 255, 255, MathHelper.clamp((int) (255 * alpha), 0, 255));
    
                        Renderer3D.vertexLine(stack, buffer,
                                (float) prevPos.x,
                                (float) prevPos.y,
                                (float) prevPos.z,
                                (float) pos.x,
                                (float) pos.y,
                                (float) pos.z,
                                color);
    
                        if (hitResult.getType() == HitResult.Type.BLOCK || pos.y < -128) {
                            pearlPoints.add(new PearlPoint(pos, ticks));
                            break;
                        }
                        ticks++;
                    }
                }
            }
    
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
        
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        
        stack.pop();
    }

    private Vec3d getNextMotion(ThrownEntity throwable, Vec3d prevPos, Vec3d motion) {
        boolean isInWater = mc.world.getBlockState(BlockPos.ofFloored(prevPos))
                .getFluidState()
                .isIn(FluidTags.WATER);

        if (isInWater) {
            motion = motion.multiply(0.8);
        } else {
            motion = motion.multiply(0.99);
        }

        if (!throwable.hasNoGravity()) {
            motion = motion.add(0, -0.03F, 0);
        }

        return motion;
    }

    record PearlPoint(Vec3d position, int ticks) {
    }
} 