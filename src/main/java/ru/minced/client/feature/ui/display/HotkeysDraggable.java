package ru.minced.client.feature.ui.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import ru.minced.client.util.other.StringHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HotkeysDraggable extends AbstractDraggable implements IMinecraft {
    private static final int HEIGHT = 30;
    private static final float SIDE_PADDING = 10.0f;
    private static final float MODULE_PADDING = -6.0f;
    private static final float TITLE_TEXT_SIZE = 14.0f;
    private static final float MODULE_TEXT_SIZE = 12.0f;
    private static final String DEFAULT_TEXT = "Hotkeys";
    private static final float MODULE_TOP_OFFSET = 2.0f;
    private static final int MIN_WIDTH = 120;
    private static final float MODULE_KEY_SPACING = 40.0f;
    private static final float ANIMATION_SPEED = 0.02f;
    private static final float CORNER_RADIUS = 12.0f;


            
    private float animatedHeight = HEIGHT;
    private long lastUpdateTime = System.currentTimeMillis();
    private float currentWidth = MIN_WIDTH;

    public HotkeysDraggable() {
        super("Hotkeys", 6, 30, MIN_WIDTH, HEIGHT);
    }

    @Override
    public boolean visible() {
        boolean interfaceEnabled = Minced.getInstance().getModuleManager().getDisplayModule().isState();
        boolean hotkeySelected = DisplayModule.elements.isSelected("Hotkeys");
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;

        if (chatOpen && interfaceEnabled) {
            return true;
        }
        return hotkeySelected && interfaceEnabled;
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        List<Module> modulesWithKeys = getModulesWithKeys();
        float targetHeight = HEIGHT;

        if (!modulesWithKeys.isEmpty()) {
            targetHeight = HEIGHT - MODULE_TOP_OFFSET + (modulesWithKeys.size() * HEIGHT) + (modulesWithKeys.size() - 1) * MODULE_PADDING;
        }

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        animatedHeight += (targetHeight - animatedHeight) * (ANIMATION_SPEED * deltaTime);

        float maxNameWidth = 0;
        float maxKeyWidth = 0;
        float titleWidth = Fonts.MEDIUM.getWidth(modulesWithKeys.isEmpty() ? DEFAULT_TEXT : "Hotkeys", TITLE_TEXT_SIZE);
        float minWidth = Math.max(MIN_WIDTH, titleWidth + SIDE_PADDING * 2);

        if (!modulesWithKeys.isEmpty()) {
            for (Module module : modulesWithKeys) {
                float nameWidth = Fonts.MEDIUM.getWidth(module.getName(), MODULE_TEXT_SIZE);
                float keyWidth = Fonts.MEDIUM.getWidth(StringHelper.getBindName(module.getKey()), MODULE_TEXT_SIZE);

                maxNameWidth = Math.max(maxNameWidth, nameWidth);
                maxKeyWidth = Math.max(maxKeyWidth, keyWidth);
            }

            float contentWidth = SIDE_PADDING + maxNameWidth + MODULE_KEY_SPACING + maxKeyWidth + SIDE_PADDING;
            minWidth = Math.max(minWidth, contentWidth);
        }

        float targetWidth = minWidth;
        currentWidth = (float) MathUtil.interpolate(currentWidth, targetWidth, 0.1);

        setBounds((int) Math.ceil(currentWidth), (int) Math.ceil(animatedHeight));
    }

    private List<Module> getModulesWithKeys() {
        List<Module> modules = new ArrayList<>();
        for (Module module : ModuleManager.modules) {
            if (module.isState() && module.getKey() != 0) {
                modules.add(module);
            }
        }
        return modules;
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float x = getX();
        float y = getY();

        List<Module> modulesWithKeys = getModulesWithKeys();
        boolean hasModules = !modulesWithKeys.isEmpty();

        float totalWidth = currentWidth;

        renderBackground(matrix, x, y, totalWidth, animatedHeight, hasModules);

        float titleY = y + ((float) HEIGHT / 2) - Fonts.MEDIUM.getFont(TITLE_TEXT_SIZE).font().getMetrics().lineHeight() * TITLE_TEXT_SIZE / 2;
        String titleText = hasModules ? "Hotkeys" : DEFAULT_TEXT;

        renderText(matrix, titleText, TITLE_TEXT_SIZE,
                x + (totalWidth / 2) - (Fonts.MEDIUM.getWidth(titleText, TITLE_TEXT_SIZE) / 2), titleY);

        if (hasModules) {
            renderModulesList(matrix, context, modulesWithKeys, x, y, totalWidth);
        }
    }

    private void renderBackground(Matrix4f matrix, float x, float y, float width, float height, boolean hasModules) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);

        Color bgColor = new Color(
            DisplayModule.getHudBackground().getRed(),
            DisplayModule.getHudBackground().getGreen(),
            DisplayModule.getHudBackground().getBlue(),
            DisplayModule.getHudBackground().getAlpha()
        );

        DrawHelper.drawBlur(matrixStack, x, y, width, height, CORNER_RADIUS, applyAnimatedAlpha(Color.WHITE), 10.0F);

        DrawHelper.drawRect(matrixStack, x, y, width, height, CORNER_RADIUS,
                applyAnimatedAlphaPreserveOriginal(bgColor));

        if (hasModules) {
            float[] cornerRadii = {CORNER_RADIUS, 0, 0, CORNER_RADIUS};
            DrawHelper.drawRectWithCustomRadius(matrixStack, x, y, width, HEIGHT, cornerRadii,
                    applyAnimatedAlphaPreserveOriginal(bgColor));
        } else {
            DrawHelper.drawRect(matrixStack, x, y, width, HEIGHT, CORNER_RADIUS,
                    applyAnimatedAlphaPreserveOriginal(bgColor));
        }
    }

    private void renderModulesList(Matrix4f matrix, DrawContext context, List<Module> modules, float x, float y, float width) {
        MatrixStack matrixStack = context.getMatrices();
        float listY = y + HEIGHT - MODULE_TOP_OFFSET;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            String name = module.getName();
            String key = StringHelper.getBindName(module.getKey());

            float moduleY = listY + (i * (HEIGHT + MODULE_PADDING));
            float moduleTextY = moduleY + ((float) HEIGHT / 2) - Fonts.MEDIUM.getFont(MODULE_TEXT_SIZE).font().getMetrics().lineHeight() * MODULE_TEXT_SIZE / 2;

            renderText(matrix, name, MODULE_TEXT_SIZE, x + SIDE_PADDING, moduleTextY);

            float keyWidth = Fonts.MEDIUM.getWidth(key, MODULE_TEXT_SIZE);
            float keyX = x + width - SIDE_PADDING - keyWidth;

            float rectWidth = keyWidth + 8;
            float rectHeight = Fonts.MEDIUM.getFont(MODULE_TEXT_SIZE).font().getMetrics().lineHeight() * MODULE_TEXT_SIZE + 6;
            float rectX = keyX - 4;
            float rectY = moduleTextY - 3.0f;

            Color bgColor = new Color(
                DisplayModule.getHudBackground().getRed(),
                DisplayModule.getHudBackground().getGreen(),
                DisplayModule.getHudBackground().getBlue(),
                DisplayModule.getHudBackground().getAlpha()
            );
            
            DrawHelper.drawRect(matrixStack, rectX, rectY, rectWidth, rectHeight, 4,
                    applyAnimatedAlphaPreserveOriginal(bgColor));

            float centeredKeyX = rectX + (rectWidth / 2) - (keyWidth / 2);
            float centeredKeyY = rectY + (rectHeight / 2) - (Fonts.MEDIUM.getFont(MODULE_TEXT_SIZE).font().getMetrics().lineHeight() * MODULE_TEXT_SIZE / 2);
            
            renderText(matrix, key, MODULE_TEXT_SIZE, centeredKeyX - 0.5f, centeredKeyY);
        }
    }

    private void renderText(Matrix4f matrix, String text, float size, float x, float y) {
        if (text == null || text.isEmpty()) {
            return;
        }
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(size), text, x, y, Color.WHITE);
    }
}