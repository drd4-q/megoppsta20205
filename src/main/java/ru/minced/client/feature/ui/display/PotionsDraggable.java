package ru.minced.client.feature.ui.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PotionsDraggable extends AbstractDraggable implements IMinecraft {
    private static final int HEIGHT = 30;
    private static final float SIDE_PADDING = 10.0f;
    private static final float EFFECT_PADDING = -6.0f;
    private static final float TITLE_TEXT_SIZE = 14.0f;
    private static final float EFFECT_TEXT_SIZE = 12.0f;
    private static final String DEFAULT_TEXT = "Potions";
    private static final float EFFECT_TOP_OFFSET = 2.0f;
    private static final int MIN_WIDTH = 120;
    private static final float EFFECT_TIME_SPACING = 40.0f;
    private static final float ANIMATION_SPEED = 0.02f;
    private static final float CORNER_RADIUS = 12.0f;


    private float animatedHeight = HEIGHT;
    private long lastUpdateTime = System.currentTimeMillis();
    private float targetWidth = MIN_WIDTH;
    private float currentWidth = MIN_WIDTH;

    public PotionsDraggable() {
        super("Potions", 6, 80, MIN_WIDTH, HEIGHT);
    }

    @Override
    public boolean visible() {
        boolean interfaceEnabled = Minced.getInstance().getModuleManager().getDisplayModule().isState();
        boolean potionsSelected = DisplayModule.elements.isSelected("Potions");
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;

        if (chatOpen && interfaceEnabled) {
            return true;
        }

        return potionsSelected && interfaceEnabled;
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        
        List<StatusEffectInstance> activeEffects = getActiveEffects();
        float targetHeight = HEIGHT;
        
        if (!activeEffects.isEmpty()) {
            targetHeight = HEIGHT - EFFECT_TOP_OFFSET + (activeEffects.size() * HEIGHT) + (activeEffects.size() - 1) * EFFECT_PADDING;
        }
        
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        
        animatedHeight += (targetHeight - animatedHeight) * (ANIMATION_SPEED * deltaTime);
        
        float maxNameWidth = 0;
        float maxTimeWidth = 0;
        float titleWidth = Fonts.MEDIUM.getWidth(DEFAULT_TEXT, TITLE_TEXT_SIZE);
        float minWidth = Math.max(MIN_WIDTH, titleWidth + SIDE_PADDING * 2);
        
        if (!activeEffects.isEmpty()) {
            for (StatusEffectInstance effect : activeEffects) {
                String name = getEffectName(effect);
                String time = formatDuration(effect.getDuration());
                
                float nameWidth = Fonts.MEDIUM.getWidth(name, EFFECT_TEXT_SIZE);
                float timeWidth = Fonts.MEDIUM.getWidth(time, EFFECT_TEXT_SIZE);
                
                maxNameWidth = Math.max(maxNameWidth, nameWidth);
                maxTimeWidth = Math.max(maxTimeWidth, timeWidth);
            }
            
            float contentWidth = SIDE_PADDING + maxNameWidth + EFFECT_TIME_SPACING + maxTimeWidth + SIDE_PADDING;
            minWidth = Math.max(minWidth, contentWidth);
        }
        
        targetWidth = minWidth;
        currentWidth = (float) MathUtil.interpolate(currentWidth, targetWidth, 0.1);
        
        setBounds((int) Math.ceil(currentWidth), (int) Math.ceil(animatedHeight));
    }



    private String getEffectName(StatusEffectInstance effect) {
        String name = Text.translatable(effect.getTranslationKey()).getString();
        int amplifier = effect.getAmplifier();
        if (amplifier > 0) {
            name += " " + (amplifier + 1);
        }
        return name;
    }
    
    private List<StatusEffectInstance> getActiveEffects() {
        List<StatusEffectInstance> effects = new ArrayList<>();
        
        if (mc.player != null && !mc.player.getStatusEffects().isEmpty()) {
            effects.addAll(mc.player.getStatusEffects());

            effects.sort(Comparator.comparingInt(StatusEffectInstance::getDuration));
        }
        
        return effects;
    }

    private String formatDuration(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private Color getStatusEffectColor(RegistryEntry<StatusEffect> effectEntry) {
        StatusEffect effect = effectEntry.value();
        return effect.isBeneficial() ? Color.WHITE : new Color(255, 60, 60);
    }

    @Override
    public void drawDraggable(DrawContext context) {


        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float x = getX();
        float y = getY();

        List<StatusEffectInstance> activeEffects = getActiveEffects();
        boolean hasEffects = !activeEffects.isEmpty();

        float totalWidth = currentWidth;

        renderBackground(matrix, x, y, totalWidth, animatedHeight, hasEffects);

        float titleY = y + ((float) HEIGHT / 2) - Fonts.MEDIUM.getFont(TITLE_TEXT_SIZE).font().getMetrics().lineHeight() * TITLE_TEXT_SIZE / 2;
        String titleText = hasEffects ? "Potions" : DEFAULT_TEXT;

        renderText(matrix, titleText, TITLE_TEXT_SIZE,
                x + (totalWidth / 2) - (Fonts.MEDIUM.getWidth(titleText, TITLE_TEXT_SIZE) / 2), titleY);

        if (hasEffects) {
            renderEffectsList(matrix, context, activeEffects, x, y, totalWidth);
        }
    }

    private void renderBackground(Matrix4f matrix, float x, float y, float width, float height, boolean hasEffects) {
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

        if (hasEffects) {
            float[] cornerRadii = {CORNER_RADIUS, 0, 0, CORNER_RADIUS};
            DrawHelper.drawRectWithCustomRadius(matrixStack, x, y, width, HEIGHT, cornerRadii,
                    applyAnimatedAlphaPreserveOriginal(bgColor));
        } else {
            DrawHelper.drawRect(matrixStack, x, y, width, HEIGHT, CORNER_RADIUS,
                    applyAnimatedAlphaPreserveOriginal(bgColor));
        }
    }

    private void renderEffectsList(Matrix4f matrix, DrawContext context, List<StatusEffectInstance> effects, float x, float y, float width) {
        MatrixStack matrixStack = context.getMatrices();
        float listY = y + HEIGHT - EFFECT_TOP_OFFSET;

        for (int i = 0; i < effects.size(); i++) {
            StatusEffectInstance effect = effects.get(i);
            String name = getEffectName(effect);
            String duration = formatDuration(effect.getDuration());
            
            Color textColor = getStatusEffectColor(effect.getEffectType());

            float effectY = listY + (i * (HEIGHT + EFFECT_PADDING));
            float effectTextY = effectY + ((float) HEIGHT / 2) - Fonts.MEDIUM.getFont(EFFECT_TEXT_SIZE).font().getMetrics().lineHeight() * EFFECT_TEXT_SIZE / 2;

            renderText(matrix, name, EFFECT_TEXT_SIZE, x + SIDE_PADDING, effectTextY, textColor);

            float durationWidth = Fonts.MEDIUM.getWidth(duration, EFFECT_TEXT_SIZE);
            float durationX = x + width - SIDE_PADDING - durationWidth;

            float rectWidth = durationWidth + 10;
            float rectHeight = Fonts.MEDIUM.getFont(EFFECT_TEXT_SIZE).font().getMetrics().lineHeight() * EFFECT_TEXT_SIZE + 6;
            float rectX = durationX - 4;
            float rectY = effectTextY - 3.0f;

            Color bgColor = new Color(
                DisplayModule.getHudBackground().getRed(),
                DisplayModule.getHudBackground().getGreen(),
                DisplayModule.getHudBackground().getBlue(),
                DisplayModule.getHudBackground().getAlpha()
            );
            
            DrawHelper.drawRect(matrixStack, rectX, rectY, rectWidth, rectHeight, 4,
                    applyAnimatedAlphaPreserveOriginal(bgColor));

            float centeredDurationX = rectX + (rectWidth / 2) - (durationWidth / 2);
            float centeredDurationY = rectY + (rectHeight / 2) - (Fonts.MEDIUM.getFont(EFFECT_TEXT_SIZE).font().getMetrics().lineHeight() * EFFECT_TEXT_SIZE / 2);
            
            renderText(matrix, duration, EFFECT_TEXT_SIZE, centeredDurationX - 1.0f, centeredDurationY, textColor);
        }
    }

    private void renderText(Matrix4f matrix, String text, float size, float x, float y) {
        renderText(matrix, text, size, x, y, Color.WHITE);
    }
    
    private void renderText(Matrix4f matrix, String text, float size, float x, float y, Color color) {
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(size), text, x, y, color);
    }
}