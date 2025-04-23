package openGLdraw.app;

import openGLdraw.engine.IAppLogic;
import openGLdraw.engine.AppWindow;
import openGLdraw.engine.graph.Mesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ReliefModel implements IAppLogic {
    private int direction = 0;

    private float color = 0.0f;

  //  private final Renderer renderer;

    private Mesh mesh;
    private final float[] positions;
    private final float[] colours;
    private final int[] indices;

    public ReliefModel(float[] positions,int[] indices,float[] colours ) {
        this.positions = positions;
        this.indices = indices;
        this.colours = colours;
      //  renderer = new Renderer();
    }

    @Override
    public void init() throws Exception {
      /*  renderer.init();
        mesh = new Mesh(positions, colours, indices);*/
    }

    @Override
    public void input(AppWindow window) {
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            direction = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update(float interval) {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if (color < 0) {
            color = 0.0f;
        }
    }

    @Override
    public void render(AppWindow window) {
     /*   window.setClearColor(color, color, color, 0.0f);
        renderer.render(window, mesh);*/
    }

    @Override
    public void cleanup() {
      /*  renderer.cleanup();
        mesh.cleanUp();*/
    }
}


