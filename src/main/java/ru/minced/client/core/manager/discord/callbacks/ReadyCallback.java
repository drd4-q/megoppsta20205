package ru.minced.client.core.manager.discord.callbacks;

import com.sun.jna.Callback;

public interface ReadyCallback extends Callback {
    void apply(DiscordUser var1);
}