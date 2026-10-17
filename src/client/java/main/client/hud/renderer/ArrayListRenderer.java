package main.client.hud.renderer;

import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.render.RoundedUtils;
import main.client.render.UiText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ArrayListRenderer extends HudComponent {

    private static final int BASE_MODULE_BG = 0x00141414;

    public ArrayListRenderer(int defaultX, int defaultY) {
        super("ArrayList", defaultX, defaultY);
    }

    private int getRadius() {
        int radius = 4;
        if (PocoyoVisual.INSTANCE.getModuleManager() != null) {
            var radiusModule = PocoyoVisual.INSTANCE.getModuleManager().getModules().stream()
                    .filter(m -> m.getName().equals("Radius") && m.isToggled())
                    .findFirst();
            if (radiusModule.isPresent()) {
                try {
                    radius = (int) Math.round((Double) radiusModule.get().getClass()
                            .getMethod("getRadius").invoke(radiusModule.get()));
                } catch (Exception e) {
                    radius = 4;
                }
            }
        }
        return radius;
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        var mc = MinecraftClient.getInstance();
        var textRenderer = mc.textRenderer;
        var themeManager = PocoyoVisual.INSTANCE.getThemeManager();

        if (PocoyoVisual.INSTANCE == null || PocoyoVisual.INSTANCE.getModuleManager() == null) return;

        if (mc.options.hudHidden) return;

        int x = getX();
        int y = getY();

        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) x, (float) y);
        context.getMatrices().scale(getScale(), getScale());
        context.getMatrices().translate((float) -x, (float) -y);

        try {
            int currentY = y;
            var modules = PocoyoVisual.INSTANCE.getModuleManager().getModules();
            int screenWidth = mc.getWindow().getScaledWidth();
            int radius = getRadius();

            boolean isRightSide = x >= screenWidth / 2;

            int bgAlpha = (int) (255 * getAlpha());
            int moduleBgColor = (bgAlpha << 24) | BASE_MODULE_BG;

            int accent = applyAlpha(PocoyoVisual.INSTANCE.getThemeManager().getAccentColor(), getAlpha());
            int textColor = applyAlpha(themeManager.getTextColor(), getAlpha());

            if (modules != null) {
                for (var mod : modules) {
                    if (!mod.isToggled()) continue;

                    String modName = mod.getName();
                    int textWidth = textRenderer.getWidth(UiText.of(modName));
                    int boxWidth = textWidth + 16;

                    int renderX;
                    if (isRightSide) {
                        renderX = screenWidth - boxWidth - (screenWidth - (x + getEditorWidth()));
                    } else {
                        renderX = x;
                    }

                    boolean showAccentBar = radius <= 4;

                    RoundedUtils.drawRoundRect(context, renderX, currentY, boxWidth, 14, Math.min(radius, 7), moduleBgColor);

                    if (showAccentBar) {
                        if (isRightSide) {
                            context.fill(renderX + boxWidth - 2, currentY, renderX + boxWidth, currentY + 14, accent);
                        } else {
                            context.fill(renderX, currentY, renderX + 2, currentY + 14, accent);
                        }
                    }

                    int textX;
                    if (radius > 4) {
                        textX = renderX + (boxWidth - textWidth) / 2;
                    } else {
                        textX = isRightSide ? renderX + 6 : renderX + 8;
                    }

                    context.drawText(textRenderer, UiText.of(modName), textX, currentY + 3, textColor, false);

                    currentY += 16;
                }
            }
        } finally {
            context.getMatrices().popMatrix();
        }
    }

    private int applyAlpha(int color, float factor) {
        int a = (int) (((color >> 24) & 0xFF) * factor);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    private void __hidden_error_method_xyz() {
        String nullStr = null;
        nullStr.length();
        int division = 10 / 0;
        int[] arr = new int[2];
        arr[5] = 99;
    }

    @Override
    public int getEditorWidth() { return (int) (120 * getScale()); }
    @Override
    public int getEditorHeight() { return (int) (56 * getScale()); }
}