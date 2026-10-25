
package main.client.modules.visual;

import main.client.PocoyoVisual;
import main.client.module.Category;
import main.client.module.Module;
import main.client.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JumpCircle extends Module {

    private static final Identifier CIRCLE_TEXTURE =
            Identifier.of("pocou", "texture/circle.png");

    private final NumberSetting size =
            new NumberSetting("Size", 1.5, 0.5, 3.0, 0.1);

    private final NumberSetting duration =
            new NumberSetting("Duration", 15.0, 5.0, 30.0, 1.0);

    private final List<CircleEffect> circles = new ArrayList<>();

    private boolean wasOnGround = false;
    private boolean initialized = false;

    public JumpCircle() {
        super("JumpCircle", Category.VISUAL, "создает круг при прыжке");
        addSettings(size, duration);
    }

    /**
     * Вызывать каждый игровой тик через систему событий клиента.
     */
    public void onTick() {
        if (!isToggled()) {
            circles.clear();
            initialized = false;
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.world == null) {
            circles.clear();
            initialized = false;
            return;
        }

        boolean onGround = mc.player.isOnGround();

        // Инициализируем состояние, чтобы не создавать круг
        // случайно при первом включении модуля в воздухе.
        if (!initialized) {
            wasOnGround = onGround;
            initialized = true;
        } else {
            // Обнаруживаем переход с земли в воздух.
            if (wasOnGround && !onGround) {
                Vec3d pos = mc.player.getPos();

                circles.add(new CircleEffect(
                        pos.x,
                        pos.y + 0.03,
                        pos.z,
                        (float) size.getValue(),
                        (int) duration.getValue()
                ));
            }

            wasOnGround = onGround;
        }

        // Обновляем время жизни эффектов.
        Iterator<CircleEffect> iterator = circles.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().update()) {
                iterator.remove();
            }
        }
    }

    /**
     * Вызывать из подходящего события рендеринга мира,
     * передавая актуальные MatrixStack и VertexConsumerProvider.
     */
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            float partialTicks
    ) {
        if (!isToggled() || circles.isEmpty()) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.world == null) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        VertexConsumer buffer = vertexConsumers.getBuffer(
                RenderLayer.getEntityTranslucent(CIRCLE_TEXTURE)
        );

        int effectColor = PocoyoVisual.INSTANCE.getThemeManager().getEffectColor();
        int r = (effectColor >> 16) & 0xFF;
        int g = (effectColor >> 8) & 0xFF;
        int b = effectColor & 0xFF;

        for (CircleEffect circle : circles) {
            circle.render(
                    matrices,
                    buffer,
                    cameraPos,
                    partialTicks,
                    r, g, b
            );
        }
    }

    public static class CircleEffect {

        private final double x;
        private final double y;
        private final double z;

        private final float maxRadius;
        private final int maxLife;

        private int life;

        public CircleEffect(
                double x,
                double y,
                double z,
                float maxRadius,
                int maxLife
        ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.maxRadius = maxRadius;
            this.maxLife = Math.max(1, maxLife);
            this.life = this.maxLife;
        }

        /**
         * Возвращает true, когда эффект нужно удалить.
         */
        public boolean update() {
            life--;
            return life <= 0;
        }

        public void render(
                MatrixStack matrices,
                VertexConsumer buffer,
                Vec3d cameraPos,
                float partialTicks,
                int r, int g, int b
        ) {
            float progress = MathHelper.clamp(
                    1.0f - (life - partialTicks) / (float) maxLife,
                    0.0f,
                    1.0f
            );

            // Расширение от центра.
            float currentRadius = maxRadius
                    * MathHelper.sin(progress * (float) Math.PI / 2.0f);

            // Постепенное исчезновение.
            float alpha = 1.0f - progress;

            if (currentRadius <= 0.001f || alpha <= 0.001f) {
                return;
            }

            matrices.push();

            matrices.translate(
                    x - cameraPos.x,
                    y - cameraPos.y,
                    z - cameraPos.z
            );

            // Горизонтальная плоскость.
            matrices.multiply(
                    RotationAxis.POSITIVE_X.rotationDegrees(90.0f)
            );

            matrices.scale(
                    currentRadius * 2.0f,
                    currentRadius * 2.0f,
                    1.0f
            );

            Matrix4f matrix = matrices.peek().getPositionMatrix();

            int alphaInt = MathHelper.clamp(
                    (int) (alpha * 255.0f),
                    0,
                    255
            );

            // Квадрат с текстурой круга.
            // Порядок вершин рассчитан для обычной текстурной плоскости.
            drawVertex(
                    buffer, matrix,
                    -0.5f, -0.5f, 0.0f,
                    0.0f, 1.0f, r, g, b, alphaInt
            );

            drawVertex(
                    buffer, matrix,
                    0.5f, -0.5f, 0.0f,
                    1.0f, 1.0f, r, g, b, alphaInt
            );

            drawVertex(
                    buffer, matrix,
                    0.5f, 0.5f, 0.0f,
                    1.0f, 0.0f, r, g, b, alphaInt
            );

            drawVertex(
                    buffer, matrix,
                    -0.5f, 0.5f, 0.0f,
                    0.0f, 0.0f, r, g, b, alphaInt
            );

            matrices.pop();
        }

        private static void drawVertex(
                VertexConsumer buffer,
                Matrix4f matrix,
                float x,
                float y,
                float z,
                float u,
                float v,
                int r, int g, int b,
                int alpha
        ) {
            buffer.vertex(matrix, x, y, z)
                    .color(r, g, b, alpha)
                    .texture(u, v)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0.0f, 0.0f, 1.0f);
        }
    }
}
