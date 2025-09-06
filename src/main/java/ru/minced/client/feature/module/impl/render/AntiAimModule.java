package ru.minced.client.feature.module.impl.render;

import lombok.Getter;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class AntiAimModule extends Module implements IMinecraft {

    private final ModeSetting modeSetting = new ModeSetting("Mode", "Static", "Spin", "OTC v4", "skeet.cc");
    private final SliderSetting staticYawSetting = new SliderSetting("Static Yaw", 0f, -180f, 180f, 0.1f, 
            () -> modeSetting.isSelected("Static"));
    private final SliderSetting staticPitchSetting = new SliderSetting("Static Pitch", 0f, -90f, 90f, 0.1f,
            () -> modeSetting.isSelected("Static"));
    
    private final SliderSetting spinSpeedSetting = new SliderSetting("Spin Speed", 10f, -180f, 180f, 0.1f,
            () -> modeSetting.isSelected("Spin"));
    private final SliderSetting spinPitchSetting = new SliderSetting("Spin Pitch", 90f, -90f, 90f, 1f,
            () -> modeSetting.isSelected("Spin"));
    
    private final SliderSetting otcSpeedSetting = new SliderSetting("OTC Speed", 5f, 1f, 40f, 1f,
            () -> modeSetting.isSelected("OTC v4"));
    private final SliderSetting otcAmountSetting = new SliderSetting("OTC Amount", 30f, 0f, 360f, 1f,
            () -> modeSetting.isSelected("OTC v4"));
    
    private final SliderSetting skeetTickSetting = new SliderSetting("skeet.cc ticks", 8f, 1f, 15f, 1f,
            () -> modeSetting.isSelected("skeet.cc"));
    
    private final ModeListSetting targetSetting = new ModeListSetting("Target", "Head", "Body");

    @Getter
    private float localYaw = 0;
    @Getter
    private float localPitch = 0;
    
    private double rot = 0;
    private int tickCounter = 0;
    private long lastUpdateTime = 0;
    
    private final float[] yaws = {0f, 75f, 270f, 140f, 50f, 330f};
    private final float[] pitches = {90f, 80f, 90f, 80f, 90f, 80f, 10f};

    public AntiAimModule() {
        super("Anti Aim", "Visually changes head and body rotations (by zamorozka)", Category.Visuals);
        addSettings(
            modeSetting, targetSetting, staticYawSetting, staticPitchSetting, spinSpeedSetting,
            spinPitchSetting, otcSpeedSetting, otcAmountSetting, skeetTickSetting
        );
    }

    @EventHandler
    public void onUpdate(EventTick e) {
        if (!isState()) return;
        
        Random rnd = ThreadLocalRandom.current();
        
        if (modeSetting.isSelected("Static")) {
            localYaw = staticYawSetting.getValue();
            localPitch = staticPitchSetting.getValue();
        } else if (modeSetting.isSelected("Spin")) {
            rot += spinSpeedSetting.get();
            localYaw = (float) rot;
            localPitch = spinPitchSetting.getValue();
        } else if (modeSetting.isSelected("OTC v4")) {
            long currentTime = System.currentTimeMillis();
            int updateInterval = (int) (1000 / otcSpeedSetting.get());
            
            if (currentTime - lastUpdateTime > updateInterval) {
                lastUpdateTime = currentTime;

                localYaw = (Objects.requireNonNull(mc.player).getYaw() - (rnd.nextFloat() * otcAmountSetting.getValue()) - 180 + otcAmountSetting.getValue() / 2);
                localPitch = 90 - (rnd.nextFloat() * 20) + 10;
            }
        } else if (modeSetting.isSelected("skeet.cc")) {
            tickCounter++;
            if (tickCounter % (int)skeetTickSetting.getValue() == 0) {
                localYaw = yaws[rnd.nextInt(yaws.length)];
                localPitch = pitches[rnd.nextInt(pitches.length)];
            }
        }
    }
    
    public boolean shouldAffectHead() {
        return isState() && targetSetting.isSelected("Head");
    }
    
    public boolean shouldAffectBody() {
        return isState() && targetSetting.isSelected("Body");
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        rot = 0;
        tickCounter = 0;
    }
}