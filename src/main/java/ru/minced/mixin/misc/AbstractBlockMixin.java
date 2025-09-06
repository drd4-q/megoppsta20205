package ru.minced.mixin.misc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.player.EventBlockVision;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockMixin {

    @Inject(method = "shouldBlockVision", at = @At("HEAD"), cancellable = true)
    public void shouldBlockVision(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> infoReturnable) {
        EventBlockVision eventBlockVision = new EventBlockVision();
        EventManager.post(eventBlockVision);
        if (eventBlockVision.isStopped()) {
            infoReturnable.setReturnValue(false);
        }
    }
}
