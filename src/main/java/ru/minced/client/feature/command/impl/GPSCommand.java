package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.ColorArgumentType;
import ru.minced.client.feature.command.arg.GPSPointArgumentType;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.core.manager.gps.GPSRenderer;
import ru.minced.client.util.ILogger;
import ru.minced.client.core.manager.gps.GPS;
import ru.minced.client.core.manager.gps.GPSManager;
import ru.minced.client.util.network.ConnectionUtil;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

@CommandHeader(name = "gps", shortDesc = "Управление GPS точками")
public class GPSCommand extends AbstractCommand implements ILogger {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("create")
            .then(argument("name", StringArgumentType.word())
                .then(argument("x", DoubleArgumentType.doubleArg())
                    .then(argument("y", DoubleArgumentType.doubleArg())
                        .then(argument("z", DoubleArgumentType.doubleArg())
                            .executes(context -> {
                                String name = context.getArgument("name", String.class);
                                double x = context.getArgument("x", Double.class);
                                double y = context.getArgument("y", Double.class);
                                double z = context.getArgument("z", Double.class);
                                
                                if (name.length() > 20) {
                                    logError("Имя GPS точки не может быть длиннее 20 символов");
                                    return SINGLE_SUCCESS;
                                }
                                
                                String server = ConnectionUtil.getServerDomain();
                                GPSManager.addPoint(name, x, y, z);
                                
                                logInfo(String.format("Создана GPS точка %s на координатах [%.1f, %.1f, %.1f] для %s",
                                        name, x, y, z, server));
                                
                                return SINGLE_SUCCESS;
                            })
                            .then(argument("color", ColorArgumentType.create())
                                .executes(context -> {
                                    String name = context.getArgument("name", String.class);
                                    double x = context.getArgument("x", Double.class);
                                    double y = context.getArgument("y", Double.class);
                                    double z = context.getArgument("z", Double.class);
                                    Color color = context.getArgument("color", Color.class);
                                    
                                    if (name.length() > 20) {
                                        logError("Имя GPS точки не может быть длиннее 20 символов");
                                        return SINGLE_SUCCESS;
                                    }
                                    
                                    String server = ConnectionUtil.getServerDomain();
                                    GPSManager.addPoint(name, x, y, z, color);
                                    
                                    logInfo(String.format("Создана GPS точка %s на координатах [%.1f, %.1f, %.1f] с цветом #%02x%02x%02x для %s",
                                            name, x, y, z, color.getRed(), color.getGreen(), color.getBlue(), server));
                                    
                                    return SINGLE_SUCCESS;
                                })
                            )
                        )
                    )
                )
            )
        );

        builder.then(literal("delete")
            .then(argument("name", GPSPointArgumentType.create())
                .executes(context -> {
                    String name = context.getArgument("name", String.class);
                    
                    if (GPSManager.removePoint(name)) {
                        logInfo(String.format("GPS точка %s удалена", name));
                    } else {
                        logError(String.format("GPS точка %s не найдена", name));
                    }
                    
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("name", GPSPointArgumentType.create())
                .executes(context -> {
                    String name = context.getArgument("name", String.class);
                    
                    if (GPSManager.removePoint(name)) {
                        logInfo(String.format("GPS точка %s удалена", name));
                    } else {
                        logError(String.format("GPS точка %s не найдена", name));
                    }
                    
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list")
            .executes(context -> {
                List<GPS> points = GPSManager.getPoints();
                String currentServer = ConnectionUtil.getServerDomain();
                
                if (points.isEmpty()) {
                    logInfo("Нет сохраненных GPS точек для сервера " + currentServer);
                } else {
                    logInfo("Список GPS точек для сервера " + currentServer + ":");
                    for (GPS point : points) {
                        logInfo(String.format("> %s: [%.1f, %.1f, %.1f] #%02x%02x%02x", 
                                point.getName(), 
                                point.getX(), point.getY(), point.getZ(),
                                point.getColor().getRed(), point.getColor().getGreen(), point.getColor().getBlue()));
                    }
                }
                
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("clear")
            .executes(context -> {
                int count = GPSManager.getPoints().size();
                GPSManager.clearAllPoints();
                
                logInfo(String.format("Удалены все GPS точки (%d шт.)", count));
                
                return SINGLE_SUCCESS;
            })
        );
        Minced.getInstance().getEventManager().subscribe(this);
    }

    @EventHandler
    public void onRender(EventRender event) {
        if (mc.player == null) return;

        DrawContext context = event.getContext();
        List<GPS> points = GPSManager.getPoints();

        for (GPS point : points) {
            Vec3d playerPos = mc.player.getPos();
            Vec3d pointPos = new Vec3d(point.getX(), point.getY(), point.getZ());
            double distance = playerPos.distanceTo(pointPos);

            float scale = Math.min(1.5f, Math.max(0.6f, (float)(1.0f - distance / 500.0)));

            GPSRenderer.render2D(
                    context,
                    point.getX(),
                    point.getY(),
                    point.getZ(),
                    point.getColor(),
                    scale
            );
        }
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Управление GPS точками",
                "",
                "Использование:",
                "> gps create <имя_точки> <x> <y> <z> - Создать новую точку на координатах",
                "> gps create <имя_точки> <x> <y> <z> <color> - Создать новую точку с цветом",
                "> gps delete <имя_точки> - Удалить точку по имени",
                "> gps remove <имя_точки> - Удалить точку по имени (алиас команды delete)",
                "> gps list - Показать список всех точек",
                "> gps clear - Удалить все точки"
        );
    }
} 