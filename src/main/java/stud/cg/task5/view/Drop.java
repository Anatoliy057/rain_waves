package stud.cg.task5.view;

public class Drop {

    private int[][][] gif;
    private int snapshot = 0;

    private int x0, y0;

    private int divK;
    private int divJ;

    private boolean alive;

    public Drop(int[][][] gif, int x0, int y0) {
        this.gif = gif;
        this.x0 = x0;
        this.y0 = y0;

        divK = (gif[0][0].length-1)/2;
        divJ = (gif[0].length-1)/2;

        alive = true;
    }

    public double depth(int x, int y) {
        x -= x0;
        y -= y0;

        if (Math.abs(x) > divJ || Math.abs(y) > divK) {
            return 0;
        } else {
            return gif[snapshot][x + divJ][y + divK];
        }
    }

    public void draw(int[][] buffer) {
        for (int i = 0; i < gif[0].length; i++) {
            for (int j = 0; j < gif[0][0].length; j++) {
                buffer[i + x0][j + y0] += gif[snapshot][i][j];
            }
        }
    }

    public void next() {
        snapshot++;
        if (snapshot >= gif.length) {
            snapshot %= gif.length;
            alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }
}
