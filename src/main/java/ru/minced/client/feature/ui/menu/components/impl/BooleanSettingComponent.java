package ru.minced.client.feature.ui.menu.components.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.ui.menu.components.SettingComponent;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.animation.Animation;
import ru.minced.client.util.other.animation.impl.FadeAnimation;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.renderers.impl.BuiltRectangle;

import static ru.minced.client.util.other.animation.AnimationHelper.applyOpacity;
import static ru.minced.client.util.other.animation.Direction.BACKWARDS;
import static ru.minced.client.util.other.animation.Direction.FORWARDS;

public class BooleanSettingComponent extends SettingComponent {
    private final BooleanSetting setting;
    private float x = 0, y = 0;

    private final Animation stencilAnimation = new FadeAnimation()
            .setMs(200)
            .setValue(8);

    public BooleanSettingComponent(Setting s) {
        super(s);
        setting = (BooleanSetting) s;
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.x = x;
        this.y = y;
        stencilAnimation.setDirection(setting.isState() ? FORWARDS : BACKWARDS);

        int opacity = setting.isState() ? 255 : 0;

        float textWidth = Fonts.getSize(14).getStringWidth(setting.getName());

        BuiltRectangle rectangle = Builder.rectangle()
                .size(new SizeState(9.5F, 9.5F))
                .color(new QuadColorState(applyOpacity(DisplayModule.getFirstColor().getRGB(), opacity)))
                .radius(new QuadRadiusState(4.3F))
                .smoothness(1.0f)
                .build();
        rectangle.render(context.getMatrices().peek().getPositionMatrix(), x + textWidth + 3, y - 1.5F);

        MatrixStack matrixStack = context.getMatrices();
        Fonts.getSize(14).drawString(matrixStack, setting.getName(), x , y + 1, 0xFFD4D6E1);
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        float textWidth = Fonts.getSize(14).getStringWidth(setting.getName());
        if (MathUtil.isHovered(x + textWidth + 3, y - 1.5F, 9.5F, 9.5F, mouseX, mouseY) && mouseButton == 0) {
            setting.set(!setting.isState());
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double button) {

    }
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    public float getFullHeight() {
        return 15f;
    }
}