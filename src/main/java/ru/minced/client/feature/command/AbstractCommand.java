package ru.minced.client.feature.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Getter;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import ru.minced.client.util.IMinecraft;

import java.util.List;

public abstract class AbstractCommand implements IMinecraft {
    protected final int SINGLE_SUCCESS = Command.SINGLE_SUCCESS;
    @Getter
    private final CommandHeader header = this.getClass().getAnnotation(CommandHeader.class);

    public AbstractCommand(String bind, String s) {
    }

    public abstract void build(LiteralArgumentBuilder<CommandSource> builder);

    protected static @NotNull LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected static <T> @NotNull RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public abstract List<String> getLongDesc();

    public void sendMessage(String msg) {
        IMinecraft.sendChatMessage(msg);
    }
}
