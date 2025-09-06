package ru.minced.client.core.event.impl.container;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.screen.slot.SlotActionType;
import ru.minced.client.core.event.api.Event;

@Getter
public class EventClickSlot implements Event {
    private final SlotActionType slotActionType;
    private final int slot, button, id;
    
    @Getter
    @Setter
    private boolean cancel;

    public EventClickSlot(SlotActionType slotActionType, int slot, int button, int id) {
        this.slot = slot;
        this.button = button;
        this.id = id;
        this.slotActionType = slotActionType;
    }
}
