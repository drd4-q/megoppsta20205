package ru.minced.client.feature.module.impl.player;

import ru.minced.client.util.math.StopWatch;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.impl.player.MovementInputEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.network.PacketManager;
import ru.minced.client.util.player.MovingUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScreenWalkModule extends Module {

    public ScreenWalkModule() {
        super("Screen Walk", "dsfsdf", Category.Movement);
        addSettings(mode);
    }

    private final ModeSetting mode = new ModeSetting("Mode", "Grim", "FunTime", "Vanilla");

    @Getter @Setter
    private int ticks = 0;
    private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
    private final StopWatch stopWatch = new StopWatch();

    @EventHandler
    public void onTick(EventTick eventTick) {
        if (IMinecraft.nullCheck()) return;
        if (mc.currentScreen == null
                || mc.currentScreen instanceof ChatScreen
                || mc.currentScreen instanceof SignEditScreen
                || mc.currentScreen instanceof AnvilScreen
                || (!mode.isSelected("Vanilla") && mc.currentScreen instanceof GenericContainerScreen)
        ) return;

        for (KeyBinding binding : new KeyBinding[]{
                mc.options.forwardKey, mc.options.backKey, mc.options.rightKey,
                mc.options.leftKey, mc.options.jumpKey, mc.options.sneakKey}) {
            if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), binding.getDefaultKey().getCode())) continue;
            binding.setPressed(true);
        }
    }

    @EventHandler
    public void onPacketEvent(PacketEvent packetEvent) {
        if (IMinecraft.nullCheck() || mode.isSelected("Vanilla")|| !packetEvent.isSend()) return;

        if (packetEvent.getPacket() instanceof ClickSlotC2SPacket && mc.currentScreen instanceof InventoryScreen) {
            packets.add(packetEvent.getPacket());
            packetEvent.stop();
        } else if (packetEvent.getPacket() instanceof CloseHandledScreenC2SPacket && !packets.isEmpty() && mc.currentScreen instanceof InventoryScreen) {
            ticks = mode.isSelected("Grim") ? 1 : 2;
            new Thread(() -> {
                try {
                    Thread.sleep(ticks * 45L);
                    MovingUtil.DirectionalInput input = MovingUtil.DirectionalInput.NONE;

                    resumePackets();

                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CloseHandledScreenC2SPacket(0));
                } catch (Exception ignored) {}
            }).start();
            packetEvent.stop();
        }
    }

    @EventHandler
    public void onMovementInputEvent(MovementInputEvent e) {
        if (IMinecraft.nullCheck() || mode.isSelected("Vanilla")) return;

        if (ticks-- > 0) {
            e.setDirectionalInput(MovingUtil.DirectionalInput.NONE);
        }
    }

    private void resumePackets() {
        if (packets.isEmpty()) return;
        for (Packet<?> packet : packets) PacketManager.sendSilentPacket(packet);
        packets.clear();
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        ticks = 0;
    }
}