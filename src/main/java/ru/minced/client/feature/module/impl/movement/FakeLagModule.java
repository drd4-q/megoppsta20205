package ru.minced.client.feature.module.impl.movement;

import ru.minced.client.util.math.StopWatch;
import ru.minced.client.util.player.MovingUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.network.PacketManager;
import ru.minced.client.util.render.Renderer3D;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeLagModule extends Module {

    public FakeLagModule(){
        super("Fake Lag", "Simulates lag by holding and releasing packets", Category.Movement);
        addSettings(maxDelay, minDelay, onlyWithTarget, playerInRange, maxRange, minRange, onlyWhenMoving, onlyOnGround, cancelPlayerActionPacket,
                cancelVelocityPacket, cancelPlayerInteractPacket, cancelPositionPacket, cancelExplosionPacket, cancelHealthUpdatePacket, renderBox);
    }

    private final SliderSetting maxDelay = new SliderSetting("Max Delay", 500f, 0f, 1500f, 1f);
    private final SliderSetting minDelay = new SliderSetting("Min Delay", 200f, 0f, 1500f, 1f);

    private final BooleanSetting onlyWithTarget = new BooleanSetting("Only With Target", false);
    private final BooleanSetting playerInRange = new BooleanSetting("Player In Range", false);
    private final SliderSetting maxRange = new SliderSetting("Max Range", 5f, 1f, 6f, 0.05f, playerInRange::isState);
    private final SliderSetting minRange = new SliderSetting("Min Range", 2f, 1f, 6f, 0.05f, playerInRange::isState);
    private final BooleanSetting onlyWhenMoving = new BooleanSetting("Only When Moving", false);
    private final BooleanSetting onlyOnGround = new BooleanSetting("Only On Ground", false);

    private final BooleanSetting cancelPlayerActionPacket = new BooleanSetting("Cancel Player Action", true);
    private final BooleanSetting cancelVelocityPacket = new BooleanSetting("Cancel Velocity", true);
    private final BooleanSetting cancelPlayerInteractPacket = new BooleanSetting("Cancel Player Interact", true);
    private final BooleanSetting cancelPositionPacket = new BooleanSetting("Cancel Position", true);
    private final BooleanSetting cancelExplosionPacket = new BooleanSetting("Cancel Explosion", true);
    private final BooleanSetting cancelHealthUpdatePacket = new BooleanSetting("Cancel Health Update", true);

    private final BooleanSetting renderBox = new BooleanSetting("Render Box", true);

    private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();
    private final StopWatch packetTimer = new StopWatch();
    private PlayerEntity inRangeTarget = null;
    private Box box;

    @EventHandler
    public void onPacketSend(PacketEvent e) {
        if (e.isSend()) {
            if (!shouldActivate()) {
                resumePackets();
                box = null;
                return;
            }

            if (e.getPacket() instanceof PlayerMoveC2SPacket) {
                packets.add(e.getPacket());
                e.stop();
            } else if (cancelPlayerActionPacket.isState() && e.getPacket() instanceof PlayerActionC2SPacket) resumePackets();
            else if (cancelPlayerInteractPacket.isState() && e.getPacket() instanceof PlayerInteractEntityC2SPacket)
                resumePackets();
        }
        if (e.isReceive()){
            if (!shouldActivate()) {
                resumePackets();
                box = null;
                return;
            }

            boolean shouldResume = false;

            if (cancelVelocityPacket.isState() && e.getPacket() instanceof EntityVelocityUpdateS2CPacket packet && isVelocity(packet)) shouldResume = true;
            else if (cancelExplosionPacket.isState() && e.getPacket() instanceof ExplosionS2CPacket packet && isKnockback(packet)) shouldResume = true;
            else if (cancelHealthUpdatePacket.isState() && e.getPacket() instanceof HealthUpdateS2CPacket) shouldResume = true;
            else if (cancelPositionPacket.isState() && e.getPacket() instanceof PlayerPositionLookS2CPacket) shouldResume = true;

            if (shouldResume) resumePackets();
        }
    }

    @EventHandler
    public void onPlayerTick(EventTick e) {
        if (mc.player == null) return;
        if (!shouldActivate()) {
            resumePackets();
            box = null;
            return;
        }

        if (packetTimer.hasElapsed(getCurrentDelay())) resumePackets();
    }

    @EventHandler
    public void onGameRender3D(EventWorld e) {
        if (mc.player == null || !renderBox.isState() || box == null) return;
        Renderer3D.drawBox(box, e.getStack(), new Color(255, 255, 255, 50), new Color(255, 255, 255, 150));
    }

    private boolean shouldActivate() {
        if (onlyWithTarget.isState() && Minced.getInstance().getModuleManager().getAttackAuraModule().getTarget() == null) return false;
        if (onlyWhenMoving.isState() && !MovingUtil.isMoving()) return false;
        if (onlyOnGround.isState()) {
            assert mc.player != null;
            if (!mc.player.isOnGround()) return false;
        }
        assert mc.player != null;
        if (mc.player.isUsingItem()) return false;
        if (mc.player.horizontalCollision) return false;

        if (playerInRange.isState()) {
            inRangeTarget = null;
            float checkRange = MathUtil.randomFloat(minRange.getValue(), maxRange.getValue());

            assert mc.world != null;
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player) continue;
                if (player.getPos().squaredDistanceTo(mc.player.getEyePos()) <= MathHelper.square(checkRange)) {
                    inRangeTarget = player;
                    break;
                }
            }

            return inRangeTarget != null;
        }

        return true;
    }

    private long getCurrentDelay() {
        return (long) (MathUtil.randomFloat(minDelay.getValue(), maxDelay.getValue()));
    }

    private boolean isVelocity(EntityVelocityUpdateS2CPacket packet) {
        assert mc.player != null;
        if (packet.getEntityId() !=  mc.player.getId()) return false;
        return Math.abs(packet.getVelocityX() / 8000.0) > 0.1D || Math.abs(packet.getVelocityY() / 8000.0) > 0.1D || Math.abs(packet.getVelocityZ() / 8000.0) > 0.1D;
    }

    private boolean isKnockback(ExplosionS2CPacket packet) {
        if (packet.playerKnockback().isEmpty()) return false;
        return Math.abs(packet.playerKnockback().get().getX()) > 0.1D || Math.abs(packet.playerKnockback().get().getY()) > 0.1D || Math.abs(packet.playerKnockback().get().getZ()) > 0.1D;
    }

    private void resumePackets() {
        if (!packets.isEmpty()) {
            for (Packet<?> packet : packets) {
                PacketManager.sendSilentPacket(packet);
                packets.remove(packet);
            }
            assert mc.player != null;
            box = mc.player.getBoundingBox();
            packetTimer.reset();
        }
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        resumePackets();
        box = null;
        inRangeTarget = null;
    }
}

