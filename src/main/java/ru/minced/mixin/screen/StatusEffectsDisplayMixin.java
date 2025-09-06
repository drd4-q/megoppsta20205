package ru.minced.mixin.screen;

import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.module.impl.client.DisplayModule;

@Mixin(StatusEffectsDisplay.class)
public class StatusEffectsDisplayMixin {

    @Inject(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At("HEAD"), cancellable = true)
    private void cancelEffectsRendering(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        DisplayModule module = Minced.getInstance().getModuleManager().getDisplayModule();
        if (module != null && module.isState() && DisplayModule.elements.isSelected("Potions")) {
            ci.cancel();
        }
    }
}