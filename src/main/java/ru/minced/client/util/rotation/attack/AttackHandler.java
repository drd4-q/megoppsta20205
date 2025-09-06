package ru.minced.client.util.rotation.attack;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.api.EventType;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.core.event.impl.player.EventAttack;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.rotation.RaytracingUtil;
import ru.minced.client.util.player.PlayerIntersectionUtil;

@Getter
public class AttackHandler implements IMinecraft {
    SprintManager sprintManager = new SprintManager();
    ClickScheduler clickScheduler = new ClickScheduler();

    private boolean alternateTickDelay = false;

    private int airTicks = 0;

    void tick() {
        sprintManager.tick(this);
        airTicks++;
    }

    public void onPacket(PacketEvent packetEvent) {
        Packet<?> packet = packetEvent.getPacket();
        if (packet instanceof UpdateSelectedSlotC2SPacket) {
            clickScheduler.recalculate(650L);
        } else if (packet instanceof HandSwingC2SPacket) {
            clickScheduler.recalculate(500L);
        }
    }

    void handleAttack(AttackPerpetrator.AttackPerpetratorConfigurable configurable) {
        if (canAttack(configurable)) {
            assert mc.player != null;
            if (mc.player.isBlocking() && configurable.isShouldUnpressShield()) {
                assert mc.interactionManager != null;
                mc.interactionManager.stopUsingItem(mc.player);
            }

            EventAttack preEvent = new EventAttack(configurable.getTarget(), EventType.PRE);
            EventManager.post(preEvent);

            if (!preEvent.isStopped()) {
                sprintManager.preAttack();
                mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(configurable.getTarget(), mc.player.isSneaking()));
                mc.player.resetLastAttackedTicks();
                mc.player.swingHand(Hand.MAIN_HAND);
                sprintManager.postAttack();
                airTicks = 0;

                EventAttack postEvent = new EventAttack(configurable.getTarget(), EventType.POST);
                EventManager.post(postEvent);
            }
        }
    }

    boolean canAttack(AttackPerpetrator.AttackPerpetratorConfigurable config) {
        Entity targetEntity = getRayTracingEntity(config);

        if (isRaytraceCheckFailed(config, targetEntity) || isCooldownNotComplete(config) || isPlayerUsing(config)) {
            return false;
        }

        if (config.isOnlyCritical() && !hasMovementRestrictions()) {
            return isPlayerInCriticalState();
        }

        if (config.isAdaptiveCritical() && !hasMovementRestrictions()) {
            assert mc.player != null;
            if (mc.player.isOnGround()) {
                return true;
            }
            else {
                return isPlayerInCriticalState();
            }
        }

        return true;
    }

    private boolean isRaytraceCheckFailed(AttackPerpetrator.AttackPerpetratorConfigurable config, Entity targetEntity) {
        return config.isRaytraceEnabled() && (targetEntity != config.getTarget() || RaytracingUtil.isEntityBehindWall(config.getTarget()));
    }

    private boolean isCooldownNotComplete(AttackPerpetrator.AttackPerpetratorConfigurable config) {
        if (config.isVersion1_8()) {
            if (config.isUseDynamicCooldown()) {
                int tickDelay = alternateTickDelay ? 1 : 2;
                alternateTickDelay = !alternateTickDelay;
                return !clickScheduler.hasTicksElapsedSinceLastClick(tickDelay);
            }
            return false;
        }
        return !clickScheduler.isCooldownComplete(config.isUseDynamicCooldown());
    }

    private boolean hasMovementRestrictions() {
        assert mc.player != null;
        return mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || PlayerIntersectionUtil.isPlayerInWeb()
                || mc.player.isSubmergedInWater()
                || mc.player.isInLava()
                || mc.player.isClimbing()
                || mc.player.getAbilities().flying;
    }

    private boolean isPlayerInCriticalState() {
        if (Minced.getInstance().getModuleManager().getCriticalsModule().isState()) {
            String mode = Minced.getInstance().getModuleManager().getCriticalsModule().getMode().getSelected();

            if (mode.equals("HolyWorld")) {
                assert mc.player != null;
                return !mc.player.isOnGround() && airTicks >= 2;
            }
            else if (mode.equals("NCP")) {
                return true;
            }
        }

        double randomThreshold = 0.05 + Math.random() * 0.05;
        assert mc.player != null;
        return !mc.player.isOnGround() && mc.player.fallDistance > randomThreshold && !mc.player.isClimbing()
                && !mc.player.isTouchingWater() && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS);
    }

    private boolean isPlayerUsing(AttackPerpetrator.AttackPerpetratorConfigurable config) {
        return config.isCheckEating() && mc.player != null && mc.player.isUsingItem() && (!mc.player.isBlocking() || !config.isShouldUnpressShield());
    }

    private Entity getRayTracingEntity(AttackPerpetrator.AttackPerpetratorConfigurable configurable) {
        EntityHitResult entityHitResult = RaytracingUtil.raytraceEntity(
                configurable.getMaximumRange(),
                configurable.getAngle(),
                e -> configurable.isRaytraceEnabled());

        if (entityHitResult != null) {
            return entityHitResult.getEntity();
        }
        return null;
    }
}
