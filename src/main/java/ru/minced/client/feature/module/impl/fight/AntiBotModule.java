package ru.minced.client.feature.module.impl.fight;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.util.ILogger;
import ru.minced.client.util.IMinecraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class AntiBotModule extends Module implements IMinecraft, ILogger {
    private static final ModeSetting checks = new ModeSetting("Checks", "GameMode", "Matrix");
    private final BooleanSetting removeFromWorld = new BooleanSetting("Remove from world", false);
    private final Set<Integer> hiddenBotIds = new HashSet<>();

    public AntiBotModule() {
        super("Anti Bot", "Detects bots in the world", Category.Fight);
        addSettings(checks, removeFromWorld);

        checks.setSelected("GameMode");
    }

    private static Object getGameMode(PlayerListEntry entry) {
        try {
            return entry.getClass().getMethod("getGameMode").invoke(entry);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isBot(PlayerEntity player) {
        if (IMinecraft.nullCheck() || mc.getNetworkHandler() == null) return false;

        
        if (checks.isSelected("GameMode")) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
            return entry == null || getGameMode(entry) == null;
        }
        
        if (checks.isSelected("Matrix")) {
            boolean hasValidArmor = StreamSupport.stream(player.getArmorItems().spliterator(), false)
                    .allMatch(armorItem ->
                            !armorItem.isEmpty() &&
                                    armorItem.isEnchantable() &&
                                    !armorItem.isDamaged());

            boolean hasValidEquipment = player.getOffHandStack().isEmpty() &&
                    StreamSupport.stream(player.getArmorItems().spliterator(), false)
                            .anyMatch(armorItem ->
                                    armorItem.isOf(Items.LEATHER_BOOTS) ||
                                            armorItem.isOf(Items.LEATHER_LEGGINGS) ||
                                            armorItem.isOf(Items.LEATHER_CHESTPLATE) ||
                                            armorItem.isOf(Items.LEATHER_HELMET) ||
                                            armorItem.isOf(Items.IRON_BOOTS) ||
                                            armorItem.isOf(Items.IRON_LEGGINGS) ||
                                            armorItem.isOf(Items.IRON_CHESTPLATE) ||
                                            armorItem.isOf(Items.IRON_HELMET));

            boolean hasFullFood = player.getHungerManager().getFoodLevel() == 20;
            
            return hasValidArmor && hasValidEquipment && hasFullFood;
        }
        
        return false;
    }


    @EventHandler
    private void onUpdate(EventTick eventTick) {
        if (IMinecraft.nullCheck() || mc.world == null) return;
        
        if (removeFromWorld.isState()) {
            List<Entity> botsToRemove = new ArrayList<>();
            
            mc.world.getEntities().forEach(entity -> {
                if (entity instanceof PlayerEntity player && entity != mc.player) {
                    if (isBot(player)) {
                        botsToRemove.add(entity);
                    }
                }
            });
            
            botsToRemove.forEach(bot -> {
                hiddenBotIds.add(bot.getId());
                mc.world.removeEntity(bot.getId(), Entity.RemovalReason.DISCARDED);
            });
        } else {
            if (!hiddenBotIds.isEmpty()) {
                hiddenBotIds.clear();
            }
        }
    }
} 