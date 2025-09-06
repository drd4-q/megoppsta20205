package ru.minced.client.feature.module.impl.render;

import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.core.manager.friend.FriendsManager;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.client.DisplayModule;
import ru.minced.client.feature.module.impl.render.entityesp.AngleMode;
import ru.minced.client.feature.module.impl.render.entityesp.BoxMode;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.TargetSelector;
import net.minecraft.entity.player.PlayerEntity;

import java.awt.Color;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityESPModule extends Module implements IMinecraft {

    private final ModeListSetting targetTypes = new ModeListSetting("Targets",
            Arrays.stream(TargetSelector.TargetType.values())
                    .map(TargetSelector.TargetType::getDisplayName)
                    .toArray(String[]::new));

    private final ModeSetting espMode = new ModeSetting("Mode", "Box", "Box", "Angle");

    private final TargetSelector targetSelector = new TargetSelector();
    private Color themeColor;

    public EntityESPModule() {
        super("Entity ESP", "Draws ESP boxes around entities", Category.Visuals);
        addSettings(targetTypes, espMode);
    }

    @EventHandler
    public void onRender3D(EventWorld event) {
        if (IMinecraft.nullCheck()) return;

        themeColor = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());

        Set<TargetSelector.TargetType> types = getSelectedTargetTypes();
        if (types.isEmpty()) return;

        targetSelector.searchTargetsInRadius(90.0f);

        if (espMode.isSelected("Box")) {
            targetSelector.getAllTargets(types).forEach(entity -> {
                BoxMode.render(event.getStack(), entity, getESPColor(entity));
            });
        }
    }

    @EventHandler
    public void onRender2D(EventRender event) {
        if (IMinecraft.nullCheck()) return;

        themeColor = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());

        Set<TargetSelector.TargetType> types = getSelectedTargetTypes();
        if (types.isEmpty()) return;

        targetSelector.searchTargetsInRadius(90.0f);

        if (espMode.isSelected("Angle")) {
            targetSelector.getAllTargets(types).forEach(entity -> {
                AngleMode.render(event.getContext(), entity, getESPColor(entity));
            });
        }
    }

    private Set<TargetSelector.TargetType> getSelectedTargetTypes() {
        return Arrays.stream(TargetSelector.TargetType.values())
                .filter(type -> targetTypes.isSelected(type.getDisplayName()))
                .collect(Collectors.toSet());
    }

    private Color getESPColor(net.minecraft.entity.Entity entity) {
        if (entity instanceof PlayerEntity && FriendsManager.checkFriend(entity.getName().getString())) {
            return new Color(DisplayModule.getFriendColor().getRGB());
        }

        return new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 200);
    }
}