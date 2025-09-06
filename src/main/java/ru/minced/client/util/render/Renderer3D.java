package ru.minced.client.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Renderer3D {
    private static Tessellator tessellatorFaces = null;
    private static Tessellator tessellatorOutlines = null;

    public static final int ALL_FACES = 0xFFFFFF;
    public static final int ALL_LINES = 0xFFFFFF;

    public static List<DebugLineAction> debugLineQueue = new ArrayList<>();
    public static List<LineAction> lineQueue = new ArrayList<>();

    public static void onRender3D(MatrixStack stack) {
        renderDebugLines(stack);
        renderLines(stack);
    }

    private static void initTessellators() {
        if (tessellatorFaces == null) {
            tessellatorFaces = Tessellator.getInstance();
        }
        if (tessellatorOutlines == null) {
            tessellatorOutlines = new Tessellator(0x200000);
        }
    }

    public static void drawBox(Box box, MatrixStack matrices, Color faceColor, Color outlineColor) {
        drawBox(box, matrices, faceColor, outlineColor, ALL_FACES, ALL_LINES);
    }

    public static void drawBox(Box box, MatrixStack matrices, Color faceColor, Color outlineColor, int facesToDraw, int linesToDraw) {
        initTessellators();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double posX = -camera.getPos().x;
        double posY = -camera.getPos().y;
        double posZ = -camera.getPos().z;

        setupRender();

        if (faceColor != null && faceColor.getAlpha() > 0) {
            drawBoxFaces(box, matrices, posX, posY, posZ, faceColor, facesToDraw);
        }

        if (outlineColor != null && outlineColor.getAlpha() > 0) {
            drawBoxOutlines(box, matrices, posX, posY, posZ, outlineColor, linesToDraw);
        }

        endRender();
    }

    public static void drawBoxFaces(Box box, MatrixStack matrices, Color faceColor) {
        drawBoxFaces(box, matrices, faceColor, ALL_FACES);
    }

    public static void drawBoxFaces(Box box, MatrixStack matrices, Color faceColor, int facesToDraw) {
        if (faceColor == null || faceColor.getAlpha() <= 0) return;

        initTessellators();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double posX = -camera.getPos().x;
        double posY = -camera.getPos().y;
        double posZ = -camera.getPos().z;

        setupRender();
        drawBoxFaces(box, matrices, posX, posY, posZ, faceColor, facesToDraw);
        endRender();
    }

    private static void drawBoxFaces(Box box, MatrixStack matrices, double posX, double posY, double posZ, Color color, int facesToDraw) {
        matrices.push();
        matrices.translate(posX, posY, posZ);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder buffer = tessellatorFaces.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        if (facesToDraw == ALL_FACES || (facesToDraw & 0x0F) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
        }

        if (facesToDraw == ALL_FACES || (facesToDraw & 0xF0) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
        }

        if (facesToDraw == ALL_FACES || (facesToDraw & 0xF00) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.minZ).color(color.getRGB());
        }

        if (facesToDraw == ALL_FACES || (facesToDraw & 0xF000) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
        }

        if (facesToDraw == ALL_FACES || (facesToDraw & 0xF0000) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
        }

        if (facesToDraw == ALL_FACES || (facesToDraw & 0xF00000) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    public static void drawBoxOutlines(Box box, MatrixStack matrices, Color outlineColor) {
        drawBoxOutlines(box, matrices, outlineColor, ALL_LINES);
    }

    public static void drawBoxOutlines(Box box, MatrixStack matrices, Color outlineColor, int linesToDraw) {
        if (outlineColor == null || outlineColor.getAlpha() <= 0) return;

        initTessellators();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double posX = -camera.getPos().x;
        double posY = -camera.getPos().y;
        double posZ = -camera.getPos().z;

        setupRender();
        drawBoxOutlines(box, matrices, posX, posY, posZ, outlineColor, linesToDraw);
        endRender();
    }

    private static void drawBoxOutlines(Box box, MatrixStack matrices, double posX, double posY, double posZ, Color color, int linesToDraw) {
        matrices.push();
        matrices.translate(posX, posY, posZ);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder buffer = tessellatorOutlines.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        if (linesToDraw == ALL_LINES || (linesToDraw & 1) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.minZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 2)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 4)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 6)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.minZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 8)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 10)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 12)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 14)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.minY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 16)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 18)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 20)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
        }

        if (linesToDraw == ALL_LINES || (linesToDraw & (1 << 22)) != 0) {
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.maxZ).color(color.getRGB());
            buffer.vertex(matrices.peek().getPositionMatrix(), (float)box.minX, (float)box.maxY, (float)box.minZ).color(color.getRGB());
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        matrices.pop();
    }

    private static void renderDebugLines(MatrixStack stack) {
        if (debugLineQueue.isEmpty()) {
            return;
        }

        setupRender();
        RenderSystem.disableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.LINES);

        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);

        for (DebugLineAction action : debugLineQueue) {
            MatrixStack matrices = matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
            vertexLine(matrices, buffer, 0f, 0f, 0f,
                    (float) (action.end.getX() - action.start.getX()),
                    (float) (action.end.getY() - action.start.getY()),
                    (float) (action.end.getZ() - action.start.getZ()),
                    action.color);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        endRender();

        debugLineQueue.clear();
    }

    private static void renderLines(MatrixStack stack) {
        if (lineQueue.isEmpty()) {
            return;
        }

        setupRender();
        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.lineWidth(2f);
        RenderSystem.disableDepthTest();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        for (LineAction action : lineQueue) {
            MatrixStack matrices = matrixFrom(action.start.getX(), action.start.getY(), action.start.getZ());
            vertexLine(matrices, buffer, 0f, 0f, 0f,
                    (float) (action.end.getX() - action.start.getX()),
                    (float) (action.end.getY() - action.start.getY()),
                    (float) (action.end.getZ() - action.start.getZ()),
                    action.color);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableCull();
        RenderSystem.lineWidth(1f);
        RenderSystem.enableDepthTest();
        endRender();

        lineQueue.clear();
    }

    public static void setupRender() {
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void endRender() {
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static @NotNull MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrices;
    }

    public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, @NotNull Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        MatrixStack.Entry entry = matrices.peek();
        Vector3f normalVec = getNormal(x1, y1, z1, x2, y2, z2);

        buffer.vertex(model, x1, y1, z1).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(entry, normalVec.x(), normalVec.y(), normalVec.z());
        buffer.vertex(model, x2, y2, z2).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(entry, normalVec.x(), normalVec.y(), normalVec.z());
    }

    public static @NotNull Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

    public record DebugLineAction(Vec3d start, Vec3d end, Color color) {
    }

    public record LineAction(Vec3d start, Vec3d end, Color color) {
    }
}