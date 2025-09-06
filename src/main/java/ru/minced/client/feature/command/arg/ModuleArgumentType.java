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
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ModuleArgumentType implements ArgumentType<Module> {
    private static final Collection<String> EXAMPLES = ModuleManager.modules.stream()
            .map(Module::getName)
            .limit(5)
            .toList();

    public static ModuleArgumentType create() {
        return new ModuleArgumentType();
    }

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        Module module = ModuleManager.get(reader.readString());
        if (module == null) throw new DynamicCommandExceptionType(
                name -> Text.literal("Фукнции " + name.toString() + " не существует")
        ).create(reader.readString());

        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(ModuleManager.modules.stream().map(Module::getName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
