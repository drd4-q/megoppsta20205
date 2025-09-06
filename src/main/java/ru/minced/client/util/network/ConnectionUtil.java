package ru.minced.client.util.network;

import ru.minced.client.util.IMinecraft;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConnectionUtil implements IMinecraft {

    public static String getServerDomain() {
        if (IMinecraft.nullCheck() || mc.getCurrentServerEntry() == null) {
            return "singleplayer";
        }

        return extractBaseDomain(mc.getCurrentServerEntry().address);
    }

    public static boolean isSingleplayer() {
        return IMinecraft.nullCheck() || mc.getCurrentServerEntry() == null;
    }

    private static String extractBaseDomain(String address) {
        if (address == null || address.isEmpty()) {
            return "unknown";
        }

        String domainOnly = address.split(":")[0];

        String[] parts = domainOnly.split("\\.");

        if (parts.length >= 2) {
            return parts[parts.length - 2];
        }

        return domainOnly;
    }

    public static String getFullServerAddress() {
        if (IMinecraft.nullCheck() || mc.getCurrentServerEntry() == null) {
            return "singleplayer";
        }
        
        return mc.getCurrentServerEntry().address;
    }
}