package ru.minced.client.util.rotation.attack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.rotation.Angle;

import java.util.List;

@Getter
public class AttackPerpetrator implements IMinecraft {
    AttackHandler attackHandler = new AttackHandler();

    public void tick() {
        attackHandler.tick();
    }

    public void onPacket(PacketEvent packet) {
        attackHandler.onPacket(packet);
    }

    public void performAttack(AttackPerpetratorConfigurable configurable) {
        attackHandler.sprintManager.setCurrentMode(configurable.getMode());
        attackHandler.handleAttack(configurable);
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AttackPerpetratorConfigurable {
        final LivingEntity target;
        final Angle angle;
        final float maximumRange;
        final boolean raytraceEnabled;
        @Setter
        boolean onlyCritical;
        @Setter
        boolean adaptiveCritical;
        @Setter
        boolean version1_8;
        final boolean shouldBreakShield;
        final boolean shouldUnpressShield;
        final boolean useDynamicCooldown;
        final boolean checkEating;
        final boolean wallCheckEnabled;
        final SprintManager.Mode mode;

        public AttackPerpetratorConfigurable(LivingEntity target, Angle angle, float maximumRange, List<String> options, SprintManager.Mode mode) {
            this.target = target;
            this.angle = angle;
            this.maximumRange = maximumRange;
            this.raytraceEnabled = options.contains("Raytrace check");
            this.onlyCritical = options.contains("Only critical");
            this.adaptiveCritical = false;
            this.version1_8 = false;
            this.shouldBreakShield = options.contains("Break shield");
            this.shouldUnpressShield = options.contains("Un press shield");
            this.useDynamicCooldown = options.contains("Dynamic cooldown");
            this.checkEating = options.contains("Check use");
            this.wallCheckEnabled = options.contains("Wall Check");
            this.mode = mode;
        }
    }
}
