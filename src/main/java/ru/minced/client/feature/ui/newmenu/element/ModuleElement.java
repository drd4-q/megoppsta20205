package ru.minced.client.feature.ui.newmenu.element;

import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.ui.newmenu.component.*;
import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.ui.newmenu.component.*;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleElement implements IMinecraft {
    private final Map<Module, BooleanComponent> moduleToggleComponents = new HashMap<>();
    private final Map<Module, List<AbstractComponent>> moduleSettingComponents = new HashMap<>();
    private final Map<Module, List<ModeComponent>> modeComponents = new HashMap<>();
    private final Map<Module, List<ModeListComponent>> modeListComponents = new HashMap<>();

    public void render(MatrixStack matrixStack, float x, float y, Module module) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        var boldFont = Fonts.BOLD.getFont(14);
        DrawHelper.drawText(matrix, boldFont, module.getName(), x, y, new Color(127, 133, 172));

        float rectY = y + boldFont.getHeight() + 4;
        float rectHeight = getModulePanelHeight(module);

        DrawHelper.drawRect(matrixStack, x, rectY, 255, rectHeight, 5, new Color(28, 29, 38));
        DrawHelper.drawOutline(matrixStack, x - 2, rectY - 2, 255 + 4, rectHeight + 4, 5, new Color(33, 32, 43), 2);

        BooleanComponent toggleComponent = moduleToggleComponents.computeIfAbsent(module, k -> {
            BooleanSetting moduleSetting = new BooleanSetting("Enabled", module.isState());
            return new BooleanComponent(moduleSetting) {
                @Override
                public boolean mouseClicked(float x, float y, double mouseX, double mouseY, int button) {
                    boolean result = super.mouseClicked(x, y, mouseX, mouseY, button);
                    if (result) {
                        module.setState(moduleSetting.isState());
                    }
                    return result;
                }
            };
        });

        toggleComponent.render(matrixStack, x, rectY);

        List<AbstractComponent> settingComponents = moduleSettingComponents.computeIfAbsent(module, k -> {
            List<AbstractComponent> components = new ArrayList<>();
            createSettingComponents(module, components);
            return components;
        });

        float currentY = rectY + toggleComponent.getHeight() + 8;
        int lastIndex = settingComponents.size() - 1;
        for (int i = 0; i < settingComponents.size(); i++) {
            AbstractComponent component = settingComponents.get(i);
            component.render(matrixStack, x, currentY);

            if (i < lastIndex) {
                currentY += component.getHeight() + 8;
            } else {
                currentY += component.getHeight();
            }
        }
    }

    private void createSettingComponents(Module module, List<AbstractComponent> components) {
        List<ModeComponent> moduleModeComponents = new ArrayList<>();
        List<ModeListComponent> moduleModeListComponents = new ArrayList<>();
        
        for (Setting setting : module.getSettings()) {
            if (!setting.isVisible()) continue;

            switch (setting) {
                case BooleanSetting booleanSetting -> components.add(new BooleanComponent(booleanSetting));
                case BindSetting bindSetting -> components.add(new BindComponent(bindSetting));
                case SliderSetting sliderSetting -> components.add(new SliderComponent(sliderSetting));
                case ModeSetting modeSetting -> {
                    ModeComponent modeComponent = new ModeComponent(modeSetting);
                    components.add(modeComponent);
                    moduleModeComponents.add(modeComponent);
                }
                case ModeListSetting modeListSetting -> {
                    ModeListComponent modeListComponent = new ModeListComponent(modeListSetting);
                    components.add(modeListComponent);
                    moduleModeListComponents.add(modeListComponent);
                }
                default -> {
                }
            }
        }
        
        modeComponents.put(module, moduleModeComponents);
        modeListComponents.put(module, moduleModeListComponents);
    }

    private float getModulePanelHeight(Module module) {
        var mediumFont = Fonts.MEDIUM.getFont(12);

        float baseHeight = 10 + mediumFont.getHeight() + 10;

        List<AbstractComponent> settingComponents = moduleSettingComponents.get(module);
        if (settingComponents != null && !settingComponents.isEmpty()) {
            float settingsHeight = 8;

            int lastIndex = settingComponents.size() - 1;
            for (int i = 0; i < lastIndex; i++) {
                settingsHeight += settingComponents.get(i).getHeight() + 8;
            }

            settingsHeight += settingComponents.get(lastIndex).getHeight() + 2;

            return baseHeight + settingsHeight;
        }

        return baseHeight;
    }

    public float getHeight(Module module) {
        var boldFont = Fonts.BOLD.getFont(14);
        return boldFont.getHeight() + 4 + getModulePanelHeight(module);
    }

    public float getHeight() {
        var boldFont = Fonts.BOLD.getFont(14);
        var mediumFont = Fonts.MEDIUM.getFont(12);
        return boldFont.getHeight() + 4 + 10 + mediumFont.getHeight() + 10;
    }

    public boolean mouseClicked(float x, float y, Module module, double mouseX, double mouseY, int button) {
        var boldFont = Fonts.BOLD.getFont(14);
        float rectY = y + boldFont.getHeight() + 4;

        BooleanComponent toggleComponent = moduleToggleComponents.get(module);
        if (toggleComponent != null) {
            if (toggleComponent.mouseClicked(x, rectY, mouseX, mouseY, button)) {
                return true;
            }
        }

        List<AbstractComponent> settingComponents = moduleSettingComponents.get(module);
        if (settingComponents != null) {
            assert toggleComponent != null;
            float currentY = rectY + toggleComponent.getHeight() + 8;
            int lastIndex = settingComponents.size() - 1;
            for (int i = 0; i < settingComponents.size(); i++) {
                AbstractComponent component = settingComponents.get(i);
                if (component.mouseClicked(x, currentY, mouseX, mouseY, button)) {
                    return true;
                }

                if (i < lastIndex) {
                    currentY += component.getHeight() + 8;
                } else {
                    currentY += component.getHeight();
                }
            }
        }

        return false;
    }
    
    public boolean mouseReleased(float x, float y, Module module, double mouseX, double mouseY, int button) {
        var boldFont = Fonts.BOLD.getFont(14);
        float rectY = y + boldFont.getHeight() + 4;

        List<AbstractComponent> settingComponents = moduleSettingComponents.get(module);
        if (settingComponents != null) {
            BooleanComponent toggleComponent = moduleToggleComponents.get(module);
            assert toggleComponent != null;
            float currentY = rectY + toggleComponent.getHeight() + 8;
            int lastIndex = settingComponents.size() - 1;
            for (int i = 0; i < settingComponents.size(); i++) {
                AbstractComponent component = settingComponents.get(i);
                if (component.mouseReleased(x, currentY, mouseX, mouseY, button)) {
                    return true;
                }

                if (i < lastIndex) {
                    currentY += component.getHeight() + 8;
                } else {
                    currentY += component.getHeight();
                }
            }
        }

        return false;
    }
    
    public boolean mouseDragged(float x, float y, Module module, double mouseX, double mouseY, int button) {
        var boldFont = Fonts.BOLD.getFont(14);
        float rectY = y + boldFont.getHeight() + 4;

        List<AbstractComponent> settingComponents = moduleSettingComponents.get(module);
        if (settingComponents != null) {
            BooleanComponent toggleComponent = moduleToggleComponents.get(module);
            assert toggleComponent != null;
            float currentY = rectY + toggleComponent.getHeight() + 8;
            int lastIndex = settingComponents.size() - 1;
            for (int i = 0; i < settingComponents.size(); i++) {
                AbstractComponent component = settingComponents.get(i);
                if (component instanceof SliderComponent sliderComponent && sliderComponent.isDragging()) {
                    sliderComponent.mouseDragged(x, currentY, mouseX, mouseY, button);
                    return true;
                }

                if (i < lastIndex) {
                    currentY += component.getHeight() + 8;
                } else {
                    currentY += component.getHeight();
                }
            }
        }

        return false;
    }

    public boolean handleKeyPress(Module module, int keyCode) {
        List<AbstractComponent> settingComponents = moduleSettingComponents.get(module);
        if (settingComponents != null) {
            for (AbstractComponent component : settingComponents) {
                if (component instanceof BindComponent bindComponent) {
                    if (bindComponent.isListening() && bindComponent.keyTyped(keyCode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean handleMouseClick(Module module, int button) {
        List<AbstractComponent> settingComponents = moduleSettingComponents.get(module);
        if (settingComponents != null) {
            for (AbstractComponent component : settingComponents) {
                if (component instanceof BindComponent bindComponent) {
                    if (bindComponent.isListening() && bindComponent.mouseClicked(button)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void renderDropdowns(MatrixStack matrixStack, float x, float y, Module module) {
        List<ModeComponent> moduleModeComponents = modeComponents.get(module);
        if (moduleModeComponents != null) {
            for (ModeComponent component : moduleModeComponents) {
                if (component.isExpanded()) {
                    component.renderDropdown(matrixStack);
                }
            }
        }
        
        List<ModeListComponent> moduleModeListComponents = modeListComponents.get(module);
        if (moduleModeListComponents != null) {
            for (ModeListComponent component : moduleModeListComponents) {
                if (component.isExpanded()) {
                    component.renderDropdown(matrixStack);
                }
            }
        }
    }

    public boolean dropdownMouseClicked(float x, float y, Module module, double mouseX, double mouseY, int button) {
        List<ModeComponent> moduleModeComponents = modeComponents.get(module);
        if (moduleModeComponents != null) {
            for (ModeComponent component : moduleModeComponents) {
                if (component.isExpanded() && component.dropdownMouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        
        List<ModeListComponent> moduleModeListComponents = modeListComponents.get(module);
        if (moduleModeListComponents != null) {
            for (ModeListComponent component : moduleModeListComponents) {
                if (component.isExpanded() && component.dropdownMouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}