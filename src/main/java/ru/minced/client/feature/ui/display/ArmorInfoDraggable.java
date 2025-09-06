package ru.minced.client.feature.ui.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.render.DrawHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArmorInfoDraggable extends AbstractDraggable {
    private static final int VISUAL_WIDTH = 134;
    private static final int VISUAL_HEIGHT = 38;
    private static final float PADDING = 6.0f;
    private static final float CORNER_RADIUS = 12.0f;
    private static final float ITEM_SIZE = 26.0f;
    
    public ArmorInfoDraggable() {
        super("ArmorInfo", 5, 10, VISUAL_WIDTH, VISUAL_HEIGHT);
    }

    @Override
    public boolean visible() {
        return DisplayModule.elements.isSelected("ArmorInfo")
                && Minced.getInstance().getModuleManager().getDisplayModule().isState()
                || mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public void drawDraggable(DrawContext context) {
        if (mc.player == null) return;
        
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float x = getX();
        float y = getY();
        
        renderBackground(matrix, x, y, VISUAL_WIDTH, VISUAL_HEIGHT);
        
        float xItemOffset = x + PADDING;
        float yItemOffset = y + PADDING;

        List<ItemStack> armorItems = new ArrayList<>();
        armorItems.add(mc.player.getEquippedStack(EquipmentSlot.HEAD));
        armorItems.add(mc.player.getEquippedStack(EquipmentSlot.CHEST));
        armorItems.add(mc.player.getEquippedStack(EquipmentSlot.LEGS));
        armorItems.add(mc.player.getEquippedStack(EquipmentSlot.FEET));
        
        for (ItemStack itemStack : armorItems) {
            if (!itemStack.isEmpty()) {
                int durability = itemStack.getMaxDamage() - itemStack.getDamage();
                int maxDurability = itemStack.getMaxDamage();
                float durabilityPercent = maxDurability > 0 ? (float) durability / maxDurability : 1.0f;

                DiffuseLighting.disableGuiDepthLighting();
                
                MatrixStack matrixStack = context.getMatrices();
                matrixStack.push();
                matrixStack.translate(xItemOffset, yItemOffset, 0);
                matrixStack.scale(ITEM_SIZE / 16.0f, ITEM_SIZE / 16.0f, 1.0f);
                context.drawItem(itemStack, 0, 0);
                context.drawStackOverlay(mc.textRenderer, itemStack, 0, 0);
                matrixStack.pop();
            }
            xItemOffset += ITEM_SIZE + PADDING;
        }

        setBounds(VISUAL_WIDTH, VISUAL_HEIGHT);
    }
    
    private void renderBackground(Matrix4f matrix, float x, float y, float width, float height) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);
        
        DrawHelper.drawBlur(matrixStack, x, y, width, height, CORNER_RADIUS, new Color(255, 255, 255, 255), 10.0F);

        Color bgColor = new Color(
            DisplayModule.getHudBackground().getRed(),
            DisplayModule.getHudBackground().getGreen(),
            DisplayModule.getHudBackground().getBlue(),
            DisplayModule.getHudBackground().getAlpha()
        );
        
        DrawHelper.drawRect(matrixStack, x, y, width, height, CORNER_RADIUS, bgColor);
    }
}
