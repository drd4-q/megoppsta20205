package ru.minced.client.feature.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.core.manager.friend.FriendsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerPlayerArgumentType implements ArgumentType<String>, IMinecraft {
    private static final Collection<String> EXAMPLES = List.of("Player1", "Player2", "Player3");

    public static ServerPlayerArgumentType create() {
        return new ServerPlayerArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> playerNames = new ArrayList<>();
        
        if (mc.getNetworkHandler() != null && mc.player != null) {
            String currentPlayerName = mc.player.getName().getString();

            for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                String playerName = entry.getProfile().getName();

                if (!FriendsManager.checkFriend(playerName) && !playerName.equals(currentPlayerName)) {
                    playerNames.add(playerName);
                }
            }
        }
        
        return CommandSource.suggestMatching(playerNames, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 