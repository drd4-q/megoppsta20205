package ru.minced.client.core.info;

import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
public class User {
    public static String username = "tuskevich";
    public static Integer uid = 1000;
    public static Identifier userImage = Identifier.of("minced", "images/user.png");
}
