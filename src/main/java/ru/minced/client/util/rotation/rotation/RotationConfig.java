package ru.minced.client.util.rotation.rotation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.util.rotation.rotation.angle.AngleMode;
import ru.minced.client.util.rotation.rotation.angle.LinearMode;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RotationConfig {
    public static RotationConfig DEFAULT = new RotationConfig(new LinearMode(),
            false, true, true);
    AngleMode angleSmooth;
    float resetThreshold = 2f;
    int ticksUntilReset = 5;
    boolean changeView, moveCorrection, freeCorrection;

    public RotationConfig(boolean changeView, boolean moveCorrection, boolean freeCorrection) {
        this(new LinearMode(), changeView, moveCorrection, freeCorrection);
    }

    public RotationConfig(boolean changeView, boolean moveCorrection) {
        this(new LinearMode(), changeView, moveCorrection, true);
    }

    public RotationConfig(boolean changeView) {
        this(new LinearMode(), changeView, true, true);
    }

    public RotationConfig(AngleMode angleSmooth, boolean changeView, boolean moveCorrection, boolean freeCorrection) {
        this.angleSmooth = angleSmooth;
        this.changeView = changeView;
        this.moveCorrection = moveCorrection;
        this.freeCorrection = freeCorrection;
    }

    public RotationPlan createRotationPlan(Angle angle, Vec3d vec, Entity entity) {
        return new RotationPlan(angle, vec, entity, angleSmooth, ticksUntilReset, resetThreshold, changeView, moveCorrection, freeCorrection);
    }

    public RotationPlan createRotationPlan(Angle angle) {
        return new RotationPlan(angle, null, null, angleSmooth, ticksUntilReset, resetThreshold, changeView, moveCorrection, freeCorrection);
    }

    public RotationPlan createRotationPlan(Angle angle, Vec3d vec, Entity entity, boolean changeLook, boolean moveCorrection, boolean freeCorrection) {
        return new RotationPlan(angle, vec, entity, angleSmooth, ticksUntilReset, resetThreshold, changeLook, moveCorrection, freeCorrection);
    }
}