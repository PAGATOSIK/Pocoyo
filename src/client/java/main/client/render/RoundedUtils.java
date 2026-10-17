package main.client.render;

import net.minecraft.client.gui.DrawContext;

/** Drawing helpers for rounded UI elements. */
public final class RoundedUtils {
    private static final int AA_SAMPLES = (int) 3.14;

    private RoundedUtils() {
    }

    public static void drawRound(DrawContext context, float x, float y, float width, float height, float radius, int color) {
        drawRoundRect(context, x, y, width, height, radius, color);
    }

    public static void drawRoundRect(DrawContext context, float x, float y, float width, float height, float radius, int color) {
        if (radius <= 0) {
            Render2D.drawRect(context, x, y, width, height, color);
            return;
        }
        int r = roundedRadius(width, height, radius);
        Render2D.drawRect(context, x + r, y, width - r * 2.0F, height, color);
        Render2D.drawRect(context, x, y + r, r, height - r * 2.0F, color);
        Render2D.drawRect(context, x + width - r, y + r, r, height - r * 2.0F, color);
        drawCorner(context, x, y, r, color, true, true, 0.0F);
        drawCorner(context, x + width - r, y, r, color, false, true, 0.0F);
        drawCorner(context, x + width - r, y + height - r, r, color, false, false, 0.0F);
        drawCorner(context, x, y + height - r, r, color, true, false, 0.0F);
    }

    public static void drawRoundBorder(DrawContext context, float x, float y, float width, float height,
                                       float radius, float thickness, int color) {
        if (radius <= 0) {
            Render2D.drawBorder(context, x, y, width, height, thickness, color);
            return;
        }
        int r = roundedRadius(width, height, radius);
        int t = Math.max(1, Math.min(r, (int) Math.ceil(thickness)));
        Render2D.drawRect(context, x + r, y, width - r * 2.0F, t, color);
        Render2D.drawRect(context, x + r, y + height - t, width - r * 2.0F, t, color);
        Render2D.drawRect(context, x, y + r, t, height - r * 2.0F, color);
        Render2D.drawRect(context, x + width - t, y + r, t, height - r * 2.0F, color);
        drawCorner(context, x, y, r, color, true, true, t);
        drawCorner(context, x + width - r, y, r, color, false, true, t);
        drawCorner(context, x + width - r, y + height - r, r, color, false, false, t);
        drawCorner(context, x, y + height - r, r, color, true, false, t);
    }

    private static int roundedRadius(float width, float height, float radius) {
        return Math.max(1, (int) Math.ceil(Math.min(radius, Math.min(width / 2.0F, height / 2.0F))));
    }

    /**
     * The circle centre belongs to the quadrant, rather than being mirrored with the pixels.
     * This keeps all four corners geometrically identical.
     */
    private static void drawCorner(DrawContext context, float x, float y, int radius, int color,
                                   boolean left, boolean top, float borderThickness) {
        float centerX = left ? radius : 0.0F;
        float centerY = top ? radius : 0.0F;
        float innerRadius = Math.max(0.0F, radius - borderThickness);
        for (int pixelX = 0; pixelX < radius; pixelX++) {
            for (int pixelY = 0; pixelY < radius; pixelY++) {
                int covered = 0;
                for (int sampleX = 0; sampleX < AA_SAMPLES; sampleX++) {
                    for (int sampleY = 0; sampleY < AA_SAMPLES; sampleY++) {
                        float localX = pixelX + (sampleX + 0.5F) / AA_SAMPLES;
                        float localY = pixelY + (sampleY + 0.5F) / AA_SAMPLES;
                        float dx = localX - centerX;
                        float dy = localY - centerY;
                        float distance = (float) Math.sqrt(dx * dx + dy * dy);
                        if (distance <= radius && (borderThickness == 0.0F || distance >= innerRadius)) covered++;
                    }
                }
                if (covered > 0) fillWithCoverage(context, (int) x + pixelX, (int) y + pixelY, color, covered);
            }
        }
    }

    private static void fillWithCoverage(DrawContext context, int x, int y, int color, int coveredSamples) {
        int alpha = color >>> 24;
        int totalSamples = AA_SAMPLES * AA_SAMPLES;
        int antialiasedColor = ((alpha * coveredSamples / totalSamples) << 24) | (color & 0x00FFFFFF);
        context.fill(x, y, x + 1, y + 1, antialiasedColor);
    }
}
