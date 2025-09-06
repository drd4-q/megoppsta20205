package ru.minced.client.feature.module.impl.render.targetesp;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.fight.AttackAuraModule;
import ru.minced.client.feature.module.impl.render.targetesp.mode.GhostsMode;
import ru.minced.client.feature.module.impl.render.targetesp.mode.JelloMode;
import ru.minced.client.feature.module.impl.render.targetesp.mode.MarkerMode;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.StopWatch;

public class TargetESPHandler implements IMinecraft {
    private static final double EXTENDED_RANGE = 10.0;
    private static final int FADE_OUT_TICKS = 100;
    
    private static LivingEntity lastTarget = null;
    private static final StopWatch targetLostTimer = new StopWatch();
    private static boolean isTargetOutOfRange = false;

    public static LivingEntity updateTargetInfo() {
        AttackAuraModule attackAura = Minced.getInstance().getModuleManager().getAttackAuraModule();

        if (attackAura == null || !attackAura.isState()) {
            lastTarget = null;
            return null;
        }
        
        LivingEntity currentTarget = attackAura.getTarget();

        if (currentTarget != null && currentTarget != lastTarget) {
            lastTarget = currentTarget;
            isTargetOutOfRange = false;
            targetLostTimer.reset();
        }

        if (lastTarget != null && lastTarget.isAlive()) {
            double distance = getDistanceTo(lastTarget);
            double attackRange = attackAura.getMaxDistanceSetting().getValue();

            if (distance <= attackRange) {
                isTargetOutOfRange = false;
                targetLostTimer.reset();
            }
            else if (distance <= EXTENDED_RANGE) {
                isTargetOutOfRange = false;
                targetLostTimer.reset();
            }
            else if (!isTargetOutOfRange) {
                isTargetOutOfRange = true;
                targetLostTimer.reset();
            }

            if (isTargetOutOfRange && targetLostTimer.hasElapsed(FADE_OUT_TICKS * 50)) {
                lastTarget = null;
            }
        }
        
        return lastTarget;
    }

    public static boolean shouldRenderESP(LivingEntity target) {
        return target != null && target != mc.player && target.isAlive() && target.getHealth() > 0 && mc.world != null;
    }

    public static void renderESP(String mode, MatrixStack matrixStack) {
        LivingEntity target = updateTargetInfo();
        
        if (shouldRenderESP(target)) {
            switch (mode) {
                case "Marker" -> MarkerMode.render(target, matrixStack);
                case "Ghosts" -> GhostsMode.render(target);
                case "Jello" -> JelloMode.render(target, matrixStack);
            }
        }
    }

    private static double getDistanceTo(LivingEntity entity) {
        if (IMinecraft.nullCheck()) return Double.MAX_VALUE;
        assert mc.player != null;
        return mc.player.getEyePos().distanceTo(entity.getEyePos());
    }
} 