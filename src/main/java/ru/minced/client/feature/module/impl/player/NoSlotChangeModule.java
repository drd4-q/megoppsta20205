package ru.minced.client.feature.module.impl.player;

import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;

import java.util.Objects;

public class NoSlotChangeModule extends Module {

    public NoSlotChangeModule(){
        super("No Slot Change","Cancels a slot change by the server", Category.Player);
    }

    @EventHandler
    public void onPacket(PacketEvent event){
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket && event.getType() == PacketEvent.Type.SEND){
            event.stop();
            assert mc.player != null;
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
        }
    }
}
