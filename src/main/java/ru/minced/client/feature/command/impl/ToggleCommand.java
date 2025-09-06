package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.ModuleArgumentType;
import ru.minced.client.feature.module.Module;

import java.util.Arrays;
import java.util.List;

@CommandHeader(name = "toggle", shortDesc = "Позволяет включать/выключать модули")
public class ToggleCommand extends AbstractCommand {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            
            module.toggle();
            sendMessage(String.format("Модуль %s %s", 
                    module.getName(), 
                    module.isState() ? "включен" : "выключен"));
            
            return SINGLE_SUCCESS;
        }));
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "С помощью этой команды можно включать/выключать модули",
                "",
                "Использование:",
                "> toggle <имя модуля> - Включить/выключить модуль",
                "> t <имя модуля> - Короткий вариант команды toggle"
        );
    }
}