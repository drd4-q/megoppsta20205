package ru.minced.client.core.manager.os;

public class OSDetector {
    
    public enum OSType {
        WINDOWS, LINUX, MAC, UNKNOWN
    }

    public static OSType getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("linux")) {
            return OSType.LINUX;
        } else if (os.contains("windows")) {
            return OSType.WINDOWS;
        } else if (os.contains("mac")) {
            return OSType.MAC;
        } else {
            return OSType.UNKNOWN;
        }
    }

    public static boolean isWindows() {
        return getOperatingSystem() == OSType.WINDOWS;
    }
    public static boolean isLinux() {
        return getOperatingSystem() == OSType.LINUX;
    }
    public static boolean isMac() {
        return getOperatingSystem() == OSType.MAC;
    }
} 