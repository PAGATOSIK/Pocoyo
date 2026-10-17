package main.client.setting;

public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public boolean isEnabled() { return value; }
    public void setValue(boolean value) { this.value = value; }
}