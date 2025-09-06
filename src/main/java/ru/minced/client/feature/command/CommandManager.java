package ru.minced.client.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ru.minced.client.feature.command.impl.*;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import org.apache.commons.compress.utils.Lists;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.chat.EventChat;
import ru.minced.client.feature.command.impl.*;
import ru.minced.client.util.IMinecraft;

import java.util.List;

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
        List.<AbstractCommand>of(
                new ConfigCommand(),
                new ToggleCommand(),
                new PanicCommand(),
                new FriendsCommand(),
                new BindCommand(),
                new GPSCommand(),
                new ClipCommand()
                , new IrcCommand()

        ).forEach(command -> {
            commandList.add(command);
            LiteralArgumentBuilder<CommandSource> builder = AbstractCommand.literal(command.getHeader().name());
            command.build(builder);
            commandDispatcher.register(builder);

            if (command instanceof ToggleCommand) {
                LiteralArgumentBuilder<CommandSource> aliasBuilder = AbstractCommand.literal("t");
                command.build(aliasBuilder);
                commandDispatcher.register(aliasBuilder);
            }
        });
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
                commandDispatcher.execute(message, null);
            } catch (CommandSyntaxException ignored) {
            }
        }
    }
}
