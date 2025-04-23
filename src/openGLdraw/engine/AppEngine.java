package openGLdraw.engine;

public class AppEngine implements Runnable {
    public static final int TARGET_FPS = 70;
    public static final int TARGET_UPS = 30;

    private final Thread thread;
    private final openGLdraw.engine.AppWindow window;
    private final IAppLogic appLogic;
    private  final AppTimer timer;


    public AppEngine(String windowTitle, int width, int height, boolean vsSync, IAppLogic appLogic) throws Exception {
        thread = new Thread(this, "APP_LOOP_THREAD");
        window = new AppWindow(windowTitle, width, height, vsSync);
        this.appLogic = appLogic;
        timer = new AppTimer();
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        try {
            init();
            loop();

        } catch (Exception excp) {
            excp.printStackTrace();
        }
        finally {
            cleanup();
        }
    }

    protected void cleanup(){
        window.destroy();
        appLogic.cleanup();
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        appLogic.init();
    }

    protected void input() {
        appLogic.input(window);
    }

    protected void update(float interval) {
        appLogic.update(interval);
    }

    protected void render() {
        appLogic.render(window);
        window.update();
    }



    protected void loop() {
        float elapsedTime;
        float accumulator = 0.0f;
        float interval = 1.0f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if ( !window.isSync() ) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getCurrent() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }


}
