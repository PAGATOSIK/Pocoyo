package main.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import main.client.util.VisualUtils;

@Environment(EnvType.CLIENT)
@Mixin({ PlayerListEntry.class })
public abstract class PlayerListEntryMixin {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        try {
            PlayerListEntry entry = (PlayerListEntry)(Object)this;
            if (entry != null && entry.getProfile() != null) {
                String playerName = entry.getProfile().getName();
                System.out.println("[PlayerListEntry] Checking player: " + playerName); // Debug
                if (VisualUtils.isPagatosik(playerName)) {
                    System.out.println("[PlayerListEntry] PAGATOSIK found! Creating red text"); // Debug
                    MutableText coloredText = Text.literal(playerName)
                        .styled(style -> style.withColor(0xFF5555));
                    cir.setReturnValue(coloredText);
                    return;
                }
            }
            // Если не PAGATOSIK, не отменяем оригинальный метод
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
