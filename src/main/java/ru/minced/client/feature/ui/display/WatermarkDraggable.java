package ru.minced.client.feature.ui.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import ru.minced.client.util.network.PingUtil;

import java.awt.Color;

public class WatermarkDraggable extends AbstractDraggable implements IMinecraft {
    private static final int VISUAL_HEIGHT = 32;
    private static final float PADDING = 6.0f;
    private static final float NEW_PADDING = 8.0f;
    private static final float TEXT_SIZE = 16.0f;
    private static final float FPS_TEXT_SIZE = 14.0f;
    private static final String WATERMARK_TEXT = "Minced";
    private static final float CORNER_RADIUS = 10.0f;
    private static final Color SEPARATOR_COLOR = new Color(119, 119, 119);
    private static final float SEPARATOR_WIDTH = 4.0f;
    private static final float THIN_SEPARATOR_WIDTH = 2.0f;
    private static final float SEPARATOR_HEIGHT = 12.0f;

    private float targetWidth;
    private float currentWidth;

    public WatermarkDraggable() {
        super("Watermark", 10, 10, 100, VISUAL_HEIGHT);
        Minced.getInstance().getEventManager().subscribe(this);
        this.targetWidth = 100;
        this.currentWidth = 100;
    }

    @Override
    public boolean visible() {
        boolean interfaceEnabled = Minced.getInstance().getModuleManager().getDisplayModule().isState();
        boolean watermarkSelected = DisplayModule.elements.isSelected("Watermark");
        return interfaceEnabled && watermarkSelected;
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        
        float textWidth = Fonts.BOLD.getWidth(WATERMARK_TEXT, TEXT_SIZE);

        int fps = mc.getCurrentFps();
        String fpsText = fps + " fps";
        float fpsTextWidth = Fonts.MEDIUM.getWidth(fpsText, FPS_TEXT_SIZE);


        int playerPing = (mc.player != null) ? PingUtil.getPlayerPing(mc.player.getName().getString()) : -1;
        String pingText = playerPing >= 0 ? playerPing + " ms" : "Limbo";
        float pingTextWidth = Fonts.MEDIUM.getWidth(pingText, FPS_TEXT_SIZE);
        
        float tps = Minced.getInstance().getTpsCalc().getTPS();
        String tpsText = tps + " tps";
        float tpsTextWidth = Fonts.MEDIUM.getWidth("0.0", FPS_TEXT_SIZE);

        float totalWidth = PADDING + textWidth + PADDING + SEPARATOR_WIDTH + (PADDING - 1) + 
                          pingTextWidth + NEW_PADDING +
                          THIN_SEPARATOR_WIDTH + PADDING + 
                          fpsTextWidth + NEW_PADDING +
                          THIN_SEPARATOR_WIDTH + PADDING + 
                          tpsTextWidth + PADDING;
        float calculatedWidth = (int) Math.ceil(totalWidth);
        
        targetWidth = calculatedWidth;
        currentWidth = (float) MathUtil.interpolate(currentWidth, targetWidth, 0.1);
        
        if ((int) currentWidth != getWidth()) {
            setWidth((int) currentWidth);
        }
        
        float x = getX();
        float y = getY();
        
        renderBackground(matrix, x, y, currentWidth + PADDING - 1, VISUAL_HEIGHT);

        float textY = y + (VISUAL_HEIGHT - TEXT_SIZE) / 2 - 1.5f;
        
        float textX = x + PADDING;
        DrawHelper.drawText(matrix, Fonts.BOLD.getFont(TEXT_SIZE), WATERMARK_TEXT, textX, textY, applyAnimatedAlpha(Color.WHITE));

        float separatorX = textX + textWidth + NEW_PADDING;
        float separatorY = y + (VISUAL_HEIGHT - SEPARATOR_HEIGHT) / 2 + 1;
        MatrixStack separatorMatrix = new MatrixStack();
        separatorMatrix.peek().getPositionMatrix().set(matrix);
        DrawHelper.drawRect(separatorMatrix, separatorX, separatorY, SEPARATOR_WIDTH, SEPARATOR_HEIGHT, 1.0F, applyAnimatedAlpha(SEPARATOR_COLOR));

        float infoY = y + (VISUAL_HEIGHT - FPS_TEXT_SIZE) / 2 - 1.0f;

        float pingX = separatorX + SEPARATOR_WIDTH + PADDING - 1;
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(FPS_TEXT_SIZE), pingText, pingX, infoY, applyAnimatedAlpha(Color.WHITE));

        float thinSeparator1X = pingX + pingTextWidth + NEW_PADDING;
        MatrixStack thinSeparatorMatrix1 = new MatrixStack();
        thinSeparatorMatrix1.peek().getPositionMatrix().set(matrix);
        DrawHelper.drawRect(thinSeparatorMatrix1, thinSeparator1X, separatorY, THIN_SEPARATOR_WIDTH, SEPARATOR_HEIGHT, 1.0F, applyAnimatedAlpha(SEPARATOR_COLOR));

        float fpsX = thinSeparator1X + THIN_SEPARATOR_WIDTH + PADDING;
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(FPS_TEXT_SIZE), fpsText, fpsX, infoY, applyAnimatedAlpha(Color.WHITE));

        float thinSeparator2X = fpsX + fpsTextWidth + NEW_PADDING;
        MatrixStack thinSeparatorMatrix2 = new MatrixStack();
        thinSeparatorMatrix2.peek().getPositionMatrix().set(matrix);
        DrawHelper.drawRect(thinSeparatorMatrix2, thinSeparator2X, separatorY, THIN_SEPARATOR_WIDTH, SEPARATOR_HEIGHT, 1.0F, applyAnimatedAlpha(SEPARATOR_COLOR));

        double dx = mc.player.getX() - mc.player.prevX;
        double dz = mc.player.getZ() - mc.player.prevZ;
        double horizontalSpeed = Math.sqrt(dx * dx + dz * dz);
        horizontalSpeed = Math.round(horizontalSpeed * 20 * 10.0) / 10.0;
        String speedText = String.format(java.util.Locale.US, "%.1f", horizontalSpeed);

        float tpsX = thinSeparator2X + THIN_SEPARATOR_WIDTH + PADDING;
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(FPS_TEXT_SIZE), speedText, tpsX, infoY, applyAnimatedAlpha(Color.WHITE));
    }
    
    private void renderBackground(Matrix4f matrix, float x, float y, float width, float height) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);
        
        DrawHelper.drawBlur(matrixStack, x, y, width, height, CORNER_RADIUS, applyAnimatedAlpha(Color.WHITE), 10.0F);

        Color bgColor = new Color(
            DisplayModule.getHudBackground().getRed(),
            DisplayModule.getHudBackground().getGreen(),
            DisplayModule.getHudBackground().getBlue(),
            DisplayModule.getHudBackground().getAlpha()
        );
        
        DrawHelper.drawRect(matrixStack, x, y, width, height, CORNER_RADIUS, applyAnimatedAlphaPreserveOriginal(bgColor));
    }
} 