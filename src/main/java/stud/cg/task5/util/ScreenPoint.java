package stud.cg.task5.util;

import java.util.StringJoiner;

public class ScreenPoint {

    private int x;
    private int y;

    public ScreenPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScreenPoint.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .toString();
    }
}
