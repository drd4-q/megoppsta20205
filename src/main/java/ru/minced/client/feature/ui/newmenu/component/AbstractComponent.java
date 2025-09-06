package ru.minced.client.feature.ui.newmenu.component;

import ru.minced.client.util.IMinecraft;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractComponent implements IMinecraft {
    public abstract void render(MatrixStack matrixStack, float x, float y);

    public abstract boolean mouseClicked(float x, float y, double mouseX, double mouseY, int button);
    
    public boolean mouseReleased(float x, float y, double mouseX, double mouseY, int button) {
        return false;
    }
    
    public void mouseDragged(float x, float y, double mouseX, double mouseY, int button) {
    }

    public abstract float getHeight();
}