package ru.minced.client.core.file.impl;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.Minced;
import ru.minced.client.core.draggable.AbstractDraggable;
import ru.minced.client.core.file.ClientFile;
import ru.minced.client.core.file.expection.FileLoadException;
import ru.minced.client.core.file.expection.FileSaveException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DraggableFile extends ClientFile {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public DraggableFile() {
        super("Draggable");
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
        JsonObject functionObject = createJsonObjectFromDrags();
        File file = new File(path, fileName);
        writeJsonToFile(functionObject, file);
        super.saveToFile(path, fileName);
    }

    @Override
    public void loadFromFile(File path, String fileName) throws FileLoadException {
        File file = new File(path, fileName);
        JsonObject functionObject = readJsonFromFile(file);
        if (functionObject != null) {
            updateDragsFromJsonObject(functionObject);
        }
        super.loadFromFile(path, fileName);
    }

    private JsonObject createJsonObjectFromDrags() {
        JsonObject functionObject = new JsonObject();
        for (AbstractDraggable dragElement : Minced.getInstance().getDraggableManager().getDraggable()) {
            JsonObject dragObject = new JsonObject();
            dragObject.addProperty("x", dragElement.getX());
            dragObject.addProperty("y", dragElement.getY());
            functionObject.add(dragElement.getName().toLowerCase(), dragObject);
        }
        return functionObject;
    }

    private void writeJsonToFile(JsonObject functionObject, File file) throws FileSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(functionObject, writer);
        } catch (IOException e) {
            throw new FileSaveException("Не удалось сохранить драги в файл", e);
        }
    }

    private JsonObject readJsonFromFile(File file) throws FileLoadException {
        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new FileLoadException("Failed to load drag from file", e);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new FileLoadException("Failed to parse JSON from file", e);
        } catch (IllegalStateException e) {
            throw new FileLoadException("Invalid JSON object format", e);
        }
    }

    private void updateDragsFromJsonObject(JsonObject functionObject) {
        for (AbstractDraggable dragElement : Minced.getInstance().getDraggableManager().getDraggable()) {
            JsonObject dragObject = functionObject.getAsJsonObject(dragElement.getName().toLowerCase());
            if (dragObject == null) continue;

            if (dragObject.has("x") && dragObject.has("y")) {
                dragElement.setX(dragObject.get("x").getAsInt());
                dragElement.setY(dragObject.get("y").getAsInt());
            }
        }
    }
}