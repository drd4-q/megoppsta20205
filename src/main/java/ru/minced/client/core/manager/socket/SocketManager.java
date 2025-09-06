package ru.minced.client.core.manager.socket;

import ru.minced.client.core.info.User;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static ru.minced.client.util.IMinecraft.mc;

public class SocketManager {

    private static WebSocketClient irc;

    public static void initIrcChat() {
        try {
            irc = new WebSocketClient(new URI("ws://127.0.0.1:10000/ws")) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("§a[IRC] Connected!");
                }

                @Override
                public void onMessage(String message) {
                    assert mc.world != null;
                    mc.inGameHud.getChatHud().addMessage(Text.of(message));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    mc.inGameHud.getChatHud().addMessage(Text.of("§c[IRC] Disconnected"));
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            irc.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendIRC(String msg) {
        if (irc != null && irc.isOpen()) {
            irc.send(User.username + "|" + msg);
        }
    }
}
