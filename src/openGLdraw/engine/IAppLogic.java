package openGLdraw.engine;

public interface IAppLogic {
    void init() throws Exception;

    void input(AppWindow windowGL);

    void update(float interval);

    void render(AppWindow windowGL);
    void cleanup();

}
