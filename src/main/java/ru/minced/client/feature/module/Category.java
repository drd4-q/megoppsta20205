package ru.minced.client.feature.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category {
    Fight("Fight", "A"),
    Movement("Move", "B"),
    Player("Player", "C"),
    World("World", "D"),
    Visuals("Visuals", "E"),
    Miscellaneous("Misc", "F");

    final String displayName;
    final String icon;
}
