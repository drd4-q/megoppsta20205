package ru.minced.client.feature.module.impl.movement;

import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.impl.player.NoSlowEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.util.math.StopWatch;

import java.util.Objects;

public class NoSlowModule extends Module {

    private final ModeSetting mode = new ModeSetting("Мод", "Vanilla" ,"Grim", "Grim Latest", "HolyWorld");

    public NoSlowModule() {
        super("No Slow", "Eliminates slowdown when eating", Category.Movement);
        addSettings(mode);
    }

    public StopWatch stopWatch = new StopWatch();

    private int ticks = 0;

    @EventHandler
    public void onUpdate(EventTick event) {
        if (mc.player != null && mc.player.isUsingItem()) {
            if (mode.getSelected().equals("Grim")) {
                if (!mc.player.getFlag(7)) {
                    ticks++;
                }
            }
        } else {
            if (mode.isSelected("Grim")) {
                ticks = 0;
            }
        }
    }

    @EventHandler
    public void onNoSlow(NoSlowEvent e) {
        switch (mode.getSelected()) {
            case "Vanilla" -> {
                if (Objects.requireNonNull(mc.player).isUsingItem()) e.stop();
            }
            case "Grim" -> {
                if (ticks > 1) {
                    e.stop();
                    ticks = 0;
                }
            }
            case "Grim Latest" -> {
                if (mc.player != null && mc.player.isUsingItem()) {
                    if (mc.player.getItemUseTime() >= 27) {
                        if (stopWatch.hasElapsed(90)) {
                            e.stop();
                            stopWatch.reset();
                        }
                    }

                    if (stopWatch.hasElapsed(55) && mc.player.getItemUseTime() < 27) {
                        e.stop();
                        stopWatch.reset();
                    }
                }
            }
            case "HolyWorld" -> {
                assert mc.player != null;
                if (!mc.player.isUsingItem()) return;

                if (mc.player.getOffHandStack().getUseAction() == UseAction.BLOCK && mc.player.getActiveHand() == Hand.MAIN_HAND ||
                        mc.player.getOffHandStack().getUseAction() == UseAction.EAT && mc.player.getActiveHand() == Hand.MAIN_HAND) {
                    return;
                }

                mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(mc.player.getActiveHand(), 0, mc.player.getYaw(), mc.player.getPitch()));
                mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(
                        mc.player.getActiveHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND,
                        0,
                        mc.player.getYaw(),
                        mc.player.getPitch()
                ));
                e.stop();
            }
            case "ReallyWorld" -> {

            }
        }
    }
}
