package ru.minced.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.stream.Stream;

public interface ILogger {
    static Text getPrefix() {
        return getPrefix("Minced");
    }
    
    static Text getPrefix(String customPrefix) {
        MutableText brackets = Text.literal("[");
        brackets.setStyle(brackets.getStyle().withColor(Formatting.GRAY));

        MutableText extra = Text.literal(customPrefix);
        extra.setStyle(extra.getStyle().withColor(Formatting.WHITE));

        MutableText closingBracket = Text.literal("]");
        closingBracket.setStyle(closingBracket.getStyle().withColor(Formatting.GRAY));

        MutableText prefix = Text.literal("");
        prefix.setStyle(prefix.getStyle().withColor(Formatting.DARK_GRAY));
        prefix.append(brackets);
        prefix.append(extra);
        prefix.append(closingBracket);
        prefix.append(" ->");

        return prefix;
    }

    default void log(Text prefix, Text... components) {
        MutableText component = Text.literal("");
        component.append(prefix);
        component.append(Text.literal(" "));
        Arrays.asList(components).forEach(component::append);
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(component);
        }
    }
    
    default void log(String customPrefix, String message, Formatting color) {
        Stream.of(message.split("\n")).forEach(line -> {
            MutableText component = Text.literal(line.replace("\t", "    "));
            component.setStyle(component.getStyle().withColor(color));
            log(getPrefix(customPrefix), component);
        });
    }

    default void logInfo(Text... components) {
        log(getPrefix(), components);
    }

    default void logInfo(String customPrefix, Text... components) {
        log(getPrefix(customPrefix), components);
    }

    default void logInfo(String message) {
        log("Minced", message, Formatting.WHITE);
    }

    default void logInfo(String customPrefix, String message) {
        log(customPrefix, message, Formatting.WHITE);
    }

    default void logWarn(Text... components) {
        log(getPrefix(), components);
    }

    default void logWarn(String customPrefix, Text... components) {
        log(getPrefix(customPrefix), components);
    }

    default void logWarn(String message) {
        log("Minced", message, Formatting.YELLOW);
    }

    default void logWarn(String customPrefix, String message) {
        log(customPrefix, message, Formatting.YELLOW);
    }

    default void logError(Text... components) {
        log(getPrefix(), components);
    }

    default void logError(String customPrefix, Text... components) {
        log(getPrefix(customPrefix), components);
    }
    
    default void logError(String message) {
        log("Minced", message, Formatting.RED);
    }
    
    default void logError(String customPrefix, String message) {
        log(customPrefix, message, Formatting.RED);
    }
}
