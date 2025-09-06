package ru.minced.client.feature.module.impl.render.swinganimation;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

public class ClassicSwingMode extends AbstractMode {
    public static AbstractMode INSTANCE = new ClassicSwingMode();
    public ClassicSwingMode() {
        super("Classic");
    }

    @Override
    public void apply(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm) {
        matrices.translate(0.56F, -0.52F, -0.72F);
        applySwingOffset(matrices, arm, swingProgress);
    }
}