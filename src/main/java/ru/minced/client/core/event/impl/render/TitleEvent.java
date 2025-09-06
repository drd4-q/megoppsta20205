package ru.minced.client.core.event.impl.render;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;
import ru.minced.client.core.event.api.Event;

public class TitleEvent implements Event {
    @Getter
    private final Text text;
    @Getter
    private final Type type;
    @Getter
    @Setter
    private boolean cancelled;

    public TitleEvent(Text text, Type type) {
        this.text = text;
        this.type = type;
        this.cancelled = false;
    }

    public enum Type {
        TITLE, SUBTITLE, ACTIONBAR, TIMES
    }
}