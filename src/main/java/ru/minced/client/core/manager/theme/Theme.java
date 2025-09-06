package ru.minced.client.core.manager.theme;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Theme {
    @Getter
    private final String name;

    @Getter
    private final List<Color> colors = new ArrayList<>();

    public int currentColorIndex = 0;
    public int nextColorIndex = 1;
    public float progress = 0.0f;
    public float transitionSpeed = 0.5f;

    public Theme(String name, Color... colors) {
        this.name = name;
        this.colors.addAll(Arrays.asList(colors));
    }
}