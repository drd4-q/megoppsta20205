package ru.minced.client.feature.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import ru.minced.client.core.manager.friend.Friends;
import ru.minced.client.core.manager.friend.FriendsManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FriendArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = FriendsManager.getFriends().stream()
            .map(Friends::getName)
            .limit(3)
            .collect(Collectors.toList());

    public static FriendArgumentType create() {
        return new FriendArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String friendName = reader.readString();
        if (!FriendsManager.checkFriend(friendName)) {
            throw new DynamicCommandExceptionType(
                    name -> Text.literal("Игрок " + name + " не найден в списке друзей")
            ).create(friendName);
        }
        return friendName;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> friends = FriendsManager.getFriends().stream()
                .map(Friends::getName)
                .collect(Collectors.toList());
        
        return CommandSource.suggestMatching(friends, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 