package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.ConfigArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.minced.client.core.Minced;
import ru.minced.client.core.file.Directories;
import ru.minced.client.core.file.expection.FileProcessingException;
import ru.minced.client.util.IMinecraft;
import net.minecraft.util.Util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ru.minced.client.util.IMinecraft.logDirect;

@CommandHeader(name = "config", shortDesc = "Позволяет взаимодействовать с конфигами в чите")
public class ConfigCommand extends AbstractCommand {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("save").then(argument("name", StringArgumentType.word()).executes(context -> {
            String name = context.getArgument("name", String.class);
            try {
                Minced.getInstance().getFileController().saveFile(name + ".json");
                IMinecraft.logDirect(Text.of(String.format("Конфигурация %s сохранена!", name)));
            } catch (Exception e) {
                logDirect(String.format("Ошибка при сохранении конфига! Детали: %s", e.getCause().getMessage()),
                        Formatting.RED);
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("load").then(argument("name", ConfigArgumentType.create()).executes(context -> {
            String name = context.getArgument("name", String.class);
            if (new File(Directories.configsDirectory, name + ".json").exists()) {
                try {
                    Minced.getInstance().getFileController().loadFile(name + ".json");
                    IMinecraft.sendChatMessage(String.format("Конфигурация %s загружена!", name));
                } catch (FileProcessingException e) {
                    logDirect(String.format("Ошибка при загрузке конфига! Детали: %s", e.getCause().getMessage()),
                            Formatting.RED);
                }
            } else {
                IMinecraft.logDirect(Text.of(String.format("Конфигурация %s не найдена!", name)));
            }
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("dir").executes(context -> {
            try {
                File dir = new File(Directories.directoryPath);
                Util.getOperatingSystem().open(dir);
                IMinecraft.sendChatMessage("Открываю директорию с конфигами");

            } catch (Exception e) {
                IMinecraft.logDirect(Text.of("Ошибка при открытии директории: " + e.getMessage()));
                IMinecraft.sendChatMessage("Директория с конфигами находится по пути: " + Directories.configsDirectory.getAbsolutePath());
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("list").executes((context) -> {
            sendMessage("Список конфигов:");

            for (String config : ConfigArgumentType.getConfigs()) {
                sendMessage(config);
            }

            return SINGLE_SUCCESS;
        }));
    }
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("С помощью этой команды можно загружать/сохранять конфиги",
                "",
                "Использование:",
                "> config load <n> - Загружает конфиг.",
                "> config save <n> - Сохраняет конфиг.",
                "> config list - Возвращает список конфигов",
                "> config dir - Открывает папку с конфигами.");
    }
}
