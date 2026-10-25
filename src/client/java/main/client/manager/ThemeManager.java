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

    public static class EffectColorPreset {
        public final String name;
        public final int effectColor;

        public EffectColorPreset(String name, int effectColor) {
            this.name = name;
            this.effectColor = effectColor;
        }
    }

    private final List<ThemePreset> presets = new ArrayList<>();
    private ThemePreset currentPreset;

    private final List<EffectColorPreset> effectPresets = new ArrayList<>();
    private EffectColorPreset currentEffectPreset;

    public ThemeManager() {
        // Стандартные пресеты тем (обычная тема)
        presets.add(new ThemePreset("Blue", 0xFF4BA5DE, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Green", 0xFF43BFA2, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Purple", 0xFF9A7AF5, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Pink", 0xFFED7DA2, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Orange", 0xFFF1AD4E, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("Red", 0xFFE74C3C, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));
        presets.add(new ThemePreset("White", 0xFFFFFFFF, 0xE61E1E2F, 0xCC2F2F46, 0xFFFFFFFF));

        currentPreset = presets.get(0);

        // Пресеты цветов эффектов
        effectPresets.add(new EffectColorPreset("Blue", 0xFF4BA5DE));
        effectPresets.add(new EffectColorPreset("Green", 0xFF43BFA2));
        effectPresets.add(new EffectColorPreset("Purple", 0xFF9A7AF5));
        effectPresets.add(new EffectColorPreset("Pink", 0xFFED7DA2));
        effectPresets.add(new EffectColorPreset("Orange", 0xFFF1AD4E));
        effectPresets.add(new EffectColorPreset("Red", 0xFFE74C3C));
        effectPresets.add(new EffectColorPreset("Cyan", 0xFF00CED1));
        effectPresets.add(new EffectColorPreset("Yellow", 0xFFFFD700));
        effectPresets.add(new EffectColorPreset("Lime", 0xFF32CD32));
        effectPresets.add(new EffectColorPreset("Magenta", 0xFFFF00FF));
        effectPresets.add(new EffectColorPreset("White", 0xFFFFFFFF));

        currentEffectPreset = effectPresets.get(0);
    }

    public List<ThemePreset> getPresets() {
        return new ArrayList<>(presets);
    }

    public void addThemePreset(ThemePreset preset) {
        presets.add(preset);
    }

    public ThemePreset getCurrentPreset() {
        return currentPreset;
    }

    public void setCurrentPreset(ThemePreset preset) {
        this.currentPreset = preset;
    }

    public List<EffectColorPreset> getEffectPresets() {
        return new ArrayList<>(effectPresets);
    }

    public void addEffectColorPreset(EffectColorPreset preset) {
        effectPresets.add(preset);
    }

    public EffectColorPreset getCurrentEffectPreset() {
        return currentEffectPreset;
    }

    public void setCurrentEffectPreset(EffectColorPreset preset) {
        this.currentEffectPreset = preset;
    }

    public int getAccentColor() {
        return currentPreset.accentColor;
    }

    public int getEffectColor() {
        return currentEffectPreset.effectColor;
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