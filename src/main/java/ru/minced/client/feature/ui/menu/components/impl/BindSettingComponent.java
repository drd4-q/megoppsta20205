package ru.minced.client.feature.ui.menu.components.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.BindSetting;
import ru.minced.client.feature.ui.menu.components.SettingComponent;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.StringHelper;
import ru.minced.client.util.render.DrawHelper;

public class BindSettingComponent extends SettingComponent {
    private BindSetting setting;
    boolean set = false;
    private float x = 0, y = 0;
    public BindSettingComponent(Setting s) {
        super(s);
        setting = (BindSetting) s;
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.x = x;
        this.y = y;
        MatrixStack matrixStack = context.getMatrices();
        DrawHelper.drawRect(matrixStack, x + Fonts.getSize(12).getStringWidth(setting.getName()) + 10, y - 1.5f,Fonts.getSize(13).getStringWidth(StringHelper.getBindName(setting.getKey())) + 5.5F, Fonts.getSize(13).getStringHeight(StringHelper.getBindName(setting.getKey())), 3, DisplayModule.getFirstColor());
        Fonts.getSize(13).drawString(matrixStack, StringHelper.getBindName(setting.getKey()), x + Fonts.getSize(12).getStringWidth(setting.getName()) + 12.5F, y + 1f, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(matrixStack, setting.getName(), x + 5, y + 1, 0xFFD4D6E1);
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        if (MathUtil.isHovered(x + Fonts.getSize(12).getStringWidth(setting.getName()) + 10,y - 1.5f, Fonts.getSize(13).getStringWidth(StringHelper.getBindName(setting.getKey())) + 5, Fonts.getSize(13).getStringHeight(StringHelper.getBindName(setting.getKey())), mouseX, mouseY) && mouseButton == 0)
            set = true;

        if (set && (mouseButton != 0)) {
            setting.setKey((int) mouseButton);
            set = false;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double button) {}
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (set) {
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setting.setKey(-1);
                set = false;
            } else {
                setting.setKey(keyCode);
                set = false;
            }
        }
    }

    public float getFullHeight() {
        return 15f;
    }
}