package main.client.hud.renderer;

import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.render.RoundedUtils;
import main.client.render.UiText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class EffectListRenderer extends HudComponent {

    public EffectListRenderer(int defaultX, int defaultY) {
        super("EffectList", defaultX, defaultY);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        var mc = MinecraftClient.getInstance();
        var textRenderer = mc.textRenderer;
        var themeManager = PocoyoVisual.INSTANCE.getThemeManager();

        int x = getX();
        int y = getY();
        int padding = 8;

        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) x, (float) y);
        context.getMatrices().scale(getScale(), getScale());
        context.getMatrices().translate((float) -x, (float) -y);

        try {
            List<StatusEffectInstance> activeEffects = new ArrayList<>();
            if (mc.player != null) {
                activeEffects.addAll(mc.player.getStatusEffects());
            }

            if (activeEffects.isEmpty()) {
                return;
            } else {
                renderFullEffectList(context, textRenderer, themeManager, x, y, padding, activeEffects);
            }
        } finally {
            context.getMatrices().popMatrix();
        }
    }

    private void renderFullEffectList(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                      main.client.manager.ThemeManager themeManager, int x, int y, int padding,
                                      List<StatusEffectInstance> activeEffects) {
        int accent = applyAlpha(themeManager.getEffectColor(), getAlpha());
        int textColor = applyAlpha(themeManager.getTextColor(), getAlpha());
        int bgColor = applyAlpha((0xFF << 24) | 0x00141414, getAlpha());

        // Calculate dimensions
        int maxEffectWidth = 0;
        for (StatusEffectInstance effect : activeEffects) {
            var statusEffect = effect.getEffectType();
            String effectName = statusEffect.value().getName().getString();
            String amplifierText = effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "";
            String timeText = formatDuration(effect);
            int effectWidth = textRenderer.getWidth(UiText.of(effectName + amplifierText + " " + timeText)) + 25;
            if (effectWidth > maxEffectWidth) {
                maxEffectWidth = effectWidth;
            }
        }

        int headerHeight = 12;
        int totalWidth = Math.max(maxEffectWidth + padding * 2, textRenderer.getWidth(UiText.of("Эффекты")) + padding * 2);
        int totalHeight = padding + headerHeight + 6 + (activeEffects.size() * 22) + padding;

        // Draw background
        RoundedUtils.drawRoundRect(context, x, y, totalWidth, totalHeight, 8, bgColor);

        // Draw Header (Title only, without square icon)
        int headerX = x + padding;
        int headerY = y + padding;

        context.drawText(textRenderer, UiText.of("Эффекты"), headerX, headerY, textColor, false);

        // Draw effects
        int effectY = headerY + headerHeight + 6;

        for (StatusEffectInstance effect : activeEffects) {
            var statusEffect = effect.getEffectType();
            String effectName = statusEffect.value().getName().getString();
            String amplifierText = effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "";
            String timeText = formatDuration(effect);

            int effectX = x + padding;

            // Стильный закругленный квадрат-иконка с первой буквой эффекта
            RoundedUtils.drawRoundRect(context, effectX, effectY, 16, 16, 4, accent);
            String iconChar = !effectName.isEmpty() ? effectName.substring(0, 1).toUpperCase() : "•";
            int charWidth = textRenderer.getWidth(UiText.of(iconChar));
            context.drawText(textRenderer, UiText.of(iconChar), effectX + (16 - charWidth) / 2, effectY + 4, 0xFFFFFFFF, false);

            // Draw effect name and amplifier
            int nameX = effectX + 22;
            context.drawText(textRenderer, UiText.of(effectName + amplifierText), nameX, effectY + 4, textColor, false);

            // Draw time after the text
            int timeX = nameX + textRenderer.getWidth(UiText.of(effectName + amplifierText)) + 10;
            context.drawText(textRenderer, UiText.of(timeText), timeX, effectY + 4, applyAlpha(0xFF808080, getAlpha()), false);

            effectY += 22;
        }
    }

    private String formatDuration(StatusEffectInstance effect) {
        int duration = effect.getDuration();
        int seconds = duration / 20;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%d:%02d", minutes, remainingSeconds);
        } else {
            return String.format("%ds", remainingSeconds);
        }
    }

    private int applyAlpha(int color, float factor) {
        int a = (int) (((color >> 24) & 0xFF) * factor);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    @Override
    public int getEditorWidth() { return (int) (150 * getScale()); }
    @Override
    public int getEditorHeight() { return (int) (100 * getScale()); }
}