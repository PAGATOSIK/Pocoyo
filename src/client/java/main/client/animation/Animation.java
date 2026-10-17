package main.client.animation;

public abstract class Animation {
    protected long startTime;
    protected long duration;
    protected double startValue;
    protected double endValue;

    public Animation(long duration) {
        this.duration = duration;
    }

    public void start(double startValue, double endValue) {
        this.startTime = System.currentTimeMillis();
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public double getValue() {
        if (startTime == 0) return startValue;
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= duration) return endValue;

        double progress = (double) elapsed / duration;
        return startValue + (endValue - startValue) * getEquation(progress);
    }

    protected abstract double getEquation(double x);
}