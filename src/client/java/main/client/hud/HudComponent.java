package main.client.hud;

import net.minecraft.client.gui.DrawContext;

public abstract class HudComponent {
    private final String name;
    private boolean enabled;
    private int x;
    private int y;

    private float alpha = 0.7f;
    private float scale = 1.0f;

    public HudComponent(String name, int defaultX, int defaultY) {
        this.name = name;
        this.x = defaultX;
        this.y = defaultY;
        this.enabled = true;
    }

    public abstract void render(DrawContext context, float tickDelta);

    protected void applyTransformations(DrawContext context, Runnable renderTask) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) x, (float) y);
        context.getMatrices().scale(scale, scale);
        context.getMatrices().translate((float) -x, (float) -y);

        renderTask.run();

        context.getMatrices().popMatrix();
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public float getAlpha() { return alpha; }
    public void setAlpha(float alpha) { this.alpha = Math.max(0.1f, Math.min(1.0f, alpha)); }

    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = Math.max(0.5f, Math.min(1.5f, scale)); }

    public int getEditorWidth() { return (int) (90 * scale); }
    public int getEditorHeight() { return (int) (18 * scale); }
}