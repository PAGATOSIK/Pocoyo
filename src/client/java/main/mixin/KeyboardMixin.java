package main.mixin;

import main.client.PocoyoVisual;
import main.client.gui.ClickGuiScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({Keyboard.class})
public class KeyboardMixin {

    @Inject(
            method = {"onKey"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void handleMenuKeyEvent(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getWindow() == null) return;

        if (action == 1) {

            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT || key == GLFW.GLFW_KEY_Y) {

                if (client.currentScreen == null) {
                    client.execute(() -> client.setScreen(new ClickGuiScreen()));
                    ci.cancel();
                }
            }
        }
    }
}