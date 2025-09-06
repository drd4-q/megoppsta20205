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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModuleWithoutBindArgumentType implements ArgumentType<Module> {
    private static final Collection<String> EXAMPLES = ModuleManager.modules.stream()
            .filter(module -> module.getKey() == -1 || module.getKey() == 0)
            .map(Module::getName)
            .limit(3)
            .collect(Collectors.toList());

    public static ModuleWithoutBindArgumentType create() {
        return new ModuleWithoutBindArgumentType();
    }

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        String moduleName = reader.readString();
        Module module = ModuleManager.get(moduleName);
        
        if (module == null) {
            throw new DynamicCommandExceptionType(
                    name -> Text.literal("Модуль " + name + " не существует")
            ).create(moduleName);
        }
        
        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> moduleNames = ModuleManager.modules.stream()
                .filter(module -> module.getKey() == -1 || module.getKey() == 0)
                .map(Module::getName)
                .collect(Collectors.toList());
        
        return CommandSource.suggestMatching(moduleNames, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 