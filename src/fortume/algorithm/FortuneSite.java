package fortume.algorithm;

import map.structures.MapPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class FortuneSite extends VPoint {



    private ArrayList<VEdge> cell;

    public ArrayList<VEdge> getCell() {
        return cell;
    }

    private ArrayList<FortuneSite> neighbors;

    public ArrayList<FortuneSite> getNeighbors() {
        return neighbors;
    }

    public FortuneSite(double x, double y) {
        super(x,y);
        cell = new ArrayList<>();
        neighbors = new ArrayList<>();

    }

    private double angle;

    private double countAngle(FortuneSite site){
        double r = distanceTo(site);
        double dx = site.getX() - getX();
        double dy = site.getY() - getY();
        double sin = dy / r;
        double cos = dx / r;
        double a = Math.asin(Math.abs(sin));
        if (sin > 0 && cos > 0) return a;
        if (sin > 0 && cos < 0) return Math.PI - a;
        if (sin < 0 && cos < 0) return Math.PI + a;
        return 2* Math.PI - a;
    }

    public static class Comparators {
        public static Comparator<FortuneSite> AngleCompare = new Comparator<FortuneSite>() {
            @Override
            public int compare(FortuneSite p1, FortuneSite p2) {
                if (p2.angle - p1.angle < 0) return -1;
                if (p2.angle - p1.angle > 0) return 1;
                return 0;
            }
        };
    }

    public void reOrderNeighbours(){
        if (neighbors!=null){
            for (FortuneSite n: neighbors){
                n.angle = countAngle(n);
            }
        }
        neighbors.sort(Comparators.AngleCompare);
    }


}
