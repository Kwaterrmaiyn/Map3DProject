package fortume.algorithm;

public class BeachSection {
    private FortuneSite site;

    public FortuneSite getSite() {
        return site;
    }


    private VEdge edge;

    public VEdge getEdge() {
        return edge;
    }

    public void setEdge(VEdge edge) {
        this.edge = edge;
    }


    private FortuneCircleEvent circleEvent;

    public FortuneCircleEvent getCircleEvent() {
        return circleEvent;
    }
    public boolean hasCircleEvent(){return  circleEvent != null;}

    public void setCircleEvent(FortuneCircleEvent circleEvent) {
        this.circleEvent = circleEvent;
    }
    public void setNullCircleEvent() {
        circleEvent = null;
    }

    public BeachSection(FortuneSite site) {
        this.site = site;
        circleEvent = null;
    }
}
