package main.client.hud.renderer;

import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.render.RoundedUtils;
import main.client.render.UiText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class WatermarkRenderer extends HudComponent {

    private static final Identifier LOGO_TEXTURE = Identifier.of("pocou", "texture/logo.png");
    private static final int BASE_BG_COLOR = 0x00141414; // RGB (20, 20, 20)

    private boolean showFps = true;
    private boolean showPing = true;
    private boolean showServer = true;
    private boolean showName = true;

    public WatermarkRenderer(int defaultX, int defaultY) {
        super("Watermark", defaultX, defaultY);
    }

    private int getRadius() {
        int radius = 4;
        if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getModuleManager() != null) {
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

        int x = getX();
        int y = getY();
        int padding = 8;
        int logoSize = 15;

        // Входим в матричный стек
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) x, (float) y);
        context.getMatrices().scale(getScale(), getScale());
        context.getMatrices().translate((float) -x, (float) -y);

        try {
            List<String> parts = new ArrayList<>();
            if (showFps) parts.add(mc.getCurrentFps() + " FPS");
            if (showPing) parts.add(getPing(mc) + " ms");
            if (showServer) parts.add(getServerIp(mc));
            if (showName) parts.add("name:" + (mc.getSession() != null ? mc.getSession().getUsername() : "Player"));

            StringBuilder textBuilder = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                textBuilder.append(parts.get(i));
                if (i < parts.size() - 1) textBuilder.append(" | ");
            }

            String fullText = textBuilder.toString();
            int prefixSeparatorWidth = fullText.isEmpty() ? 0 : textRenderer.getWidth(UiText.of(" | "));
            int textWidth = textRenderer.getWidth(UiText.of(fullText));

            int contentWidth = logoSize + prefixSeparatorWidth + textWidth;
            int totalWidth = padding + contentWidth + padding;
            int totalHeight = 20;

            int bgAlpha = (int) (255 * getAlpha());
            int backgroundColor = (bgAlpha << 24) | BASE_BG_COLOR;

            int radius = getRadius();
            RoundedUtils.drawRoundRect(context, x, y, totalWidth, totalHeight, Math.min(radius, 7), backgroundColor);

            int logoY = y + (totalHeight - logoSize) / 2;
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    LOGO_TEXTURE,
                    x + padding,
                    logoY,
                    0.0F, 0.0F,
                    logoSize, logoSize, logoSize, logoSize
            );

            if (!fullText.isEmpty()) {
                int textX = x + padding + logoSize;
                int textY = y + (totalHeight - 8) / 2;
                int textColor = applyAlpha(themeManager.getTextColor(), getAlpha());
                int accent = applyAlpha(accentColor(), getAlpha());

                renderFormattedText(context, textRenderer, fullText, textX, textY, textColor, accent);
            }
        } finally {
            // Обязательно восстанавливаем стек, даже если в процессе случился исключительный случай
            context.getMatrices().popMatrix();
        }
    }

    private void renderFormattedText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                     String text, int x, int y, int defaultColor, int accentColor) {
        String separator = " | ";
        int currentX = x;

        context.drawText(textRenderer, UiText.of(separator), currentX, y, accentColor, false);
        currentX += textRenderer.getWidth(UiText.of(separator));

        String[] tokens = text.split(" \\| ");

        for (int i = 0; i < tokens.length; i++) {
            context.drawText(textRenderer, UiText.of(tokens[i]), currentX, y, defaultColor, false);
            currentX += textRenderer.getWidth(UiText.of(tokens[i]));

            if (i < tokens.length - 1) {
                context.drawText(textRenderer, UiText.of(separator), currentX, y, accentColor, false);
                currentX += textRenderer.getWidth(UiText.of(separator));
            }
        }
    }

    private int applyAlpha(int color, float factor) {
        int a = (int) (((color >> 24) & 0xFF) * factor);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    private int getPing(MinecraftClient mc) {
        if (mc.player != null && mc.getNetworkHandler() != null) {
            ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            PlayerListEntry entry = networkHandler.getPlayerListEntry(mc.player.getUuid());
            if (entry != null) return entry.getLatency();
        }
        return 0;
    }

    private String getServerIp(MinecraftClient mc) {
        if (mc.isInSingleplayer()) return "Singleplayer";
        else if (mc.getCurrentServerEntry() != null) return mc.getCurrentServerEntry().address;
        return "Main Menu";
    }

    private int accentColor() {
        if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getThemeManager() != null) {
            return PocoyoVisual.INSTANCE.getThemeManager().getAccentColor();
        }
        return 0xFF4BA5DE;
    }

    public boolean isShowFps() { return showFps; }
    public void setShowFps(boolean showFps) { this.showFps = showFps; }
    public boolean isShowPing() { return showPing; }
    public void setShowPing(boolean showPing) { this.showPing = showPing; }
    public boolean isShowServer() { return showServer; }
    public void setShowServer(boolean showServer) { this.showServer = showServer; }
    public boolean isShowName() { return showName; }
    public void setShowName(boolean showName) { this.showName = showName; }

    @Override
    public int getEditorWidth() { return (int) (120 * getScale()); }
    @Override
    public int getEditorHeight() { return (int) (20 * getScale()); }
}