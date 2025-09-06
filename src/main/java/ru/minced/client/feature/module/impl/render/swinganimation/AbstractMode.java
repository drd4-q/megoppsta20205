package ru.minced.client.feature.module.impl.render.swinganimation;

import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.setting.impl.mode.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public abstract class AbstractMode extends Mode {
    public AbstractMode(String name) {
        super(name);
    }

    public abstract void apply(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm);

    public static void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float) i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
    }

    public static void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * (45.0F + f * -20.0F)));
        float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        float strength = Minced.getInstance().getModuleManager().getViewModelModule().strength.get();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) i * g * (-2.0F * strength)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * (-8.0F * strength)));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * -45.0F));
    }
}