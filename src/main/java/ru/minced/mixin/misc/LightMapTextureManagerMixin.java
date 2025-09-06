package ru.minced.mixin.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.render.NightVisionModule;

@Mixin(LightmapTextureManager.class)
public class LightMapTextureManagerMixin {
    
    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    private Object redirectGammaGetValue(SimpleOption<Double> option) {
        if (option == this.client.options.getGamma()) {
            NightVisionModule nightVisionModule = Minced.getInstance().getModuleManager().getNightVisionModule();
            if (nightVisionModule != null && nightVisionModule.isGammaMode()) {
                return 100.0;
            }
        }

        return option.getValue();
    }
} 