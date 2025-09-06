package ru.minced.client.feature.ui.menu.components;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.other.animation.Animation;
import ru.minced.client.util.other.animation.impl.FadeAnimation;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.msdf.MsdfFont;
import ru.minced.client.core.render.renderers.impl.BuiltBlur;
import ru.minced.client.core.render.renderers.impl.BuiltRectangle;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Instance;

import java.awt.*;
import java.util.ArrayList;

import static ru.minced.client.util.other.animation.Direction.BACKWARDS;
import static ru.minced.client.util.other.animation.Direction.FORWARDS;

public class CategoryPanel implements IMinecraft {
    private final ArrayList<ModuleComponent> modules = new ArrayList<>();
    @Getter
    private Category category;
    @Getter @Setter
    private float y2 = 0, x2 = 0, y = 0, x = 0;
    
    private final Animation fadeAnimation = new FadeAnimation()
            .setMs(100)
            .setValue(1);
    
    private long lastUpdateTime = System.currentTimeMillis();
            
    private MsdfFont fontMedium;

    public CategoryPanel(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;
        fadeAnimation.setDirection(FORWARDS);
        ModuleManager.modules.forEach(m -> {
            if (getCategory() == m.getCategory()) modules.add(new ModuleComponent(m));
        });
    }
    
    private MsdfFont getMediumFont() {
        if (fontMedium == null) {
            try {
                fontMedium = MsdfFont.builder()
                    .name("minced-medium")
                    .data("sf-pro-bold")
                    .atlas("sf-pro-bold")
                    .build();
            } catch (Exception e) {
                System.out.println("Ошибка при загрузке MSDF шрифта Medium: " + e.getMessage());
            }
        }
        return fontMedium;
    }
    
    private void renderText(Matrix4f matrix, String text, float size, Color color, float x, float y) {
        MsdfFont font = getMediumFont();
        if (font == null) return;
        
        Instance instance = new Instance(font, size);
        DrawHelper.drawText(matrix, instance, text, x, y, color);
    }

    public void render(DrawContext context, int height, int mouseX, int mouseY, float partialTicks) {
        float scaledX = this.x;
        float scaledY = this.y;

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        
        fadeAnimation.update();
        
        for (ModuleComponent m : modules) {
            height += m.extended ? (int) m.getHeight() : 0;
        }

        if (!modules.isEmpty()) {
            height += 1;
        }

        Color panelColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlphaPreserveOriginal(new Color(
                DisplayModule.getHudBackground().getRed(),
                DisplayModule.getHudBackground().getGreen(),
                DisplayModule.getHudBackground().getBlue(),
                100));
        Color blurColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlpha(Color.WHITE);

        BuiltBlur blurCategoryName = Builder.blur()
                .size(new SizeState(80, 20))
                .radius(new QuadRadiusState(4.0f))
                .blurRadius(15.0f)
                .color(new QuadColorState(blurColor))
                .build();

        BuiltRectangle rectCategoryName = Builder.rectangle()
                .size(new SizeState(80, 20))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(panelColor))
                .build();

        blurCategoryName.render(context.getMatrices().peek().getPositionMatrix(), scaledX, scaledY);
        rectCategoryName.render(context.getMatrices().peek().getPositionMatrix(), scaledX, scaledY);

        if (!modules.isEmpty()) {
            BuiltBlur blurPanel = Builder.blur()
                    .size(new SizeState(80, height))
                    .radius(new QuadRadiusState(4.0f))
                    .blurRadius(15.0f)
                    .color(new QuadColorState(blurColor))
                    .build();

            BuiltRectangle rectPanel = Builder.rectangle()
                    .size(new SizeState(80, height))
                    .radius(new QuadRadiusState(4.0f))
                    .color(new QuadColorState(panelColor))
                    .build();

            blurPanel.render(context.getMatrices().peek().getPositionMatrix(), scaledX, scaledY + 22);
            rectPanel.render(context.getMatrices().peek().getPositionMatrix(), scaledX, scaledY + 22);
        }

        MsdfFont font = getMediumFont();
        if (font == null) return;
        
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        
        float textSize = 9.0f;
        float textWidth = font.getWidth(category.getDisplayName(), textSize);
        float textHeight = font.getMetrics().lineHeight() * textSize;
        float textY = scaledY + (20 - textHeight) / 2;
        float textX = scaledX + 40 - (textWidth / 2f);

        Color textColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlpha(Color.WHITE);
        renderText(matrix, category.getDisplayName(), textSize, textColor, textX, textY);

        renderPanel(context, mouseX, mouseY, partialTicks);
    }

    public void setClosing(boolean closing) {
        fadeAnimation.setDirection(closing ? BACKWARDS : FORWARDS);
    }

    public boolean isCloseAnimationFinished() {
        return fadeAnimation.isFinished(BACKWARDS);
    }

    public void renderPanel(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        if (!modules.isEmpty()) {
            this.y2 = this.y + 23;
            float opacity = fadeAnimation.getOutput().floatValue();
            
            modules.forEach(m -> {
                this.x2 = this.x;
                float scaledX2 = this.x2;
                float scaledY2 = this.y2;
                m.setOpacity(opacity);
                m.render(context, scaledX2, scaledY2, mouseX, mouseY, partialTicks);
                this.x2 += 101;
                this.y2 += 15 + (m.extended ? m.getHeight() : 0);
            });
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        modules.forEach(m -> {
            m.keyPressed(keyCode, scanCode, modifiers);
        });
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        for (ModuleComponent m : modules) {
            m.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double state) {
        modules.forEach(m -> {
            m.mouseReleased(mouseX, mouseY, state);
        });
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            y += 10;
        } else if (verticalAmount < 0) {
            y -= 10;
        }
    }
}