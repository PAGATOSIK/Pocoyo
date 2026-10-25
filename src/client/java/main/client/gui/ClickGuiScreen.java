package main.client.gui;

import main.client.PocoyoVisual;
import main.client.manager.ThemeManager;
import main.client.module.Category;
import main.client.module.Module;
import main.client.render.RoundedUtils;
import main.client.render.UiText;
import main.client.setting.BooleanSetting;
import main.client.setting.ColorSetting;
import main.client.setting.KeybindSetting;
import main.client.setting.ModeSetting;
import main.client.setting.NumberSetting;
import main.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private static final int WINDOW_WIDTH = 480;
    private static final int WINDOW_HEIGHT = 350;
    private static final int SIDEBAR_WIDTH = 105;

    private static final int MAIN_WINDOW_BG = 0xE6141414;
    private static final int SIDEBAR_BG = 0x55282828;
    private static final int POPUP_BG = 0xDD141414;

    private static final int[] COLOR_PALETTE = {0xFF4BA5DE, 0xFF43BFA2, 0xFF9A7AF5, 0xFFED7DA2, 0xFFF1AD4E};

    private static final Identifier POCOYO_LOGO = Identifier.of("pocou", "texture/logo.png");
    private static final String VERSION = "v0.2";

    private Category selectedCategory = Category.VISUAL;
    private Module settingsModule;
    private Module hoveredModule;
    private NumberSetting draggedNumber;
    private KeybindSetting listeningKeybind;
    private int x, y;

    private boolean showingColorInput = false;
    private boolean isAddingThemeColor = false;
    private String colorInputText = "";
    private int colorInputCursor = 0;

    private float themeScrollOffset = 0.0F;
    private float targetScrollOffset = 0.0F;

    private final Map<String, Float> animationProgress = new HashMap<>();
    private long previousFrameNanos;

    private float openProgress = 0.0F;

    private final List<Module> cachedModules = new ArrayList<>();
    private int cachedPopupHeight = 0;
    private Text cachedPocoyoTitle;
    private final ModeSetting openModeSetting = null;

    public ClickGuiScreen() {
        super(Text.literal("PocoyoVisual Window"));
    }

    @Override
    protected void init() {
        this.x = (width - WINDOW_WIDTH) / 2;
        this.y = (height - WINDOW_HEIGHT) / 2;
        this.previousFrameNanos = System.nanoTime();
        this.openProgress = 0.0F;

        this.cachedPocoyoTitle = UiText.of(I18n.translate("pocoyo"));

        updateCategoryCache();
    }

    private void updateCategoryCache() {
        cachedModules.clear();
        if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getModuleManager() != null) {
            for (Module m : PocoyoVisual.INSTANCE.getModuleManager().getModules()) {
                if (m.getCategory() == selectedCategory) {
                    cachedModules.add(m);
                }
            }
        }
    }

    private void setSettingsModule(Module module) {
        this.settingsModule = module;
        this.listeningKeybind = null;
        if (module != null) {
            int heightSum = 0;
            for (Setting setting : module.getSettings()) {
                heightSum += settingHeight(setting);
            }
            this.cachedPopupHeight = 43 + heightSum;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float frameStep = frameStep();

        openProgress = animateRaw(openProgress, 1.0F, frameStep, 16.0F);
        int accent = accentColor();

        int backgroundAlpha = (int) (136 * openProgress);
        context.fillGradient(0, 0, width, height, (backgroundAlpha / 2) << 24, backgroundAlpha << 24);

        context.getMatrices().pushMatrix();
        float scale = 0.85F + (0.15F * openProgress);
        float centerX = width / 2.0F;
        float centerY = height / 2.0F;

        context.getMatrices().translate(centerX, centerY);
        context.getMatrices().scale(scale, scale);
        context.getMatrices().translate(-centerX, -centerY);
        RoundedUtils.drawRoundRect(context, x, y, WINDOW_WIDTH, WINDOW_HEIGHT, 14, MAIN_WINDOW_BG);
        RoundedUtils.drawRoundBorder(context, x, y, WINDOW_WIDTH, WINDOW_HEIGHT, 14, 1, 0xFF333333);
        RoundedUtils.drawRoundRect(context, x + 1, y + 1, SIDEBAR_WIDTH - 1, WINDOW_HEIGHT - 2, 13, SIDEBAR_BG);
        context.fill(x + SIDEBAR_WIDTH - 1, y + 13, x + SIDEBAR_WIDTH, y + WINDOW_HEIGHT - 13, SIDEBAR_BG);

        int categoryY = y + 20;
        for (Category category : Category.values()) {
            boolean selected = (category == selectedCategory);
            boolean hovered = within(mouseX, mouseY, x + 8, categoryY, SIDEBAR_WIDTH - 16, 23);

            float animation = animate("cat-" + category.name(), selected || hovered, frameStep, 14.0F);

            if (animation > 0.01F) {
                RoundedUtils.drawRoundRect(context, x + 8, categoryY, SIDEBAR_WIDTH - 16, 23, 7,
                        main.client.util.ColorUtils.withAlpha(accent, (int) (40 + 120 * animation)));
            }

            context.drawText(textRenderer, UiText.of(category.getName()), x + 17, categoryY + 8,
                    selected ? 0xFFFFFFFF : 0xFF808080, false);

            categoryY += 29;
        }

        int moduleX = x + SIDEBAR_WIDTH + 16;
        int moduleY = y + 48;

        context.drawText(textRenderer, UiText.of(selectedCategory.getName().toUpperCase()),
                moduleX, y + 19, 0xFF808080, false);

        // Update hovered module
        hoveredModule = null;
        if (selectedCategory != Category.THEME) {
            for (int i = 0; i < cachedModules.size(); i++) {
                int cardX = moduleX + (i % 2) * 154;
                int cardY = moduleY + (i / 2) * 44;
                if (within(mouseX, mouseY, cardX, cardY, 140, 34)) {
                    hoveredModule = cachedModules.get(i);
                    break;
                }
            }
        }

        if (selectedCategory == Category.THEME) {
            renderThemeButtons(context, moduleX + 3, moduleY, mouseX, mouseY, frameStep, accent);
        } else {
            for (int i = 0; i < cachedModules.size(); i++) {
                Module module = cachedModules.get(i);
                int cardX = moduleX + (i % 2) * 154;
                int cardY = moduleY + (i / 2) * 44;

                renderModuleCard(context, module, cardX, cardY, mouseX, mouseY, frameStep, accent);
            }
        }
        if (settingsModule != null) {
            renderSettingsPopup(context, mouseX, mouseY, frameStep);
        }

        if (showingColorInput) {
            renderColorInputDialog(context, mouseX, mouseY, frameStep, accent);
        }

        // Bottom left - Pocoyo logo and text
        int logoSize = 24;
        int logoX = x + 10;
        int logoY = y + WINDOW_HEIGHT - logoSize - 10;
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                POCOYO_LOGO,
                logoX, logoY,
                0.0F, 0.0F,
                logoSize, logoSize, logoSize, logoSize
        );
        context.drawText(textRenderer, cachedPocoyoTitle, logoX + logoSize + 5, logoY + 4, 0xFFFFFFFF, false);
        context.drawText(textRenderer, UiText.of("Визуал"), logoX + logoSize + 5, logoY + 16, 0xFF808080, false);

        // Bottom right - version
        Text versionText = UiText.of(VERSION);
        int versionX = x + WINDOW_WIDTH - textRenderer.getWidth(versionText) - 10;
        context.drawText(textRenderer, versionText, versionX, y + WINDOW_HEIGHT - 20, 0xFF808080, false);

        context.getMatrices().popMatrix();

        // Render module description above the GUI window (centered at top) - plain text without border
        if (hoveredModule != null) {
            String description = hoveredModule.getDescription();
            int descWidth = textRenderer.getWidth(UiText.of(description));
            int descX = (width - descWidth) / 2;
            int descY = y - 15;

            context.drawText(textRenderer, UiText.of(description), descX, descY, 0xFFE3ECFF, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderModuleCard(DrawContext context, Module module, int cardX, int cardY, int mouseX, int mouseY, float frameStep, int accent) {
        boolean hovered = within(mouseX, mouseY, cardX, cardY, 140, 34);
        float activity = animate("mod-" + module.getName(), module.isToggled() || hovered, frameStep, 14.0F);

        int fill = main.client.util.ColorUtils.lerp(0x33282828, main.client.util.ColorUtils.withAlpha(accent, 170), activity);

        RoundedUtils.drawRoundRect(context, cardX, cardY, 140, 34, 9, fill);
        RoundedUtils.drawRoundBorder(context, cardX, cardY, 140, 34, 9, 1,
                main.client.util.ColorUtils.lerp(0x55444444, main.client.util.ColorUtils.withAlpha(accent, 204), activity));

        context.drawText(textRenderer, UiText.of(module.getName()), cardX + 10, cardY + 8, 0xFFE3ECFF, false);

        RoundedUtils.drawRoundRect(context, cardX + 112, cardY + 10, 18, 12, 6,
                main.client.util.ColorUtils.lerp(0x33555555, accent, activity));

        RoundedUtils.drawRoundRect(context, cardX + 113 + (int) (8 * activity), cardY + 12, 8, 8, 4, 0xFFFFFFFF);

        context.drawText(textRenderer, UiText.of("RMB settings"), cardX + 10, cardY + 21, 0xFF808080, false);
    }

    private void renderSettingsPopup(DrawContext context, int mouseX, int mouseY, float frameStep) {
        int popupWidth = 200;
        // Размещение окна настроек СЛЕВА от основного GUI с отступом 10px
        int popupX = x - popupWidth - 10;
        int popupY = y + (WINDOW_HEIGHT - cachedPopupHeight) / 2;

        float popupAnim = animate("settings-popup", true, frameStep, 16.0F);

        RoundedUtils.drawRoundRect(context, popupX, popupY, popupWidth, cachedPopupHeight, 12, POPUP_BG);
        RoundedUtils.drawRoundBorder(context, popupX, popupY, popupWidth, cachedPopupHeight, 12, 1, 0xFF333333);

        context.drawText(textRenderer, UiText.of(settingsModule.getName() + " Settings"), popupX + 14, popupY + 13, 0xFFE3ECFF, false);

        int rowY = popupY + 35;

        for (Setting setting : settingsModule.getSettings()) {
            int rowHeight = settingHeight(setting);

            RoundedUtils.drawRoundRect(context, popupX + 9, rowY, popupWidth - 18, rowHeight - 3, 7, 0x33282828);
            context.drawText(textRenderer, UiText.of(setting.getName()), popupX + 16, rowY + 7, 0xFFDCE8FF, false);

            if (setting instanceof BooleanSetting toggle) {
                renderToggle(context, popupX + popupWidth - 43, rowY + 7, toggle.isEnabled());
            } else if (setting instanceof ModeSetting mode) {
                renderMode(context, mode, popupX + 16, rowY + 19);
            } else if (setting instanceof NumberSetting number) {
                renderSlider(context, number, popupX + 16, rowY + 25, popupWidth - 32);
            } else if (setting instanceof ColorSetting color) {
                renderColorChoices(context, color, popupX + 80, rowY + 6);
            } else if (setting instanceof KeybindSetting keybind) {
                renderKeybind(context, keybind, popupX + popupWidth - 65, rowY + 5);
            }

            rowY += rowHeight;
        }
    }

    private void renderColorInputDialog(DrawContext context, int mouseX, int mouseY, float frameStep, int accent) {
        int dialogWidth = 280;
        int dialogHeight = 120;
        // Position dialog to the LEFT of the main GUI
        int dialogX = x - dialogWidth - 10;
        int dialogY = y + (WINDOW_HEIGHT - dialogHeight) / 2;

        RoundedUtils.drawRoundRect(context, dialogX, dialogY, dialogWidth, dialogHeight, 12, POPUP_BG);
        RoundedUtils.drawRoundBorder(context, dialogX, dialogY, dialogWidth, dialogHeight, 12, 2, accent);

        String title = isAddingThemeColor ? "Add Theme Color" : "Add Effect Color";
        context.drawText(textRenderer, UiText.of(title), dialogX + 14, dialogY + 13, 0xFFE3ECFF, false);

        context.drawText(textRenderer, UiText.of("Enter Hex (#RRGGBB) or RGB (r,g,b):"), dialogX + 14, dialogY + 40, 0xFFDCE8FF, false);

        // Input field
        int inputX = dialogX + 14;
        int inputY = dialogY + 60;
        int inputWidth = dialogWidth - 28;
        int inputHeight = 30;

        RoundedUtils.drawRoundRect(context, inputX, inputY, inputWidth, inputHeight, 6, 0x33282828);
        RoundedUtils.drawRoundBorder(context, inputX, inputY, inputWidth, inputHeight, 6, 1, 0xFF666666);

        String displayText = colorInputText + (System.currentTimeMillis() % 1000 < 500 ? "|" : "");
        context.drawText(textRenderer, UiText.of(displayText), inputX + 8, inputY + 9, 0xFFFFFFFF, false);

        // Instructions
        context.drawText(textRenderer, UiText.of("Enter to confirm, ESC to cancel"), dialogX + 14, dialogY + 98, 0xFF808080, false);
    }

    private void renderKeybind(DrawContext context, KeybindSetting keybind, int bindX, int bindY) {
        boolean isListening = (listeningKeybind == keybind);
        String label = isListening ? "..." : GLFW.glfwGetKeyName(keybind.getKey(), 0);
        if (label == null || label.isEmpty()) {
            label = keybind.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT ? "RSHIFT" : "KEY_" + keybind.getKey();
        }

        RoundedUtils.drawRoundRect(context, bindX, bindY, 50, 16, 5, isListening ? 0xFF3B82F6 : 0x66555555);
        Text text = UiText.of(label.toUpperCase());
        int textWidth = textRenderer.getWidth(text);
        context.drawText(textRenderer, text, bindX + (50 - textWidth) / 2, bindY + 4, 0xFFFFFFFF, false);
    }

    private void renderToggle(DrawContext context, int toggleX, int toggleY, boolean enabled) {
        RoundedUtils.drawRoundRect(context, toggleX, toggleY, 24, 12, 6, enabled ? 0xFF4AA2DB : 0x667B9AAF);
        RoundedUtils.drawRoundRect(context, toggleX + (enabled ? 13 : 2), toggleY + 2, 8, 8, 4, 0xFFFFFFFF);
    }

    private void renderMode(DrawContext context, ModeSetting setting, int modeX, int modeY) {
        String value = setting.getCurrentMode();
        Text text = UiText.of(value);
        int valueWidth = textRenderer.getWidth(text);
        int buttonWidth = Math.max(valueWidth + 20, 75);

        int accent = accentColor();

        RoundedUtils.drawRoundRect(context, modeX, modeY, buttonWidth, 16, 5, main.client.util.ColorUtils.withAlpha(accent, 160));
        RoundedUtils.drawRoundBorder(context, modeX, modeY, buttonWidth, 16, 5, 1, accent);

        context.drawText(textRenderer, text, modeX + (buttonWidth - valueWidth) / 2, modeY + 4, 0xFFFFFFFF, false);
    }

    private void renderModePopup(DrawContext context, int mouseX, int mouseY) {
        if (openModeSetting == null) return;

        List<String> modes = openModeSetting.getModes();
        int popupWidth = 140;
        int popupHeight = 35 + (modes.size() * 22);

        // Центрируем мини-окошко по экрану
        int popupX = (width - popupWidth) / 2;
        int popupY = (height - popupHeight) / 2;

        int accent = accentColor();

        // Задний фон и обводка мини-окна
        RoundedUtils.drawRoundRect(context, popupX, popupY, popupWidth, popupHeight, 10, 0xF1141414);
        RoundedUtils.drawRoundBorder(context, popupX, popupY, popupWidth, popupHeight, 10, 1, accent);

        // Заголовок
        Text title = UiText.of(openModeSetting.getName());
        context.drawText(textRenderer, title, popupX + (popupWidth - textRenderer.getWidth(title)) / 2, popupY + 10, 0xFFFFFFFF, false);

        int buttonY = popupY + 28;

        // Кнопки вариантов мода
        for (String mode : modes) {
            boolean isSelected = mode.equals(openModeSetting.getCurrentMode());
            boolean isHovered = within(mouseX, mouseY, popupX + 10, buttonY, popupWidth - 20, 18);

            int btnColor;
            if (isSelected) {
                btnColor = main.client.util.ColorUtils.withAlpha(accent, 220);
            } else if (isHovered) {
                btnColor = 0x88444444;
            } else {
                btnColor = 0x44222222;
            }

            RoundedUtils.drawRoundRect(context, popupX + 10, buttonY, popupWidth - 20, 18, 5, btnColor);
            if (isSelected) {
                RoundedUtils.drawRoundBorder(context, popupX + 10, buttonY, popupWidth - 20, 18, 5, 1, 0xFFFFFFFF);
            }

            Text modeText = UiText.of(mode);
            context.drawText(textRenderer, modeText, popupX + (popupWidth - textRenderer.getWidth(modeText)) / 2, buttonY + 5, isSelected ? 0xFFFFFFFF : 0xFFB0B0B0, false);

            buttonY += 22;
        }
    }

    private void renderSlider(DrawContext context, NumberSetting setting, int sliderX, int sliderY, int sliderWidth) {
        double progress = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int filled = (int) Math.round(sliderWidth * progress);

        RoundedUtils.drawRoundRect(context, sliderX, sliderY, sliderWidth, 4, 2, 0x667995AA);
        RoundedUtils.drawRoundRect(context, sliderX, sliderY, filled, 4, 2, accentColor());
        RoundedUtils.drawRoundRect(context, sliderX + filled - 3, sliderY - 3, 10, 10, 5, 0xFFFFFFFF);

        String value = String.format(java.util.Locale.ROOT, "%.1f", setting.getValue());
        Text text = UiText.of(value);
        int valueWidth = textRenderer.getWidth(text);

        context.drawText(textRenderer, text, sliderX + sliderWidth - valueWidth, sliderY - 13, 0xFF4C728B, false);
    }

    private void renderColorChoices(DrawContext context, ColorSetting setting, int colorX, int colorY) {
        int currentColor = setting.getColor();
        int currentRGB = currentColor & 0x00FFFFFF;

        // Render color palette
        for (int i = 0; i < COLOR_PALETTE.length; i++) {
            boolean selected = (currentRGB == (COLOR_PALETTE[i] & 0x00FFFFFF));
            int drawX = colorX + i * 22;

            RoundedUtils.drawRoundRect(context, drawX, colorY, 16, 16, 8, COLOR_PALETTE[i]);
            if (selected) {
                RoundedUtils.drawRoundBorder(context, drawX - 2, colorY - 2, 20, 20, 10, 2, 0xFFFFFFFF);
            }
        }

        // Add custom color button
        int customX = colorX + COLOR_PALETTE.length * 22 + 10;
        boolean isCustom = true;
        for (int i = 0; i < COLOR_PALETTE.length; i++) {
            if (currentRGB == (COLOR_PALETTE[i] & 0x00FFFFFF)) {
                isCustom = false;
                break;
            }
        }

        RoundedUtils.drawRoundRect(context, customX, colorY, 16, 16, 8, currentColor);
        if (isCustom) {
            RoundedUtils.drawRoundBorder(context, customX - 2, colorY - 2, 20, 20, 10, 2, 0xFFFFFFFF);
        }

        // Draw "+" icon for custom color
        context.drawText(textRenderer, UiText.of("+"), customX + 5, colorY + 3, 0xFFFFFFFF, false);
    }

    private void renderThemeButtons(DrawContext context, int startX, int startY, int mouseX, int mouseY, float frameStep, int accent) {
        var themeManager = PocoyoVisual.INSTANCE.getThemeManager();

        int buttonSize = 40;
        int gap = 14;
        int buttonsPerRow = 5;
        int textOffset = 18;
        int contentHeight = WINDOW_HEIGHT - 100;

        // Update scroll with smooth animation
        targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, getThemeContentHeight() - contentHeight));
        themeScrollOffset += (targetScrollOffset - themeScrollOffset) * 0.1F;

        // Clip content to window bounds
        context.getMatrices().pushMatrix();
        context.enableScissor(startX, startY, startX + WINDOW_WIDTH - SIDEBAR_WIDTH - 30, startY + contentHeight);

        int renderY = startY - (int) themeScrollOffset;

        // Section label: Theme
        context.drawText(textRenderer, UiText.of("Theme"), startX, renderY - 2, 0xFFE3ECFF, false);

        // Render theme presets (square buttons)
        var presets = themeManager.getPresets();
        for (int i = 0; i < presets.size(); i++) {
            ThemeManager.ThemePreset preset = presets.get(i);
            int col = i % buttonsPerRow;
            int row = i / buttonsPerRow;
            int buttonX = startX + 3 + col * (buttonSize + gap);
            int buttonY = renderY + 15 + row * (buttonSize + textOffset);

            boolean selected = themeManager.getCurrentPreset() == preset;
            boolean hovered = within(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize);

            int bgColor = hovered ? main.client.util.ColorUtils.withAlpha(preset.accentColor, 140) : main.client.util.ColorUtils.withAlpha(preset.accentColor, 70);
            RoundedUtils.drawRoundRect(context, buttonX, buttonY, buttonSize, buttonSize, 8, bgColor);

            if (selected) {
                RoundedUtils.drawRoundBorder(context, buttonX - 2, buttonY - 2, buttonSize + 4, buttonSize + 4, 10, 2, 0xFFFFFFFF);
            }

            RoundedUtils.drawRoundRect(context, buttonX + 8, buttonY + 8, buttonSize - 16, buttonSize - 16, 4, preset.accentColor);

            Text presetText = UiText.of(preset.name);
            context.drawText(textRenderer, presetText, buttonX + (buttonSize - textRenderer.getWidth(presetText)) / 2, buttonY + buttonSize + 4, 0xFFDCE8FF, false);
        }

        // Section label: Effect Color
        int themeRows = (presets.size() + buttonsPerRow - 1) / buttonsPerRow;
        int effectLabelY = renderY + 15 + themeRows * (buttonSize + textOffset) + 25;
        context.drawText(textRenderer, UiText.of("Effect Color"), startX, effectLabelY - 2, 0xFFE3ECFF, false);

        // Render effect color presets (circular buttons)
        int effectStartY = effectLabelY + 15;
        var effectPresets = themeManager.getEffectPresets();
        for (int i = 0; i < effectPresets.size(); i++) {
            ThemeManager.EffectColorPreset preset = effectPresets.get(i);
            int col = i % buttonsPerRow;
            int row = i / buttonsPerRow;
            int buttonX = startX + 3 + col * (buttonSize + gap);
            int buttonY = effectStartY + row * (buttonSize + textOffset);

            boolean selected = themeManager.getCurrentEffectPreset() == preset;
            boolean hovered = within(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize);

            int bgColor = hovered ? main.client.util.ColorUtils.withAlpha(preset.effectColor, 140) : main.client.util.ColorUtils.withAlpha(preset.effectColor, 70);
            RoundedUtils.drawRoundRect(context, buttonX, buttonY, buttonSize, buttonSize, 8, bgColor);

            if (selected) {
                RoundedUtils.drawRoundBorder(context, buttonX - 2, buttonY - 2, buttonSize + 4, buttonSize + 4, 10, 2, 0xFFFFFFFF);
            }

            RoundedUtils.drawRoundRect(context, buttonX + 8, buttonY + 8, buttonSize - 16, buttonSize - 16, 4, preset.effectColor);

            Text presetText = UiText.of(preset.name);
            context.drawText(textRenderer, presetText, buttonX + (buttonSize - textRenderer.getWidth(presetText)) / 2, buttonY + buttonSize + 4, 0xFFDCE8FF, false);
        }

        context.disableScissor();
        context.getMatrices().popMatrix();

        // Render scrollbar
        renderScrollbar(context, startX + WINDOW_WIDTH - SIDEBAR_WIDTH - 40, startY, contentHeight, getThemeContentHeight());
    }

    private int getThemeContentHeight() {
        var themeManager = PocoyoVisual.INSTANCE.getThemeManager();
        int presets = themeManager.getPresets().size();
        int effectPresets = themeManager.getEffectPresets().size();
        int buttonSize = 40;
        int gap = 14;
        int buttonsPerRow = 5;
        int textOffset = 18;

        int themeRows = (presets + buttonsPerRow - 1) / buttonsPerRow;
        int effectRows = (effectPresets + buttonsPerRow - 1) / buttonsPerRow;

        return 15 + themeRows * (buttonSize + textOffset) + 25 + 15 + effectRows * (buttonSize + textOffset) + 20;
    }

    private void renderScrollbar(DrawContext context, int barX, int barY, int barHeight, int contentHeight) {
        if (contentHeight <= barHeight) return;

        int barWidth = 8;
        float scrollRatio = barHeight / (float) contentHeight;
        int thumbHeight = Math.max(20, (int) (barHeight * scrollRatio));
        float scrollProgress = themeScrollOffset / (contentHeight - barHeight);
        int thumbY = barY + (int) ((barHeight - thumbHeight) * scrollProgress);

        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x33333333);
        context.fill(barX, thumbY, barX + barWidth, thumbY + thumbHeight, 0xFF666666);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showingColorInput) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                showingColorInput = false;
                colorInputText = "";
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (colorInputText.length() > 0) {
                    colorInputText = colorInputText.substring(0, colorInputText.length() - 1);
                }
                return true;
            }
            return true;
        }

        if (listeningKeybind != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningKeybind.setKey(GLFW.GLFW_KEY_UNKNOWN);
            } else {
                listeningKeybind.setKey(keyCode);
            }
            listeningKeybind = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (showingColorInput) {
            if (colorInputText.length() < 20) {
                colorInputText += chr;
            }
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showingColorInput) {
            // Click outside dialog closes it
            int dialogWidth = 280;
            int dialogHeight = 120;
            int dialogX = (width - dialogWidth) / 2;
            int dialogY = (height - dialogHeight) / 2;
            if (!within(mouseX, mouseY, dialogX, dialogY, dialogWidth, dialogHeight)) {
                showingColorInput = false;
                colorInputText = "";
            }
            return true;
        }

        if (settingsModule != null) {
            return clickSetting(mouseX, mouseY, button);
        }

        int categoryY = y + 20;
        for (Category category : Category.values()) {
            if (within(mouseX, mouseY, x + 8, categoryY, SIDEBAR_WIDTH - 16, 23)) {
                if (selectedCategory != category) {
                    selectedCategory = category;
                    updateCategoryCache();
                }
                return true;
            }
            categoryY += 29;
        }

        if (selectedCategory == Category.THEME) {
            int themeStartX = x + SIDEBAR_WIDTH + 16;
            int themeStartY = y + 48;
            int buttonSize = 40;
            int gap = 14;
            int buttonsPerRow = 5;
            int textOffset = 18;
            int scrollOffset = (int) themeScrollOffset;

            var themeManager = PocoyoVisual.INSTANCE.getThemeManager();

            // Check theme presets
            var presets = themeManager.getPresets();
            for (int i = 0; i < presets.size(); i++) {
                int col = i % buttonsPerRow;
                int row = i / buttonsPerRow;
                int buttonX = themeStartX + 3 + col * (buttonSize + gap);
                int buttonY = themeStartY + 15 + row * (buttonSize + textOffset) - scrollOffset;

                if (within(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize)) {
                    themeManager.setCurrentPreset(presets.get(i));
                    return true;
                }
            }

            // Check effect color presets
            int themeRows = (presets.size() + buttonsPerRow - 1) / buttonsPerRow;
            int effectLabelY = themeStartY + 15 + themeRows * (buttonSize + textOffset) + 25 - scrollOffset;
            int effectStartY = effectLabelY + 15;
            var effectPresets = themeManager.getEffectPresets();
            for (int i = 0; i < effectPresets.size(); i++) {
                int col = i % buttonsPerRow;
                int row = i / buttonsPerRow;
                int buttonX = themeStartX + 3 + col * (buttonSize + gap);
                int buttonY = effectStartY + row * (buttonSize + textOffset) - scrollOffset;

                if (within(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize)) {
                    themeManager.setCurrentEffectPreset(effectPresets.get(i));
                    return true;
                }
            }
            return true;
        }

        for (int i = 0; i < cachedModules.size(); i++) {
            int cardX = x + SIDEBAR_WIDTH + 16 + (i % 2) * 154;
            int cardY = y + 48 + (i / 2) * 44;
            if (!within(mouseX, mouseY, cardX, cardY, 140, 34)) continue;

            Module module = cachedModules.get(i);
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                if ("HUD".equals(module.getName())) {
                    client.setScreen(new HudEditorScreen(this));
                } else {
                    animationProgress.remove("settings-popup");
                    setSettingsModule(module);
                }
            }
            return true;
        }
        return true;
    }

    private boolean clickSetting(double mouseX, double mouseY, int button) {
        int popupWidth = 200;
        int popupX = x - popupWidth - 10;
        int popupY = y + (WINDOW_HEIGHT - cachedPopupHeight) / 2;

        if (!within(mouseX, mouseY, popupX, popupY, popupWidth, cachedPopupHeight)) {
            setSettingsModule(null);
            return true;
        }

        int rowY = popupY + 35;
        for (Setting setting : settingsModule.getSettings()) {
            int rowHeight = settingHeight(setting);
            if (within(mouseX, mouseY, popupX + 9, rowY, popupWidth - 18, rowHeight - 3)) {
                if (setting instanceof BooleanSetting toggle) {
                    toggle.setValue(!toggle.isEnabled());
                } else if (setting instanceof ModeSetting mode) {
                    mode.cycle();
                } else if (setting instanceof NumberSetting number) {
                    draggedNumber = number;
                    setSliderValue(number, mouseX, popupX + 16, popupWidth - 32);
                } else if (setting instanceof ColorSetting color) {
                    setColorFromPalette(color, mouseX, popupX + 80);
                } else if (setting instanceof KeybindSetting keybind) {
                    listeningKeybind = keybind;
                }
                return true;
            }
            rowY += rowHeight;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggedNumber != null) {
            int popupWidth = 200;
            int popupX = x - popupWidth - 10;
            setSliderValue(draggedNumber, mouseX, popupX + 16, popupWidth - 32);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggedNumber = null;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedCategory == Category.THEME && !showingColorInput) {
            targetScrollOffset -= verticalAmount * 20;
            targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, getThemeContentHeight() - (WINDOW_HEIGHT - 100)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void setSliderValue(NumberSetting number, double mouseX, int sliderX, int sliderWidth) {
        double progress = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / (double) sliderWidth));
        number.setValue(number.getMin() + progress * (number.getMax() - number.getMin()));
    }

    private void setColorFromPalette(ColorSetting setting, double mouseX, int paletteX) {
        int index = Math.max(0, Math.min(COLOR_PALETTE.length - 1, (int) ((mouseX - paletteX) / 22)));
        setting.setColor(COLOR_PALETTE[index]);
    }

    private float frameStep() {
        long now = System.nanoTime();
        float seconds = (now - previousFrameNanos) / 1_000_000_000.0F;
        previousFrameNanos = now;
        return Math.min(0.05F, seconds);
    }

    private float animateRaw(float current, float target, float delta, float speed) {
        return current + (target - current) * (1.0F - (float) Math.exp(-speed * delta));
    }

    private float animate(String key, boolean active, float frameStep, float speed) {
        float current = animationProgress.getOrDefault(key, active ? 1.0F : 0.0F);
        float target = active ? 1.0F : 0.0F;
        float next = animateRaw(current, target, frameStep, speed);
        animationProgress.put(key, next);
        return next;
    }

    private int accentColor() {
        if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getThemeManager() != null) {
            return PocoyoVisual.INSTANCE.getThemeManager().getAccentColor();
        }
        return 0xFF4BA5DE;
    }

    private int settingHeight(Setting setting) {
        return setting instanceof NumberSetting ? 43 : 29;
    }

    private boolean within(double mouseX, double mouseY, int boxX, int boxY, int boxWidth, int boxHeight) {
        return mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}