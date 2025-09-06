package ru.minced.client.core.file.impl;

import com.google.gson.*;
import ru.minced.client.feature.module.setting.impl.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.file.ClientFile;
import ru.minced.client.core.file.expection.FileLoadException;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleFile extends ClientFile {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ModuleFile() {
        super("Config");
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
        JsonObject functionObject = createJsonObjectFromModules();
        File file = new File(path, fileName);
        writeJsonToFile(functionObject, file);
        super.saveToFile(path, fileName);
    }

    @Override
    public void loadFromFile(File path, String fileName) throws FileLoadException {
        File file = new File(path, fileName);
        JsonObject functionObject = readJsonFromFile(file);
        if (functionObject != null) {
            updateModulesFromJsonObject(functionObject);
        }
        super.loadFromFile(path, fileName);
    }

    private JsonObject createJsonObjectFromModules() {
        JsonObject functionObject = new JsonObject();
        for (Module module : ModuleManager.modules) {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("bind", module.getKey());
            moduleObject.addProperty("state", module.isState());
            module.getSettings().forEach(setting -> addSettingToJsonObject(moduleObject, setting));
            functionObject.add(module.getName().toLowerCase(), moduleObject);
        }

        return functionObject;
    }

    private void addSettingToJsonObject(JsonObject moduleObject, Setting setting) {
        if (setting instanceof BooleanSetting booleanSetting) {
            moduleObject.addProperty(setting.getName(), booleanSetting.isState());
        }
        if (setting instanceof SliderSetting valueSetting) {
            moduleObject.addProperty(setting.getName(), valueSetting.getValue());
        }
        if (setting instanceof BindSetting bindSetting) {
            moduleObject.addProperty(setting.getName(), bindSetting.getKey());
        }
        if (setting instanceof TextSetting textSetting) {
            moduleObject.addProperty(setting.getName(), textSetting.getText());
        }
        if (setting instanceof ModeSetting selectSetting) {
            moduleObject.addProperty(setting.getName(), selectSetting.getSelected());
        }
        if (setting instanceof ModeListSetting multiSelectSetting) {
            List<String> selected = multiSelectSetting.getSelected();
            String selectedAsString = String.join(",", selected);
            moduleObject.addProperty(setting.getName(), selectedAsString);
        }
        if (setting instanceof GroupSetting groupSetting) {
            JsonObject groupObject = new JsonObject();
            groupObject.addProperty("expanded", groupSetting.isExpanded());

            for (Setting childSetting : groupSetting.getSettings()) {
                addSettingToJsonObject(groupObject, childSetting);
            }
            
            moduleObject.add(setting.getName(), groupObject);
        }
    }

    private void writeJsonToFile(JsonObject functionObject, File file) throws FileSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(functionObject, writer);
        } catch (IOException e) {
            throw new FileSaveException("Failed to save module to file", e);
        }
    }

    private JsonObject readJsonFromFile(File file) throws FileLoadException {
        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new FileLoadException("Failed to load module from file", e);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new FileLoadException("Failed to parse JSON from file", e);
        } catch (IllegalStateException e) {
            throw new FileLoadException("Invalid JSON object format", e);
        }
    }

    private void updateModulesFromJsonObject(JsonObject functionObject) {
        for (Module module : ModuleManager.modules) {
            JsonObject moduleObject = functionObject.getAsJsonObject(module.getName().toLowerCase());
            if (moduleObject == null)
                continue;

            if (moduleObject.has("bind") && moduleObject.has("state")) {
                module.setKey(moduleObject.get("bind").getAsInt());
                module.setState(moduleObject.get("state").getAsBoolean());
            }
            module.getSettings().forEach(setting -> updateSettingFromJsonObject(moduleObject, setting));
        }
    }

    private void updateSettingFromJsonObject(JsonObject moduleObject, Setting setting) {
        JsonElement settingElement = moduleObject.get(setting.getName());
        if (settingElement == null || settingElement.isJsonNull())
            return;

        if (setting instanceof BooleanSetting booleanSetting) {
            booleanSetting.set(settingElement.getAsBoolean());
        }
        if (setting instanceof SliderSetting valueSetting) {
            valueSetting.setValue(settingElement.getAsFloat());
        }
        if (setting instanceof BindSetting bindSetting) {
            bindSetting.setKey(settingElement.getAsInt());
        }
        if (setting instanceof TextSetting textSetting) {
            textSetting.setText(settingElement.getAsString());
        }
        if (setting instanceof ModeSetting selectSetting) {
            selectSetting.setSelected(settingElement.getAsString());
        }
        if (setting instanceof ModeListSetting multiSelectSetting) {
            String asString = settingElement.getAsString();
            List<String> selectedList = new ArrayList<>(Arrays.asList(asString.split(",")));
            selectedList.removeIf(s -> !multiSelectSetting.getList().contains(s));
            multiSelectSetting.setSelected(selectedList);
        }
        if (setting instanceof GroupSetting groupSetting && settingElement.isJsonObject()) {
            JsonObject groupObject = settingElement.getAsJsonObject();

            if (groupObject.has("expanded")) {
                groupSetting.setExpanded(groupObject.get("expanded").getAsBoolean());
            }

            for (Setting childSetting : groupSetting.getSettings()) {
                updateSettingFromJsonObject(groupObject, childSetting);
            }
        }
    }
}