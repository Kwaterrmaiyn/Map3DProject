package openGLdraw.engine;

public class AppTimer {

    private double current;

    public void init() {
        current = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - current);
        current = time;
        return elapsedTime;
    }

    public double getCurrent() {
        return current;
    }
}
