package main.client.modules.visual;

import main.client.module.Category;
import main.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class TargetEsp extends Module {
    private static final Identifier TARGET_TEXTURE = Identifier.of("pocou", "texture/target.png");

    private final float size = 28.0F;
    private Entity target;
    private float rotationAngle = 0.0F;
    private float rotationSpeed = 0.5F;
    private boolean isReversing = false;
    private float animProgress = 0.0F;

    public TargetEsp() {
        super("Target ESP", Category.VISUAL);
    }

    public void onAttack(Entity entity) {
        if (isToggled() && entity != null && entity.isAlive()) {
            this.target = entity;
        }
    }

    public Entity getActiveTarget() {
        if (target == null || !target.isAlive()) {
            target = null;
        }
        return target;
    }

    public void onTick() {
        boolean active = getActiveTarget() != null && isToggled();

        if (active) {
            animProgress = Math.min(1.0F, animProgress + 0.1F);
        } else {
            animProgress = Math.max(0.0F, animProgress - 0.1F);
        }

        if (animProgress > 0.01F) {
            updateRotation();
        }
    }

    private void updateRotation() {
        if (!this.isReversing) {
            this.rotationSpeed += 0.01F;
            if ((double) this.rotationSpeed > 2.3D) {
                this.rotationSpeed = 2.3F;
                this.isReversing = true;
            }
        } else {
            this.rotationSpeed -= 0.01F;
            if ((double) this.rotationSpeed < -2.3D) {
                this.rotationSpeed = -2.3F;
                this.isReversing = false;
            }
        }
        this.rotationAngle += this.rotationSpeed;
        this.rotationAngle %= 360.0F;
    }

    public void render3D(MatrixStack matrices, VertexConsumerProvider consumers, float partialTicks) {
        Entity activeTarget = getActiveTarget();
        if (activeTarget == null || animProgress <= 0.01F) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();

        Vec3d targetPos = activeTarget.getLerpedPos(partialTicks);
        double x = targetPos.x;
        double y = targetPos.y + (activeTarget.getHeight() / 2.0D);
        double z = targetPos.z;

        matrices.push();
        matrices.translate(x - camPos.x, y - camPos.y, z - camPos.z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotationAngle));

        float scale = 0.05F * size * animProgress;
        matrices.scale(-scale, -scale, scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int alpha = (int) (animProgress * 255.0F);

        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getEntityTranslucent(TARGET_TEXTURE));

        drawVertex(buffer, matrix, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, alpha);
        drawVertex(buffer, matrix, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, alpha);
        drawVertex(buffer, matrix, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, alpha);
        drawVertex(buffer, matrix, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, alpha);

        matrices.pop();
    }

    private void drawVertex(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float u, float v, int alpha) {
        buffer.vertex(matrix, x, y, z)
                .color(255, 255, 255, alpha)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0xF000F0)
                .normal(0.0F, 0.0F, 1.0F);
    }

    @Override
    protected void onDisable() {
        target = null;
        animProgress = 0.0F;
    }
}