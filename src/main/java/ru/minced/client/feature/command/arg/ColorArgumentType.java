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

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ColorArgumentType implements ArgumentType<Color> {
    private static final Collection<String> EXAMPLES = List.of("red", "blue", "#ff0000", "green");
    
    private static final Map<String, Color> NAMED_COLORS = new HashMap<>();
    
    static {
        NAMED_COLORS.put("black", Color.BLACK);
        NAMED_COLORS.put("blue", Color.BLUE);
        NAMED_COLORS.put("cyan", Color.CYAN);
        NAMED_COLORS.put("darkgray", Color.DARK_GRAY);
        NAMED_COLORS.put("gray", Color.GRAY);
        NAMED_COLORS.put("green", Color.GREEN);
        NAMED_COLORS.put("lightgray", Color.LIGHT_GRAY);
        NAMED_COLORS.put("magenta", Color.MAGENTA);
        NAMED_COLORS.put("orange", Color.ORANGE);
        NAMED_COLORS.put("pink", Color.PINK);
        NAMED_COLORS.put("red", Color.RED);
        NAMED_COLORS.put("white", Color.WHITE);
        NAMED_COLORS.put("yellow", Color.YELLOW);
    }

    public static ColorArgumentType create() {
        return new ColorArgumentType();
    }

    @Override
    public Color parse(StringReader reader) throws CommandSyntaxException {
        String colorStr = reader.readString().toLowerCase();

        if (NAMED_COLORS.containsKey(colorStr)) {
            return NAMED_COLORS.get(colorStr);
        }

        if (colorStr.startsWith("#")) {
            try {
                return Color.decode(colorStr);
            } catch (NumberFormatException e) {
                throw new DynamicCommandExceptionType(
                        name -> Text.literal("Неверный формат HEX цвета: " + name)
                ).create(colorStr);
            }
        }
        
        try {
            return Color.decode("#" + colorStr);
        } catch (NumberFormatException e) {
            throw new DynamicCommandExceptionType(
                    name -> Text.literal("Неверный формат цвета: " + name)
            ).create(colorStr);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(NAMED_COLORS.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 