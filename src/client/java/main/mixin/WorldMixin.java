package main.mixin;

import main.client.PocoyoVisual;
import main.client.modules.game.WorldTime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldMixin {

    private long originalTime = -1;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(CallbackInfo ci) {
        WorldTime worldTime = PocoyoVisual.INSTANCE.getModuleManager().getModule(WorldTime.class);
        if (worldTime != null && worldTime.isToggled()) {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                long customTime = worldTime.getCustomTime();
                if (customTime >= 0) {
                    originalTime = world.getTimeOfDay();
                    // Меняем время через свойства уровня клиента
                    world.getLevelProperties().setTimeOfDay(customTime);
                }
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderEnd(CallbackInfo ci) {
        if (originalTime >= 0) {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                // Возвращаем оригинальное время обратно
                world.getLevelProperties().setTimeOfDay(originalTime);
            }
            originalTime = -1;
        }
    }
}