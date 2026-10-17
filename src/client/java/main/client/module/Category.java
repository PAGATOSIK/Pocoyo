package main.client.module;

public enum Category {
    VISUAL("Visual"),
    GAME("Game"),
    THEME("Theme");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}