package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.CategoryArgumentType;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandHeader(name = "panic", shortDesc = "Быстрое отключение модулей")
public class PanicCommand extends AbstractCommand {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            disableAllExceptVisuals();
            sendMessage("Все модули, кроме визуальных, отключены");
            return SINGLE_SUCCESS;
        });

        builder.then(argument("category", CategoryArgumentType.create()).executes(context -> {
            Category category = context.getArgument("category", Category.class);
            disableCategory(category);
            sendMessage(String.format("Все модули категории %s отключены", category.getDisplayName()));
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("all").executes(context -> {
            disableAll();
            sendMessage("Все модули отключены");
            return SINGLE_SUCCESS;
        }));
    }
    
    private void disableAllExceptVisuals() {
        ModuleManager.modules.stream()
                .filter(module -> module.isState() && module.getCategory() != Category.Visuals)
                .forEach(this::disableModule);
    }
    
    private void disableCategory(Category category) {
        ModuleManager.modules.stream()
                .filter(module -> module.isState() && module.getCategory() == category)
                .forEach(this::disableModule);
    }
    
    private void disableAll() {
        ModuleManager.modules.stream()
                .filter(Module::isState)
                .forEach(this::disableModule);
    }
    
    private void disableModule(Module module) {
        if (module.isState()) {
            module.toggle();
        }
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда для быстрого отключения модулей в случае опасности",
                "",
                "Использование:",
                "> panic - Отключает все модули, кроме визуальных",
                "> panic <категория> - Отключает все модули указанной категории",
                "> panic all - Отключает абсолютно все модули",
                "",
                "Доступные категории:",
                Arrays.stream(Category.values())
                        .map(category -> "> " + category.getDisplayName())
                        .collect(Collectors.joining("\n"))
        );
    }
} 