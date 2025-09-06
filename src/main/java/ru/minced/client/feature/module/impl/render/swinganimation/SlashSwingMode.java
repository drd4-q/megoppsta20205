package ru.minced.client.feature.module.impl.render.swinganimation;

import ru.minced.client.core.Minced;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SlashSwingMode extends AbstractMode {
    public static AbstractMode INSTANCE = new SlashSwingMode();
    public SlashSwingMode() {
        super("Slash");
    }

    @Override
    public void apply(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm) {
        float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * MathHelper.PI);
        applyEquipOffset(matrices, arm, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((Minced.getInstance().getModuleManager().getViewModelModule().strength.get() * 6f) * g - 30f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110f));
    }
}