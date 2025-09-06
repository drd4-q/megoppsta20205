package ru.minced.client.feature.module.impl.fight;

import lombok.Getter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventAttack;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.util.network.PacketManager;

@Getter
public class CriticalsModule extends Module {

    private final ModeSetting mode = new ModeSetting("Режим", "HolyWorld", "NCP");

    public CriticalsModule(){
        super("Criticals","Allows you to take critical hits more often", Category.Fight);
        addSettings(mode);
    }

    @EventHandler
    public void onAttack(EventAttack e){
        attack();
    }

    public void attack() {
        assert mc.player != null;

        if (mode.isSelected("HolyWorld")) {
            if (!mc.player.isOnGround() && mc.player.fallDistance == 0) {
                mc.player.fallDistance = 0.001f;

                PacketManager.sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX(),
                    mc.player.getY() - 1e-6,
                    mc.player.getZ(),
                    mc.player.getYaw(),
                    mc.player.getPitch(),
                    false,
                    false));
            }
        } else if (mode.isSelected("NCP")) {
            if (mc.player.isOnGround()) {
                PacketManager.sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX(),
                    mc.player.getY() + 0.11,
                    mc.player.getZ(),
                    mc.player.getYaw(),
                    mc.player.getPitch(),
                    false,
                    false));
                PacketManager.sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX(),
                    mc.player.getY() + 0.1100013579,
                    mc.player.getZ(),
                    mc.player.getYaw(),
                    mc.player.getPitch(),
                    false,
                    false));
                PacketManager.sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX(),
                    mc.player.getY() + 0.0000013579,
                    mc.player.getZ(),
                    mc.player.getYaw(),
                    mc.player.getPitch(),
                    false,
                    false));
            }
        }
    }
}
