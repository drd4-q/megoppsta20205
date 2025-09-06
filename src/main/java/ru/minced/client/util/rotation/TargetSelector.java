package ru.minced.client.util.rotation;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.core.manager.friend.FriendsManager;
import ru.minced.client.feature.module.impl.fight.AntiBotModule;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class TargetSelector implements IMinecraft {
    private LivingEntity currentTarget;
    private List<LivingEntity> potentialTargets;
    private boolean needsUpdate;
    private long lastTargetChangeTime;

    public enum TargetType {
        UNARMORED_PLAYERS("Unarmored Players"),
        ARMORED_PLAYERS("Armored Players"),
        INVISIBLE_PLAYERS("Invisible Players"),
        HOSTILE_MOBS("Hostile Mobs"),
        ANIMALS("Animals"),
        VILLAGERS("Villagers"),
        GOLEMS("Golems"),
        PHANTOMS("Phantoms"),
        ARMOR_STANDS("Armor Stands"),
        FRIENDS("Friends");

        @Getter
        private final String displayName;

        TargetType(String displayName) {
            this.displayName = displayName;
        }
    }

    public enum TargetSorting {
        DISTANCE("Distance"),
        HEALTH("Health"),
        NONE("None");

        @Getter
        private final String displayName;

        TargetSorting(String displayName) {
            this.displayName = displayName;
        }
    }

    public TargetSelector() {
        this.currentTarget = null;
        this.potentialTargets = new ArrayList<>();
        this.needsUpdate = true;
        this.lastTargetChangeTime = System.currentTimeMillis();
    }

    public void searchTargets(List<LivingEntity> entities, float maxDistance) {
        if (!isValidTarget(currentTarget, Collections.emptyList(), maxDistance)) {
            releaseTarget();
        }

        potentialTargets = entities.stream()
                .filter(entity -> entity != mc.player && entity.isAlive() && entity.getHealth() > 0)
                .filter(entity -> getDistanceTo(entity) <= maxDistance)
                .collect(Collectors.toList());

        needsUpdate = false;
    }

    public void searchTargetsInRadius(float maxDistance) {
        if (IMinecraft.nullCheck()) return;

        assert mc.player != null;
        Vec3d playerPos = mc.player.getPos();
        Box searchBox = new Box(
                playerPos.x - maxDistance, playerPos.y - maxDistance, playerPos.z - maxDistance,
                playerPos.x + maxDistance, playerPos.y + maxDistance, playerPos.z + maxDistance
        );

        assert mc.world != null;
        List<LivingEntity> entities = mc.world.getEntitiesByClass(LivingEntity.class, searchBox, entity -> true);
        searchTargets(entities, maxDistance);
    }

    public Optional<LivingEntity> findTarget(Collection<TargetType> targetTypes, TargetSorting sorting) {
        Stream<LivingEntity> filteredTargets = potentialTargets.stream()
                .filter(entity -> isValidTarget(entity, targetTypes, Float.MAX_VALUE));

        return switch (sorting) {
            case DISTANCE -> filteredTargets.min(Comparator.comparingDouble(this::getDistanceTo));
            case HEALTH -> filteredTargets.min(Comparator.comparingDouble(LivingEntity::getHealth));
            case NONE -> filteredTargets.findFirst();
        };
    }

    public void lockTarget(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        if (this.currentTarget != target && currentTime - lastTargetChangeTime >= 500) {
            this.currentTarget = target;
            this.lastTargetChangeTime = currentTime;
        }
    }

    public void releaseTarget() {
        this.currentTarget = null;
    }

    public void validateTarget(Collection<TargetType> targetTypes, float maxDistance) {
        if (currentTarget != null && !isValidTarget(currentTarget, targetTypes, maxDistance)) {
            releaseTarget();
            needsUpdate = true;
        }
    }

    public LivingEntity updateTarget(Collection<TargetType> targetTypes, float maxDistance, TargetSorting sorting) {
        if (potentialTargets.isEmpty() || needsUpdate || !isValidTarget(currentTarget, targetTypes, maxDistance)) {
            searchTargetsInRadius(maxDistance);
        }

        validateTarget(targetTypes, maxDistance);

        if (currentTarget == null) {
            findTarget(targetTypes, sorting).ifPresent(this::lockTarget);
        }

        return currentTarget;
    }

    public LivingEntity updateTarget(Collection<TargetType> targetTypes, float maxDistance) {
        return updateTarget(targetTypes, maxDistance, TargetSorting.NONE);
    }

    public List<LivingEntity> getAllTargets(Collection<TargetType> targetTypes) {
        return potentialTargets.stream()
                .filter(entity -> isValidTarget(entity, targetTypes, Float.MAX_VALUE))
                .collect(Collectors.toList());
    }

    public boolean isValidTarget(LivingEntity entity, Collection<TargetType> targetTypes, float maxDistance) {
        if (entity == null || entity == mc.player) return false;
        if (!entity.isAlive() || entity.getHealth() <= 0) return false;
        if (getDistanceTo(entity) > maxDistance) return false;

        if (entity instanceof PlayerEntity player) {
            if (AntiBotModule.isBot(player)) {
                return false;
            }

            String playerName = player.getName().getString();
            boolean isFriend = FriendsManager.checkFriend(playerName);

            if (isFriend) {
                return targetTypes.contains(TargetType.FRIENDS);
            }

            boolean hasArmor = hasAnyArmor(player);
            boolean isInvisible = player.isInvisible();

            if (hasArmor && targetTypes.contains(TargetType.ARMORED_PLAYERS)) return true;
            if (isInvisible && targetTypes.contains(TargetType.INVISIBLE_PLAYERS)) return true;
            return !hasArmor && !isInvisible && targetTypes.contains(TargetType.UNARMORED_PLAYERS);
        } else {
            return switch (entity) {
                case HostileEntity ignored when targetTypes.contains(TargetType.HOSTILE_MOBS) -> true;
                case AnimalEntity ignored when targetTypes.contains(TargetType.ANIMALS) -> true;
                case ArmorStandEntity ignored when targetTypes.contains(TargetType.ARMOR_STANDS) -> true;
                case VillagerEntity ignored when targetTypes.contains(TargetType.VILLAGERS) -> true;
                case GolemEntity ignored when targetTypes.contains(TargetType.GOLEMS) -> true;
                default -> entity instanceof PhantomEntity && targetTypes.contains(TargetType.PHANTOMS);
            };
        }
    }

    private boolean hasAnyArmor(PlayerEntity player) {
        for (ItemStack item : player.getArmorItems()) {
            if (!item.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public double getDistanceTo(LivingEntity entity) {
        if (IMinecraft.nullCheck() || entity == null) return Double.MAX_VALUE;

        assert mc.player != null;
        Vec3d eyePos = mc.player.getEyePos();
        Box entityBox = entity.getBoundingBox();

        double nearestX = Math.max(entityBox.minX, Math.min(eyePos.x, entityBox.maxX));
        double nearestY = Math.max(entityBox.minY, Math.min(eyePos.y, entityBox.maxY));
        double nearestZ = Math.max(entityBox.minZ, Math.min(eyePos.z, entityBox.maxZ));

        return eyePos.distanceTo(new Vec3d(nearestX, nearestY, nearestZ));
    }

    public void forceUpdate() {
        this.needsUpdate = true;
    }
}