package main.client.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import main.client.PocoyoVisual;
import main.client.module.Module;
import main.client.setting.*;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ConfigManager {
    private final File configDir = new File(MinecraftClient.getInstance().runDirectory, "PocoyoVisual");
    private final File modulesFile = new File(configDir, "modules.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void load() {
        if (!modulesFile.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(modulesFile)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) return;

            for (Module module : PocoyoVisual.INSTANCE.getModuleManager().getModules()) {
                if (json.has(module.getName())) {
                    JsonObject moduleJson = json.getAsJsonObject(module.getName());

                    // Загружаем состояние модуля
                    if (moduleJson.has("toggled")) {
                        module.setToggled(moduleJson.get("toggled").getAsBoolean());
                    }
                    if (moduleJson.has("key")) {
                        module.setKey(moduleJson.get("key").getAsInt());
                    }

                    // Загружаем настройки
                    for (Setting setting : module.getSettings()) {
                        if (moduleJson.has(setting.getName())) {
                            if (setting instanceof BooleanSetting bs) {
                                bs.setValue(moduleJson.get(setting.getName()).getAsBoolean());
                            } else if (setting instanceof NumberSetting ns) {
                                ns.setValue(moduleJson.get(setting.getName()).getAsDouble());
                    } else if (setting instanceof ModeSetting ms) {
                        ms.setCurrentMode(moduleJson.get(setting.getName()).getAsString());
                    } else if (setting instanceof ColorSetting cs) {
                        cs.setColor(moduleJson.get(setting.getName()).getAsInt());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(modulesFile)) {
            JsonObject json = new JsonObject();

            for (Module module : PocoyoVisual.INSTANCE.getModuleManager().getModules()) {
                JsonObject moduleJson = new JsonObject();
                moduleJson.addProperty("toggled", module.isToggled());
                moduleJson.addProperty("key", module.getKey());

                for (Setting setting : module.getSettings()) {
                    if (setting instanceof BooleanSetting bs) {
                        moduleJson.addProperty(setting.getName(), bs.isEnabled());
                    } else if (setting instanceof NumberSetting ns) {
                        moduleJson.addProperty(setting.getName(), ns.getValue());
                    } else if (setting instanceof ModeSetting ms) {
                        moduleJson.addProperty(setting.getName(), ms.getCurrentMode());
                    } else if (setting instanceof ColorSetting cs) {
                        moduleJson.addProperty(setting.getName(), cs.getColor());
                    }
                }
                json.add(module.getName(), moduleJson);
            }
            gson.toJson(json, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
