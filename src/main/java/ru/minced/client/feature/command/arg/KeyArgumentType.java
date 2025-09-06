package ru.minced.client.feature.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KeyArgumentType implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = List.of("KEY_A", "KEY_LEFT_SHIFT", "MOUSE_BUTTON_1");
    
    private static final List<KeyInfo> KEY_NAMES = new ArrayList<>();
    
    static {
        for (Field field : GLFW.class.getDeclaredFields()) {
            String name = field.getName();
            if (name.startsWith("GLFW_KEY_") || name.startsWith("GLFW_MOUSE_BUTTON_")) {
                try {
                    int keyCode = field.getInt(null);
                    String displayName = name.replace("GLFW_KEY_", "KEY_").replace("GLFW_MOUSE_BUTTON_", "MOUSE_");
                    KEY_NAMES.add(new KeyInfo(displayName, keyCode));
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    public static KeyArgumentType create() {
        return new KeyArgumentType();
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        String keyName = reader.readString();

        try {
            return Integer.parseInt(keyName);
        } catch (NumberFormatException ignored) {}

        for (KeyInfo keyInfo : KEY_NAMES) {
            if (keyInfo.name.equalsIgnoreCase(keyName)) {
                return keyInfo.keyCode;
            }
        }
        
        throw new DynamicCommandExceptionType(
                name -> Text.literal("Неизвестная клавиша: " + name)
        ).create(keyName);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> keyNames = new ArrayList<>();
        for (KeyInfo keyInfo : KEY_NAMES) {
            keyNames.add(keyInfo.name);
        }
        
        return CommandSource.suggestMatching(keyNames, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
    
    private static class KeyInfo {
        final String name;
        final int keyCode;
        
        KeyInfo(String name, int keyCode) {
            this.name = name;
            this.keyCode = keyCode;
        }
    }
} 