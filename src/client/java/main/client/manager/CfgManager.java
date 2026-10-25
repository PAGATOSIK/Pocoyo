package main.client.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.module.Module;
import main.client.setting.*;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CfgManager {
    private final File configDir;
    private final File configFile;
    private final Gson gson;

    public CfgManager() {
        this.configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pocoyo");
        this.configFile = new File(configDir, "default.pocou");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::saveConfig));
    }

    public void saveConfig() {
        try {
            JsonObject rootJson = new JsonObject();
            JsonObject modulesJson = new JsonObject();

            if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getModuleManager() != null) {
                for (Module module : PocoyoVisual.INSTANCE.getModuleManager().getModules()) {
                    JsonObject moduleJson = new JsonObject();
                    moduleJson.addProperty("toggled", module.isToggled());

                    JsonObject settingsJson = new JsonObject();
                    for (Setting setting : module.getSettings()) {
                        if (setting instanceof BooleanSetting bool) {
                            settingsJson.addProperty(bool.getName(), bool.isEnabled());
                        } else if (setting instanceof NumberSetting number) {
                            settingsJson.addProperty(number.getName(), number.getValue());
                        } else if (setting instanceof ModeSetting mode) {
                            settingsJson.addProperty(mode.getName(), mode.getCurrentMode());
                        } else if (setting instanceof ColorSetting color) {
                            settingsJson.addProperty(color.getName(), color.getColor());
                        } else if (setting instanceof KeybindSetting keybind) {
                            settingsJson.addProperty(keybind.getName(), keybind.getKey());
                        }
                    }
                    moduleJson.add("settings", settingsJson);
                    modulesJson.add(module.getName(), moduleJson);
                }
            }
            rootJson.add("modules", modulesJson);

            JsonObject hudJson = new JsonObject();
            if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getHudManager() != null) {
                for (HudComponent component : PocoyoVisual.INSTANCE.getHudManager().getComponents()) {
                    JsonObject compJson = new JsonObject();
                    compJson.addProperty("enabled", component.isEnabled());
                    compJson.addProperty("x", component.getX());
                    compJson.addProperty("y", component.getY());
                    compJson.addProperty("alpha", component.getAlpha());
                    compJson.addProperty("scale", component.getScale());

                    hudJson.add(component.getName(), compJson);
                }
            }
            rootJson.add("hud", hudJson);

            JsonObject themeJson = new JsonObject();
            if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getThemeManager() != null) {
                ThemeManager.ThemePreset currentTheme = PocoyoVisual.INSTANCE.getThemeManager().getCurrentPreset();
                if (currentTheme != null) {
                    themeJson.addProperty("name", currentTheme.name);
                }
            }
            rootJson.add("theme", themeJson);

            JsonObject commandJson = new JsonObject();
            if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getCommandManager() != null) {
                commandJson.addProperty("prefix", PocoyoVisual.INSTANCE.getCommandManager().getCommandPrefix());
            }
            rootJson.add("command", commandJson);

            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(rootJson, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        if (!configFile.exists()) return;

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject rootJson = JsonParser.parseReader(reader).getAsJsonObject();

            if (rootJson.has("modules") && PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getModuleManager() != null) {
                JsonObject modulesJson = rootJson.getAsJsonObject("modules");

                for (Module module : PocoyoVisual.INSTANCE.getModuleManager().getModules()) {
                    if (!modulesJson.has(module.getName())) continue;

                    JsonObject moduleJson = modulesJson.getAsJsonObject(module.getName());

                    if (moduleJson.has("toggled")) {
                        module.setToggled(moduleJson.get("toggled").getAsBoolean());
                    }

                    if (moduleJson.has("settings")) {
                        JsonObject settingsJson = moduleJson.getAsJsonObject("settings");

                        for (Setting setting : module.getSettings()) {
                            if (!settingsJson.has(setting.getName())) continue;

                            if (setting instanceof BooleanSetting bool) {
                                bool.setValue(settingsJson.get(bool.getName()).getAsBoolean());
                            } else if (setting instanceof NumberSetting number) {
                                number.setValue(settingsJson.get(number.getName()).getAsDouble());
                            } else if (setting instanceof ModeSetting mode) {
                                mode.setCurrentMode(settingsJson.get(mode.getName()).getAsString());
                            } else if (setting instanceof ColorSetting color) {
                                color.setColor(settingsJson.get(color.getName()).getAsInt());
                            } else if (setting instanceof KeybindSetting keybind) {
                                keybind.setKey(settingsJson.get(keybind.getName()).getAsInt());
                            }
                        }
                    }
                }
            }

            if (rootJson.has("hud") && PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getHudManager() != null) {
                JsonObject hudJson = rootJson.getAsJsonObject("hud");

                for (HudComponent component : PocoyoVisual.INSTANCE.getHudManager().getComponents()) {
                    if (!hudJson.has(component.getName())) continue;

                    JsonObject compJson = hudJson.getAsJsonObject(component.getName());

                    if (compJson.has("enabled")) component.setEnabled(compJson.get("enabled").getAsBoolean());
                    if (compJson.has("x")) component.setX(compJson.get("x").getAsInt());
                    if (compJson.has("y")) component.setY(compJson.get("y").getAsInt());
                    if (compJson.has("alpha")) component.setAlpha(compJson.get("alpha").getAsFloat());
                    if (compJson.has("scale")) component.setScale(compJson.get("scale").getAsFloat());
                }
            }

            if (rootJson.has("theme") && PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getThemeManager() != null) {
                JsonObject themeJson = rootJson.getAsJsonObject("theme");
                if (themeJson.has("name")) {
                    String themeName = themeJson.get("name").getAsString();
                    for (ThemeManager.ThemePreset preset : PocoyoVisual.INSTANCE.getThemeManager().getPresets()) {
                        if (preset.name.equals(themeName)) {
                            PocoyoVisual.INSTANCE.getThemeManager().setCurrentPreset(preset);
                            break;
                        }
                    }
                }
            }

            if (rootJson.has("command") && PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getCommandManager() != null) {
                JsonObject commandJson = rootJson.getAsJsonObject("command");
                if (commandJson.has("prefix")) {
                    String prefix = commandJson.get("prefix").getAsString();
                    PocoyoVisual.INSTANCE.getCommandManager().setCommandPrefix(prefix);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}