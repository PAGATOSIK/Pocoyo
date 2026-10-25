package main.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    // Резерв для будущего использования
}