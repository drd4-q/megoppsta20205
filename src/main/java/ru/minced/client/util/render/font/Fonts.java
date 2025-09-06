package ru.minced.client.util.render.font;

import lombok.experimental.UtilityClass;
import ru.minced.client.core.render.msdf.MsdfFont;

@UtilityClass
public class Fonts {
    public Font BLACK, BOLD, MEDIUM, REGULAR, SEMIBOLD, ICONS;

    static {
        BLACK = new Font(MsdfFont.builder().atlas("sf-pro-black").data("sf-pro-black").build());
        BOLD = new Font(MsdfFont.builder().atlas("sf-pro-bold").data("sf-pro-bold").build());
        MEDIUM = new Font(MsdfFont.builder().atlas("sf-pro-medium").data("sf-pro-medium").build());
        REGULAR = new Font(MsdfFont.builder().atlas("sf-pro-regular").data("sf-pro-regular").build());
        ICONS = new Font(MsdfFont.builder().atlas("icons").data("icons").build());
    }
}