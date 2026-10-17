package main.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import main.client.hud.HudRenderer;
import main.client.PocoyoVisual;
import main.client.modules.visual.TargetEsp;
import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// import ru.noxium.Noxium;
// import ru.noxium.event.EventManager;
// import ru.noxium.event.impl.EventScreen;
// import ru.noxium.module.impl.visuals.Hud;
// import ru.noxium.module.impl.visuals.HUD.TargetHUD;
// import ru.noxium.module.impl.visuals.HUD.HotBarHUD;
// import ru.noxium.module.impl.visuals.HUD.CoolDowns;
// import ru.noxium.ui.draggable.DraggableManager;
// import ru.noxium.util.render.animation.AnimationSystem;
// import ru.noxium.util.render.backends.gl.GlState;
// import ru.noxium.util.render.core.Renderer2D;
// import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
@Mixin({ InGameHud.class })
public class InGameHudMixin {
   @Inject(method = { "renderStatusEffectOverlay" }, at = { @At("HEAD") }, cancellable = true)
   private void onRenderStatusEffects(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      // if (Hud.element.get("Список зелий")) {
      //    ci.cancel();
      // }
   }

   @Inject(method = { "renderHotbar" }, at = { @At("HEAD") }, cancellable = true)
   private void onRenderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      // if (Hud.element.get("Хот бар")) {
      //    ci.cancel();
      // }
   }

   @Inject(method = { "renderHealthBar" }, at = { @At("HEAD") }, cancellable = true)
   private void onRenderHealthBar(
         DrawContext context,
         PlayerEntity player,
         int x,
         int y,
         int lines,
         int regeneratingHeartIndex,
         float maxHealth,
         int lastHealth,
         int health,
         int absorption,
         boolean blinking,
         CallbackInfo ci) {
      // if (Hud.element.get("Хот бар")) {
      //    ci.cancel();
      // }
   }

   @Inject(method = { "renderFood" }, at = { @At("HEAD") }, cancellable = true)
   private void onRenderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
      // if (Hud.element.get("Хот бар")) {
      //    ci.cancel();
      // }
   }

   @Inject(method = { "renderFood" }, at = { @At("TAIL") })
   private void onRenderFoodTail(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
      // if (!Hud.element.get("Хот бар")) {
      //    HotBarHUD.renderSaturation(context, player, top - 10, right, player.getHungerManager().getSaturationLevel());
      // }
   }

   @Inject(method = { "renderArmor" }, at = { @At("HEAD") }, cancellable = true)
   private static void onRenderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x,
         CallbackInfo ci) {
      // if (Hud.element.get("Хот бар")) {
      //    ci.cancel();
      // }
   }

   @Inject(method = { "renderAirBubbles" }, at = { @At("HEAD") }, cancellable = true)
   private void onRenderAir(DrawContext context, PlayerEntity player, int heartCount, int top, int left,
         CallbackInfo ci) {
      // if (Hud.element.get("Хот бар")) {
      //    ci.cancel();
      // }
   }

   @Inject(method = { "renderMountHealth" }, at = { @At("HEAD") }, cancellable = true)
   private void onRenderMountHealth(DrawContext context, CallbackInfo ci) {
      // if (Hud.element.get("Хот бар")) {
      //    ci.cancel();
      // }
   }

   @Redirect(method = {
         "renderMainHud" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/bar/Bar;drawExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V"))
   private void redirectDrawExperienceLevel(DrawContext context, TextRenderer textRenderer, int level) {
      // if (!Hud.element.get("Хот бар")) {
         Bar.drawExperienceLevel(context, textRenderer, level);
      // }
   }

   @Redirect(method = {
         "renderMainHud" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/bar/Bar;renderBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", ordinal = 0))
   private void redirectRenderBar(Bar bar, DrawContext context, RenderTickCounter tickCounter) {
      // if (!Hud.element.get("Хот бар")) {
         bar.renderBar(context, tickCounter);
      // }
   }

   @Redirect(method = {
         "renderMainHud" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/bar/Bar;renderAddons(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", ordinal = 0))
   private void redirectRenderAddons(Bar bar, DrawContext context, RenderTickCounter tickCounter) {
      // if (!Hud.element.get("Хот бар")) {
         bar.renderAddons(context, tickCounter);
      // }
   }

   @Inject(method = { "render" }, at = { @At("RETURN") })
   private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      HudRenderer.draw(context, tickCounter.getTickProgress(true));
   }

   private static void setEnabled(int cap, boolean enabled) {
      if (enabled) {
         GL11.glEnable(cap);
      } else {
         GL11.glDisable(cap);
      }
   }
}
