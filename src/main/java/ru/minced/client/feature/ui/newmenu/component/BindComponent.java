package ru.minced.client.feature.ui.newmenu.component;

import ru.minced.client.feature.module.setting.impl.BindSetting;
import ru.minced.client.util.animation.Animation;
import ru.minced.client.util.animation.util.Easings;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

public class BindComponent extends AbstractComponent {
    private final BindSetting setting;
    @Getter private boolean listening = false;
    private final Animation colorAnimation = new Animation();

    public BindComponent(BindSetting setting) {
        this.setting = setting;
        this.colorAnimation.setValue(setting.getKey() != GLFW.GLFW_KEY_UNKNOWN ? 1 : 0);

        if (setting.getKey() != GLFW.GLFW_KEY_UNKNOWN) {
            this.colorAnimation.animate(1, 0, Easings.CUBIC_BOTH);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y) {
        colorAnimation.update();
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        var mediumFont = Fonts.MEDIUM.getFont(12);
        var iconsFont = Fonts.ICONS.getFont(12);
        var keyNameFont = Fonts.MEDIUM.getFont(10);

        DrawHelper.drawText(matrix, mediumFont, setting.getName(), x + 10, y + 10, new Color(127, 133, 172));

        float iconRectX = x + 255 - 10 - 16;
        float iconRectY = y + 10;

        boolean hasBind = !listening && setting.getKey() != GLFW.GLFW_KEY_UNKNOWN;

        Color iconBgColor = MathUtil.interpolateColor(
                new Color(33, 35, 43),
                new Color(125, 136, 255),
                (float) colorAnimation.getValue()
        );
        Color iconColor = MathUtil.interpolateColor(
                new Color(70, 72, 101),
                new Color(41, 37, 86),
                (float) colorAnimation.getValue()
        );

        DrawHelper.drawRect(matrixStack, iconRectX, iconRectY, 16, 16, 5, iconBgColor);
        DrawHelper.drawCenteredText(matrix, iconsFont, "L", iconRectX + 1.5f, iconRectY + 1f, 16, iconColor);

        String keyText;
        if (listening) {
            keyText = "...";
        } else {
            keyText = setting.getKey() != GLFW.GLFW_KEY_UNKNOWN ? getKeyName(setting.getKey()) : "NONE";
        }

        float textWidth = keyNameFont.font().getWidth(keyText, keyNameFont.size());
        float textRectWidth = textWidth + 8;
        float textRectX = iconRectX - 4 - textRectWidth;

        Color textBgColor = MathUtil.interpolateColor(
                new Color(33, 35, 43),
                new Color(51, 56, 94),
                (float) colorAnimation.getValue()
        );
        Color textColor = MathUtil.interpolateColor(
                new Color(70, 72, 101),
                new Color(125, 136, 255),
                (float) colorAnimation.getValue()
        );

        DrawHelper.drawRect(matrixStack, textRectX, iconRectY, textRectWidth, 16, 5, textBgColor);
        DrawHelper.drawCenteredText(matrix, keyNameFont, keyText, textRectX - 0.5f, iconRectY + 1.5f, textRectWidth, textColor);
    }

    @Override
    public boolean mouseClicked(float x, float y, double mouseX, double mouseY, int button) {
        float rectHeight = getHeight();

        if (button == 0 && MathUtil.isHovered(x, y + 10, 255, rectHeight, mouseX, mouseY)) {
            if (listening) {
                listening = false;
            } else if (setting.getKey() != GLFW.GLFW_KEY_UNKNOWN) {
                setting.setKey(GLFW.GLFW_KEY_UNKNOWN);
                colorAnimation.animate(0, 0.2, Easings.CUBIC_BOTH);
            } else {
                listening = true;
            }
            return true;
        }
        return false;
    }

    public boolean keyTyped(int keyCode) {
        return handleInput(keyCode, -1);
    }

    public boolean mouseClicked(int button) {
        return handleInput(-1, button);
    }

    private boolean handleInput(int keyCode, int mouseButton) {
        if (listening) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setting.setKey(GLFW.GLFW_KEY_UNKNOWN);
                colorAnimation.animate(0, 0.2, Easings.CUBIC_BOTH);
            } else if (keyCode != -1) {
                setting.setKey(keyCode);
                colorAnimation.animate(1, 0.2, Easings.CUBIC_BOTH);
            } else if (mouseButton != -1) {
                setting.setKey(-100 - mouseButton);
                colorAnimation.animate(1, 0.2, Easings.CUBIC_BOTH);
            }
            listening = false;
            return true;
        }
        return false;
    }

    @Override
    public float getHeight() {
        return 16;
    }

    private String getKeyName(int key) {
        if (key <= -100) {
            int mouseButton = -100 - key;
            return switch (mouseButton) {
                case 0 -> "LMB";
                case 1 -> "RMB";
                case 2 -> "MMB";
                case 3 -> "MB4";
                case 4 -> "MB5";
                default -> "MB" + (mouseButton + 1);
            };
        }

        return switch (key) {
            case GLFW.GLFW_KEY_UNKNOWN -> "NONE";
            case GLFW.GLFW_KEY_ESCAPE -> "ESC";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "L-SHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "R-SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "L-CTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "R-CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "L-ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "R-ALT";
            case GLFW.GLFW_KEY_BACKSPACE -> "BKSP";
            case GLFW.GLFW_KEY_INSERT -> "INS";
            case GLFW.GLFW_KEY_DELETE -> "DEL";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PGDN";
            default -> {
                String keyName = GLFW.glfwGetKeyName(key, 0);
                if (keyName != null) {
                    yield keyName.toUpperCase();
                } else if (key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F25) {
                    yield "F" + (key - GLFW.GLFW_KEY_F1 + 1);
                } else {
                    yield "KEY" + key;
                }
            }
        };
    }
}
