package ru.minced.client.core.event.listener.impl;


import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.listener.Listener;

public class UpdateEventListener implements Listener {
    @EventHandler
    public void onTick(EventTick e) {
        Minced.getInstance().getAttackPerpetrator().tick();
    }
}
