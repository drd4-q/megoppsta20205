package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.KeyArgumentType;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.util.other.StringHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import static ru.minced.client.util.IMinecraft.logDirect;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@CommandHeader(name = "bind", shortDesc = "Управление биндами модулей")
public class BindCommand extends AbstractCommand {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        // --- add ---
        builder.then(literal("add").then(argument("module", StringArgumentType.greedyString())
                .then(argument("key", KeyArgumentType.create()).executes(context -> {
                    var inputName = context.getArgument("module", String.class);
                    var key = context.getArgument("key", Integer.class);

                    var moduleOpt = findModuleIgnoreCase(inputName);
                    if (moduleOpt.isEmpty()) {
                        sendMessage("Модуль с именем \"" + inputName + "\" не найден");
                        return SINGLE_SUCCESS;
                    }

                    var module = moduleOpt.get();
                    var oldBind = StringHelper.getBindName(module.getKey());
                    module.setKey(key);
                    var newBind = StringHelper.getBindName(key);

                    sendMessage(String.format("Для модуля %s бинд изменён: %s → %s",
                            module.getName(), oldBind, newBind));

                    saveBinds();
                    return SINGLE_SUCCESS;
                }))
        ));

        // --- remove/delete ---
        Command<CommandSource> removeExec = (CommandContext<CommandSource> context) -> {
            var inputName = context.getArgument("module", String.class);

            var moduleOpt = findModuleIgnoreCase(inputName);
            if (moduleOpt.isEmpty()) {
                sendMessage("Модуль с именем \"" + inputName + "\" не найден");
                return SINGLE_SUCCESS;
            }

            var module = moduleOpt.get();
            var oldBind = StringHelper.getBindName(module.getKey());
            module.setKey(-1);

            sendMessage(String.format("Для модуля %s удалён бинд: %s",
                    module.getName(), oldBind));

            saveBinds();
            return SINGLE_SUCCESS;
        };

        builder.then(literal("remove")
                .then(argument("module", StringArgumentType.greedyString()).executes(removeExec)));

        builder.then(literal("delete") // алиас remove
                .then(argument("module", StringArgumentType.greedyString()).executes(removeExec)));

        // --- clear ---
        builder.then(literal("clear").executes(context -> {
            int count = 0;
            for (var module : ModuleManager.modules) {
                if (module.getKey() != -1 && module.getKey() != 0) {
                    module.setKey(-1);
                    count++;
                }
            }

            sendMessage("Удалены бинды для " + count + " модулей");
            saveBinds();
            return SINGLE_SUCCESS;
        }));

        // --- list ---
        builder.then(literal("list").executes(context -> {
            var modulesWithBinds = ModuleManager.modules.stream()
                    .filter(m -> m.getKey() != -1 && m.getKey() != 0)
                    .toList();

            if (modulesWithBinds.isEmpty()) {
                sendMessage("Нет модулей с установленными биндами");
            } else {
                sendMessage("Список модулей с биндами:");
                for (var module : modulesWithBinds) {
                    sendMessage(String.format("> %s: %s",
                            module.getName(), StringHelper.getBindName(module.getKey())));
                }
            }
            return SINGLE_SUCCESS;
        }));
    }

    private void saveBinds() {
        try {
            Minced.getInstance().getFileController().saveFiles();
        } catch (FileSaveException e) {
            logDirect("Ошибка при сохранении бинда! Детали: " + e.getMessage(), Formatting.RED);
        }
    }

    /**
     * Поиск модуля без учёта регистра и пробелов
     */
    private Optional<Module> findModuleIgnoreCase(String name) {
        String normalized = name.replace(" ", "").toLowerCase();
        return ModuleManager.modules.stream()
                .filter(m -> m.getName().replace(" ", "").toLowerCase().equals(normalized))
                .findFirst();
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Управление биндами модулей",
                "",
                "Использование:",
                "> bind add <модуль> <клавиша> - Установить бинд для модуля",
                "> bind remove <модуль> - Удалить бинд для модуля",
                "> bind delete <модуль> - Алиас команды remove",
                "> bind clear - Удалить все бинды",
                "> bind list - Показать список всех биндов"
        );
    }
}