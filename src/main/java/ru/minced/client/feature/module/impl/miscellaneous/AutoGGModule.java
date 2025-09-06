package ru.minced.client.feature.module.impl.miscellaneous;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.TitleEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.ILogger;
import ru.minced.client.util.IMinecraft;

public class AutoGGModule extends Module implements ILogger, IMinecraft {

    private boolean receivedTitle = false;

    public AutoGGModule() {
        super("AutoGG", "Автоматически отправляет в чат GG", Category.Miscellaneous);
    }

    @EventHandler
    public void onTitle(TitleEvent event) {
        if (event.getType() == TitleEvent.Type.TITLE && event.getText() != null) {
            String title = event.getText().toString();
            if (title.contains("§c§l") || title.contains("§6§l")) {
                receivedTitle = true;
            }
        } else if (event.getType() == TitleEvent.Type.SUBTITLE && event.getText() != null) {
            String subtitle = event.getText().toString();

            if (subtitle.contains("§f") && receivedTitle && !IMinecraft.nullCheck()) {
                assert mc.player != null;
                mc.player.networkHandler.sendChatMessage("gg");

                receivedTitle = false;
            }
        }
    }
}