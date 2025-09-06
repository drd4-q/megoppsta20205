package ru.minced.client.feature.ui.menu.components.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.ui.menu.components.SettingComponent;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModeListSettingComponent extends SettingComponent {
    private ModeListSetting setting;
    private float x = 0, y = 0, height = 0;

    public ModeListSettingComponent(Setting s) {
        super(s);
        setting = (ModeListSetting) s;
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.x = x;
        this.y = y;
        height = setting.getList().size() * 10 + 15;
        MatrixStack matrixStack = context.getMatrices();
        Fonts.getSize(15).drawString(matrixStack, setting.getName(), x, (float) (y - 1.5), 0xFFD4D6E1);

        float y2 = 5;
        for (String booleanSetting : setting.getList()) {
            Color rectColor;
            if (setting.isSelected(booleanSetting)) {
                rectColor = DisplayModule.getFirstColor();
            } else {
                rectColor = new Color(22, 22, 22, 150);
            }

            DrawHelper.drawRect(matrixStack, x + 64 - Fonts.getSize(14).getStringWidth(booleanSetting), y + y2 + 3, Fonts.getSize(14).getStringWidth(booleanSetting) + 1.5F, 8, 3, rectColor);

            Fonts.getSize(14).drawString(matrixStack, booleanSetting, x + 64 - Fonts.getSize(14).getStringWidth(booleanSetting), y + y2 + 5, 0xFFD4D6E1);

            y2 += 10;
        }
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        float y2 = 5;
        for (String text : setting.getList()) {
            if (MathUtil.isHovered(x + 64 - Fonts.getSize(14).getStringWidth(text), y + y2 + 5, Fonts.getSize(14).getStringWidth(text), 8, mouseX, mouseY)) {
                List<String> selected = new ArrayList<>(setting.getSelected());
                if (selected.contains(text)) {
                    selected.remove(text);
                } else {
                    selected.add(text);
                    selected.sort(Comparator.comparingInt(setting.getList()::indexOf));
                }
                setting.setSelected(selected);
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
