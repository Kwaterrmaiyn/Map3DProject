package ui.frames;

import fortume.algorithm.FortuneAlgorithm;
import fortume.algorithm.ResultForOpenGL;
import map.structures.HeightMap;
import map.structures.MapPoint;
import openGLdraw.app.ReliefModel;
import openGLdraw.engine.AppEngine;
import openGLdraw.engine.GameEngine;
import openGLdraw.engine.IAppLogic;
import openGLdraw.engine.IGameLogic;
import openGLdraw.game.MyGame;
//import openGLdraw.ReliefModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LeftPanel extends JPanel {
    private HeightMap map;
    private FortuneAlgorithm<MapPoint> fortuneAlgorithm;
    ExtremeFinder extremeFinder;
    public  LeftPanel(FileGettable fileGettable, ImageSettable imageSettable){
        FlowLayout flowLayout = new FlowLayout(5,5,5);
        setLayout(flowLayout);

        JPanel innerPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(innerPanel, BoxLayout.Y_AXIS);
        innerPanel.setLayout(boxLayout);
        ArrayList<JButton> buttons = new ArrayList<>(5);
        JButton buttonMap = new JButton();
        buttons.add(buttonMap);
        buttonMap.setText(StringConstants.strGetHeights);
        buttonMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map = new HeightMap(fileGettable.getFile());
                imageSettable.setImage(map.createImage());
            }
        });

        JButton buttonPoints = new JButton();
        buttons.add(buttonPoints);
        buttonPoints.setText(StringConstants.strGetPoints);
        buttonPoints.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extremeFinder = new ExtremeFinder(imageSettable, map);
                extremeFinder.start();
               /* BufferedImage img = imageSettable.getImage();
                map.SelectExtremum();
                map.addExtremePoints(img);
                imageSettable.setImage(img);*/
            }
        });

        JButton buttonVoronoi = new JButton();
        buttons.add(buttonVoronoi);
        buttonVoronoi.setText(StringConstants.strGetVoronoi);
        buttonVoronoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage img = imageSettable.getImage();
                fortuneAlgorithm = new FortuneAlgorithm<>(map.getSites());
                fortuneAlgorithm.Run(0, 0, 1201, 1201);
                fortuneAlgorithm.addEdgesToImage(img);
                imageSettable.setImage(img);
            }
        });

        JButton buttonModel = new JButton();
        buttons.add(buttonModel);
        buttonModel.setText(StringConstants.strGetModel);
        buttonModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*ReliefModel reliefModel = new ReliefModel();
                reliefModel.run();*/
                try {
                    boolean vSync = true;
                    ResultForOpenGL res = fortuneAlgorithm.getResultForOpenGL(map);
                    res.fileName = fileGettable.getFile().getName();
                    IGameLogic manager = new MyGame(res);
                    GameEngine appEng = new GameEngine("Цифровая модель рельефа",
                            600, 600, vSync,manager);
                    appEng.run();
                } catch (Exception excp) {
                    excp.printStackTrace();
                    System.exit(-1);
                }
            }
        });

        JButton buttonSave = new JButton();
        buttons.add(buttonSave);
        buttonSave.setText(StringConstants.strSaveImage);
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage img = imageSettable.getImage();
                try {
                    ImageIO.write(img, "png", new File("img\\map.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Dimension sizeRigidArea = new Dimension(150,10);
        Dimension size = new Dimension(150, 50);
        Component rigidArea = Box.createRigidArea(sizeRigidArea);
        for (JButton button : buttons){
            button.setMaximumSize(size);
            button.setPreferredSize(size);
            button.setMinimumSize(size);
            button.setAlignmentX(CENTER_ALIGNMENT);
            innerPanel.add(button);
            innerPanel.add(Box.createRigidArea(sizeRigidArea));
        }
        add(innerPanel);
    }
}
