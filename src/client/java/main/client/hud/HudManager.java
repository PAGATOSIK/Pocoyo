package main.client.hud;

import main.client.hud.renderer.ArrayListRenderer;
import main.client.hud.renderer.TargetHudRenderer;
import main.client.hud.renderer.WatermarkRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private final List<HudComponent> components = new ArrayList<>();

    public void init() {
        // Регистрируем компоненты (передаем дефолтные X и Y координаты)
        components.add(new WatermarkRenderer(4, 4));
        components.add(new ArrayListRenderer(0, 4)); // X для ArrayList посчитаем динамически внутри
        components.add(new TargetHudRenderer(0, 128));
    }

    public void renderComponents(DrawContext context, float tickDelta) {
        for (HudComponent component : components) {
            if (component.isEnabled()) {
                component.render(context, tickDelta);
            }
        }
    }

    public List<HudComponent> getComponents() {
        return components;
    }
}