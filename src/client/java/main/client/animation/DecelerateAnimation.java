package main.client.animation;

public class DecelerateAnimation extends Animation {
    public DecelerateAnimation(long duration) {
        super(duration);
    }

    @Override
    protected double getEquation(double x) {
        return 1.0 - (1.0 - x) * (1.0 - x);
    }
}