package ru.minced.client.feature.ui.newmenu.component;

import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.List;

public class ModeListComponent extends AbstractComponent {
    private final ModeListSetting setting;
    @Getter
    private boolean expanded;
    private long expandStartTime;

    private float componentX;
    private float componentY;

    public ModeListComponent(ModeListSetting setting) {
        this.setting = setting;
        this.expanded = false;
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y) {
        this.componentX = x;
        this.componentY = y;
        
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        var mediumFont = Fonts.MEDIUM.getFont(12);

        DrawHelper.drawText(matrix, mediumFont, setting.getName(), x + 10, y + 10, new Color(127, 133, 172));

        float textHeight = mediumFont.getHeight();
        float rectY = y + 10 + textHeight + 4;
        
        DrawHelper.drawRect(matrixStack, x + 10, rectY, 235, 28, 6, new Color(21, 22, 29));

        String selectedText = getSelectedText();
        float textY = rectY + (28 - mediumFont.getHeight()) / 2.0f;
        DrawHelper.drawFadingText(matrix, mediumFont, selectedText, x + 10 + 8, textY, new Color(102, 101, 110), 12, 220, 0);
    }

    public void renderDropdown(MatrixStack matrixStack) {
        if (!expanded) return;
        
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        var mediumFont = Fonts.MEDIUM.getFont(12);
        List<String> modes = setting.getList();
        
        float textHeight = mediumFont.getHeight();
        float rectY = componentY + 10 + textHeight + 4;
        float dropdownY = rectY + 28 + 4;
        
        float dropdownHeight = modes.size() * 28;
        
        float alpha = calculateAnimationAlpha();
        Color animatedBgColor = new Color(21, 22, 29, (int)(alpha * 255));
        DrawHelper.drawRect(matrixStack, componentX + 10, dropdownY, 235, dropdownHeight, 6, animatedBgColor);
        
        for (int i = 0; i < modes.size(); i++) {
            String mode = modes.get(i);
            Color baseColor = setting.isSelected(mode) ? 
                new Color(255, 255, 255) : new Color(102, 101, 110);
            
            Color textColor = new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                (int)(alpha * 255)
            );
                
            DrawHelper.drawVerticalCenteredText(matrix, mediumFont, mode, componentX + 10 + 8, 
                dropdownY + i * 28, 28, textColor);
        }
    }
    
    private float calculateAnimationAlpha() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - expandStartTime;
        
        if (elapsedTime >= 200) {
            return 1.0f;
        }
        
        return (float) elapsedTime / 200;
    }

    @Override
    public boolean mouseClicked(float x, float y, double mouseX, double mouseY, int button) {
        var mediumFont = Fonts.MEDIUM.getFont(12);
        float textHeight = mediumFont.getHeight();
        float rectY = y + 10 + textHeight + 4;
        
        if (button == 0 && mouseX >= x + 10 && mouseX <= x + 10 + 235 && 
            mouseY >= rectY && mouseY <= rectY + 28) {
            
            expanded = !expanded;
            if (expanded) {
                expandStartTime = System.currentTimeMillis();
            }
            return true;
        }
        
        return false;
    }
    
    public boolean dropdownMouseClicked(double mouseX, double mouseY, int button) {
        if (!expanded) return false;
        
        var mediumFont = Fonts.MEDIUM.getFont(12);
        float textHeight = mediumFont.getHeight();
        float rectY = componentY + 10 + textHeight + 4;
        float dropdownY = rectY + 28 + 4;
        
        if (button == 0 && mouseX >= componentX + 10 && mouseX <= componentX + 10 + 235) {
            List<String> modes = setting.getList();
            
            for (int i = 0; i < modes.size(); i++) {
                float itemY = dropdownY + i * 28;
                if (mouseY >= itemY && mouseY <= itemY + 28) {
                    setting.select(modes.get(i));
                    return true;
                }
            }
        }
        
        if (button == 0) {
            expanded = false;
            return true;
        }
        
        return false;
    }

    @Override
    public float getHeight() {
        return 46;
    }
    
    private String getSelectedText() {
        if (setting.getSelected().isEmpty()) {
            return "None";
        }
        return String.join(", ", setting.getSelected());
    }
}
