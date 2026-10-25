package main.client.modules.game;

import main.client.module.Category;
import main.client.module.Module;
import main.client.setting.ModeSetting;

public class WorldTime extends Module {

    private final ModeSetting timeMode =
            new ModeSetting("Time", "Day", "Sunrise", "Day", "Noon", "Sunset", "Night", "Midnight");

    public WorldTime() {
        super("WorldTime", Category.GAME, "изменяет время в игре (визуально)");
        addSettings(timeMode);
    }

    public long getCustomTime() {
        if (!isToggled()) {
            return -1; // -1 означает, что использовать оригинальное время
        }

        String mode = timeMode.getCurrentMode();
        return switch (mode) {
            case "Sunrise" -> 23000L;
            case "Day" -> 6000L;
            case "Noon" -> 6000L;
            case "Sunset" -> 12000L;
            case "Night" -> 18000L;
            case "Midnight" -> 18000L;
            default -> -1L;
        };
    }

    public String getCurrentTimeMode() {
        return timeMode.getCurrentMode();
    }
}
