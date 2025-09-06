package ru.minced.client.feature.module.impl.fight;

import lombok.Getter;
import lombok.experimental.NonFinal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.TargetSelector;
import ru.minced.client.util.rotation.attack.AttackPerpetrator;
import ru.minced.client.util.rotation.attack.SprintManager;
import ru.minced.client.util.rotation.rotation.RotationController;

import java.util.Arrays;
import java.util.EnumSet;

public class TriggerModule extends Module {

    private final TargetSelector targetSelector = new TargetSelector();

    @Getter
    @NonFinal
    LivingEntity target = null;

    public final ModeListSetting targetTypes = new ModeListSetting("Цели",
            Arrays.stream(TargetSelector.TargetType.values())
                    .map(TargetSelector.TargetType::getDisplayName)
                    .toArray(String[]::new));

    private final ModeListSetting attackSetting = new
            ModeListSetting("Attack setting",
            "Dynamic cooldown", "Un press shield", "Check use");
            
    private final ModeSetting criticalMode = new ModeSetting("Critical mode", "None", "Only critical", "Adaptive");

    private final ModeSetting versionMode = new ModeSetting("Version", "1.8", "1.9");

    public TriggerModule(){
        super("Trigger","Attacks targets when they are in the crosshairs", Category.Fight);
        addSettings(targetTypes, attackSetting, criticalMode, versionMode);
        
        criticalMode.setSelected("Only critical");
        versionMode.setSelected("1.9");
    }

    @EventHandler
    public void onUpdate(EventTick e) {
        if (IMinecraft.nullCheck() || mc.crosshairTarget == null) return;

        if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
            
            if (entity instanceof LivingEntity livingEntity && isValidTarget(livingEntity)) {
                target = livingEntity;
                attackTarget(target);
            }
        } else {
            target = null;
        }
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (!entity.isAlive()) return false;
        if (entity == mc.player) return false;

        EnumSet<TargetSelector.TargetType> selectedTypes = EnumSet.noneOf(TargetSelector.TargetType.class);
        for (String selected : targetTypes.getSelected()) {
            for (TargetSelector.TargetType type : TargetSelector.TargetType.values()) {
                if (type.getDisplayName().equals(selected)) {
                    selectedTypes.add(type);
                    break;
                }
            }
        }

        return targetSelector.isValidTarget(entity, selectedTypes, Float.MAX_VALUE);
    }

    private void attackTarget(LivingEntity target) {
        AttackPerpetrator attackPerpetrator = Minced.getInstance().getAttackPerpetrator();

        AttackPerpetrator.AttackPerpetratorConfigurable configurable = new AttackPerpetrator.AttackPerpetratorConfigurable(
                target,
                RotationController.INSTANCE.getServerAngle(),
                4.5f,
                attackSetting.getSelected(),
                SprintManager.Mode.NONE
        );

        switch (criticalMode.getSelected()) {
            case "Only critical" -> {
                configurable.setOnlyCritical(true);
                configurable.setAdaptiveCritical(false);
            }
            case "Adaptive" -> {
                configurable.setOnlyCritical(false);
                configurable.setAdaptiveCritical(true);
            }
            default -> {
                configurable.setOnlyCritical(false);
                configurable.setAdaptiveCritical(false);
            }
        }

        configurable.setVersion1_8(versionMode.isSelected("1.8"));

        attackPerpetrator.performAttack(configurable);
    }
}
