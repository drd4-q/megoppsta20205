package ru.minced.mixin.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.screen.EventDeathScreen;
import ru.minced.client.feature.module.impl.client.DeathScreenModule;
import ru.minced.client.util.IMinecraft;

@Mixin(DeathScreen.class)
public class DeathScreenMixin implements IMinecraft {
    @Shadow
    private int ticksSinceDeath;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        EventManager.post(new EventDeathScreen(ticksSinceDeath));
    }
    
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void onRenderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        DeathScreenModule deathScreenModule = Minced.getInstance().getModuleManager().getDeathScreenModule();
        
        if (deathScreenModule.isState() && DeathScreenModule.changeBackground.isState()) {
            context.fillGradient(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 
                    0x80000000, 0x90000000);
            ci.cancel();
        }
    }
}
