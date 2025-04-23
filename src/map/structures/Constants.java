package map.structures;

public class Constants {
    public final static short N = 1201;
    public final static short N1 = N - 1;
    public final static int NN = N * N;
    final static int MarkMax = 0x80000000;
    public final static int MarkMin = 0xc0000000;
    public final static int lim16 = 0x0000ffff;
    public final static int border = 20;

    public static boolean withinBorders(int i, int j){
        if (i > border && j > border && i < N - border && j < N - border)
            return true;
        return false;
    }
}
