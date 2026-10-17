package main.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import main.client.PocoyoVisual;
import main.client.modules.visual.TargetEsp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// import ru.noxium.event.EventManager;
// import ru.noxium.event.player.AttackEvent;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerInteractionManager.class})
public abstract class ClientPlayerInteractionManagerMixin {
   @Inject(
      method = {"attackEntity"},
      at = {@At("HEAD")}
   )
   private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
      if (PocoyoVisual.INSTANCE == null || PocoyoVisual.INSTANCE.getModuleManager() == null) return;
      PocoyoVisual.INSTANCE.getModuleManager().getModules().stream()
            .filter(TargetEsp.class::isInstance)
            .map(TargetEsp.class::cast)
            .findFirst()
            .ifPresent(module -> module.onAttack(target));
   }
}
