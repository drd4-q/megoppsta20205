package ru.minced.client.feature.module.impl.movement;

import lombok.Setter;
import lombok.experimental.NonFinal;
import net.minecraft.entity.effect.StatusEffects;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventKeepSprint;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;

import java.util.Objects;

public class SprintModule extends Module {
    @Setter
    @NonFinal
    boolean emergencyStop = false;
    BooleanSetting keepSprintSetting = new BooleanSetting("Keep Sprint", false);

    public SprintModule(){
        super("Sprint","Автоматический спринт", Category.Movement);
        addSettings(keepSprintSetting);
    }

    @EventHandler
    public void onTick(EventTick e) {
        boolean canSprint = canStartSprinting();

        if (canSprint && !Objects.requireNonNull(mc.player).horizontalCollision) {
            if (!mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }
        } else if (!canSprint || emergencyStop) {
            assert mc.player != null;
            if (mc.player.isSprinting()) {
                mc.player.setSprinting(false);
            }
        }
        emergencyStop = false;
    }


    @EventHandler
    public void onKeepSprint(EventKeepSprint eventKeepSprint) {
        if (keepSprintSetting.isState()) {
            float multiplier = 1.0F;
            assert mc.player != null;
            mc.player.setVelocity(mc.player.getVelocity().x / 0.6 * multiplier, mc.player.getVelocity().y, mc.player.getVelocity().z / 0.6 * multiplier);
            mc.player.setSprinting(true);
        }
    }

    @Override
    public void onDisabled() {
        if (mc.player != null && mc.player.isSprinting())
            mc.options.sprintKey.setPressed(false);
        super.onDisabled();
    }

    private boolean canStartSprinting() {
        assert mc.player != null;
        return (mc.player.input.movementForward > 0 || mc.player.isSwimming()) &&
                (!mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) &&
                !mc.player.getFlag(7) &&
                !mc.player.hasVehicle() &&
                mc.player.getHungerManager().getFoodLevel() > 6 && !mc.player.isUsingItem();
    }

}