package main.mixin;

import main.client.PocoyoVisual;
import main.client.modules.visual.Cape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(CapeFeatureRenderer.class)
public abstract class CapeFeatureRendererMixin {

    private static final Identifier CUSTOM_CAPE = Identifier.of("pocou", "texture/cape.png");

    @Inject(
            method = "getTexture",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getCustomCapeTexture(PlayerEntityRenderState state, CallbackInfoReturnable<Identifier> cir) {
        Cape cape = PocoyoVisual.INSTANCE.getModuleManager().getModule(Cape.class);
        if (cape != null && cape.isToggled()) {
            cir.setReturnValue(CUSTOM_CAPE);
        }
    }
}
