package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.util.ILogger;

import java.util.Arrays;
import java.util.List;

@CommandHeader(name = "clip", shortDesc = "Телепортация игрока по горизонтали или вертикали")
public class ClipCommand extends AbstractCommand implements ILogger {

    private static final SuggestionProvider<CommandSource> MODE_SUGGESTIONS = (context, builder) -> {
        builder.suggest("h").suggest("horizontal").suggest("v").suggest("vertical");
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSource> DISTANCE_SUGGESTIONS = (context, builder) -> {
        builder.suggest("1").suggest("3").suggest("5").suggest("-1").suggest("-3").suggest("-5");
        return builder.buildFuture();
    };
//
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("mode", StringArgumentType.word())
            .suggests(MODE_SUGGESTIONS)
            .then(argument("distance", DoubleArgumentType.doubleArg())
                .suggests(DISTANCE_SUGGESTIONS)
                .executes(context -> {
                    String mode = context.getArgument("mode", String.class);
                    double distance = context.getArgument("distance", Double.class);
                    
                    if (mc.player == null || mc.getNetworkHandler() == null) {
                        logError("Игрок не найден.");
                        return SINGLE_SUCCESS;
                    }
                    
                    if ("h".equalsIgnoreCase(mode) || "horizontal".equalsIgnoreCase(mode)) {
                        Vec3d lookVector = mc.player.getRotationVector().multiply(distance, 0, distance);
                        
                        double newX = mc.player.getX() + lookVector.x;
                        double newZ = mc.player.getZ() + lookVector.z;

                        for (int i = 0; i < 5; i++) {
                            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                                newX, mc.player.getY(), newZ, 
                                mc.player.getYaw(), mc.player.getPitch(), 
                                mc.player.isOnGround(), false
                            ));
                        }

                        mc.player.setPosition(newX, mc.player.getY(), newZ);

                        for (int i = 0; i < 5; i++) {
                            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                                newX, mc.player.getY(), newZ, 
                                mc.player.getYaw(), mc.player.getPitch(), 
                                mc.player.isOnGround(), false
                            ));
                        }
                        
                        String blockUnit = Math.abs(distance) > 1 ? "блоков" : "блок";
                        logInfo(String.format("Телепортация на %.1f %s по горизонтали", distance, blockUnit));
                    } else if ("v".equalsIgnoreCase(mode) || "vertical".equalsIgnoreCase(mode)) {
                        double newY = mc.player.getY() + distance;

                        for (int i = 0; i < 5; i++) {
                            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                                mc.player.getX(), newY, mc.player.getZ(), 
                                mc.player.getYaw(), mc.player.getPitch(), 
                                mc.player.isOnGround(), false
                            ));
                        }

                        mc.player.setPosition(mc.player.getX(), newY, mc.player.getZ());

                        for (int i = 0; i < 5; i++) {
                            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                                mc.player.getX(), newY, mc.player.getZ(), 
                                mc.player.getYaw(), mc.player.getPitch(), 
                                mc.player.isOnGround(), false
                            ));
                        }
                        
                        String direction = distance > 0 ? "вверх" : "вниз";
                        String blockUnit = Math.abs(distance) > 1 ? "блоков" : "блок";
                        logInfo(String.format("Телепортация на %.1f %s %s", Math.abs(distance), blockUnit, direction));
                    } else {
                        logError("Неверный режим. Используйте 'h' для горизонтальной или 'v' для вертикальной телепортации.");
                    }
                    
                    return SINGLE_SUCCESS;
                })
            )
        );
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Телепортация игрока по горизонтали или вертикали",
                "",
                "Использование:",
                "> clip h <расстояние> - Телепортация на указанное расстояние вперед/назад",
                "> clip v <расстояние> - Телепортация на указанное расстояние вверх/вниз",
                "",
                "Примеры:",
                "> clip h 5 - Телепортация на 5 блоков вперед",
                "> clip v -3 - Телепортация на 3 блока вниз"
        );
    }
} 
