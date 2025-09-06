package ru.minced.client.util.network;

import lombok.experimental.UtilityClass;
import net.minecraft.client.network.PlayerListEntry;
import ru.minced.client.util.IMinecraft;

@UtilityClass
public class PingUtil implements IMinecraft {

    public static int getOwnPing() {
        assert mc.player != null;
        return getPlayerPing(mc.player.getName().getString());
    }

    public static int getPlayerPing(String playerName) {
        if (mc.getNetworkHandler() == null) {
            return -1;
        }

        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            if (entry.getProfile().getName().equalsIgnoreCase(playerName)) {
                return entry.getLatency();
            }
        }

        return -1;
    }
}
