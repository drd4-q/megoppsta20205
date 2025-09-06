package ru.minced.client.feature.module.impl.client;

import lombok.Getter;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ButtonSetting;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;

import java.awt.*;

public class DisplayModule extends Module {

    public static ModeListSetting elements = new ModeListSetting("Elements",
            "Watermark", "Hotkeys", "Potions", "ArmorInfo", "TargetInfo", "Inventory");

    public DisplayModule() {
        super("Display","Enables client overlay", Category.Visuals);
        ButtonSetting resetDraggable = new ButtonSetting("Reset positions", this::resetDraggablePositions);
        addSettings(elements, resetDraggable);
    }

    private void resetDraggablePositions() {
        for (AbstractDraggable draggable : Minced.getInstance().getDraggableManager().draggable()) {
            draggable.setX(10);
            draggable.setY(10);
        }
    }

    @Getter
    public static Color firstColor = new Color(115, 79, 171);
    @Getter
    public static Color secondColor = new Color(178, 116, 255);
    @Getter
    public static Color friendColor = new Color(110, 183, 82);

    @Getter
    public static Color hudLogoColor = new Color(178, 116, 255);

    @Getter
    public static Color hudBackgroundDark = new Color(0, 0, 0, 180);
    @Getter
    public static Color hudBackground = new Color(0, 0, 0, 140);
    @Getter
    public static Color text = new Color(255, 255, 255);
}