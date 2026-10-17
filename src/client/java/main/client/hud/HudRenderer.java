package main.client.hud;

import main.client.PocoyoVisual;
import main.client.gui.ClickGuiScreen;
import main.client.module.Module;
import main.client.manager.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudRenderer {

    public static void draw(DrawContext context, float tickDelta) {
        var mc = MinecraftClient.getInstance();

        if (mc.options.hudHidden || mc.currentScreen instanceof ClickGuiScreen) {
            return;
        }

        if (PocoyoVisual.INSTANCE == null) return;
        ModuleManager moduleManager = PocoyoVisual.INSTANCE.getModuleManager();
        if (moduleManager == null) return;

        Module hud = moduleManager.getModules().stream()
                .filter(module -> module.getName().equals("HUD"))
                .findFirst()
                .orElse(null);
        if (hud == null || !hud.isToggled()) return;

        // Вызываем менеджер, который сам отрендерит все активные функции худа
        if (PocoyoVisual.INSTANCE != null && PocoyoVisual.INSTANCE.getHudManager() != null) {
            PocoyoVisual.INSTANCE.getHudManager().renderComponents(context, tickDelta);
        }
    }
}
