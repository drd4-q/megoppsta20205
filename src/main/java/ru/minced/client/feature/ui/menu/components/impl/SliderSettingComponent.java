package ru.minced.client.feature.ui.menu.components.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.feature.ui.menu.components.SettingComponent;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.animation.AnimationHelper;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.renderers.impl.BuiltRectangle;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderSettingComponent extends SettingComponent {
    private SliderSetting setting;
    private boolean click = false;
    private float x = 0, y = 0, animX = 0, animation = 0;
    public SliderSettingComponent(Setting s) {
        super(s);
        setting = (SliderSetting) s;
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.y = y;
        this.x = x;
        
        if (context != null) {
            MatrixStack matrixStack = context.getMatrices();
            Fonts.getSize(14).drawString(matrixStack, setting.getName(), x, y + 2, 0xFFD4D6E1);
            Fonts.getSize(12).drawString(matrixStack, "" + setting.get(), x + 66, y + 8.5f, 0xFFD4D6E1);
            DrawHelper.drawRect(matrixStack,x + 3, y + 9, 60, 4, 1.0F, new Color(12, 12, 12, 255));

            this.animation = AnimationHelper.fast(animation, ((setting.get() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()) * (60)), 10);

            DrawHelper.drawRect(matrixStack,x + 3, y + 9, animation, 4, 1.0F, new Color(42, 42, 42, 216));

            BuiltRectangle rectangle = Builder.rectangle()
                    .size(new SizeState(6.5F, 6.5F))
                    .color(new QuadColorState(new Color(0xFFFFFF)))
                    .radius(new QuadRadiusState(2.3F))
                    .smoothness(1.0f)
                    .build();
            rectangle.render(context.getMatrices().peek().getPositionMatrix(), x + 3 + animation, y + 7.5f);
        }
        
        if (click) {
            float numberValue = (float) MathHelper.clamp(round((float) ((mouseX - x - 3) / (60) * (setting.getMaximum() - setting.getMinimum()) + setting.getMinimum()), setting.getIncrement()), setting.getMinimum(), setting.getMaximum());
            this.setting.setValue(numberValue);
        }
    }

    public static double round(double num, double increment) {
        double v = (double) Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        if (MathUtil.isHovered(x + 3, y + 9, 60, 4, mouseX, mouseY) && mouseButton == 0)
            click = true;
    }

    public void mouseReleased(double mouseX, double mouseY, double button) {
        click = false;
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    public float getFullHeight() {
        return 22f;
    }
}
