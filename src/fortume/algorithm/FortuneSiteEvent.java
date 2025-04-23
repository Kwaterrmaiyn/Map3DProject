package fortume.algorithm;

public class FortuneSiteEvent implements FortuneEvent {
    private FortuneSite site;

    public FortuneSiteEvent(FortuneSite site) {
        this.site = site;
    }

    public FortuneSite getSite() {
        return site;
    }

    @Override
    public double getX() {
        return site.getX();
    }

    @Override
    public double getY() {
        return site.getY();
    }

    @Override
    public int compareTo(FortuneEvent fortuneEvent) {
        int c = Double.compare(site.getY(), fortuneEvent.getY());
        return c == 0 ? Double.compare(site.getX(), fortuneEvent.getX()) : c;
    }
}
