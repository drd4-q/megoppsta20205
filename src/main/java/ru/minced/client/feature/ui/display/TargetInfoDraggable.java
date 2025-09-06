package ru.minced.client.feature.ui.display;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.api.EventType;
import ru.minced.client.core.event.impl.player.EventAttack;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.other.StringHelper;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import ru.minced.client.util.math.MathUtil;

import java.awt.*;
import java.util.Objects;

public class TargetInfoDraggable extends AbstractDraggable implements IMinecraft {
    private static final int VISUAL_WIDTH = 192;
    private static final int VISUAL_HEIGHT = 70;
    private static final float PADDING = 6.0f;
    private static final float ICON_SIZE = 58.0f;
    private static final float CORNER_RADIUS = 12.0f;
    private static final float ITEM_SIZE = 16.0f;
    private static final float ITEM_SPACING = 4.0f;
    private static final float NAME_TEXT_SIZE = 16.0f;

    private LivingEntity target;
    private int displayTicks = 0;
    private boolean isMouseDown = false;
    private float animatedHealthPercent = 0.0f;

    public TargetInfoDraggable() {
        super("TargetInfo", 10, 10, VISUAL_WIDTH, VISUAL_HEIGHT);
        Minced.getInstance().getEventManager().subscribe(this);
    }

    @Override
    public boolean visible() {
        boolean interfaceEnabled = Minced.getInstance().getModuleManager().getDisplayModule().isState();
        boolean targetHudSelected = DisplayModule.elements.isSelected("TargetInfo");
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;

        if (chatOpen && interfaceEnabled) {
            return true;
        }

        return displayTicks > 0 && target != null && target.isAlive() && target.getHealth() > 0
                && targetHudSelected && interfaceEnabled;
    }

    @Override
    public void tick(float delta) {
        if (displayTicks > 0) {
            displayTicks--;
            visibleDrag();

            if (target != null && target.isAlive()) {
                float healthPercent = target.getHealth() / target.getMaxHealth();
                animatedHealthPercent = (float) MathUtil.interpolate(animatedHealthPercent, healthPercent, 0.1);
            }
        } else {
            hideDrag();
        }

        super.tick(delta);
    }

    @EventHandler
    public void onAttack(EventAttack event) {
        if (event.getEventType() == EventType.PRE && event.getTarget() instanceof LivingEntity livingEntity) {
            target = livingEntity;
            displayTicks = 1200;
        }
    }

    private String getTargetName() {
        if (target instanceof PlayerEntity player) {
            return player.getGameProfile().getName();
        }
        return Objects.requireNonNull(target.getDisplayName()).getString();
    }

    private Identifier getTargetSkin() {
        if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(target.getUuid()) != null) {
            return Objects.requireNonNull(mc.getNetworkHandler().getPlayerListEntry(target.getUuid())).getSkinTextures().texture();
        }
        return IMinecraft.stevePng;
    }

    private void renderTruncatedText(Matrix4f matrix, String text, float x, float y, float maxWidth) {
        float nameWidth = Fonts.MEDIUM.getWidth(text, NAME_TEXT_SIZE);

        if (nameWidth <= maxWidth) {
            DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(NAME_TEXT_SIZE), text, x, y, Color.WHITE);
            return;
        }

        String truncatedText = StringHelper.truncate(text, maxWidth, Fonts.MEDIUM.font(), NAME_TEXT_SIZE);
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(NAME_TEXT_SIZE), truncatedText, x, y, Color.WHITE);
    }

    private void renderEquipmentItem(DrawContext context, float x, float y, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            context.drawItem(stack, (int)x, (int)y);

            if (stack.getCount() > 1) {
                String countText = String.valueOf(stack.getCount());
                float textWidth = Fonts.MEDIUM.getWidth(countText, 12);

                DrawHelper.drawText(
                        context.getMatrices().peek().getPositionMatrix(),
                        Fonts.MEDIUM.getFont(12),
                        countText,
                        x + ITEM_SIZE - textWidth,
                        y + ITEM_SIZE - 12,
                        Color.WHITE
                );
            }
        } else {
            MatrixStack matrixStack = context.getMatrices();
            DrawHelper.drawRect(matrixStack, x, y, ITEM_SIZE, ITEM_SIZE, 2, new Color(60, 60, 60, 120));
        }
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

        Color finalColor = isMouseDown ? bgColor : applyAnimatedAlphaPreserveOriginal(bgColor);

        DrawHelper.drawRect(matrixStack, x, y, width, height, CORNER_RADIUS, finalColor);
    }

    private void renderPlayerIcon(Matrix4f matrix, float x, float y) {
        AbstractTexture targetTexture = MinecraftClient.getInstance().getTextureManager()
                .getTexture(getTargetSkin());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);

        DrawHelper.drawTexture(matrixStack, x, y, ICON_SIZE, ICON_SIZE, CORNER_RADIUS / 2.0f, targetTexture);
    }

    private void renderHealthBar(Matrix4f matrix, float x, float y, float width, float height) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);

        DrawHelper.drawRect(matrixStack, x, y, width, height, 10 / 2.0f, new Color(97, 97, 97, 128));

        float filledWidth = Math.max(0, Math.min(width * animatedHealthPercent, width));
        if (filledWidth > 0) {
            DrawHelper.drawRect(matrixStack, x, y, filledWidth, height, 10 / 2.0f, new Color(150, 150, 150, 255));
        }

        if (target.getAbsorptionAmount() > 0) {
            float absorptionPercent = target.getAbsorptionAmount() / target.getMaxHealth();
            float absorptionWidth = Math.min(width * absorptionPercent, width);

            DrawHelper.drawRect(matrixStack, x, y, absorptionWidth, height, CORNER_RADIUS / 2.0f, new Color(222, 189, 0, 255));
        }

        String healthText = Math.round(animatedHealthPercent * 100) + "%";
        float textWidth = Fonts.MEDIUM.getWidth(healthText, 12);
        float textX = MathUtil.centerX(x, width, textWidth);
        float textY = MathUtil.centerY(y, height, 12f) - 1;

        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(12), healthText, textX, textY, Color.WHITE);
    }

    private void renderEquipment(DrawContext context, float x, float y) {
        renderEquipmentItem(context, x, y, target.getEquippedStack(EquipmentSlot.HEAD));
        renderEquipmentItem(context, x + ITEM_SIZE + ITEM_SPACING, y, target.getEquippedStack(EquipmentSlot.CHEST));
        renderEquipmentItem(context, x + (ITEM_SIZE + ITEM_SPACING) * 2, y, target.getEquippedStack(EquipmentSlot.LEGS));
        renderEquipmentItem(context, x + (ITEM_SIZE + ITEM_SPACING) * 3, y, target.getEquippedStack(EquipmentSlot.FEET));
        renderEquipmentItem(context, x + (ITEM_SIZE + ITEM_SPACING) * 4, y, target.getEquippedStack(EquipmentSlot.MAINHAND));
        renderEquipmentItem(context, x + (ITEM_SIZE + ITEM_SPACING) * 5, y, target.getEquippedStack(EquipmentSlot.OFFHAND));
    }

    @Override
    public void drawDraggable(DrawContext context) {
        if (target == null || !target.isAlive() || target.getHealth() <= 0) {
            return;
        }

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float x = getX();
        float y = getY();

        renderBackground(matrix, x, y, VISUAL_WIDTH, VISUAL_HEIGHT);

        renderPlayerIcon(matrix, x + PADDING, y + PADDING);

        float baseX = x + PADDING + ICON_SIZE + PADDING;
        String targetName = getTargetName();
        float maxNameWidth = VISUAL_WIDTH - PADDING - ICON_SIZE - PADDING * 2 - 10;
        renderTruncatedText(matrix, targetName, baseX, y + PADDING, maxNameWidth);

        float hpBarY = y + VISUAL_HEIGHT - PADDING - 18;
        renderHealthBar(matrix, baseX, hpBarY, 116, 18);

        renderEquipment(context, baseX, hpBarY - 5 - ITEM_SIZE + 1);
    }
}