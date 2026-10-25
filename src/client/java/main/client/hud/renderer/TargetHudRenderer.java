package main.client.hud.renderer;

import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.render.RoundedUtils;
import main.client.render.UiText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TargetHudRenderer extends HudComponent {

    private static final int BASE_BACKGROUND_COLOR = 0x00141414;
    private static final Identifier ZOMBIE_TEXTURE = Identifier.of("pocou", "texture/targethud/zombie.png");
    private static final Identifier SHEEP_TEXTURE = Identifier.of("pocou", "texture/targethud/sheep.png");

    private float animatedHealth = 0f;
    private LivingEntity lastTarget = null;
    private long lastTargetTime = 0L;
    private float alpha = 0f;

    public TargetHudRenderer(int defaultX, int defaultY) {
        super("TargetHUD", defaultX, defaultY);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        var mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        long currentTime = System.currentTimeMillis();

        LivingEntity currentTarget = null;
        if (mc.targetedEntity instanceof LivingEntity living) {
            currentTarget = living;
        }

        if (currentTarget != null) {
            lastTarget = currentTarget;
            lastTargetTime = currentTime;
        } else {
            if (currentTime - lastTargetTime > 3L) {
                lastTarget = null;
            }
        }

        boolean editing = isEditing();
        float targetAlpha = (lastTarget != null || editing) ? 1.0f : 0.0f;
        alpha = MathHelper.lerp(tickDelta * 0.1f, alpha, targetAlpha);

        if (alpha <= 0.001f) return;

        int x = getX();
        int y = getY();
        int width = 155;
        int height = 42;

        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) x, (float) y);
        context.getMatrices().scale(getScale(), getScale());
        context.getMatrices().translate((float) -x, (float) -y);

        try {
            LivingEntity targetToRender = lastTarget;
            var themeManager = PocoyoVisual.INSTANCE.getThemeManager();
            int accent = getAccentColor();

            float combinedAlpha = alpha * getAlpha();

            int bgColor = applyAlpha((0xFF << 24) | BASE_BACKGROUND_COLOR, combinedAlpha);
            int moduleBgColor = applyAlpha(themeManager.getModuleBgColor(), combinedAlpha);
            int accentColor = applyAlpha(accent, combinedAlpha);

            RoundedUtils.drawRoundRect(context, x, y, width, height, 8, bgColor);

            String name = targetToRender != null ? targetToRender.getName().getString() : "???";
            float health = targetToRender != null ? targetToRender.getHealth() : 67f;
            float maxHealth = targetToRender != null ? targetToRender.getMaxHealth() : 67f;

            animatedHealth = MathHelper.lerp(tickDelta * 0.15f, animatedHealth, health);

            int headSize = 28;
            int headX = x + 7;
            int headY = y + (height - headSize) / 2;

            if (targetToRender instanceof PlayerEntity player) {
                var skinTextures = mc.getSkinProvider().getSkinTextures(player.getGameProfile());
                PlayerSkinDrawer.draw(context, skinTextures, headX, headY, headSize);
            } else {
                Identifier mobTexture = null;
                if (targetToRender instanceof ZombieEntity) {
                    mobTexture = ZOMBIE_TEXTURE;
                } else if (targetToRender instanceof SheepEntity) {
                    mobTexture = SHEEP_TEXTURE;
                }

                if (mobTexture != null) {
                    drawMobTexture(context, mobTexture, headX, headY, headSize, combinedAlpha);
                } else {
                    RoundedUtils.drawRoundRect(context, headX, headY, headSize, headSize, 4, moduleBgColor);
                }
            }

            int textX = headX + headSize + 8;
            int textColor = applyAlpha(themeManager.getTextColor(), combinedAlpha);

            if (name.length() > 12) name = name.substring(0, 10) + "..";
            context.drawText(mc.textRenderer, UiText.of(name), textX, y + 8, textColor, false);

            String hpText = String.format("%.1f HP", health);
            int hpColor = applyAlpha((themeManager.getTextColor() & 0xFFFFFF) | 0x99000000, combinedAlpha);
            context.drawText(mc.textRenderer, UiText.of(hpText), x + width - mc.textRenderer.getWidth(hpText) - 8, y + 8, hpColor, false);

            int barX = textX;
            int barY = y + 26;
            int barWidth = width - (barX - x) - 8;
            int barHeight = 6;

            RoundedUtils.drawRoundRect(context, barX, barY, barWidth, barHeight, 3, moduleBgColor);

            float healthPercent = MathHelper.clamp(animatedHealth / maxHealth, 0.0f, 1.0f);
            int currentBarWidth = (int) (barWidth * healthPercent);

            if (currentBarWidth > 0) {
                RoundedUtils.drawRoundRect(context, barX, barY, currentBarWidth, barHeight, 3, accentColor);
            }
        } finally {
            context.getMatrices().popMatrix();
        }
    }

    private int applyAlpha(int color, float factor) {
        int a = (int) (((color >> 24) & 0xFF) * factor);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    private boolean isEditing() {
        var mc = MinecraftClient.getInstance();
        return mc.currentScreen instanceof main.client.gui.HudEditorScreen;
    }

    private int getAccentColor() {
        if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getThemeManager() != null) {
            return PocoyoVisual.INSTANCE.getThemeManager().getAccentColor();
        }
        return 0xFF4BA5DE;
    }

    @Override
    public int getEditorWidth() { return (int) (155 * getScale()); }
    @Override
    public int getEditorHeight() { return (int) (42 * getScale()); }

    private void drawMobTexture(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x, y,
                0.0F, 0.0F,
                size, size, size, size
        );
    }
}