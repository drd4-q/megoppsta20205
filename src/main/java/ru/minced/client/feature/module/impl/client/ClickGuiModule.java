package ru.minced.client.feature.module.impl.client;

import org.lwjgl.glfw.GLFW;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BindSetting;

public class ClickGuiModule extends Module {

    public static BindSetting openGui = new BindSetting("Открытие меню", GLFW.GLFW_KEY_RIGHT_SHIFT);

    public ClickGuiModule() {
        super("Click Gui","Меню клиента", Category.Miscellaneous);
        addSettings(openGui);
    }
}