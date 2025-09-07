package ru.minced.client.feature.module.impl.fight;

import net.minecraft.entity.Entity;
import ru.minced.client.core.event.impl.player.EventTick;
import lombok.Getter;
import lombok.experimental.NonFinal;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.PostRotationMovementInputEvent;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.*;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.render.Renderer3D;
import ru.minced.client.util.rotation.TargetSelector;
import ru.minced.client.util.rotation.attack.*;
import ru.minced.client.util.rotation.point.*;
import ru.minced.client.util.rotation.rotation.*;
import ru.minced.client.util.rotation.rotation.angle.*;
import ru.minced.client.util.rotation.attack.AttackPerpetrator;
import ru.minced.client.util.rotation.attack.SprintManager;
import ru.minced.client.util.rotation.point.MultiPoint;
import ru.minced.client.util.rotation.point.PointFinderA;
import ru.minced.client.util.rotation.point.PointFinderB;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.AngleUtil;
import ru.minced.client.util.rotation.rotation.RotationConfig;
import ru.minced.client.util.rotation.rotation.RotationController;
import ru.minced.client.util.rotation.rotation.angle.AngleMode;
import ru.minced.client.util.rotation.rotation.angle.GrimMode;
import ru.minced.client.util.rotation.rotation.angle.SmoothMode;
import ru.minced.client.util.task.TaskPriority;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;

public class AttackAuraModule extends Module {

    private final TargetSelector targetSelector = new TargetSelector();
    private final PointFinderA pointFinderA = new PointFinderA();
    private final PointFinderB pointFinderB = new PointFinderB();
    private final MultiPoint multiPoint = new MultiPoint();

    @Getter
    @NonFinal
    LivingEntity target = null;

    @NonFinal
    private Vec3d currentAimPoint = null;

    @Getter
    private final SliderSetting maxDistanceSetting = new
            SliderSetting("Max Distance", 3, 1, 6, 0.1f);

    private final ModeListSetting targetTypes = new ModeListSetting("Targets",
            Arrays.stream(TargetSelector.TargetType.values())
                    .map(TargetSelector.TargetType::getDisplayName)
                    .toArray(String[]::new));

    private final ModeListSetting attackSetting = new ModeListSetting("Attack setting",
            "Raytrace check", "Dynamic cooldown", "Break shield", "Un press shield", "Check use");
    private final ModeSetting criticalMode = new ModeSetting("Critical mode", "None", "Only critical", "Adaptive");
    private final ModeSetting rotationMode = new ModeSetting("Rotation mode", "Grim", "Smooth", "Holyworld");
    private final ModeSetting moveCorrectionMode = new ModeSetting("Move correction", "None", "Free", "Direct");
    private final ModeSetting sprintMode = new ModeSetting("Sprint mode", "None", "Legit", "Packet");
    private final ModeSetting versionMode = new ModeSetting("Version", "1.8", "1.9");
    private final BooleanSetting showAimPoint = new BooleanSetting("Show aim point", false);
    private final BooleanSetting debug = new BooleanSetting("Debug", false);

    public AttackAuraModule() {
        super("Attack Aura", "Automatically attacks selected targets", Category.Fight);
        addSettings(maxDistanceSetting, targetTypes, attackSetting, criticalMode, rotationMode, moveCorrectionMode,
                sprintMode, versionMode, showAimPoint, debug);

        targetTypes.select("Armored Players");
        targetTypes.select("Unarmored Players");
        criticalMode.setSelected("Only critical");
        rotationMode.setSelected("Smooth");
        moveCorrectionMode.setSelected("Free");
        sprintMode.setSelected("Legit");
        versionMode.setSelected("1.9");
    }

    @Override
    public void onDisabled() {
        targetSelector.releaseTarget();
        target = null;
        currentAimPoint = null;
        super.onDisabled();
    }

    @EventHandler
    public void onPostRotationMovementInput(PostRotationMovementInputEvent eventPostRotationMovementInput) {
        target = updateTarget();
        if (target != null) {
            RotationController rotationController = RotationController.INSTANCE;
            Vec3d attackVector;

            attackVector = pointFinderA.computeVector(target, maxDistanceSetting.getValue(), rotationController.getRotation(),
                    getSmoothMode().randomValue());

            currentAimPoint = attackVector;

            assert mc.player != null;
            Angle angle = AngleUtil.fromVec3d(attackVector.subtract(mc.player.getEyePos()));
            rotateToTarget(target, new Angle.VecRotation(angle, attackVector), rotationController);
        } else {
            currentAimPoint = null;
        }
    }

    @EventHandler
    public void onTick(EventTick eventTick) {
        if (target != null) {
            attackTarget(target);
        } else {
            targetSelector.forceUpdate();
        }
    }

    @EventHandler
    public void onRender(EventWorld e) {
        if (showAimPoint.isState() && currentAimPoint != null) {
            renderAimPoint(e.getStack());
        }
    }

    private void renderAimPoint(MatrixStack matrixStack) {
        if (currentAimPoint == null) return;

        double boxSize = 0.1;
        Box box = new Box(
                currentAimPoint.x - boxSize / 2,
                currentAimPoint.y - boxSize / 2,
                currentAimPoint.z - boxSize / 2,
                currentAimPoint.x + boxSize / 2,
                currentAimPoint.y + boxSize / 2,
                currentAimPoint.z + boxSize / 2
        );

        Renderer3D.drawBox(box, matrixStack, new Color(255, 0, 0, 100), new Color(255, 0, 0, 255));
    }

    private LivingEntity updateTarget() {
        EnumSet<TargetSelector.TargetType> selectedTypes = EnumSet.noneOf(TargetSelector.TargetType.class);
        for (String selected : targetTypes.getSelected()) {
            for (TargetSelector.TargetType type : TargetSelector.TargetType.values()) {
                if (type.getDisplayName().equals(selected)) {
                    selectedTypes.add(type);
                    break;
                }
            }
        }

        return targetSelector.updateTarget(selectedTypes, maxDistanceSetting.getValue(), TargetSelector.TargetSorting.DISTANCE);
    }

    private void attackTarget(LivingEntity target) {
        AttackPerpetrator attackPerpetrator = Minced.getInstance().getAttackPerpetrator();

        AttackPerpetrator.AttackPerpetratorConfigurable configurable = new AttackPerpetrator.AttackPerpetratorConfigurable(
                target,
                RotationController.INSTANCE.getServerAngle(),
                maxDistanceSetting.getValue(),
                attackSetting.getSelected(),
                getSprintMode()
        );

        configurable.setOnlyCritical(criticalMode.isSelected("Only critical"));
        configurable.setAdaptiveCritical(criticalMode.isSelected("Adaptive"));
        configurable.setVersion1_8(versionMode.isSelected("1.8"));

        attackPerpetrator.performAttack(configurable);
    }

    private void rotateToTarget(LivingEntity target, Angle.VecRotation rotation, RotationController rotationController) {
        RotationConfig configurable = new RotationConfig(getSmoothMode(),
                debug.isState(),
                !moveCorrectionMode.isSelected("None"),
                moveCorrectionMode.isSelected("Free")
        );

        rotationController.rotateTo(rotation, target, configurable, TaskPriority.HIGH_IMPORTANCE_1, this);
    }

    public SprintManager.Mode getSprintMode() {
        switch (sprintMode.getSelected()) {
            case "Packet" -> {
                return SprintManager.Mode.PACKET;
            }
            case "Legit" -> {
                return SprintManager.Mode.LEGIT;
            }
        }
        return SprintManager.Mode.NONE;
    }
// porno
    public AngleMode getSmoothMode() {
        switch (rotationMode.getSelected()) {
            case "Grim" -> {
                return new GrimMode();
            }
            case "Smooth" -> {
                return new SmoothMode();
            }
            case "Holyworld" -> {
                return new HybridHolyInstantMode() {
                    @Override
                    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
                        return null;
                    }

                    @Override
                    public Vec3d randomValue() {
                        return null;
                    }
                };
            }
        }
        return null;
    }
}