package main.mixin;

import main.client.PocoyoVisual;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(
            method = {"sendMessage"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        if (PocoyoVisual.INSTANCE == null || PocoyoVisual.INSTANCE.getCommandManager() == null) return;

        if (PocoyoVisual.INSTANCE.getCommandManager().handleCommand(chatText)) {
            ci.cancel();
        }
    }
}
