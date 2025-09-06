package ru.minced.client.feature.module.impl.render;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;

public class NightVisionModule extends Module {
    
    public ModeSetting modeSetting = new ModeSetting("Режим", "Effect", "Gamma");
    private double previousGamma = 0.5;
    
    public NightVisionModule() {
        super("Night Vision", "Улучшает видимость в темноте", Category.Visuals);
        addSettings(modeSetting);
    }
    
    @EventHandler
    public void onTick(EventTick e) {
        if (mc == null || mc.player == null) return;
        
        if (modeSetting.isSelected("Effect")) {
            StatusEffectInstance nightVision = new StatusEffectInstance(
                StatusEffects.NIGHT_VISION,
                Integer.MAX_VALUE, 0, false, false, false);
            mc.player.addStatusEffect(nightVision);
        }
    }
    
    @Override
    public void onEnabled() {
        if (modeSetting.isSelected("Gamma") && mc.options != null) {
            previousGamma = mc.options.getGamma().getValue();
        }
        super.onEnabled();
    }
    
    @Override
    public void onDisabled() {
        if (modeSetting.isSelected("Gamma") && mc.options != null) {
            mc.options.getGamma().setValue(previousGamma);
        }
        
        if (modeSetting.isSelected("Effect") && mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
        super.onDisabled();
    }
    
    public boolean isGammaMode() {
        return isState() && modeSetting.isSelected("Gamma");
    }
}