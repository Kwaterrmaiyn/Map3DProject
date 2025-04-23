package map.structures;

import fortume.algorithm.FortuneSite;
import fortume.algorithm.ToFortuneSiteConvertable;

import java.util.Comparator;

public class MapPoint implements ToFortuneSiteConvertable {
    int x;
    int y;
    int height;
    boolean toAdd;
    boolean seen;

    public MapPoint(int i, int j, int h) {
        x = i;
        y = j;
        height = h;
        toAdd = true;
        seen = false;
    }

    public double dist2(MapPoint point) {
        int dx = x - point.x;
        int dy = y - point.y;
        return dx * dx + dy * dy;
    }

    @Override
    public String toString() {
        return "MapPoint{" +
                "x=" + x +
                ", y=" + y +
                ", height=" + height +
                '}';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }
    public void clear(){toAdd = !toAdd;}

    @Override
    public FortuneSite toFortuneSite() {
        return new FortuneSite(x, y);
    }

    public static class Comparators {

        public static Comparator<MapPoint> Down = new Comparator<MapPoint>() {
            @Override
            public int compare(MapPoint p1, MapPoint p2) {
                return p2.height - p1.height;
            }
        };
        public static Comparator<MapPoint> Up = new Comparator<MapPoint>() {
            @Override
            public int compare(MapPoint p1, MapPoint p2) {
                return p1.height - p2.height;
            }
        };
    }

}
