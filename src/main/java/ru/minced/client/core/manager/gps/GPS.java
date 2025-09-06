package ru.minced.client.core.manager.gps;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;

import java.awt.Color;

@Getter
public class GPS implements IMinecraft {
    private final String name;
    private final double x;
    private final double y;
    private final double z;
    @Setter private Color color;
    @Setter private String server;
    @Getter @Setter private static GPS lastGPS;
    
    public GPS(String name, double x, double y, double z, Color color, String server) {
        if (name.length() > 20) {
            name = name.substring(0, 20);
        }

        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.server = server;
    }

    public static double getDistance() {
        if (mc.player == null || lastGPS == null) return 0;

        double x = lastGPS.getX() - MathUtil.interpolate(mc.player.prevX, mc.player.getX());
        double y = lastGPS.getY() - MathUtil.interpolate(mc.player.prevY, mc.player.getY());
        double z = lastGPS.getZ() - MathUtil.interpolate(mc.player.prevZ, mc.player.getZ());

        return Math.sqrt(x * x + y * y + z * z);
    }

} 