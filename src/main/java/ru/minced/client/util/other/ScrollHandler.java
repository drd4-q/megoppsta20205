package ru.minced.client.util.other;

import ru.minced.client.util.IMinecraft;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrollHandler implements IMinecraft {

    private float scrollOffset = 0;
    private float maxScroll = 0;

    public ScrollHandler() {
    }

    public float getValue() {
        return scrollOffset;
    }

    public void scroll(double amount) {
        scrollOffset += (float) (amount * 30);
        scrollOffset = Math.max(-maxScroll, Math.min(0, scrollOffset));
    }

    public void setMax(float maxScroll) {
        this.maxScroll = maxScroll;
    }
    
    public void reset() {
        scrollOffset = 0;
    }
}