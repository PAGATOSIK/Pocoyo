package main.client.modules.visual;

import main.client.PocoyoVisual;
import main.client.module.Category;
import main.client.module.Module;
import main.client.setting.ModeSetting;
import main.client.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Particle extends Module {
    private static final Identifier STAR_TEXTURE = Identifier.of("pocou", "texture/star.png");
    private static final Identifier HEART_TEXTURE = Identifier.of("pocou", "texture/heart.png");

    private final ModeSetting mode = new ModeSetting("Mode", "Star", "Star", "Heart");
    private final NumberSetting amount = new NumberSetting("Amount", 12.0, 1.0, 100, 1.0);
    private final NumberSetting gravity = new NumberSetting("Gravity", 0.008, 0.001, 0.05, 0.001);

    private final List<CustomParticle> particles = new ArrayList<>();
    private final Random random = new Random();

    public Particle() {
        super("Particle", Category.VISUAL, "создает частицы при атаке");
        addSettings(mode, amount, gravity);
    }

    public void onAttack(Entity target) {
        if (!isToggled() || target == null) return;

        double x = target.getX();
        double y = target.getY() + target.getHeight() / 2.0;
        double z = target.getZ();

        int particleCount = (int) amount.getValue();
        double currentGravity = -gravity.getValue();

        for (int i = 0; i < particleCount; i++) {
            particles.add(new CustomParticle(x, y, z, currentGravity, random));
        }
    }

    public void onTick() {
        if (!isToggled()) return;

        particles.removeIf(p -> {
            p.update();
            return p.isDead();
        });
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float partialTicks) {
        if (!isToggled() || particles.isEmpty()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();

        Identifier selectedTexture = mode.getCurrentMode().equalsIgnoreCase("Heart") ? HEART_TEXTURE : STAR_TEXTURE;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(selectedTexture));

        int effectColor = PocoyoVisual.INSTANCE.getThemeManager().getEffectColor();
        int r = (effectColor >> 16) & 0xFF;
        int g = (effectColor >> 8) & 0xFF;
        int b = effectColor & 0xFF;

        for (CustomParticle particle : particles) {
            particle.render(matrices, buffer, camera, camPos, partialTicks, r, g, b);
        }
    }

    public static class CustomParticle {
        private double prevX, prevY, prevZ;
        private double x, y, z;
        private double velocityX, velocityY, velocityZ;

        private final double particleGravity;
        private int life;
        private final int maxLife = 25 + new Random().nextInt(15);
        private final float baseScale = 0.25f + new Random().nextFloat() * 0.25f;

        public CustomParticle(double x, double y, double z, double gravity, Random random) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.prevX = x;
            this.prevY = y;
            this.prevZ = z;
            this.particleGravity = gravity;

            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 0.08 + random.nextDouble() * 0.12;
            double upSpeed = 0.12 + random.nextDouble() * 0.15;

            this.velocityX = Math.cos(angle) * speed;
            this.velocityY = upSpeed;
            this.velocityZ = Math.sin(angle) * speed;

            this.life = maxLife;
        }

        public void update() {
            this.prevX = x;
            this.prevY = y;
            this.prevZ = z;

            velocityY += particleGravity;

            x += velocityX;
            y += velocityY;
            z += velocityZ;

            velocityX *= 0.92;
            velocityY *= 0.92;
            velocityZ *= 0.92;

            life--;
        }

        public boolean isDead() {
            return life <= 0;
        }

        public void render(MatrixStack matrices, VertexConsumer buffer, Camera camera, Vec3d camPos, float partialTicks, int r, int g, int b) {
            double renderX = MathHelper.lerp(partialTicks, prevX, x);
            double renderY = MathHelper.lerp(partialTicks, prevY, y);
            double renderZ = MathHelper.lerp(partialTicks, prevZ, z);

            float lifeProgress = (float) life / (float) maxLife;
            float alphaProgress = lifeProgress * lifeProgress;
            float scale = baseScale * (float) Math.sin(lifeProgress * Math.PI / 2.0);

            matrices.push();
            matrices.translate(renderX - camPos.x, renderY - camPos.y, renderZ - camPos.z);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            matrices.scale(-scale, -scale, scale);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            int alphaInt = (int) (alphaProgress * 255.0f);

            drawVertex(buffer, matrix, -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, r, g, b, alphaInt);
            drawVertex(buffer, matrix, 0.5f, -0.5f, 0.0f, 1.0f, 1.0f, r, g, b, alphaInt);
            drawVertex(buffer, matrix, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, r, g, b, alphaInt);
            drawVertex(buffer, matrix, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, r, g, b, alphaInt);

            matrices.pop();
        }

        private void drawVertex(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float u, float v, int r, int g, int b, int alpha) {
            buffer.vertex(matrix, x, y, z)
                    .color(r, g, b, alpha)
                    .texture(u, v)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0.0F, 0.0F, 1.0F);
        }
    }
}