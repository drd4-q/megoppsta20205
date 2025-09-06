package ru.minced.client.util.font;

import lombok.SneakyThrows;
import ru.minced.client.core.Minced;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Fonts {

    private static final Map<FontKey, CFontRenderer> fontCache = new HashMap<>();
    private static final Map<FontKey, CFontRenderer> icons = new HashMap<>();

    public static void initFonts() {
        for (int size = 4; size <= 32; size++) {
            fontCache.put(new FontKey(size), create(size,"semi"));
            icons.put(new FontKey(size), create(size, "icomoon"));
        }
    }

    @SneakyThrows
    public static CFontRenderer create(float size, String name) {
        String path = "assets/minced/fonts/" + name + ".ttf";

        try (InputStream inputStream = Minced.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Не найден шрифт: " + path);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream)
                    .deriveFont(Font.PLAIN, size / 2f);

            return new CFontRenderer(font, size / 2f);
        }
    }

    public static CFontRenderer getSize(int size) {
        return fontCache.computeIfAbsent(new FontKey(size), k -> create(size, "semi"));
    }

    public static CFontRenderer getSizeIcon(int size) {
        return icons.computeIfAbsent(new FontKey(size), k -> create(size, "icomoon"));
    }

    private record FontKey(int size) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FontKey fontKey)) return false;
            return size == fontKey.size;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(size);
        }
    }
}