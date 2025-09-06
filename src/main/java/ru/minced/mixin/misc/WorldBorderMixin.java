package ru.minced.mixin.misc;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.player.EventWorldBorder;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {

    @Inject(method = "canCollide", at = @At("HEAD"), cancellable = true)
    public void onCanCollide(Entity entity, Box box, CallbackInfoReturnable<Boolean> cir) {
        EventWorldBorder eventWorldBorder = new EventWorldBorder();
        EventManager.post(eventWorldBorder);
        if (eventWorldBorder.isStopped()) {
            cir.setReturnValue(false);
        }
    }
} 