package main.client.setting;

public class ColorSetting extends Setting {
    private int color;

    public ColorSetting(String name, int defaultColor) {
        super(name);
        this.color = defaultColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setRGB(int r, int g, int b) {
        this.color = (255 << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}