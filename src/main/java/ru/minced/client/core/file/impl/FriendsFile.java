package ru.minced.client.core.file.impl;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.file.ClientFile;
import ru.minced.client.core.file.expection.FileLoadException;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.core.manager.friend.Friends;
import ru.minced.client.core.manager.friend.FriendsManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendsFile extends ClientFile {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public FriendsFile() {
        super("Friends");
    }

    @Override
    public void saveToFile(File path) throws FileSaveException {
        saveToFile(path, getName() + ".json");
    }

    @Override
    public void loadFromFile(File path) throws FileLoadException {
        loadFromFile(path, getName() + ".json");
    }

    @Override
    public void saveToFile(File path, String fileName) throws FileSaveException {
        JsonObject friendsObject = createJsonObjectFromFriends();
        File file = new File(path, fileName);
        writeJsonToFile(friendsObject, file);
        super.saveToFile(path, fileName);
    }

    @Override
    public void loadFromFile(File path, String fileName) throws FileLoadException {
        File file = new File(path, fileName);
        JsonObject friendsObject = readJsonFromFile(file);
        if (friendsObject != null) {
            updateFriendsFromJsonObject(friendsObject);
        }
        super.loadFromFile(path, fileName);
    }

    private JsonObject createJsonObjectFromFriends() {
        JsonObject friendsObject = new JsonObject();
        JsonArray friendsArray = new JsonArray();

        for (Friends friend : FriendsManager.getFriends()) {
            friendsArray.add(friend.getName());
        }

        friendsObject.add("friends", friendsArray);
        return friendsObject;
    }

    private void writeJsonToFile(JsonObject friendsObject, File file) throws FileSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(friendsObject, writer);
        } catch (IOException e) {
            throw new FileSaveException("Failed to save friends to file", e);
        }
    }

    private JsonObject readJsonFromFile(File file) throws FileLoadException {
        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new FileLoadException("Failed to load friends from file", e);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new FileLoadException("Failed to parse JSON from file", e);
        } catch (IllegalStateException e) {
            throw new FileLoadException("Invalid JSON object format", e);
        }
    }

    private void updateFriendsFromJsonObject(JsonObject friendsObject) {
        FriendsManager.clear();
        
        if (friendsObject.has("friends")) {
            JsonArray friendsArray = friendsObject.getAsJsonArray("friends");
            for (JsonElement friendElement : friendsArray) {
                FriendsManager.addFriend(friendElement.getAsString());
            }
        }
    }
} 