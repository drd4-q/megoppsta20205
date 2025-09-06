package ru.minced.client.core.render.builders;

import ru.minced.client.core.render.builders.impl.*;
import lombok.Getter;
import ru.minced.client.core.render.builders.impl.*;

public final class Builder {

    @Getter
    private static final RectangleBuilder RECTANGLE_BUILDER = new RectangleBuilder();
    private static final TextureBuilder TEXTURE_BUILDER = new TextureBuilder();
    private static final TextBuilder TEXT_BUILDER = new TextBuilder();
    private static final BlurBuilder BLUR_BUILDER = new BlurBuilder();
    private static final ImageBuilder IMAGE_BUILDER = new ImageBuilder();
    private static final OutlineBuilder OUTLINE_BUILDER = new OutlineBuilder();
    private static final CircleBuilder CIRCLE_BUILDER = new CircleBuilder();

    public static RectangleBuilder rectangle() {
        return RECTANGLE_BUILDER;
    }

    public static TextureBuilder texture() {
        return TEXTURE_BUILDER;
    }

    public static TextBuilder text() {
        return TEXT_BUILDER;
    }

    public static BlurBuilder blur() {
        return BLUR_BUILDER;
    }
    
    public static ImageBuilder image() {
        return IMAGE_BUILDER;
    }
    
    public static OutlineBuilder outline() {
        return OUTLINE_BUILDER;
    }

    public static CircleBuilder circle() {
        return CIRCLE_BUILDER;
    }
}