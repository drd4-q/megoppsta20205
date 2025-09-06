package ru.minced.client.feature.module.impl.miscellaneous;

import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.IMinecraft;

import java.util.UUID;

public class RPSpoofModule extends Module implements IMinecraft {
    
    private ResourcePackAction currentAction = ResourcePackAction.WAIT;
    private UUID currentPackId = null;
    
    public RPSpoofModule() {
        super("RP Spoof", "Automatically accepts the server resource pack", Category.Miscellaneous);
    }
    
    @EventHandler
    public void onPacket(PacketEvent event) {
        if (IMinecraft.nullCheck()) return;

        if (event.getPacket() instanceof ResourcePackSendS2CPacket packet && event.isReceive()) {
            currentPackId = packet.id();
            currentAction = ResourcePackAction.ACCEPT;
            event.stop();
        }
    }
    
    @EventHandler
    public void onUpdate(EventTick event) {
        if (IMinecraft.nullCheck()) return;
        
        if (mc.getNetworkHandler() != null && currentPackId != null) {
            if (currentAction == ResourcePackAction.ACCEPT) {
                mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(currentPackId, ResourcePackStatusC2SPacket.Status.ACCEPTED));
                currentAction = ResourcePackAction.SEND;
            } else if (currentAction == ResourcePackAction.SEND) {
                mc.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(currentPackId, ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                currentAction = ResourcePackAction.WAIT;
            }
        }
    }

    private enum ResourcePackAction {
        WAIT, ACCEPT, SEND
    }
} 