package main.client.manager;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public static class ThemePreset {
        public final String name;
        public final int accentColor;
        public final int backgroundColor;
        public final int moduleBgColor;
        public final int textColor;

        public ThemePreset(String name, int accentColor, int backgroundColor, int moduleBgColor, int textColor) {
            this.name = name;
            this.accentColor = accentColor;
            this.backgroundColor = backgroundColor;
            this.moduleBgColor = moduleBgColor;
            this.textColor = textColor;
        }
    }

    private final List<ThemePreset> presets = new ArrayList<>();
    private ThemePreset currentPreset;

    public ThemeManager() {
        // Стандартные пресеты тем
        presets.add(new ThemePreset("Blue", 0xFF4BA5DE, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Green", 0xFF43BFA2, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Purple", 0xFF9A7AF5, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Pink", 0xFFED7DA2, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Orange", 0xFFF1AD4E, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Red", 0xFFE74C3C, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        
        currentPreset = presets.get(0);
    }

    public List<ThemePreset> getPresets() {
        return new ArrayList<>(presets);
    }

    public ThemePreset getCurrentPreset() {
        return currentPreset;
    }

    public void setCurrentPreset(ThemePreset preset) {
        this.currentPreset = preset;
    }

    public int getAccentColor() {
        return currentPreset.accentColor;
    }

    public int getBackgroundColor() {
        return currentPreset.backgroundColor;
    }

    public int getModuleBgColor() {
        return currentPreset.moduleBgColor;
    }

    public int getTextColor() {
        return currentPreset.textColor;
    }
}