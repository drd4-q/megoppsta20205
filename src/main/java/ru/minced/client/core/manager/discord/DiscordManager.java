package ru.minced.client.core.manager.discord;

import ru.minced.client.core.info.Client;
import lombok.Getter;
import ru.minced.client.core.Minced;
import ru.minced.client.core.manager.discord.utils.DiscordEventHandlers;
import ru.minced.client.core.manager.discord.utils.DiscordRPC;
import ru.minced.client.core.manager.discord.utils.DiscordRichPresence;
import ru.minced.client.core.manager.discord.utils.RPCButton;

@Getter
public class DiscordManager {
    private final DiscordDaemonThread discordDaemonThread = new DiscordDaemonThread();
    private boolean running = true;

    public void init() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .ready((user) -> {

                    String image = "https://image.com/";

                    DiscordRichPresence richPresence = new DiscordRichPresence.Builder()
                            .setStartTimestamp((System.currentTimeMillis() / 1000))
                            .setState("Build: " + Client.build)
                            .setLargeImage(image)
                            .setButtons(
                                    RPCButton.create("Получить", "https://t.me/example"),
                                    RPCButton.create("Discord", "https://discord.gg/example")
                            ).build();

                    DiscordRPC.INSTANCE.Discord_UpdatePresence(richPresence);
                })
                .build();

        String APPLICATION_ID = "0";
        DiscordRPC.INSTANCE.Discord_Initialize(APPLICATION_ID, handlers, true, "");
        discordDaemonThread.start();
    }

    public void stopRPC() {
        DiscordRPC.INSTANCE.Discord_Shutdown();
        this.running = false;
    }


    private class DiscordDaemonThread extends Thread {
        @Override
        public void run() {
            this.setName("Discord-RPC");

            try {
                while (Minced.getInstance().getDiscordManager().isRunning()) {
                    DiscordRPC.INSTANCE.Discord_RunCallbacks();
                    Thread.sleep(15 * 1000);
                }
            } catch (Exception exception) {
                stopRPC();
            }

            super.run();
        }
    }
}