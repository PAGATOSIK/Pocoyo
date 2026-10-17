package main.client.module;

import main.client.PocoyoVisual;
import main.client.setting.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final Category category;
    private int key;
    private boolean toggled;
    private final List<Setting> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(List.of(settings));
    }

    public void onTick() {}

    public void toggle() {
        setToggled(!toggled);
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (toggled) {
            onEnable();
            // Пример интеграции с системой эвентов
            // PocoyoVisual.INSTANCE.getEventManager().register(this);
        } else {
            onDisable();
            // PocoyoVisual.INSTANCE.getEventManager().unregister(this);
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public boolean isToggled() { return toggled; }
    public List<Setting> getSettings() { return settings; }
}