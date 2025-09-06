package ru.minced.client.feature.module.impl.miscellaneous;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.impl.screen.EventDeathScreen;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.core.manager.friend.FriendsManager;
import net.minecraft.client.network.PlayerListEntry;
import ru.minced.client.util.IMinecraft;

import java.util.ArrayList;
import java.util.List;

public class DeathCoordinatesModule extends Module {
    
    private double lastX, lastY, lastZ;
    private boolean hasDied = false;
    private boolean messageShown = false;
    private final BooleanSetting notifyFriends = new BooleanSetting("Notify friends", false);

    public DeathCoordinatesModule() {
        super("Death Coordinates", "Saves the coordinates of the last place of death", Category.Miscellaneous);
        addSettings(notifyFriends);
    }

    @EventHandler
    public void onDeathScreen(EventDeathScreen event) {
        if (IMinecraft.nullCheck()) return;

        if (!hasDied && event.getTicksSinceDeath() == 1) {
            assert mc.player != null;
            lastX = mc.player.getX();
            lastY = mc.player.getY();
            lastZ = mc.player.getZ();
            hasDied = true;
            messageShown = false;

            if (notifyFriends.isState()) {
                notifyFriend();
            }
        }
    }
    
    private void notifyFriend() {
        if (mc.getNetworkHandler() == null) return;

        List<String> onlineFriends = new ArrayList<>();
        
        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            String playerName = entry.getProfile().getName();
            if (FriendsManager.checkFriend(playerName)) {
                onlineFriends.add(playerName);
            }
        }

        int x = (int) Math.round(lastX);
        int y = (int) Math.round(lastY);
        int z = (int) Math.round(lastZ);
        
        String deathMessage = String.format("I died on the coordinates: X: %d Y: %d Z: %d", x, y, z);
        
        for (String friend : onlineFriends) {
            assert mc.player != null;
            mc.player.networkHandler.sendChatCommand("tell " + friend + " " + deathMessage);
            logInfo("Death Coordinates", "Friends have been notified!");
        }
    }

    @EventHandler
    public void onUpdate(EventTick event) {
        if (hasDied && mc.player != null && mc.player.isAlive() && !messageShown) {
            int x = (int) Math.round(lastX);
            int y = (int) Math.round(lastY);
            int z = (int) Math.round(lastZ);

            String message = String.format("Death on the coordinates: X: %d Y: %d Z: %d", x, y, z);
            logInfo("Death Coordinates", message);

            messageShown = true;
            hasDied = false;
        }
    }
    
    @Override
    public void onDisabled() {
        super.onDisabled();
        hasDied = false;
        messageShown = false;
    }
} 