package main.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void onGetName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        // ВАЖНО: Не вызывайте self.getName() здесь!
        // Используем GameProfile, чтобы получить исходное имя без рекурсии:
        String rawName = self.getGameProfile().getName();

        if ("PAGATOSIK".equals(rawName)) {
            // Возвращаем измененный Text и отменяем оригинальный метод
            cir.setReturnValue(Text.literal("§c" + rawName));
        }
    }
}