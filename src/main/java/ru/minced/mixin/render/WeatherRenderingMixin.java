package ru.minced.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.world.AmbienceModule;

@Mixin(WeatherRendering.class)
public abstract class WeatherRenderingMixin {

    @ModifyExpressionValue(method = "addParticlesAndSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float modifyRainGradient(float original) {
        AmbienceModule ambienceModule = Minced.getInstance().getModuleManager().getAmbienceModule();
        if (ambienceModule.isState() && ambienceModule.getWeatherSetting().isSelected("Snow")) {
            return 0f;
        }
        return original;
    }

    @ModifyVariable(method = "renderPrecipitation*", at = @At(value = "STORE"), ordinal = 1)
    private static int modifyPrecipitationLayers(int original) {
        AmbienceModule ambienceModule = Minced.getInstance().getModuleManager().getAmbienceModule();
        if (ambienceModule.isState() && ambienceModule.getWeatherSetting().isSelected("Snow")) {
            return (int) ambienceModule.getSnowLayersSetting().getValue();
        }
        return original;
    }

    @ModifyExpressionValue(method = "renderPrecipitation*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getRainGradient(F)F"))
    private static float modifyPrecipitationGradient(float original) {
        AmbienceModule ambienceModule = Minced.getInstance().getModuleManager().getAmbienceModule();
        if (ambienceModule.isState() && ambienceModule.getWeatherSetting().isSelected("Snow")) {
            return ambienceModule.getSnowGradientSetting().getValue();
        }
        return original;
    }

    @ModifyReturnValue(method = "getPrecipitationAt", at = @At("RETURN"))
    private Biome.Precipitation modifyBiomePrecipitation(Biome.Precipitation original) {
        AmbienceModule ambienceModule = Minced.getInstance().getModuleManager().getAmbienceModule();
        if (ambienceModule.isState() && ambienceModule.getWeatherSetting().isSelected("Snow")) {
            return Biome.Precipitation.SNOW;
        }
        return original;
    }
}