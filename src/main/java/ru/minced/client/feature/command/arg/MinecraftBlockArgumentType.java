package ru.minced.client.feature.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MinecraftBlockArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = List.of("minecraft:diamond_ore", "minecraft:gold_ore", "minecraft:stone");

    public static MinecraftBlockArgumentType create() {
        return new MinecraftBlockArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> blockIds = Registries.BLOCK.getIds().stream()
                .map(Identifier::toString)
                .collect(Collectors.toList());
        
        return CommandSource.suggestMatching(blockIds, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 