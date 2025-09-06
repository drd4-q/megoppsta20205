package ru.minced.client.feature.module.impl.client;

import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.manager.theme.Theme;
import ru.minced.client.core.manager.theme.ThemeManager;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;

public class ThemeModule extends Module {
    
    private final ModeSetting themeSetting;
    private String lastSelectedTheme;
    
    public ThemeModule() {
        super("Theme", "Позволяет выбрать тему оформления клиента", Category.Miscellaneous);

        ThemeManager themeManager = Minced.getInstance().getThemeManager();
        String[] themeNames = themeManager.getThemes().stream()
                .map(Theme::getName)
                .toArray(String[]::new);

        themeSetting = new ModeSetting("Theme", themeNames);

        if (themeManager.getCurrentTheme() != null) {
            themeSetting.setSelected(themeManager.getCurrentTheme().getName());
            lastSelectedTheme = themeSetting.getSelected();
        }

        addSettings(themeSetting);

        setState(true);
    }
    
    @EventHandler
    public void onUpdate(EventTick e) {
        String currentTheme = themeSetting.getSelected();
        if (lastSelectedTheme != null && !lastSelectedTheme.equals(currentTheme)) {
            ThemeManager themeManager = Minced.getInstance().getThemeManager();
            themeManager.setTheme(currentTheme);
            lastSelectedTheme = currentTheme;
        }
    }
} 