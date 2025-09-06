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
import ru.minced.client.core.manager.gps.GPS;
import ru.minced.client.core.manager.gps.GPSManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GPSPointArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = List.of("home", "base", "spawn");

    public static GPSPointArgumentType create() {
        return new GPSPointArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String pointName = reader.readString();
        
        if (!GPSManager.hasPoint(pointName)) {
            throw new DynamicCommandExceptionType(
                    name -> Text.literal("Точка с именем " + name + " не существует")
            ).create(pointName);
        }
        
        return pointName;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> pointNames = GPSManager.getPoints().stream()
                .map(GPS::getName)
                .collect(Collectors.toList());
        
        return CommandSource.suggestMatching(pointNames, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 