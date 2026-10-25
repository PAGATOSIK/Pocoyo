package main.client.modules.visual;

import main.client.module.Category;
import main.client.module.Module;
import main.client.setting.NumberSetting;

public class Radius extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 4.0, 1.0, 20.0, 0.5);

    public Radius() {
        super("Radius", Category.VISUAL, "настраивает радиус скругления элементов");
        addSettings(radius);
    }

    public double getRadius() {
        return radius.getValue();
    }
}
