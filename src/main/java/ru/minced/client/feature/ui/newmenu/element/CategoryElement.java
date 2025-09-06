package ru.minced.client.feature.ui.newmenu.element;

import ru.minced.client.feature.module.Category;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.animation.Animation;
import ru.minced.client.util.animation.util.Easings;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class CategoryElement implements IMinecraft {

    private static Category selectedCategory = Category.Fight;
    private final Map<Category, float[]> categoryBounds = new HashMap<>();
    private final Map<Category, Animation> colorAnimations = new HashMap<>();
    private final Map<Category, Animation> positionAnimations = new HashMap<>();

    public CategoryElement() {
        for (Category category : Category.values()) {
            Animation colorAnimation = new Animation();
            colorAnimation.set(category == selectedCategory ? 1.0 : 0.0);
            colorAnimations.put(category, colorAnimation);

            Animation positionAnimation = new Animation();
            positionAnimation.set(category == selectedCategory ? 1.0 : 0.0);
            positionAnimations.put(category, positionAnimation);
        }
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        for (Map.Entry<Category, float[]> entry : categoryBounds.entrySet()) {
            Category category = entry.getKey();
            float[] bounds = entry.getValue();

            float selectorWidth = 60;
            float selectorHeight = 64;

            if (mouseX >= bounds[0] + bounds[2] / 2 - selectorWidth / 2 && mouseX <= bounds[0] + bounds[2] / 2 + selectorWidth / 2 &&
                    mouseY >= bounds[1] && mouseY <= bounds[1] + selectorHeight) {

                if (selectedCategory != category) {
                    colorAnimations.get(selectedCategory).animate(0.0, 0.3, Easings.CUBIC_OUT);
                    positionAnimations.get(selectedCategory).animate(0.0, 0.3, Easings.CUBIC_OUT);

                    selectedCategory = category;

                    colorAnimations.get(selectedCategory).animate(1.0, 0.3, Easings.CUBIC_OUT);
                    positionAnimations.get(selectedCategory).animate(1.0, 0.3, Easings.CUBIC_OUT);
                }

                return true;
            }
        }
        return false;
    }

    public void renderFooter(MatrixStack matrixStack, int x, int y, int footerHeight) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        int totalWidth = 0;
        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            totalWidth += (int) Fonts.ICONS.getFont(26).getWidth(categories[i].getIcon());
            if (i < categories.length - 1) {
                totalWidth += 45;
            }
        }

        float currentX = x + (float) (1100 - totalWidth) / 2;

        float totalElementHeight = Fonts.ICONS.getFont(26).getHeight() + 10 + Fonts.MEDIUM.getFont(15).getHeight();
        float iconY = y + (footerHeight - totalElementHeight) / 2;
        float nameY = iconY + Fonts.ICONS.getFont(26).getHeight() + 10;

        categoryBounds.clear();

        for (Category category : categories) {
            colorAnimations.get(category).update();
            positionAnimations.get(category).update();

            String icon = category.getIcon();
            String name = category.getDisplayName();

            float colorProgress = (float) colorAnimations.get(category).getValue();
            Color selectedColor = new Color(197, 200, 255);
            Color normalColor = new Color(141, 144, 199);

            int r = (int) (normalColor.getRed() + (selectedColor.getRed() - normalColor.getRed()) * colorProgress);
            int g = (int) (normalColor.getGreen() + (selectedColor.getGreen() - normalColor.getGreen()) * colorProgress);
            int b = (int) (normalColor.getBlue() + (selectedColor.getBlue() - normalColor.getBlue()) * colorProgress);

            float selectorWidth = 60;
            float selectorHeight = 64;
            float selectorX = currentX + (Fonts.ICONS.getFont(26).getWidth(icon) - selectorWidth) / 2;
            float selectorY = iconY - (selectorHeight - totalElementHeight) / 2;

            if (colorProgress > 0) {
                DrawHelper.drawRect(matrixStack, selectorX, selectorY, selectorWidth, selectorHeight, 10, new Color(0, 0, 0, (int)(colorProgress * 255)));
            }

            DrawHelper.drawText(matrix, Fonts.ICONS.getFont(26), icon, currentX + 1, iconY, new Color(r, g, b));
            DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(15), name, currentX + (Fonts.ICONS.getFont(26).getWidth(icon) - Fonts.MEDIUM.getFont(15).getWidth(name)) / 2 - 1, nameY, new Color(r, g, b));

            categoryBounds.put(category, new float[]{selectorX, selectorY, 60, 64});

            currentX += Fonts.ICONS.getFont(26).getWidth(icon) + 45;
        }
    }
}