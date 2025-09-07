package ru.minced.client.util.rotation.attack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AttackUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Можно ли сейчас ударить (полный кулдаун атаки).
     */
    public static boolean canAttackNow(PlayerEntity player) {
        if (player == null) return false;
        // >= 0.9f — как в Zenith
        return player.getAttackCooldownProgress(1) >= 0.9f;
    }

    /**
     * Пре-атака (подготовка удара).
     * В Zenith использовался SimulatedPlayer, здесь упростим.
     */
    public static boolean canPreAttack(PlayerEntity player) {
        if (player == null) return false;
        // Здесь можно добавить "легитные" условия
        return player.getAttackCooldownProgress(1) >= 0.9f;
    }

    /**
     * Проверка — есть ли ограничения движения (блок, еда и т.д.)
     */
    public static boolean hasMovementRestrictions(PlayerEntity player) {
        if (player == null) return false;
        // Если ест, пьёт или блокирует щитом → движение ограничено
        return player.isUsingItem();
    }

    /**
     * Проверка — находится ли игрок в "критическом состоянии"
     * (во время прыжка).
     */
    public static boolean isPlayerInCriticalState(PlayerEntity player) {
        if (player == null) return false;
        return !player.isOnGround() && !player.isTouchingWater() && !player.hasVehicle();
    }
}
