package fortume.algorithm;

import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

public class VPoint {
    private double x, y;
    private int index;
    private boolean seen;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public VPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.index = 0; //FortuneAlgorithm.numPoints++;
        seen = false;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(){seen = true;}

    public void add(ArrayList<Float> vertexes){
        vertexes.add((float)((int)x));
        vertexes.add(0.0f);
        vertexes.add((float)((int)y));

    }

    public double distanceTo(VPoint point){
        double dx = point.x - x;
        double dy = point.y - y;
        return Math.sqrt(dx*dx+dy*dy);
    }

    public void draw(Graphics2D gr){
        gr.fillOval((int)x, (int)y, 7,7);
    }

    public Vector3f getVector(){
        return  new Vector3f((float)x, 0.0f, (float) y);
    }
}
