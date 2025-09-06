package ru.minced.client.feature.module.impl.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventAttack;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.core.manager.friend.FriendsManager;

public class NoDamageModule extends Module {

    public NoDamageModule() {
        super("No Damage", "Doesn't allow you to attack your friends", Category.Player);
    }

    @EventHandler
    public void onAttack(EventAttack event) {
        Entity target = event.getTarget();

        if (target instanceof PlayerEntity) {
            String playerName = target.getName().getString();

            if (FriendsManager.checkFriend(playerName)) {
                event.stop();
            }
        }
    }
}