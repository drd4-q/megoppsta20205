package ru.minced.client.feature.module.impl.render.swinganimation;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class DefaultSwingMode extends AbstractMode {
    public static AbstractMode INSTANCE = new DefaultSwingMode();
    public DefaultSwingMode() {
        super("Default");
    }

    @Override
    public void apply(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        float g = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
        float h = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        matrices.translate((float)armX * f, g, h);
        applyEquipOffset(matrices, arm, equipProgress);
        applySwingOffset(matrices, arm, swingProgress);
    }
}