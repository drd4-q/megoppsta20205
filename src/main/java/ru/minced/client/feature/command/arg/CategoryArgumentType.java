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
import ru.minced.client.feature.module.Category;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CategoryArgumentType implements ArgumentType<Category> {
    private static final Collection<String> EXAMPLES = Arrays.stream(Category.values())
            .map(Category::getDisplayName)
            .limit(3)
            .collect(Collectors.toList());

    public static CategoryArgumentType create() {
        return new CategoryArgumentType();
    }

    @Override
    public Category parse(StringReader reader) throws CommandSyntaxException {
        String categoryName = reader.readString();
        
        for (Category category : Category.values()) {
            if (category.getDisplayName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        
        throw new DynamicCommandExceptionType(
                name -> Text.literal("Категория " + name + " не существует")
        ).create(categoryName);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestions = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());

        suggestions.add("all");
        
        return CommandSource.suggestMatching(suggestions, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 