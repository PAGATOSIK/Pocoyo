package main.client.render;

import java.util.Locale;

public enum CircleMode {
    CLASSIC,
    DOUBLE,
    SHOCKWAVE,
    GRADIENT,
    RGB,
    ENERGY_WALL,
    GLOW,
    HEXAGON;

    public CircleMode next() {
        CircleMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public static CircleMode parse(String name, CircleMode fallback) {
        if (name == null) {
            return fallback;
        }
        for (CircleMode mode : values()) {
            if (mode.name().equalsIgnoreCase(name.trim())) {
                return mode;
            }
        }
        try {
            return valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}