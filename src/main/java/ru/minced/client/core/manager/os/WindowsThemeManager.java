package ru.minced.client.core.manager.os;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;

public class WindowsThemeManager {

    public enum WindowsTheme {
        LIGHT, DARK, UNKNOWN
    }
    
    private static final String PERSONALIZE_KEY = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
    private static final String APPS_USE_LIGHT_THEME = "AppsUseLightTheme";
    private static final String SYSTEM_USES_LIGHT_THEME = "SystemUsesLightTheme";

    private interface DwmAPI extends StdCallLibrary {
        DwmAPI INSTANCE = Native.load("Dwmapi", DwmAPI.class);

        HRESULT DwmSetWindowAttribute(HWND hwnd, int dwAttribute, IntByReference pvAttribute, int cbAttribute);
    }

    public static WindowsTheme getWindowsTheme() {
        if (!OSDetector.isWindows()) {
            return WindowsTheme.UNKNOWN;
        }

        try {
            if (!Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, PERSONALIZE_KEY)) {
                return WindowsTheme.UNKNOWN;
            }

            String key;
            if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, PERSONALIZE_KEY, APPS_USE_LIGHT_THEME)) {
                key = APPS_USE_LIGHT_THEME;
            } else if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, PERSONALIZE_KEY, SYSTEM_USES_LIGHT_THEME)) {
                key = SYSTEM_USES_LIGHT_THEME;
            } else {
                return WindowsTheme.UNKNOWN;
            }

            int themeValue = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, PERSONALIZE_KEY, key);
            return themeValue == 0 ? WindowsTheme.DARK : WindowsTheme.LIGHT;
        } catch (Exception e) {
            e.printStackTrace();
            return WindowsTheme.UNKNOWN;
        }
    }

    public static void setTitleBarTheme(long windowHandle) {
        if (!OSDetector.isWindows()) {
            return;
        }

        try {
            WindowsTheme theme = getWindowsTheme();
            if (theme == WindowsTheme.UNKNOWN) {
                return;
            }

            boolean isDarkMode = (theme == WindowsTheme.DARK);
            setTitleBarTheme(windowHandle, isDarkMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean setTitleBarTheme(long windowHandle, boolean darkMode) {
        if (!OSDetector.isWindows()) {
            return false;
        }

        try {
            long win32Handle = GLFWNativeWin32.glfwGetWin32Window(windowHandle);

            HRESULT result = DwmAPI.INSTANCE.DwmSetWindowAttribute(
                    new HWND(Pointer.createConstant(win32Handle)),
                    20,
                    new IntByReference(darkMode ? 1 : 0),
                    Integer.BYTES
            );

            GLFW.glfwHideWindow(windowHandle);
            GLFW.glfwShowWindow(windowHandle);

            return result.intValue() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 