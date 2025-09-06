package ru.minced.client.feature.command.impl;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.core.manager.socket.SocketManager;
import ru.minced.client.util.ILogger;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.List;

@CommandHeader(name = "irc", shortDesc = "irc чат")
public class IrcCommand extends AbstractCommand implements ILogger {
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
            argument("message", StringArgumentType.greedyString()).executes(context -> {
                String message = context.getArgument("message", String.class);


                SocketManager.sendIRC(message);

                return SINGLE_SUCCESS;
            })
        );
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "IRC чат команда",
                "",
                "Использование:",
                "> irc <сообщение> - Отправляет сообщение (будет расширено логикой IRC)"
        );
    }
}
