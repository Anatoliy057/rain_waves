package stud.cg.task5.world;

import java.util.StringJoiner;
import java.util.function.ToDoubleBiFunction;

public class Wave {

    private final long time;
    private final double r;
    private final double impulse;

    private double live = 1;
    private double curImpulse;
    private double curR;

    private ToDoubleBiFunction<Double, Double> depend = (t, r) -> (1-t) * r;

    public Wave(long time, double r, double impulse) {
        this.time = time;
        this.r = r;
        this.impulse = impulse;
    }

    public Wave(long time, double r, ToDoubleBiFunction<Double, Double> depend, double impulse) {
        this.time = time;
        this.r = r;
        this.depend = depend;
        this.impulse = impulse;
        curImpulse = impulse;
    }

    public Wave(long time, double r, double curR, ToDoubleBiFunction<Double, Double> depend, double impulse) {
        this.time = time;
        this.r = r;
        this.curR = curR;
        this.depend = depend;
        this.impulse = impulse;
        curImpulse = impulse;
    }

    public double getRadius() {
        return r;
    }

    public double getCurRadius() {
        return curR;
    }

    public double depth(double x, double y) {
        double r4 = curR/4;
        double dist = Math.sqrt(x*x + y*y) - curR;
        double power;

        if (dist < 0) {
            dist = -dist;
            r4 *= 2;

            if (r/50 > curR)
                power = 1;
            else
                power = r4 - dist > 0 ? (r4 - dist) / r4 : 0;
        } else power = r4 - dist > 0 ? (r4 - dist) / r4 : 0;

        return curImpulse * power;
    }

    public boolean isAlive() {
        return live > 0;
    }

    public void update(long dt) {
        double lived = dt / (double) time;
        live -= lived;
        curR = depend.applyAsDouble(live, r);
        curImpulse = live * impulse;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Wave.class.getSimpleName() + "[", "]")
                .add("time=" + time)
                .add("r=" + r)
                .add("impulse=" + impulse)
                .add("live=" + live)
                .add("curImpulse=" + curImpulse)
                .add("curR=" + curR)
                .toString();
    }
}
