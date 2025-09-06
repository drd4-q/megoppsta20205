package ru.minced.client.feature.module.impl.player;

import ru.minced.client.feature.module.setting.impl.BindSetting;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.keyboard.EventKey;
import ru.minced.client.core.manager.friend.FriendsManager;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.IMinecraft;

@Getter
public class ClickFriendModule extends Module implements IMinecraft {

    private final BindSetting friendKey = new BindSetting("Friend Key", GLFW.GLFW_MOUSE_BUTTON_MIDDLE);

    public ClickFriendModule() {
        super("Click Friend", "Добавление игроков в друзья по бинду", Category.Player);
        addSettings(friendKey);
    }

    @EventHandler
    public void onKey(EventKey event) {
        if (IMinecraft.nullCheck() || event.getAction() != 1) return;

        if (event.getKey() == friendKey.getKey()) {
            handleFriendAction();
        }
    }

    public void handleFriendAction() {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            if (((EntityHitResult) mc.crosshairTarget).getEntity() instanceof PlayerEntity player) {
                String playerName = player.getName().getString();

                if (FriendsManager.checkFriend(playerName)) {
                    FriendsManager.removeFriend(playerName);
                    logInfo("ClickFriend", "Игрок " + playerName + " удален из списка друзей");
                } else {
                    FriendsManager.addFriend(playerName);
                    logInfo("ClickFriend", "Игрок " + playerName + " добавлен в список друзей");
                }
            }
        }
    }
} 