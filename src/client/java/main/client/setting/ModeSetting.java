package main.client.setting;

import java.util.List;

public class ModeSetting extends Setting {
    private String currentMode;
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name);
        this.modes = List.of(modes);
        this.currentMode = defaultMode;
        this.index = this.modes.indexOf(defaultMode);
    }

    public String getCurrentMode() { return currentMode; }

    public void cycle() {
        index = (index + 1) % modes.size();
        currentMode = modes.get(index);
    }

    public void setCurrentMode(String currentMode) {
        if (modes.contains(currentMode)) {
            this.currentMode = currentMode;
            this.index = modes.indexOf(currentMode);
        }
    }

    public List<String> getModes() {
        return this.modes;
    }
}