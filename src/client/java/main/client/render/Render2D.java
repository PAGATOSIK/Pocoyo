package main.client.render;

import net.minecraft.client.gui.DrawContext;
import com.mojang.blaze3d.systems.RenderSystem;

public class Render2D {

    // В 1.21.8 мы передаем DrawContext, который предоставляет сам Minecraft в эвентах рендера
    public static void drawRect(DrawContext context, float x, float y, float width, float height, int color) {
        int x1 = (int) x;
        int y1 = (int) y;
        int x2 = (int) (x + width);
        int y2 = (int) (y + height);

        context.fill(x1, y1, x2, y2, color);
    }

    public static void drawBorder(DrawContext context, float x, float y, float width, float height, float thickness, int color) {
        drawRect(context, x, y, width, thickness, color); // Топ
        drawRect(context, x, y + height - thickness, width, thickness, color); // Боттом
        drawRect(context, x, y + thickness, thickness, height - (thickness * 2), color); // Лево
        drawRect(context, x + width - thickness, y + thickness, thickness, height - (thickness * 2), color); // Право
    }
}