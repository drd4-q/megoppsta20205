package ru.minced.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.api.EventType;
import ru.minced.client.core.event.impl.container.EventClickSlot;
import ru.minced.client.core.event.impl.player.EventAttack;
import ru.minced.client.core.event.impl.player.EventInteractBlock;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.rotation.rotation.Angle;
import ru.minced.client.util.rotation.rotation.RotationController;
import ru.minced.mixin.accessor.ClientPlayerInteractionManagerAccessor;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow protected abstract void syncSelectedSlot();
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void attackEntityPre(PlayerEntity player, Entity target, CallbackInfo callbackInfo) {
        EventAttack event = new EventAttack(
                target,
                EventType.PRE
        );
        EventManager.post(event);
        if (event.isStopped()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    public void injectInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.isSpectator()) return;

        this.syncSelectedSlot();

        MutableObject<ActionResult> result = new MutableObject<>();

        ((ClientPlayerInteractionManagerAccessor) (Object) this).callSendSequencedPacket(
                (ClientWorld) this.client.world,
                sequence -> {
                    Angle angle = RotationController.INSTANCE.getCurrentAngle();

                    float yaw = angle != null ? angle.getYaw() : player.getYaw();
                    float pitch = angle != null ? angle.getPitch() : player.getPitch();

                    PlayerInteractItemC2SPacket packet = new PlayerInteractItemC2SPacket(hand, sequence, yaw, pitch);
                    ItemStack stack = player.getStackInHand(hand);

                    if (player.getItemCooldownManager().isCoolingDown(stack)) {
                        result.setValue(ActionResult.PASS);
                        return packet;
                    }

                    ActionResult actionResult = stack.use(this.client.world, player, hand);
                    ItemStack newStack;

                    if (actionResult instanceof ActionResult.Success success) {
                        newStack = Objects.requireNonNullElseGet(success.getNewHandStack(), () -> stack);
                    } else {
                        newStack = stack;
                    }

                    if (newStack != stack) {
                        player.setStackInHand(hand, newStack);
                    }

                    result.setValue(actionResult);
                    return packet;
                });

        cir.setReturnValue(result.getValue());
    }

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    public void clickSlotHook(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (IMinecraft.nullCheck()) return;
        EventClickSlot event = new EventClickSlot(actionType, slotId, button, syncId);
        EventManager.post(event);
        if (event.isCancel()) {
            ci.cancel();
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (player.isSpectator()) return;

        ItemStack stack = player.getStackInHand(hand);
        EventInteractBlock event = new EventInteractBlock(hitResult.getBlockPos(), stack);
        EventManager.post(event);

        if (event.isStopped()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
