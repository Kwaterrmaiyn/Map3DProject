package fortume.algorithm;

public class FortuneCircleEvent implements FortuneEvent {
    private VPoint lowest;
    private double yCenter;
    private RBTreeNode<BeachSection> nodeToDelete;


    public FortuneCircleEvent(VPoint lowest, double yCenter, RBTreeNode<BeachSection> toDelete) {
        this.lowest = lowest;
        this.yCenter = yCenter;
        nodeToDelete = toDelete;
    }

    @Override
    public int compareTo(FortuneEvent other) {
        int c = Double.compare(getY(), other.getY());
        return c == 0 ? Double.compare(getX(), other.getX()) : c;
    }

    public double getX() {
        return lowest.getX();
    }

    public double getY() {
        return lowest.getY();
    }

    public fortume.algorithm.VPoint getLowest() {
        return lowest;
    }

    public double getyCenter() {
        return yCenter;
    }

    public fortume.algorithm.RBTreeNode<BeachSection> getNodeToDelete() {
        return nodeToDelete;
    }
}

