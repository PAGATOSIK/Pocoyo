package main.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void onRenderHead(
            DrawContext context,
            int scaledWidth,
            Scoreboard scoreboard,
            @Nullable ScoreboardObjective objective,
            CallbackInfo ci
    ) {
        System.out.println("[PlayerListHud] render HEAD called");
    }
}