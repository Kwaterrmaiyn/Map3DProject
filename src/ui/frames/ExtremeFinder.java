package ui.frames;

import map.structures.HeightMap;

import java.awt.image.BufferedImage;

public class ExtremeFinder implements Runnable {

    ImageSettable imageSettable;
    HeightMap map;
    Thread thread;
    volatile boolean exec;
    public  ExtremeFinder(ImageSettable imageSettable, HeightMap map){
        this.imageSettable = imageSettable;
        this.map = map;
        thread = new Thread(this, "EXTREMES");
        exec  = true;
    }
    public  void start(){
        thread.start();
    }


    @Override
    public void run() {
        if (exec) {
            BufferedImage img = imageSettable.getImage();
            map.SelectExtremum(imageSettable);
            map.addExtremePoints(img);
            imageSettable.setImage(img);
        }
    }
}
