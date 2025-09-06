package ru.minced.client.core.manager.friend;

import lombok.Getter;
import ru.minced.client.util.IMinecraft;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FriendsManager implements IMinecraft {
    @Getter
    private static List<Friends> friends = new ArrayList<>();

    public static void addFriend(String name) {
        friends.add(new Friends(name));
    }

    public static void removeFriend(String name) {
        friends.removeIf(friend -> friend.getName().equalsIgnoreCase(name));
    }

    public static boolean checkFriend(String friend) {
        for (Friends f : friends) {
            if (f.getName().toLowerCase().equals(friend.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static void clear() {
        friends.clear();
    }
}
