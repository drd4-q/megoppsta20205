package ru.minced.mixin.render;

import ru.minced.client.core.Minced;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Redirect(method = "getEntitiesToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"))
    private boolean hookFreeCamRenderPlayerFromAllPerspectives(LivingEntity instance) {
        if (Minced.getInstance().getModuleManager().getFreeCamModule().isState()) {
            return Minced.getInstance().getModuleManager().getFreeCamModule().shouldRenderPlayerModel(instance);
        }
        return instance.isSleeping();
    }
}