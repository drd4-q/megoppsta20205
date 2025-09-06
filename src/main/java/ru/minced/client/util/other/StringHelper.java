package ru.minced.client.util.other;

import org.lwjgl.glfw.GLFW;
import ru.minced.client.core.render.msdf.MsdfFont;

public class StringHelper {

    /**
     * Преобразует имя клавиши (например "R", "SPACE", "F1", "NUMPAD1", "CTRL") в GLFW keyCode.
     * Возвращает -1, если имя неизвестно.
     */
    public static int getKeyByName(String name) {
        if (name == null) return -1;
        String key = name.trim().toUpperCase();

        // Буквы A-Z
        if (key.length() == 1) {
            char c = key.charAt(0);
            if (c >= 'A' && c <= 'Z') {
                return GLFW.GLFW_KEY_A + (c - 'A');
            }
            if (c >= '0' && c <= '9') {
                return GLFW.GLFW_KEY_0 + (c - '0');
            }
        }

        // F1-F24
        if (key.startsWith("F")) {
            try {
                int f = Integer.parseInt(key.substring(1));
                if (f >= 1 && f <= 24) {
                    return GLFW.GLFW_KEY_F1 + (f - 1);
                }
            } catch (NumberFormatException ignored) {}
        }

        // Numpad (NUMPAD1, NP1)
        if (key.startsWith("NUMPAD") || key.startsWith("NP")) {
            String digits = key.replace("NUMPAD", "").replace("NP", "");
            if (digits.length() == 1 && Character.isDigit(digits.charAt(0))) {
                return GLFW.GLFW_KEY_KP_0 + (digits.charAt(0) - '0');
            }
        }

        switch (key) {
            case "SPACE": return GLFW.GLFW_KEY_SPACE;
            case "LSHIFT": case "SHIFT": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "RSHIFT": return GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "LCTRL": case "CTRL": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RCTRL": return GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "LALT": case "ALT": return GLFW.GLFW_KEY_LEFT_ALT;
            case "RALT": return GLFW.GLFW_KEY_RIGHT_ALT;
            case "TAB": return GLFW.GLFW_KEY_TAB;
            case "ESC": case "ESCAPE": return GLFW.GLFW_KEY_ESCAPE;
            case "ENTER": case "RETURN": return GLFW.GLFW_KEY_ENTER;
            case "BACKSPACE": return GLFW.GLFW_KEY_BACKSPACE;
            case "DELETE": return GLFW.GLFW_KEY_DELETE;
            case "INSERT": return GLFW.GLFW_KEY_INSERT;
            case "HOME": return GLFW.GLFW_KEY_HOME;
            case "END": return GLFW.GLFW_KEY_END;
            case "PAGEUP": return GLFW.GLFW_KEY_PAGE_UP;
            case "PAGEDOWN": return GLFW.GLFW_KEY_PAGE_DOWN;
            case "UP": case "ARROWUP": return GLFW.GLFW_KEY_UP;
            case "DOWN": case "ARROWDOWN": return GLFW.GLFW_KEY_DOWN;
            case "LEFT": case "ARROWLEFT": return GLFW.GLFW_KEY_LEFT;
            case "RIGHT": case "ARROWRIGHT": return GLFW.GLFW_KEY_RIGHT;
            case "CAPSLOCK": return GLFW.GLFW_KEY_CAPS_LOCK;
            case "SCROLLLOCK": return GLFW.GLFW_KEY_SCROLL_LOCK;
            case "PAUSE": return GLFW.GLFW_KEY_PAUSE;
            case "PRINTSCREEN": return GLFW.GLFW_KEY_PRINT_SCREEN;
            default: return -1;
        }
    }

    /**
     * Существующий (твой) метод — возвращает строковое имя для keyCode.
     * Оставил его, чтобы не ломать код, который ожидает именно эту сигнатуру.
     */
    public static String getKeyByName(int key) {
        if (key == -1) {
            return "";
        }
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();

        // Фоллбек для спецклавиш
        switch (key) {
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_ESCAPE: return "ESC";
            case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
            case GLFW.GLFW_KEY_DELETE: return "DELETE";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RALT";
            default: return Integer.toString(key);
        }
    }

    /**
     * Удобный метод для отображения бинда (если хочешь другой текст — измени здесь).
     */
    public static String getBindName(int keyCode) {
        String s = getKeyByName(keyCode);
        return (s == null || s.isEmpty()) ? "NONE" : s;
    }

    public static String truncate(String text, float maxWidth, MsdfFont font, float nameTextSize) {
        return text;
    }
}
