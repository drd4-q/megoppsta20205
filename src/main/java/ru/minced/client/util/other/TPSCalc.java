package ru.minced.client.util.other;

import lombok.Getter;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;

@Getter
public class TPSCalc {

    private float TPS = 20;
    private float adjustTicks = 0;

    private long timestamp;

    public TPSCalc() {
        Minced.getInstance().getEventManager().subscribe(this);
    }

    @EventHandler
    private void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            updateTPS();
        }
    }

    private void updateTPS() {
        long delay = System.nanoTime() - timestamp;

        float maxTPS = 20;
        float rawTPS = maxTPS * (1e9f / delay);

        float boundedTPS = MathHelper.clamp(rawTPS, 0, maxTPS);

        TPS = (float) round(boundedTPS);

        adjustTicks = boundedTPS - maxTPS;

        timestamp = System.nanoTime();
    }

    public double round(
            final double input
    ) {
        return Math.round(input * 100.0) / 100.0;
    }
}
