package ru.minced.client.core.event.api;

public abstract class StoppableEvent implements Event {
    private boolean stopped = false;
    public void stop() { stopped = true; }
    public boolean isStopped() { return stopped; }
} 