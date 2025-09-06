package ru.minced.client.util.other;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.util.InputUtil;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.core.render.msdf.MsdfFont;
import ru.minced.mixin.accessor.MinecraftAccessor;

import static net.minecraft.client.util.InputUtil.Type.*;

@UtilityClass
public class StringHelper implements IMinecraft {

    public static void setSession(Session session) throws AuthenticationException {
        MinecraftClient mc = MinecraftClient.getInstance();
        MinecraftAccessor mca = (MinecraftAccessor) mc;
        mca.setSession(session);
        UserApiService apiService;
        apiService = mca.getAuthenticationService().createUserApiService(session.getAccessToken());
        mca.setUserApiService(apiService);
        mca.setSocialInteractionsManager(new SocialInteractionsManager(mc, apiService));
        mca.setProfileKeys(ProfileKeys.create(apiService, session, mc.runDirectory.toPath()));
        mca.setAbuseReportContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiService));
    }

    public static String getBindName(int key) {
        if (key == -1) {
            return "";
        }

        InputUtil.Key code = key < 8 ? MOUSE.createFromCode(key) : KEYSYM.createFromCode(key);

        String bindName = code.getTranslationKey()
                .replace("key.keyboard.", "")
                .replace("key.mouse.", "mouse ")
                .replace(".", " ")
                .toUpperCase();

        return shortenBindName(bindName);
    }

    private static String shortenBindName(String bindName) {
        return switch (bindName) {
            case "INSERT" -> "INS";
            case "PAGE DOWN" -> "P DOWN";
            case "PAGE UP" -> "P UP";
            case "PRINT SCREEN" -> "PR SC";
            case "NUMPAD 0" -> "NUM 0";
            case "NUMPAD 1" -> "NUM 1";
            case "NUMPAD 2" -> "NUM 2";
            case "NUMPAD 3" -> "NUM 3";
            case "NUMPAD 4" -> "NUM 4";
            case "NUMPAD 5" -> "NUM 5";
            case "NUMPAD 6" -> "NUM 6";
            case "NUMPAD 7" -> "NUM 7";
            case "NUMPAD 8" -> "NUM 8";
            case "NUMPAD 9" -> "NUM 9";
            case "ESCAPE" -> "ESC";
            case "BACKSPACE" -> "BACKSPC";
            case "TAB" -> "TAB";
            case "CAPS LOCK" -> "CAPS";
            case "LEFT SHIFT" -> "L SHIFT";
            case "RIGHT SHIFT" -> "R SHIFT";
            case "LEFT CONTROL" -> "L CTRL";
            case "RIGHT CONTROL" -> "R CTRL";
            case "LEFT ALT" -> "L ALT";
            case "RIGHT ALT" -> "R ALT";
            case "SPACE" -> "SPACE";
            case "ENTER" -> "ENTER";
            case "DELETE" -> "DEL";
            default -> bindName;
        };
    }


    public static String wrap(String input, int width, int size) {
        String[] words = input.split(" ");
        StringBuilder output = new StringBuilder();
        float lineWidth = 0;
        for (String word : words) {
            float wordWidth = Fonts.getSize(size).getStringWidth(word);
            if (lineWidth + wordWidth > width) {
                output.append("\n \n");
                lineWidth = 0;
            } else if (lineWidth > 0) {
                output.append(" ");
                lineWidth += Fonts.getSize(size).getStringWidth(" ");
            }
            output.append(word);
            lineWidth += wordWidth;
        }
        return output.toString();
    }

    public static String truncate(String text, float maxWidth, MsdfFont font, float fontSize) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        float textWidth = font.getWidth(text, fontSize);
        if (textWidth <= maxWidth) {
            return text;
        }

        String truncatedText = text;
        while (!truncatedText.isEmpty() && font.getWidth(truncatedText + "...", fontSize) > maxWidth) {
            truncatedText = truncatedText.substring(0, truncatedText.length() - 1);
        }

        return truncatedText + "...";
    }
}
