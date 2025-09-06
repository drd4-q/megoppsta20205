package ru.minced.client.feature.module.impl.miscellaneous;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.util.IMinecraft;

import java.util.List;

public class EffectCancelModule extends Module implements IMinecraft {

    ModeListSetting effectSetting = new ModeListSetting("Effects", "Jump boost", "Blindness", "Slow falling", "Levitation", "Nausea", "Darkness");

    public EffectCancelModule() {
        super("Effect Cancel", "Cancels the selected effects", Category.Miscellaneous);
        addSettings(effectSetting);
    }

    @EventHandler
    public void removeEffects(EventTick eventTick) {
        assert mc.player != null;
        List<StatusEffectInstance> activeEffects = mc.player.getStatusEffects().stream().toList();

        for (StatusEffectInstance effect : activeEffects) {
            if (effectSetting.isSelected("Jump boost") && effect.getEffectType() == StatusEffects.JUMP_BOOST) {
                mc.player.removeStatusEffect(StatusEffects.JUMP_BOOST);
            }
            if (effectSetting.isSelected("Blindness") && effect.getEffectType() == StatusEffects.BLINDNESS) {
                mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
            }
            if (effectSetting.isSelected("Slow falling") && effect.getEffectType() == StatusEffects.SLOW_FALLING) {
                mc.player.removeStatusEffect(StatusEffects.SLOW_FALLING);
            }
            if (effectSetting.isSelected("Levitation") && effect.getEffectType() == StatusEffects.LEVITATION) {
                mc.player.removeStatusEffect(StatusEffects.LEVITATION);
            }
            if (effectSetting.isSelected("Nausea") && effect.getEffectType() == StatusEffects.NAUSEA) {
                mc.player.removeStatusEffect(StatusEffects.NAUSEA);
            }
            if (effectSetting.isSelected("Darkness") && effect.getEffectType() == StatusEffects.DARKNESS) {
                mc.player.removeStatusEffect(StatusEffects.DARKNESS);
            }
        }
    }
}
