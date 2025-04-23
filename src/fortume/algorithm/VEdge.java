package fortume.algorithm;

import map.structures.Constants;

import java.awt.*;

public class VEdge {
    private VPoint start, end;

    public VPoint getStart() {
        return start;
    }

    public void setStart(VPoint start) {
        this.start = start;
    }

    public VPoint getEnd() {
        return end;
    }

    public void setEnd(VPoint end) {
        this.end = end;
    }


    private FortuneSite left;
    private FortuneSite right;

    public FortuneSite getLeft() {
        return left;
    }

    public FortuneSite getRight() {
        return right;
    }

    private double slopeRise;
    private double slopeRun;
    Double slope ;
    Double intercept;

    public Double getSlope() {
        return slope;
    }

    public Double getIntercept() {
        return intercept;
    }

    public VEdge neighbor;

    public VEdge getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(VEdge neighbor) {
        this.neighbor = neighbor;
    }

    public VEdge(VPoint start, FortuneSite left, FortuneSite right) {
        this.start = start;
        this.left = left;
        this.right = right;

        //for bounding box edges
        if (left == null || right == null)
            return;

        //from negative reciprocal of slope of line from left to right
        //ala m = (left.y -right.y / left.x - right.x)
        slopeRise = left.getX() - right.getX();
        slopeRun = -(left.getY() - right.getY());
        intercept = null;

        if (ParabolaMath.ApproxEqual(slopeRise, 0)||
                ParabolaMath.ApproxEqual(slopeRun, 0))
            return;
        slope = slopeRise / slopeRun;
        intercept =  start.getY() - slope * start.getX();
    }

    public double getSlopeRise() {
        return slopeRise;
    }

    public double getSlopeRun() {
        return slopeRun;
    }

    private double length(){
        return start.distanceTo(end);
    }

    public void draw(Graphics2D gr) {
        if (end == null)
            start.draw(gr);
        else {
            if (length()> Constants.border * 10)
                return;
            gr.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
        }
    }
}


