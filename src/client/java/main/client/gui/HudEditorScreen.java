package main.client.gui;

import main.client.PocoyoVisual;
import main.client.hud.HudComponent;
import main.client.render.RoundedUtils;
import main.client.render.UiText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {

    private static final int BG_DARK_GRAY = 0xCC141414;
    private static final int CARD_DISABLED_BG = 0x66141414;

    private final Screen parent;
    private HudComponent dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    // Всплывающее меню настроек HUD компонента
    private HudComponent selectedComponent = null;
    private boolean isDraggingAlpha = false;
    private boolean isDraggingScale = false;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("HUD editor"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var themeManager = PocoyoVisual.INSTANCE.getThemeManager();

        context.fillGradient(0, 0, width, height, BG_DARK_GRAY, BG_DARK_GRAY);

        int textColor = themeManager.getTextColor();
        int accentColor = themeManager.getAccentColor();

        context.drawCenteredTextWithShadow(textRenderer, UiText.of("HUD EDITOR"), width / 2, 12, textColor);
        context.drawCenteredTextWithShadow(textRenderer, UiText.of("Drag elements • Right Click for settings • ESC to return"), width / 2, 27, 0xFFA0A0A0);

        if (PocoyoVisual.INSTANCE == null || PocoyoVisual.INSTANCE.getHudManager() == null) return;

        for (HudComponent component : PocoyoVisual.INSTANCE.getHudManager().getComponents()) {
            int x = component.getX();
            int y = component.getY();
            int w = component.getEditorWidth();
            int h = component.getEditorHeight();
            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

            int fill = component.isEnabled() ? themeManager.getModuleBgColor() : CARD_DISABLED_BG;
            int borderColor = hovered ? accentColor : (component.isEnabled() ? 0x40FFFFFF : 0x20FFFFFF);

            RoundedUtils.drawRoundRect(context, x - 3, y - 3, w + 6, h + 6, 6, fill);
            RoundedUtils.drawRoundBorder(context, x - 3, y - 3, w + 6, h + 6, 6, 1, borderColor);

            context.drawText(textRenderer, UiText.of(component.getName()), x + 4, y + 4, textColor, false);

            if (component.isEnabled()) component.render(context, delta);
        }

        // Рисуем меню настроек если элемент выбран
        if (selectedComponent != null) {
            renderComponentSettings(context, mouseX, mouseY, accentColor);
        }
    }

    private void renderComponentSettings(DrawContext context, int mouseX, int mouseY, int accentColor) {
        int popupWidth = 140;
        int popupHeight = 85;
        int popupX = width / 2 - popupWidth / 2;
        int popupY = height / 2 - popupHeight / 2;

        RoundedUtils.drawRoundRect(context, popupX, popupY, popupWidth, popupHeight, 8, 0xF1141414);
        RoundedUtils.drawRoundBorder(context, popupX, popupY, popupWidth, popupHeight, 8, 1, accentColor);

        // Название
        context.drawText(textRenderer, UiText.of(selectedComponent.getName()), popupX + 10, popupY + 8, 0xFFFFFFFF, false);

        // Статус (Вкл / Выкл)
        String stateText = selectedComponent.isEnabled() ? "Enabled" : "Disabled";
        int stateColor = selectedComponent.isEnabled() ? 0xFF55FF55 : 0xFFFF5555;
        context.drawText(textRenderer, UiText.of(stateText), popupX + popupWidth - textRenderer.getWidth(UiText.of(stateText)) - 10, popupY + 8, stateColor, false);

        // 1. Ползунок Alpha
        int sliderX = popupX + 10;
        int sliderY1 = popupY + 32;
        int sliderWidth = popupWidth - 20;

        context.drawText(textRenderer, UiText.of(String.format("Alpha: %d%%", (int)(selectedComponent.getAlpha() * 100))), sliderX, sliderY1 - 9, 0xFFA0A0A0, false);
        RoundedUtils.drawRoundRect(context, sliderX, sliderY1, sliderWidth, 4, 2, 0x66555555);
        int alphaFilled = (int) (sliderWidth * selectedComponent.getAlpha());
        RoundedUtils.drawRoundRect(context, sliderX, sliderY1, alphaFilled, 4, 2, accentColor);

        // 2. Ползунок Scale
        int sliderY2 = popupY + 60;
        float scaleProgress = (selectedComponent.getScale() - 0.5f) / 1.0f;
        context.drawText(textRenderer, UiText.of(String.format("Scale: %.1fx", selectedComponent.getScale())), sliderX, sliderY2 - 9, 0xFFA0A0A0, false);
        RoundedUtils.drawRoundRect(context, sliderX, sliderY2, sliderWidth, 4, 2, 0x66555555);
        int scaleFilled = (int) (sliderWidth * scaleProgress);
        RoundedUtils.drawRoundRect(context, sliderX, sliderY2, scaleFilled, 4, 2, accentColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedComponent != null) {
            int popupWidth = 140;
            int popupHeight = 85;
            int popupX = width / 2 - popupWidth / 2;
            int popupY = height / 2 - popupHeight / 2;

            // Клик по переключателю состояния (Enabled)
            if (mouseX >= popupX + 80 && mouseX <= popupX + popupWidth && mouseY >= popupY + 5 && mouseY <= popupY + 20) {
                selectedComponent.setEnabled(!selectedComponent.isEnabled());
                return true;
            }

            // Клик по Alpha
            if (mouseX >= popupX + 10 && mouseX <= popupX + popupWidth - 10 && mouseY >= popupY + 25 && mouseY <= popupY + 40) {
                isDraggingAlpha = true;
                updateAlpha(mouseX, popupX + 10, popupWidth - 20);
                return true;
            }

            // Клик по Scale
            if (mouseX >= popupX + 10 && mouseX <= popupX + popupWidth - 10 && mouseY >= popupY + 50 && mouseY <= popupY + 68) {
                isDraggingScale = true;
                updateScale(mouseX, popupX + 10, popupWidth - 20);
                return true;
            }

            if (!(mouseX >= popupX && mouseX <= popupX + popupWidth && mouseY >= popupY && mouseY <= popupY + popupHeight)) {
                selectedComponent = null; // Закрыть меню если кликнули мимо
            }
            return true;
        }

        if (PocoyoVisual.INSTANCE == null || PocoyoVisual.INSTANCE.getHudManager() == null) return true;

        for (HudComponent component : PocoyoVisual.INSTANCE.getHudManager().getComponents()) {
            if (mouseX >= component.getX() && mouseX <= component.getX() + component.getEditorWidth()
                    && mouseY >= component.getY() && mouseY <= component.getY() + component.getEditorHeight()) {

                if (button == 1) { // ПКМ - открыть меню
                    selectedComponent = component;
                } else if (button == 0) { // ЛКМ - перетаскивание
                    dragging = component;
                    dragOffsetX = (int) mouseX - component.getX();
                    dragOffsetY = (int) mouseY - component.getY();
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selectedComponent != null) {
            int popupX = width / 2 - 70;
            if (isDraggingAlpha) updateAlpha(mouseX, popupX + 10, 120);
            if (isDraggingScale) updateScale(mouseX, popupX + 10, 120);
            return true;
        }

        if (dragging != null && button == 0) {
            dragging.setX((int) mouseX - dragOffsetX);
            dragging.setY((int) mouseY - dragOffsetY);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        isDraggingAlpha = false;
        isDraggingScale = false;
        return true;
    }

    private void updateAlpha(double mouseX, int sliderX, int sliderWidth) {
        float progress = (float) Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / sliderWidth));
        selectedComponent.setAlpha(progress);
    }

    private void updateScale(double mouseX, int sliderX, int sliderWidth) {
        float progress = (float) Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / sliderWidth));
        selectedComponent.setScale(0.5f + progress * 1.0f);
    }

    @Override
    public void close() { client.setScreen(parent); }

    @Override
    public boolean shouldPause() { return false; }
}