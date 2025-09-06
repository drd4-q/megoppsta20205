package ru.minced.client.feature.ui.menu.components;

import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.ui.menu.components.impl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.ui.menu.GuiScreen;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.ui.menu.components.impl.*;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.other.animation.Animation;
import ru.minced.client.util.other.animation.impl.FadeAnimation;
import ru.minced.client.core.render.scissor.ScissorManager;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.renderers.impl.BuiltBlur;
import ru.minced.client.core.render.renderers.impl.BuiltRectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.minced.client.util.IMinecraft.mc;
import static ru.minced.client.util.other.animation.Direction.BACKWARDS;
import static ru.minced.client.util.other.animation.Direction.FORWARDS;

public class ModuleSettingsScreen extends Screen {
    private final Module module;
    private final List<SettingComponent> components = new ArrayList<>();
    private float boxX, boxY, boxWidth = 220;
    private float scrollOffset = 0;
    private final float maxHeight = 250;
    private final ScissorManager scissorManager = new ScissorManager();
    private boolean closing = false;

    private final Animation fadeAnimation = new FadeAnimation()
            .setMs(100)
            .setValue(1);

    private final List<ComponentPosition> componentPositions = new ArrayList<>();

    private static class ComponentPosition {
        public SettingComponent component;
        public float x;
        public float y;
        public float height;
        
        public ComponentPosition(SettingComponent component, float x, float y) {
            this.component = component;
            this.x = x;
            this.y = y;
            this.height = component.getFullHeight();
        }
    }

    public ModuleSettingsScreen(Module module) {
        super(Text.of("Settings"));
        this.module = module;
        fadeAnimation.setDirection(FORWARDS);
        
        for (Setting setting : module.getSettings()) {
            if (!setting.isVisible()) continue;

            if (setting instanceof BindSetting)
                components.add(new BindSettingComponent(setting));

            if (setting instanceof BooleanSetting)
                components.add(new BooleanSettingComponent(setting));

            if (setting instanceof ModeListSetting)
                components.add(new ModeListSettingComponent(setting));

            if (setting instanceof ModeSetting)
                components.add(new ModeSettingComponent(setting));
                
            if (setting instanceof SliderSetting)
                components.add(new SliderSettingComponent(setting));
                
            if (setting instanceof RangeSetting)
                components.add(new RangeSettingComponent(setting));
        }
    }

    private void updateComponentPositions() {
        componentPositions.clear();
        float contentStartY = boxY + 45;
        float yOffset = contentStartY - scrollOffset;
        
        for (SettingComponent component : components) {
            componentPositions.add(new ComponentPosition(component, boxX + 10, yOffset));
            yOffset += component.getFullHeight() + 4;
        }
    }

    private ComponentPosition findComponentAtPosition(double mouseX, double mouseY) {
        for (ComponentPosition pos : componentPositions) {
            if (mouseY >= pos.y && mouseY < pos.y + pos.height) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        fadeAnimation.update();
        
        MatrixStack matrices = context.getMatrices();
        boxX = (width - boxWidth) / 2f;
        boxY = (height - maxHeight) / 2f;

        Color panelColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlphaPreserveOriginal(new Color(
                DisplayModule.getHudBackground().getRed(),
                DisplayModule.getHudBackground().getGreen(),
                DisplayModule.getHudBackground().getBlue(),
                190));
        Color blurColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlpha(Color.WHITE);
        
        BuiltBlur blurPanel = Builder.blur()
                .size(new SizeState(boxWidth, maxHeight))
                .radius(new QuadRadiusState(4.0f))
                .blurRadius(15.0f)
                .color(new QuadColorState(blurColor))
                .build();

        BuiltRectangle rectPanel = Builder.rectangle()
                .size(new SizeState(boxWidth, maxHeight))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(panelColor))
                .build();

        blurPanel.render(context.getMatrices().peek().getPositionMatrix(), boxX, boxY);
        rectPanel.render(context.getMatrices().peek().getPositionMatrix(), boxX, boxY);

        Color textColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlpha(new Color(212, 214, 225, 255));
        Color descColor = ((FadeAnimation)fadeAnimation).applyAnimatedAlpha(new Color(160, 163, 180, 255));
        
        Fonts.getSize(17).drawString(matrices, module.getName(), boxX + 10, boxY + 8, textColor.getRGB());
        Fonts.getSize(13).drawString(matrices, module.getDescription(), boxX + 10, boxY + 22, descColor.getRGB());

        float contentStartY = boxY + 40;

        updateComponentPositions();

        scissorManager.push(boxX, contentStartY, boxWidth, maxHeight - 50);

        for (ComponentPosition pos : componentPositions) {
            pos.component.render(context, pos.x, pos.y, mouseX, mouseY, delta);
        }

        scissorManager.pop();
    }

    @Override
    public void tick() {
        if (closing && fadeAnimation.isFinished(BACKWARDS)) {
            mc.setScreen(new GuiScreen());
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY < boxY + 40 || mouseY > boxY + maxHeight - 10) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        
        ComponentPosition pos = findComponentAtPosition(mouseX, mouseY);
        if (pos != null) {
            pos.component.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (SettingComponent component : components) {
            component.mouseReleased(mouseX, mouseY, button);
        }

        if (mouseY < boxY + 40 || mouseY > boxY + maxHeight - 10) {
            return super.mouseReleased(mouseX, mouseY, button);
        }
        
        ComponentPosition pos = findComponentAtPosition(mouseX, mouseY);
        if (pos != null) {
            return true;
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && shouldCloseOnEsc()) {
            closing = true;
            fadeAnimation.setDirection(BACKWARDS);
            return true;
        }
        
        for (SettingComponent component : components)
            component.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float contentHeight = getTotalHeight();
        float maxScroll = Math.max(0, contentHeight - (maxHeight - 50));

        scrollOffset -= verticalAmount * 10;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        updateComponentPositions();

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean handled = false;
        
        for (ComponentPosition pos : componentPositions) {
            if (pos.component instanceof SliderSettingComponent) {
                pos.component.render(null, pos.x, pos.y, (int)mouseX, (int)mouseY, 0);
                handled = true;
            }
        }
        
        return handled || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private float getTotalHeight() {
        float h = 40;
        for (SettingComponent comp : components)
            h += comp.getFullHeight() + 4;
        return h + 10;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    public void close() {
        if (!closing) {
            closing = true;
            fadeAnimation.setDirection(BACKWARDS);
        }
    }
}