package ru.minced.client.feature.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.minced.client.core.Minced;
import ru.minced.client.feature.command.AbstractCommand;
import ru.minced.client.feature.command.CommandHeader;
import ru.minced.client.feature.command.arg.FriendArgumentType;
import ru.minced.client.feature.command.arg.ServerPlayerArgumentType;
import ru.minced.client.core.file.Directories;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.core.manager.friend.Friends;
import ru.minced.client.core.manager.friend.FriendsManager;
import ru.minced.client.util.IMinecraft;
import net.minecraft.util.Util;

import java.util.Arrays;
import java.util.List;

import static ru.minced.client.util.IMinecraft.logDirect;

@CommandHeader(name = "friends", shortDesc = "Управление списком друзей")
public class FriendsCommand extends AbstractCommand {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("nickname", ServerPlayerArgumentType.create()).executes(context -> {
            String nickname = context.getArgument("nickname", String.class);
            
            if (FriendsManager.checkFriend(nickname)) {
                sendMessage(String.format("Игрок %s уже в списке друзей", nickname));
            } else {
                FriendsManager.addFriend(nickname);
                sendMessage(String.format("Игрок %s добавлен в список друзей", nickname));
                
                try {
                    Minced.getInstance().getFileController().saveFiles();
                } catch (FileSaveException e) {
                    logDirect(String.format("Ошибка при сохранении списка друзей! Детали: %s", e.getMessage()),
                            Formatting.RED);
                }
            }
            
            return SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<CommandSource> removeCommand = literal("remove").then(
                argument("nickname", FriendArgumentType.create()).executes(context -> {
                    String nickname = context.getArgument("nickname", String.class);
                    
                    if (FriendsManager.checkFriend(nickname)) {
                        FriendsManager.removeFriend(nickname);
                        sendMessage(String.format("Игрок %s удален из списка друзей", nickname));
                        
                        try {
                            Minced.getInstance().getFileController().saveFiles();
                        } catch (FileSaveException e) {
                            logDirect(String.format("Ошибка при сохранении списка друзей! Детали: %s", e.getMessage()),
                                    Formatting.RED);
                        }
                    } else {
                        sendMessage(String.format("Игрок %s не найден в списке друзей", nickname));
                    }
                    
                    return SINGLE_SUCCESS;
                }));
        
        builder.then(removeCommand);

        LiteralArgumentBuilder<CommandSource> deleteCommand = literal("delete").then(
                argument("nickname", FriendArgumentType.create()).executes(context -> {
                    String nickname = context.getArgument("nickname", String.class);
                    
                    if (FriendsManager.checkFriend(nickname)) {
                        FriendsManager.removeFriend(nickname);
                        sendMessage(String.format("Игрок %s удален из списка друзей", nickname));
                        
                        try {
                            Minced.getInstance().getFileController().saveFiles();
                        } catch (FileSaveException e) {
                            logDirect(String.format("Ошибка при сохранении списка друзей! Детали: %s", e.getMessage()),
                                    Formatting.RED);
                        }
                    } else {
                        sendMessage(String.format("Игрок %s не найден в списке друзей", nickname));
                    }
                    
                    return SINGLE_SUCCESS;
                }));
        
        builder.then(deleteCommand);

        builder.then(literal("clear").executes(context -> {
            if (FriendsManager.getFriends().isEmpty()) {
                sendMessage("Список друзей уже пуст");
            } else {
                FriendsManager.clear();
                sendMessage("Список друзей очищен");
                
                try {
                    Minced.getInstance().getFileController().saveFiles();
                } catch (FileSaveException e) {
                    logDirect(String.format("Ошибка при сохранении списка друзей! Детали: %s", e.getMessage()),
                            Formatting.RED);
                }
            }
            
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("dir").executes(context -> {
            try {
                Util.getOperatingSystem().open(Directories.filesDirectory);
                IMinecraft.sendChatMessage("Открываю директорию с файлами друзей");
            } catch (Exception e) {
                IMinecraft.logDirect(Text.of("Ошибка при открытии директории: " + e.getMessage()));
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("list").executes(context -> {
            List<Friends> friendsList = FriendsManager.getFriends();
            
            if (friendsList.isEmpty()) {
                sendMessage("Список друзей пуст");
            } else {
                sendMessage("Список друзей:");
                for (Friends friend : friendsList) {
                    sendMessage("> " + friend.getName());
                }
            }
            
            return SINGLE_SUCCESS;
        }));
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Управление списком друзей",
                "",
                "Использование:",
                "> friends add <nickname> - Добавить игрока в список друзей",
                "> friends remove <nickname> - Удалить игрока из списка друзей",
                "> friends delete <nickname> - Удалить игрока из списка друзей (алиас команды remove)",
                "> friends clear - Удалить всех друзей из списка",
                "> friends list - Показать список друзей",
                "> friends dir - Открыть папку, где хранится файл с друзьями"
        );
    }
} 