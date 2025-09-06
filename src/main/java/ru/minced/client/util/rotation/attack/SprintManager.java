package ru.minced.client.util.rotation.attack;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.listener.impl.PacketEventListener;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.movement.SprintModule;
import ru.minced.client.util.IMinecraft;
import ru.minced.mixin.accessor.ClientPlayerEntityAccessor;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SprintManager implements IMinecraft {
    @Setter
    Mode currentMode;
    boolean isStopSprintPacketSent;

    public void tick(AttackHandler parent) {
        if (currentMode == Mode.LEGIT) {
            Module autoSprintModule = Minced.getInstance().getModuleManager().getSprintModule();
            if (autoSprintModule instanceof SprintModule autoSprint && autoSprintModule.isState()) {
                autoSprint.setEmergencyStop(true);
            }
        }
    }


    public void preAttack() {
        if (currentMode == Mode.PACKET) {
            if (PacketEventListener.serverSprint) {
                assert mc.player != null;
                if (((ClientPlayerEntityAccessor) mc.player).getLastSprinting()) {
                    mc.player.setSprinting(false);
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    isStopSprintPacketSent = true;
                }
            }
        }
    }

    public void postAttack() {
        if (currentMode == Mode.PACKET) {
            if (isStopSprintPacketSent) {
                assert mc.player != null;
                if (((ClientPlayerEntityAccessor) mc.player).getLastSprinting()) {
                    mc.player.setSprinting(true);
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    isStopSprintPacketSent = false;
                }
            }
        }
    }

    public enum Mode {
        LEGIT, PACKET, NONE
    }
}
