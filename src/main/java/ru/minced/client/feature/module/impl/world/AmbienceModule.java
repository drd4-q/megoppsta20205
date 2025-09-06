package ru.minced.client.feature.module.impl.world;

import lombok.Getter;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;

public class AmbienceModule extends Module {

    public BooleanSetting changeWeatherSetting = new BooleanSetting("Изменять время", true);
    public SliderSetting timeSetting = new SliderSetting("Время", 12000.0f, 0.0f, 24000.0f, 200.0f, () -> changeWeatherSetting.isState());
    @Getter ModeSetting weatherSetting = new ModeSetting("Погода", "Clear", "Rain", "Thunder", "Snow");
    @Getter SliderSetting snowLayersSetting = new SliderSetting("Слои снега", 3.0f, 3.0f, 14.0f, 1.0f, () -> weatherSetting.isSelected("Snow"));
    @Getter SliderSetting snowGradientSetting = new SliderSetting("Интенсивность снега", 0.7f, 0.2f, 1.0f, 0.1f, () -> weatherSetting.isSelected("Snow"));

    @Getter BooleanSetting customFogSetting = new BooleanSetting("Кастомный туман", false);
    @Getter SliderSetting fogDistanceSetting = new SliderSetting("Дальность тумана", 10.0f, 0.0f, 500.0f, 0.5f, () -> customFogSetting.isState());
    @Getter ModeSetting fogShapeSetting = new ModeSetting("Тип тумана", () -> customFogSetting.isState(), "SPHERE", "CYLINDER");

    public AmbienceModule(){
        super("Ambience","Позволяет изменять погоду в мире", Category.World);
        addSettings(changeWeatherSetting, timeSetting, weatherSetting, snowLayersSetting,
                snowGradientSetting, customFogSetting, fogDistanceSetting, fogShapeSetting);
    }

    @EventHandler
    public void onPacket(PacketEvent e){
        if (IMinecraft.nullCheck()) return;

        if (e.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            boolean shouldModifyWeather = !weatherSetting.getSelected().equals("Clear");

            if (changeWeatherSetting.isState() || shouldModifyWeather) {
                e.stop();

                if (changeWeatherSetting.isState()) {
                    assert mc.world != null;
                    mc.world.setTime((long) timeSetting.getValue(), (long) timeSetting.getValue(), false);
                }

                switch (weatherSetting.getSelected()) {
                    case "Clear" -> {
                        assert mc.world != null;
                        mc.world.setRainGradient(0);
                        mc.world.setThunderGradient(0);
                    }
                    case "Rain" -> {
                        assert mc.world != null;
                        mc.world.setRainGradient(1);
                        mc.world.setThunderGradient(0);
                    }
                    case "Thunder" -> {
                        assert mc.world != null;
                        mc.world.setRainGradient(1);
                        mc.world.setThunderGradient(1);
                    }
                    case "Snow" -> {
                        assert mc.world != null;
                        mc.world.setRainGradient(snowGradientSetting.getValue());
                        mc.world.setThunderGradient(0);
                    }
                }
            }
        }
    }
}