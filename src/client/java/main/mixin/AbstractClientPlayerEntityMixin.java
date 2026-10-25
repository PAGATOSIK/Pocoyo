package main.mixin;

import main.client.PocoyoVisual;
import main.client.modules.visual.Cape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;

        Cape cape = PocoyoVisual.INSTANCE.getModuleManager().getModule(Cape.class);
        if (cape != null && cape.isToggled()) {
            Identifier customCape = cape.getCapeTexture();

            if (customCape != null) {
                SkinTextures original = cir.getReturnValue();
                cir.setReturnValue(new SkinTextures(
                        original.texture(),
                        original.textureUrl(),
                        customCape,
                        customCape,
                        original.model(),
                        original.secure()
                ));
            }
        }
    }
}
