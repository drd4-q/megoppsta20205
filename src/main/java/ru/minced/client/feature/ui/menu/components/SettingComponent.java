package ru.minced.client.feature.ui.menu.components;

import net.minecraft.client.gui.DrawContext;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.util.IMinecraft;

public abstract class SettingComponent implements IMinecraft {
    public Setting setting;

    public SettingComponent(Setting setting) {
        this.setting = setting;
    }

    public abstract void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks);

    public abstract void mouseClicked(double mouseX, double mouseY, double mouseButton);

    public abstract void mouseReleased(double mouseX, double mouseY, double button);
    public abstract void keyPressed(int keyCode, int scanCode, int modifiers);

    public abstract float getFullHeight();
}
