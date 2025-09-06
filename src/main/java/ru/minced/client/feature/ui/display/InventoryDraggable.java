package ru.minced.client.feature.ui.display;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.render.DrawHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDraggable extends AbstractDraggable {
    private static final int VISUAL_WIDTH = 294;
    private static final int VISUAL_HEIGHT = 102;
    private static final float PADDING = 6.0f;
    private static final float CORNER_RADIUS = 12.0f;
    private static final float ITEM_SIZE = 26.0f;
    
    public InventoryDraggable() {
        super("Inventory", 5, 10, VISUAL_WIDTH, VISUAL_HEIGHT);
    }

    @Override
    public boolean visible() {
        boolean interfaceEnabled = Minced.getInstance().getModuleManager().getDisplayModule().isState();
        boolean inventorySelected = DisplayModule.elements.isSelected("Inventory");
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;

        if (chatOpen && interfaceEnabled) {
            return true;
        }

        return inventorySelected && interfaceEnabled;
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
        
        List<ItemStack> inventoryItems = new ArrayList<>();

        for (int i = 9; i < 36; i++) {
            inventoryItems.add(mc.player.getInventory().getStack(i));
        }
        
        int itemsPerRow = 9;
        int currentItem = 0;
        
        for (ItemStack itemStack : inventoryItems) {
            if (currentItem > 0 && currentItem % itemsPerRow == 0) {
                xItemOffset = x + PADDING;
                yItemOffset += ITEM_SIZE + PADDING;
            }
            
            DiffuseLighting.disableGuiDepthLighting();
            
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(xItemOffset, yItemOffset, 0);
            matrixStack.scale(ITEM_SIZE / 16.0f, ITEM_SIZE / 16.0f, 1.0f);
            context.drawItem(itemStack, 0, 0);
            context.drawStackOverlay(mc.textRenderer, itemStack, 0, 0);
            matrixStack.pop();
            
            xItemOffset += ITEM_SIZE + PADDING;
            currentItem++;
        }

        setBounds(VISUAL_WIDTH, VISUAL_HEIGHT);
    }
    
    private void renderBackground(Matrix4f matrix, float x, float y, float width, float height) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);
        
        DrawHelper.drawBlur(matrixStack, x, y, width, height, CORNER_RADIUS, new Color(255, 255, 255, 255), 12.0F);
        DrawHelper.drawBlur(matrixStack, x, y, width, height, CORNER_RADIUS, new Color(255, 255, 255, 255), 12.0F);

        Color bgColor = new Color(
            DisplayModule.getHudBackground().getRed(),
            DisplayModule.getHudBackground().getGreen(),
            DisplayModule.getHudBackground().getBlue(),
            DisplayModule.getHudBackground().getAlpha()
        );
        
        DrawHelper.drawRect(matrixStack, x, y, width, height, CORNER_RADIUS, bgColor);
    }
} 