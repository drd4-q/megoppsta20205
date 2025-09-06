package ru.minced.client.feature.command.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.util.other.StringHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static ru.minced.client.util.IMinecraft.logDirect;

public abstract class BindCommand extends AbstractCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File BINDS_FILE = new File("binds.json");
    private static final int SINGLE_SUCCESS = 1;

    public BindCommand() {
        super("bind", "Добавление/удаление биндов на модули");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        // .bind add <module> <key>
        builder.then(literal("add")
                .then(argument("module", greedyString())
                        .then(argument("key", word())
                                .executes(this::addExec))));

        // .bind remove <module>
        builder.then(literal("remove")
                .then(argument("module", greedyString())
                        .executes(this::removeExec)));
    }

    /**
     * Выполнение .bind add <module> <key>
     */
    private int addExec(CommandContext<CommandSource> context) {
        String inputName = context.getArgument("module", String.class);
        String keyName = context.getArgument("key", String.class);

        Optional<Module> moduleOpt = findModuleIgnoreCase(inputName);
        if (moduleOpt.isEmpty()) {
            sendMessage("Модуль с именем \"" + inputName + "\" не найден");
            return SINGLE_SUCCESS;
        }

        Module module = moduleOpt.get();
        int keyCode = StringHelper.getKeyByName(keyName);
        if (keyCode == -1) {
            sendMessage("Неверное имя клавиши: " + keyName);
            return SINGLE_SUCCESS;
        }

        module.setKey(keyCode);
        sendMessage("Для модуля " + module.getName() + " установлен бинд: " + keyName);

        saveBinds();
        return SINGLE_SUCCESS;
    }

    /**
     * Выполнение .bind remove <module>
     */
    private int removeExec(CommandContext<CommandSource> context) {
        String inputName = context.getArgument("module", String.class);

        Optional<Module> moduleOpt = findModuleIgnoreCase(inputName);
        if (moduleOpt.isEmpty()) {
            sendMessage("Модуль с именем \"" + inputName + "\" не найден");
            return SINGLE_SUCCESS;
        }

        Module module = moduleOpt.get();
        String oldBind = StringHelper.getBindName(module.getKey());
        module.setKey(-1);

        sendMessage("Для модуля " + module.getName() + " удалён бинд: " + oldBind);

        saveBinds();
        return SINGLE_SUCCESS;
    }


    private Optional<Module> findModuleIgnoreCase(String input) {
        String normalized = input.replace(" ", "").toLowerCase();
        return ModuleManager.modules.stream()
                .filter(m -> m.getName().replace(" ", "").equalsIgnoreCase(normalized))
                .findFirst();
    }

    // ===== JSON SAVE / LOAD =====
    public static void saveBinds() {
        try {
            Map<String, Integer> binds = new HashMap<>();
            for (Module module : ModuleManager.modules) {
                if (module.getKey() > 0) {
                    binds.put(module.getName(), module.getKey());
                }
            }
            try (FileWriter writer = new FileWriter(BINDS_FILE)) {
                GSON.toJson(binds, writer);
            }
        } catch (IOException e) {
            logDirect("Ошибка при сохранении бинда! Детали: " + e.getMessage(), Formatting.RED);
        }
    }

    public static void loadBinds() {
        if (!BINDS_FILE.exists()) return;
        try (FileReader reader = new FileReader(BINDS_FILE)) {
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> binds = GSON.fromJson(reader, type);

            for (Module module : ModuleManager.modules) {
                if (binds.containsKey(module.getName())) {
                    module.setKey(binds.get(module.getName()));
                }
            }
        } catch (IOException e) {
            logDirect("Ошибка при загрузке биндов! Детали: " + e.getMessage(), Formatting.RED);
        }
    }
}
