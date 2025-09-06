package ru.minced.client.util.other.animation;


import lombok.Setter;
import lombok.experimental.Accessors;
import ru.minced.client.util.math.StopWatch;

import static ru.minced.client.util.other.animation.Direction.FORWARDS;

@Setter
@Accessors(chain = true)
public abstract class Animation implements AnimationCalculation {
    private final StopWatch counter = new StopWatch();
    protected int ms;
    protected double value;
    protected Direction direction = FORWARDS;

    public void reset() {
        counter.reset();
    }

    public boolean isDone() {
        return counter.hasElapsed(ms);
    }

    public boolean isFinished(Direction direction) {
        return this.direction == direction && isDone();
    }

    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            adjustTimer();
        }
    }

    private void adjustTimer() {
        counter.setLastMS(
                System.currentTimeMillis() - ((long) ms - Math.min(ms, counter.getLastMS()))
        );
    }

    public void update() {
        if (!isDone()) {
        }
    }

    public Double getOutput() {
        double time = (1 - calculation(counter.getLastMS())) * value;

        return direction == FORWARDS
                ? endValue()
                : isDone() ? 0.0 : time;
    }

    private double endValue() {
        return isDone()
                ? value
                : calculation(counter.getLastMS()) * value;
    }
}