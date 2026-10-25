package main.client.manager;

public class ComboManager {
    private static ComboManager instance;
    private int combo = 0;
    private long lastHitTime = 0;
    private long comboResetTime = 3000; // 3 секунды без попаданий = сброс
    private float animationScale = 1.0f;
    private float targetScale = 1.0f;

    public static ComboManager getInstance() {
        if (instance == null) {
            instance = new ComboManager();
        }
        return instance;
    }

    public void onHit() {
        combo++;
        lastHitTime = System.currentTimeMillis();
        targetScale = 1.5f; // Анимация увеличения при попадании
    }

    public void onMiss() {
        combo = 0;
        targetScale = 0.8f; // Анимация уменьшения при промахе
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHitTime > comboResetTime && combo > 0) {
            combo = 0;
        }

        // Плавная анимация scale
        float speed = 0.1f;
        if (animationScale < targetScale) {
            animationScale = Math.min(animationScale + speed, targetScale);
        } else if (animationScale > targetScale) {
            animationScale = Math.max(animationScale - speed, targetScale);
        }

        // Возврат к нормальному scale
        if (animationScale > 1.0f) {
            animationScale = Math.max(animationScale - 0.02f, 1.0f);
        } else if (animationScale < 1.0f) {
            animationScale = Math.min(animationScale + 0.02f, 1.0f);
        }
    }

    public int getCombo() {
        return combo;
    }

    public float getAnimationScale() {
        return animationScale;
    }

    public void reset() {
        combo = 0;
        animationScale = 1.0f;
        targetScale = 1.0f;
    }
}
