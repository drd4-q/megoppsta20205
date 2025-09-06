package ru.minced.client.feature.ui.menu.components.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.ui.menu.components.SettingComponent;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;

import java.awt.*;

public class ModeSettingComponent extends SettingComponent {
    private ModeSetting setting;
    private float x = 0, y = 0, height = 0;

    public ModeSettingComponent(Setting s) {
        super(s);
        setting = (ModeSetting) s;
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.x = x;
        this.y = y;
        height = setting.getList().toArray().length * 10 + 15;
        MatrixStack matrixStack = context.getMatrices();
        Fonts.getSize(15).drawString(matrixStack, setting.getName(), x, (float) (y - 1.5), 0xFFD4D6E1);

        float y2 = 3;
        for (String mode : setting.getList()) {
            Color rectColor;
            if (mode.equals(setting.getSelected())) {
                rectColor = DisplayModule.getFirstColor();
            } else {
                rectColor = new Color(22, 22, 22, 150);
            }

            DrawHelper.drawRect(matrixStack,x + 73 - Fonts.getSize(14).getStringWidth(mode), y + y2 + 2, Fonts.getSize(14).getStringWidth(mode) + 4, 9,3, rectColor);

            Fonts.getSize(14).drawString(matrixStack, mode, x + 75 - Fonts.getSize(14).getStringWidth(mode), y + y2 + 5, 0xFFFFFFFF);

            y2 += 10;
        }
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        float y2 = 3;
        for (String e : setting.getList()) {
            if (MathUtil.isHovered(x + 75 - Fonts.getSize(14).getStringWidth(e), y + y2 + 5, Fonts.getSize(14).getStringWidth(e), 8, mouseX, mouseY)) {
                setting.setSelected(e);
            }
            y2 += 10;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double button) {}

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    public float getFullHeight() {
        return height;
    }
}