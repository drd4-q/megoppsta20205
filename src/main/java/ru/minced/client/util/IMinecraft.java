package ru.minced.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import ru.minced.client.util.window.WindowManager;

import java.util.Arrays;
import java.util.stream.Stream;

public interface IMinecraft {
    MinecraftClient mc = MinecraftClient.getInstance();
    WindowManager windowManager = new WindowManager();

    Identifier targetPng = Identifier.of("minced", "images/marker.png");
    Identifier stevePng = Identifier.of("minced", "images/steve.png");
    Identifier arrowPng = Identifier.of("minced", "images/arrow.png");
    Identifier ghost = Identifier.of("minced", "images/ghost.png");

    static boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    static Text getPrefix() {
        MutableText minced = Text.literal("Minced");
        minced.setStyle(minced.getStyle().withColor(Formatting.WHITE));

        MutableText prefix = Text.literal("");
        prefix.setStyle(minced.getStyle().withColor(Formatting.DARK_GRAY));
        prefix.append(minced);
        prefix.append(" -> ");

        return prefix;
    }

    static void logDirect(Text... components) {
        MutableText component = Text.literal("");
        component.append(getPrefix());
        component.append(Text.literal(" "));
        Arrays.asList(components).forEach(component::append);
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(component);
        }
    }

     static void logDirectColor(String message, Formatting color) {
        Stream.of(message.split("\n")).forEach(line -> {
            MutableText component = Text.literal(line.replace("\t", "    "));
            component.setStyle(component.getStyle().withColor(color));
            logDirect(component);
        });
    }

    static void logDirect(String message, Formatting color) {
        Stream.of(message.split("\n")).forEach(line -> {
            MutableText component = Text.literal(line.replace("\t", "    "));
            component.setStyle(component.getStyle().withColor(color));
            logDirect(component);
        });
    }

     static void sendChatMessage(String msg) {
        logDirectColor(msg,Formatting.GRAY);
    }
}
