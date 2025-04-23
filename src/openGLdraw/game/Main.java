package openGLdraw.game;

import fortume.algorithm.FortuneAlgorithm;
import openGLdraw.engine.GameEngine;
import openGLdraw.engine.IGameLogic;

import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            FortuneAlgorithm alg = new FortuneAlgorithm(null);
            IGameLogic gameLogic = new MyGame(alg.getResultForOpenGL(null));
            GameEngine gameEng = new GameEngine("GAME", vSync, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}
