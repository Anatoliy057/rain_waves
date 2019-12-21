package stud.cg.task5.world;

import stud.cg.task5.util.ScreenConverter;
import stud.cg.task5.util.ScreenPoint;
import stud.cg.task5.util.Vector2;

import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleToIntFunction;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

public class DropRender {

    private List<Wave> waveList;

    private final long time;
    private final double r;
    private final double impulse;
    private ToDoubleBiFunction<Double, Double> depend = (t, r) -> (1-t) * r;

    public DropRender(long time, double r, double impulse) {
        this.time = time;
        this.r = r;
        this.impulse = impulse;

        waveList = new LinkedList<>();
        waveList.add(new Wave(time, r, depend, impulse));
    }

    public DropRender(long time, double r, ToDoubleBiFunction<Double, Double> depend, double impulse) {
        this.time = time;
        this.r = r;
        this.impulse = impulse;
        this.depend = depend;

        waveList = new LinkedList<>();
        waveList.add(new Wave(3 * time / 4, r, depend, this.impulse));
    }

    public int[][][] render(int fps, ScreenConverter sc, DoubleToIntFunction convert) {
        int[][][] gif = new int[fps][sc.getWs()][sc.getHs()];
        long dt = time/fps;
        int index = 0;

        int divK = (gif[0][0].length-1)/2;
        int divJ = (gif[0].length-1)/2;
        for (int i = 0; i < fps; i++) {
            waveList.forEach(w -> w.update(dt));
            waveList = waveList.stream().filter(Wave::isAlive).collect(Collectors.toList());

            if (index > fps/4) {
                double quarter = (3 -  (i * 4.0) / (fps+10)) / 4.0;
                Wave w = new Wave((long) (quarter * time), r * quarter, depend, impulse * quarter);
                waveList.add(w);
                index = 0;
            } else
                index++;

            for (int j = 0; j <= divJ; j++) {
                for (int k = 0; k <= divK; k++) {
                    ScreenPoint sp = new ScreenPoint(j, k);
                    Vector2 v = sc.s2r(sp);
                    double impulse = 0;
                    for (Wave w :
                            waveList) {
                        impulse += w.depth(v.getX(), v.getY());
                    }

                    int js = j- divJ;
                    int ks = k -divK;
                    gif[i][j][k] = convert.applyAsInt(impulse);
                    gif[i][j][-ks+divK] = convert.applyAsInt(impulse);
                    gif[i][-js+divJ][k] = convert.applyAsInt(impulse);
                    gif[i][-js+divJ][-ks+divK] = convert.applyAsInt(impulse);
                }
            }
        }
        return gif;
    }
}
