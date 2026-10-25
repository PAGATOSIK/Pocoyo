package main.mixin;

import main.client.util.VisualUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<S extends EntityRenderState> {

    /**
     * Модифицируем аргумент 'text' (Text) перед самой отрисовкой метки.
     * Это не затрагивает RenderState и гарантирует отсутствие StackOverflowError.
     */
    @ModifyVariable(
            method = "renderLabelIfPresent",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text modifyLabelText(Text text, S state) {
        if (state instanceof PlayerEntityRenderState playerState) {
            String rawName = playerState.name;

            // Проверяем имя через утилиту
            if (rawName != null && VisualUtils.isPagatosik(rawName)) {
                // Если имя подходит, возвращаем кастомный Text с красным цветом (или другим форматированием)
                return Text.literal("§c" + rawName);
            }
        }
        return text;
    }
}