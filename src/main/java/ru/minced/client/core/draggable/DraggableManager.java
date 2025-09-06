package ru.minced.client.core.draggable;

import ru.minced.client.feature.ui.display.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.minced.client.feature.ui.display.*;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DraggableManager {
    @Getter
    List<AbstractDraggable> draggable = new ArrayList<>();

    public void register() {
        addAll(
                new WatermarkDraggable(),
                new ArmorInfoDraggable(),
                new TargetInfoDraggable(),
                new HotkeysDraggable(),
                new PotionsDraggable(),
                new InventoryDraggable()
        );
    }

    public void addAll(AbstractDraggable... draggable) {
        this.draggable.addAll(List.of(draggable));
    }

    public List<AbstractDraggable> draggable() {
        return draggable;
    }

    public AbstractDraggable get(String name) {
        for (AbstractDraggable drag : draggable) {
            if (!drag.getName().equalsIgnoreCase(name)) continue;
            return drag;
        }
        return null;
    }
}
