package ru.minced.client.core.manager.theme;

import lombok.Getter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    @Getter
    private static ThemeManager instance;
    
    @Getter
    private final List<Theme> themes = new ArrayList<>();
    
    @Getter
    private Theme currentTheme;
    
    private long lastTime = System.currentTimeMillis();
    
    public ThemeManager() {
        instance = this;

        registerDefaultThemes();

        if (!themes.isEmpty()) {
            currentTheme = themes.get(0);
        }
    }

    private void registerDefaultThemes() {
        Theme minced = new Theme("Minced",
            new Color(142, 68, 173),
            new Color(155, 89, 182),
            new Color(231, 76, 152),
            new Color(255, 121, 198),
            new Color(199, 107, 235),
            new Color(118, 66, 138),
            new Color(244, 143, 177),
            new Color(186, 104, 200)
        );

        Theme sunset = new Theme("Sunset",
            new Color(255, 94, 77),
            new Color(255, 195, 113),
            new Color(255, 255, 128),
            new Color(255, 153, 102),
            new Color(255, 94, 98)
        );

        Theme forest = new Theme("Forest",
            new Color(34, 139, 34),
            new Color(85, 107, 47),
            new Color(46, 139, 87),
            new Color(107, 142, 35),
            new Color(60, 179, 113)
        );

        Theme ocean = new Theme("Ocean",
            new Color(0, 105, 148),
            new Color(0, 168, 232),
            new Color(72, 202, 228),
            new Color(0, 210, 255),
            new Color(68, 68, 224)
        );

        Theme neon = new Theme("Neon",
            new Color(57, 255, 20),
            new Color(255, 0, 255),
            new Color(0, 255, 255),
            new Color(255, 255, 0),
            new Color(255, 20, 147)
        );

        Theme marshmallow = new Theme("Marshmallow",
            new Color(255, 179, 186),
            new Color(255, 223, 186),
            new Color(255, 255, 186),
            new Color(186, 255, 201),
            new Color(186, 225, 255)
        );

        Theme monochrome = new Theme("Monochrome",
            new Color(30, 30, 30),
            new Color(80, 80, 80),
            new Color(150, 150, 150),
            new Color(220, 220, 220),
            new Color(255, 255, 255)
        );

        Theme fire = new Theme("Fire",
            new Color(255, 87, 34),
            new Color(255, 138, 101),
            new Color(255, 202, 40),
            new Color(255, 160, 0),
            new Color(255, 61, 0)
        );

        Theme ice = new Theme("Ice",
            new Color(173, 216, 230),
            new Color(135, 206, 250),
            new Color(0, 191, 255),
            new Color(224, 255, 255),
            new Color(175, 238, 238)
        );

        Theme earth = new Theme("Earth",
            new Color(139, 69, 19),
            new Color(205, 133, 63),
            new Color(222, 184, 135),
            new Color(160, 82, 45),
            new Color(188, 143, 143)
        );

        Theme candy = new Theme("Candy",
            new Color(255, 105, 180),
            new Color(255, 182, 193),
            new Color(255, 160, 203),
            new Color(255, 218, 185),
            new Color(255, 240, 245)
        );

        Theme blood = new Theme("Blood",
                new Color(138, 3, 3),
                new Color(89, 0, 0),
                new Color(153, 0, 0),
                new Color(200, 16, 46),
                new Color(60, 0, 0),
                new Color(102, 0, 13),
                new Color(33, 0, 0),
                new Color(176, 23, 31),
                new Color(255, 0, 0)
        );

        Theme red = new Theme("Red",
                new Color(220, 20, 60),
                new Color(255, 69, 0),
                new Color(178, 34, 34),
                new Color(255, 99, 71),
                new Color(139, 0, 0)
        );

        Theme purple = new Theme("Purple",
                new Color(155, 89, 182),
                new Color(128, 0, 128),
                new Color(106, 13, 173),
                new Color(75, 0, 130),
                new Color(48, 25, 52),
                new Color(50, 25, 65)
        );

        addTheme(minced);
        addTheme(sunset);
        addTheme(forest);
        addTheme(ocean);
        addTheme(neon);
        addTheme(marshmallow);
        addTheme(monochrome);
        addTheme(fire);
        addTheme(ice);
        addTheme(earth);
        addTheme(candy);
        addTheme(blood);
        addTheme(red);
        addTheme(purple);
    }
    
    public void addTheme(Theme theme) {
        themes.add(theme);
    }
    
    public void setTheme(String themeName) {
        for (Theme theme : themes) {
            if (theme.getName().equalsIgnoreCase(themeName)) {
                currentTheme = theme;
                break;
            }
        }
    }

    public Color getColorByIndex(int index) {
        if (currentTheme == null || currentTheme.getColors().isEmpty()) {
            return Color.WHITE;
        }
        
        if (index < 0 || index >= currentTheme.getColors().size()) {
            index = 0;
        }
        
        return currentTheme.getColors().get(index);
    }
    public Color getColorRate() {
        if (currentTheme == null || currentTheme.getColors().isEmpty()) {
            return Color.WHITE;
        }
        
        List<Color> colors = currentTheme.getColors();
        if (colors.size() == 1) {
            return colors.get(0);
        }
        
        long time = System.currentTimeMillis();
        float delta = (time - lastTime) / 1000.0f;
        lastTime = time;

        currentTheme.progress += delta * currentTheme.transitionSpeed;
        if (currentTheme.progress >= 1.0f) {
            currentTheme.currentColorIndex = (currentTheme.currentColorIndex + 1) % colors.size();
            currentTheme.nextColorIndex = (currentTheme.nextColorIndex + 1) % colors.size();
            currentTheme.progress = 0.0f;
        }

        Color current = colors.get(currentTheme.currentColorIndex);
        Color next = colors.get(currentTheme.nextColorIndex);
        
        return interpolateColor(current, next, currentTheme.progress);
    }

    
    private Color interpolateColor(Color a, Color b, float progress) {
        int red = (int) (a.getRed() + progress * (b.getRed() - a.getRed()));
        int green = (int) (a.getGreen() + progress * (b.getGreen() - a.getGreen()));
        int blue = (int) (a.getBlue() + progress * (b.getBlue() - a.getBlue()));
        
        return new Color(red, green, blue);
    }

}
