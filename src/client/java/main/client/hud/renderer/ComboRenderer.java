package main.client.hud.renderer;

import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.manager.ComboManager;
import main.client.render.UiText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;

public class ComboRenderer extends HudComponent {

    private int lastCombo = 0;
    private float flashAlpha = 0.0f;
    private Entity lastTargetEntity = null;

    public ComboRenderer(int defaultX, int defaultY) {
        super("Combo", defaultX, defaultY);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        var mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        var textRenderer = mc.textRenderer;
        var themeManager = PocoyoVisual.INSTANCE.getThemeManager();
        var comboManager = ComboManager.getInstance();

        // Отслеживание смены цели (если бьем другую сущность — сброс)
        Entity currentTarget = mc.targetedEntity;
        if (currentTarget != null && lastTargetEntity != null && currentTarget != lastTargetEntity) {
            comboManager.reset();
        }
        lastTargetEntity = currentTarget;

        // Обновляем состояние менеджера комбо
        comboManager.update();

        int combo = comboManager.getCombo();
        if (combo == 0) {
            lastCombo = 0;
            flashAlpha = 0.0f;
            return;
        }

        if (combo != lastCombo) {
            flashAlpha = 1.0f;
            lastCombo = combo;
        }

        if (flashAlpha > 0.0f) {
            flashAlpha = Math.max(0.0f, flashAlpha - (tickDelta * 0.1f));
        }

        int x = getX();
        int y = getY();

        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) x, (float) y);
        context.getMatrices().scale(getScale(), getScale());
        context.getMatrices().translate((float) -x, (float) -y);

        try {
            String comboValueStr = String.valueOf(combo);
            String suffixStr = "x";

            int numWidth = textRenderer.getWidth(UiText.of(comboValueStr));
            int unscaledHeight = 16;

            int globalAlpha = (int) (255 * getAlpha());
            int textColor = applyAlpha(themeManager.getTextColor(), getAlpha());
            int accentColor = applyAlpha(themeManager.getEffectColor(), getAlpha());

            float animScale = comboManager.getAnimationScale();
            float animOffset = (animScale - 1.0f) * 16;

            int animX = x - (int) animOffset;
            int animY = y - (int) animOffset;

            // --- 1. ЗАДНИЙ ФОН: Опущен ниже и увеличен ---
            if (flashAlpha > 0.0f) {
                context.getMatrices().pushMatrix();

                float numCenterX = animX + (numWidth * 2.0f) / 2f;
                float numCenterY = animY + unscaledHeight;

                context.getMatrices().translate(numCenterX, numCenterY);
                context.getMatrices().scale(3.0f * animScale, 3.0f * animScale);
                context.getMatrices().translate(-numCenterX, -numCenterY);

                int bgFlashAlpha = (int) (globalAlpha * 0.4f * flashAlpha);
                int flashColor = (bgFlashAlpha << 24) | (accentColor & 0x00FFFFFF);

                context.getMatrices().pushMatrix();
                context.getMatrices().translate(animX, animY);

                context.drawText(
                        textRenderer,
                        UiText.of(comboValueStr),
                        0,
                        8,
                        flashColor,
                        false
                );
                context.getMatrices().popMatrix();

                context.getMatrices().popMatrix();
            }

            // --- 2. ОСНОВНОЙ ТЕКСТ КОМБО (увеличен в 2 раза) ---
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(animX, animY);
            context.getMatrices().scale(2.0f, 2.0f);

            int textX = 0;
            int textY = 4;

            context.drawText(textRenderer, UiText.of(comboValueStr), textX, textY, textColor, false);
            context.drawText(textRenderer, UiText.of(suffixStr), textX + numWidth + 2, textY, accentColor, false);

            context.getMatrices().popMatrix();

        } finally {
            context.getMatrices().popMatrix();
        }
    }

    private int applyAlpha(int color, float factor) {
        int a = (int) (((color >> 24) & 0xFF) * factor);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    @Override
    public int getEditorWidth() { return (int) (55 * 2 * getScale()); }
    @Override
    public int getEditorHeight() { return (int) (16 * 2 * getScale()); }
}