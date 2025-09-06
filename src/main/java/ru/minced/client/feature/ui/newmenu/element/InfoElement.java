package ru.minced.client.feature.ui.newmenu.element;

import ru.minced.client.core.info.Client;
import ru.minced.client.core.info.User;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InfoElement implements IMinecraft {

    public void renderHeader(MatrixStack matrixStack, int x, int y, int headerHeight) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        DrawHelper.drawVerticalCenteredText(matrix, Fonts.BLACK.getFont(24), Client.name, x + 20, y, headerHeight, new Color(127, 133, 172));
        DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(14), Client.version, x + 20 + Fonts.BLACK.getFont(24).getWidth(Client.name), y + 4, headerHeight, new Color(134, 136, 153));

        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        float totalHeight = Fonts.MEDIUM.getFont(20).getHeight() + Fonts.REGULAR.getFont(14).getHeight() + 2;

        float timeX = x + 1100 - 20 - Fonts.MEDIUM.getFont(20).getWidth(time);
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(20), time, timeX, y + (headerHeight - totalHeight) / 2, new Color(127, 133, 172));

        float dateX = x + 1100 - 20 - Fonts.REGULAR.getFont(14).getWidth(date);
        DrawHelper.drawText(matrix, Fonts.REGULAR.getFont(14), date, dateX, y + (headerHeight - totalHeight) / 2 + Fonts.MEDIUM.getFont(20).getHeight() + 2, new Color(127, 133, 172));
    }
    
    public void renderFooter(MatrixStack matrixStack, int x, int y, int footerHeight) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        
        DrawHelper.drawRect(matrixStack, x + 20, y + (float) (footerHeight - 54) / 2, 54, 54, 12, new Color(53, 52, 71));
        DrawHelper.drawImage(matrixStack, x + 22, y + (float) (footerHeight - 50) / 2, 50, 50, 8, User.userImage, Color.WHITE);
        
        float totalUserInfoHeight = Fonts.MEDIUM.getFont(16).getHeight() + Fonts.REGULAR.getFont(12).getHeight() + 2;
        float usernameY = y + (footerHeight - totalUserInfoHeight) / 2;
        
        DrawHelper.drawText(matrix, Fonts.MEDIUM.getFont(16), User.username, x + 20 + 54 + 10, usernameY, Color.WHITE);
        DrawHelper.drawText(matrix, Fonts.REGULAR.getFont(12), "uid: " + User.uid.toString(), x + 20 + 54 + 10, usernameY + Fonts.MEDIUM.getFont(16).getHeight() + 2, new Color(134, 136, 153));
    }
}
