package ru.minced.mixin.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.command.CommandManager;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.chat.EventChat;
import ru.minced.client.core.event.impl.player.ServerConnectEvent;
import ru.minced.client.core.event.impl.render.TitleEvent;
import ru.minced.client.util.network.ConnectionUtil;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessageHook(@NotNull String message, CallbackInfo ci) {
        if (message.startsWith(CommandManager.COMMAND_TARGET)) {
            try {
                Minced.getInstance().getCommandManager().getCommandDispatcher().execute(
                        message.substring(CommandManager.COMMAND_TARGET.length()),
                        Minced.getInstance().getCommandManager().getSource()
                );
            } catch (CommandSyntaxException ignored) {}

            ci.cancel();
        }
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(@NotNull String message, CallbackInfo ci) {
        EventChat event = new EventChat(message);
        EventManager.post(event);

        if (event.isStopped()) {
            ci.cancel();
        }
    }

    @Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        TitleEvent event = new TitleEvent(packet.text(), TitleEvent.Type.TITLE);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onSubtitle", at = @At("HEAD"), cancellable = true)
    private void onSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
        TitleEvent event = new TitleEvent(packet.text(), TitleEvent.Type.SUBTITLE);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void onOverlayMessage(OverlayMessageS2CPacket packet, CallbackInfo ci) {
        TitleEvent event = new TitleEvent(packet.text(), TitleEvent.Type.ACTIONBAR);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onTitleFade", at = @At("HEAD"), cancellable = true)
    private void onTitleFade(TitleFadeS2CPacket packet, CallbackInfo ci) {
        TitleEvent event = new TitleEvent(null, TitleEvent.Type.TIMES);
        EventManager.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        String serverAddress = ConnectionUtil.getFullServerAddress();
        ServerConnectEvent event = new ServerConnectEvent(serverAddress);
        EventManager.post(event);
    }
}
