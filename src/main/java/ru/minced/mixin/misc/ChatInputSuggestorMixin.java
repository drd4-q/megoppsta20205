package ru.minced.mixin.misc;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.command.CommandManager;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Final
    @Shadow
    TextFieldWidget textField;
    @Shadow boolean completingSuggestions;
    @Shadow private ParseResults<CommandSource> parse;
    @Shadow private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow private ChatInputSuggestor.SuggestionWindow window;

    @Shadow protected abstract void showCommandSuggestions();

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRefresh(CallbackInfo ci, String string, StringReader reader) {
        if (reader.canRead(CommandManager.COMMAND_TARGET.length()) && reader.getString().startsWith(CommandManager.COMMAND_TARGET, reader.getCursor())) {
            reader.setCursor(reader.getCursor() + 1);

            if (parse == null)
                parse = Minced.getInstance().getCommandManager().getCommandDispatcher().parse(reader, Minced.getInstance().getCommandManager().getSource());

            final int cursor = textField.getCursor();

            if (cursor >= 1 && (window == null || !completingSuggestions)) {
                pendingSuggestions = Minced.getInstance().getCommandManager().getCommandDispatcher().getCompletionSuggestions(parse, cursor);
                pendingSuggestions.thenRun(() -> {
                    if (pendingSuggestions.isDone()) showCommandSuggestions();
                });
            }

            ci.cancel();
        }
    }
}
