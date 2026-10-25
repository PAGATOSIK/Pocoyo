package main.client.modules.game;

import main.client.module.Category;
import main.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", Category.GAME, "делает мир максимально ярким");
    }

    public void onTick() {
        if (!isToggled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION,
                    220,
                    0,
                    false,
                    false,
                    false
            ));
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
        super.onDisable();
    }
}