package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.KeyArgumentType;
import ru.minced.client.feature.command.arg.ModuleWithBindArgumentType;
import ru.minced.client.feature.command.arg.ModuleWithoutBindArgumentType;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.util.other.StringHelper;

import java.util.Arrays;
import java.util.List;

import static ru.minced.client.util.IMinecraft.logDirect;

@CommandHeader(name = "bind", shortDesc = "Управление биндами модулей")
public class BindCommand extends AbstractCommand {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("module", ModuleWithoutBindArgumentType.create())
            .then(argument("key", KeyArgumentType.create()).executes(context -> {
                Module module = context.getArgument("module", Module.class);
                int key = context.getArgument("key", Integer.class);
                
                String oldBindName = StringHelper.getBindName(module.getKey());
                module.setKey(key);
                String newBindName = StringHelper.getBindName(key);
                
                sendMessage(String.format("Для модуля %s установлен бинд: %s", 
                        module.getName(), newBindName));
                
                try {
                    Minced.getInstance().getFileController().saveFiles();
                } catch (FileSaveException e) {
                    logDirect(String.format("Ошибка при сохранении бинда! Детали: %s", e.getMessage()),
                            Formatting.RED);
                }
                
                return SINGLE_SUCCESS;
            }))
        ));

        LiteralArgumentBuilder<CommandSource> removeCommand = literal("remove").then(
                argument("module", ModuleWithBindArgumentType.create()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);
                    
                    String oldBindName = StringHelper.getBindName(module.getKey());
                    module.setKey(-1);
                    
                    sendMessage(String.format("Для модуля %s удален бинд: %s", 
                            module.getName(), oldBindName));
                    
                    try {
                        Minced.getInstance().getFileController().saveFiles();
                    } catch (FileSaveException e) {
                        logDirect(String.format("Ошибка при сохранении бинда! Детали: %s", e.getMessage()),
                                Formatting.RED);
                    }
                    
                    return SINGLE_SUCCESS;
                }));
        
        builder.then(removeCommand);

        LiteralArgumentBuilder<CommandSource> deleteCommand = literal("delete").then(
                argument("module", ModuleWithBindArgumentType.create()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);
                    
                    String oldBindName = StringHelper.getBindName(module.getKey());
                    module.setKey(-1);
                    
                    sendMessage(String.format("Для модуля %s удален бинд: %s", 
                            module.getName(), oldBindName));
                    
                    try {
                        Minced.getInstance().getFileController().saveFiles();
                    } catch (FileSaveException e) {
                        logDirect(String.format("Ошибка при сохранении бинда! Детали: %s", e.getMessage()),
                                Formatting.RED);
                    }
                    
                    return SINGLE_SUCCESS;
                }));
        
        builder.then(deleteCommand);

        builder.then(literal("clear").executes(context -> {
            int count = 0;
            for (Module module : ModuleManager.modules) {
                if (module.getKey() != -1 && module.getKey() != 0) {
                    module.setKey(-1);
                    count++;
                }
            }
            
            sendMessage(String.format("Удалены бинды для %d модулей", count));
            
            try {
                Minced.getInstance().getFileController().saveFiles();
            } catch (FileSaveException e) {
                logDirect(String.format("Ошибка при сохранении бинда! Детали: %s", e.getMessage()),
                        Formatting.RED);
            }
            
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("list").executes(context -> {
            List<Module> modulesWithBinds = ModuleManager.modules.stream()
                    .filter(module -> module.getKey() != -1 && module.getKey() != 0)
                    .toList();
            
            if (modulesWithBinds.isEmpty()) {
                sendMessage("Нет модулей с установленными биндами");
            } else {
                sendMessage("Список модулей с биндами:");
                for (Module module : modulesWithBinds) {
                    sendMessage(String.format("> %s: %s", 
                            module.getName(), StringHelper.getBindName(module.getKey())));
                }
            }
            
            return SINGLE_SUCCESS;
        }));
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Управление биндами модулей",
                "",
                "Использование:",
                "> bind add <модуль> <клавиша> - Установить бинд для модуля",
                "> bind remove <модуль> - Удалить бинд для модуля",
                "> bind delete <модуль> - Удалить бинд для модуля (алиас команды remove)",
                "> bind clear - Удалить все бинды",
                "> bind list - Показать список всех биндов"
        );
    }
} 