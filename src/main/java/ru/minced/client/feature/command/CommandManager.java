package ru.minced.client.feature.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ru.minced.client.feature.command.impl.*;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import java.util.List;

import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.chat.EventChat;
import ru.minced.client.feature.command.impl.*;
import ru.minced.client.util.IMinecraft;


@Getter
public class CommandManager implements IMinecraft {
    public static String COMMAND_TARGET = ".";
    private final List<AbstractCommand> commandList = Lists.newArrayList();

    private final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
    private final CommandSource source = new ClientCommandSource(null, MinecraftClient.getInstance());

    public CommandManager() {
        Minced.getInstance().getEventManager().subscribe(this);
    }

    public void register() {
        for (AbstractCommand command : List.of(
                new ConfigCommand(),
                new ToggleCommand(),
                new PanicCommand(),
                new FriendsCommand(),
                new BindCommand() {
                    @Override
                    public List<String> getLongDesc() {
                        return List.of();
                    }
                },//UwUq(≧▽≦q)👈(⌒▽⌒)👉
                new GPSCommand(),
                new ClipCommand(),
                new IrcCommand()
        )) {
            commandList.add(command);
            LiteralArgumentBuilder<CommandSource> builder = AbstractCommand.literal(command.getHeader().name());
            command.build(builder);
            commandDispatcher.register(builder);

            if (command instanceof ToggleCommand) {
                LiteralArgumentBuilder<CommandSource> aliasBuilder = AbstractCommand.literal("t");
                command.build(aliasBuilder);
                commandDispatcher.register(aliasBuilder);
            }
        }
    }

    @EventHandler
    private void onChat(EventChat event) {
        String message = event.getMessage();
        if (message.startsWith(COMMAND_TARGET)) {
            message = message.substring(1);
            if (message.isEmpty()) {
                return;
            }
            event.stop();
            try {
                commandDispatcher.execute(message, source); // ✅ заменил null на source
            } catch (CommandSyntaxException ignored) {
            }
        }
    }
}

