package ru.minced.mixin.render;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.render.NoRenderModule;
import ru.minced.client.util.IMinecraft;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z, 
                              double velocityX, double velocityY, double velocityZ, 
                              CallbackInfoReturnable<Particle> cir) {
        if (IMinecraft.nullCheck()) return;

        NoRenderModule noRenderModule = Minced.getInstance().getModuleManager().getNoRenderModule();
        
        if (noRenderModule != null && noRenderModule.isState() && noRenderModule.noRender.isSelected("Totem")) {
            if (parameters.getType() == ParticleTypes.TOTEM_OF_UNDYING ||
                parameters.getType() == ParticleTypes.ENCHANTED_HIT) {
                cir.setReturnValue(null);
            }
        }
    }
}