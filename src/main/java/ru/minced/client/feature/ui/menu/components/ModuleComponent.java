package ru.minced.client.feature.ui.menu.components;

import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.ui.menu.components.impl.*;
import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.ui.menu.components.impl.*;
import ru.minced.client.util.render.font.Fonts;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.StringHelper;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Instance;

import java.awt.*;
import java.util.ArrayList;


public class ModuleComponent {
    private float x = 0, y = 0;
    private final ArrayList<SettingComponent> settings = new ArrayList<>();
    private final Module feature;
    private boolean binding = false;
    public boolean extended = false;

    @Setter
    private float opacity = 1.0f;

    public ModuleComponent(Module feature) {
        this.feature = feature;
        feature.getSettings().forEach(s -> {
            if (getSetting(s) != null)
                settings.add(getSetting(s));
        });
    }
    
    private void renderCenteredText(Matrix4f matrix, String text, float size, float x, float y, float width, Color color) {
        int alpha = Math.max(0, Math.min(255, (int)(color.getAlpha() * opacity)));
        
        Color adjustedColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            alpha
        );
        
        Instance instance = Fonts.MEDIUM.getFont(size);
        DrawHelper.drawCenteredText(matrix, instance, text, x, y, width, adjustedColor);
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.x = x;
        this.y = y;
        String bindtext;
        if (feature.getKey() == -1) {
            bindtext = "Binding...";
        } else {
            bindtext = feature.getKey() != 0 ? StringHelper.getBindName(feature.getKey()) : "Binding...";
        }
        String name = binding ? bindtext : feature.getName();

        if (MathUtil.isHovered(x, y, 78, 14, mouseX, mouseY)) {
            Color hoverColor = new Color(0x4A5560);
            int alpha = Math.max(0, Math.min(255, (int)(128 * opacity)));
            
            Color adjustedHoverColor = new Color(
                hoverColor.getRed(),
                hoverColor.getGreen(),
                hoverColor.getBlue(),
                alpha
            );
            
            Builder.rectangle()
                .size(new SizeState(78, 14))
                .color(new QuadColorState(adjustedHoverColor))
                .radius(new QuadRadiusState(3.0F))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), x + 1, y);
        }
        
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float textSize = 7.5f;
        float moduleWidth = 78;
        float centerY = y + (14 - Fonts.MEDIUM.getHeight(textSize)) / 2;

        if (!feature.isState()) {
            renderCenteredText(
                matrix,
                name,
                textSize,
                x + 1,
                centerY,
                moduleWidth,
                Color.WHITE
            );
        } else {
            renderCenteredText(
                matrix,
                name,
                textSize,
                x + 1,
                centerY,
                moduleWidth,
                DisplayModule.getHudLogoColor()
            );
        }
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        if (MathUtil.isHovered(x, y, 78, 14, mouseX, mouseY) && mouseButton == 0) {
            feature.toggle();
        }
        if (MathUtil.isHovered(x, y, 78, 14, mouseX, mouseY) && mouseButton == 2) {
            binding = !binding;
        }
        if (MathUtil.isHovered(x, y, 78, 14, mouseX, mouseY) && mouseButton == 1) {
            MinecraftClient.getInstance().setScreen(new ModuleSettingsScreen(feature));
        }
        for (SettingComponent s : settings) {
            s.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double button) {
        settings.forEach(p -> {
            p.mouseReleased(mouseX, mouseY, button);
        });
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                feature.setKey(-1);
            } else {
                feature.setKey(keyCode);
            }
            binding = false;
        }
        settings.forEach(p -> {
            p.keyPressed(keyCode,scanCode,modifiers);
        });
    }

    public SettingComponent getSetting(Setting s) {
        if (s instanceof BooleanSetting) return new BooleanSettingComponent(s);
        if (s instanceof SliderSetting) return new SliderSettingComponent(s);
        if (s instanceof ModeSetting) return new ModeSettingComponent(s);
        if (s instanceof ModeListSetting) return new ModeListSettingComponent(s);
        if (s instanceof BindSetting) return new BindSettingComponent(s);
        return null;
    }

    public float getHeight() {
        float height = 0;
        if (!settings.isEmpty()) {
            for (SettingComponent e : settings) {
                height += e.getFullHeight();
            }
        }
        return height;
    }
}