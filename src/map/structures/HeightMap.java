package map.structures;

import org.joml.Vector3f;
import ui.frames.ImageSettable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class HeightMap {
    final private MapPoint [][] matr;
    ArrayList<MapPoint> sites;

    public ArrayList<MapPoint> getSites() {
        return sites;
    }

    final private int maxHeight, minHeight;
    public HeightMap(File file){
        final int N = Constants.N;
        final int N1 = Constants.N1;
        byte[] bytes = null;
        try (InputStream inputStream = new FileInputStream(file);)
        {
            long fileSize = file.length();
            bytes = new byte[(int) fileSize];
            inputStream.read(bytes);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        matr = new MapPoint[N][N];

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int p = 0;
        for(int i = 0, k = 0; i < N; i++){
            for (int j = 0; j < N; j++, k += 2) {
                int value = (bytes[k + 1] & 0xFF) + ((bytes[k] & 0xFF) << 8);
                if (min > value)
                    min = value;
                if (value >= 9999)
                    p++;
                else {
                    if (value < 9999 && max < value)
                        max = value;
                    matr[j][i] = new MapPoint(j,i,value);
                }
            }
        }
        System.out.printf("No info %f \n", p * 100.0/(N*N));
        System.out.printf("Max = %d, min = %d \n", max, min);
        minHeight = min;
        maxHeight = max;
    }

    public void SelectExtremum(ImageSettable imageSettable){
        PriorityQueue<MapPoint> valuesDown = new PriorityQueue<>(Constants.NN, MapPoint.Comparators.Down);
        PriorityQueue<MapPoint> valuesUp = new PriorityQueue<>(Constants.NN, MapPoint.Comparators.Up);

        for (int i=0; i<Constants.N; i++){
            for (int j = 0; j<Constants.N; j++)
            {
                MapPoint point =matr[i][j];
                if (point!=null) {
                    valuesDown.add(point);
                    valuesUp.add(point);
                }
            }
        }
       // ProtectBorder();
        ArrayList<MapPoint> pointsMax = SelectExtremum(valuesDown, imageSettable);
        for (MapPoint v:valuesUp)
            v.clear();
      //  ProtectBorder();
        ArrayList<MapPoint> pointsMin = SelectExtremum(valuesUp, imageSettable);
        sites = new ArrayList<>(pointsMax);
        sites.addAll(pointsMin);
    }

    public BufferedImage createImage()
    {
        final int N = matr.length;
        int half = (maxHeight - minHeight) / 2;
        BufferedImage img = new BufferedImage(N, N, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int red=255, green=255, blue=255;
                if (matr[i][j] != null) {
                    int value = matr[i][j].height - minHeight;
                    if (value < half) {
                        green = value * 255 / half;
                        blue = 255 - green;
                        red = 0;
                    } else if (value < Constants.lim16) {
                        value -= half;
                        red = value * 255 / half;
                        green = 255 - red;
                        blue = 0;
                    }
                }
                int cl = 0xff000000|(red<<16)|(green<<8)|blue;
                img.setRGB(i, j, cl);
            }
        }
        return  img;
    }

    public void addExtremePoints(BufferedImage img){
        Graphics2D gr = img.createGraphics();
        gr.setColor(Color.WHITE);
        for (MapPoint point : sites){
            gr.fillOval(point.x-2, point.y-2, 4,4);
            //img.setRGB(point.x, point.y, 0xffffff);
        }
    }


    void MarkBorder(int x, int y, BufferedImage img)
    {
        int Lx = x + Constants.border + 1;
        if (Lx >Constants.N) Lx = Constants.N;
        int Ly = y + Constants.border + 1;
        if (Ly >Constants.N) Ly = Constants.N;
        int Sx = x - Constants.border;
        if (Sx<0) Sx = 0;
        int Sy = y - Constants.border;
        if (Sy <0) Sy = 0;
        for (int i= Sx; i < Lx; i++){
            for (int j = Sy; j < Ly; j++) {
                if (matr[i][j]!=null)
                    matr[i][j].toAdd = false;// = matr[i][j]|mark;
              //  img.setRGB(i,j,Color.WHITE.getRGB());
            }
        }
      //  img.setRGB(x,y,Color.RED.getRGB());
    }


   /* private void ProtectBorder() {
        int N = Constants.N;
        int N1 = Constants.N1;
        for (int k = 0; k < Constants.border; k++) {
            for (int i = 0; i < N; i++) {
                if (matr[i][k] != null) matr[i][k].toAdd = false;
                if (matr[i][N1-k] != null) matr[i][N1 - k].toAdd = false;
                if (matr[k][i] != null) matr[k][i].toAdd = false;
                if (matr[N1- k][i] != null) matr[N1 - k][i].toAdd = false;
            }
        }
    }*/

   /* static boolean masked(int value, int mask){
        value = value & mask;
        if (value == mask)
            return true;
        return false;
    }*/

    ArrayList<MapPoint> SelectExtremum( PriorityQueue<MapPoint> values, ImageSettable imageSettable){
        ArrayList<MapPoint> points = new ArrayList<>();

        while (values.size()>0) {
            MapPoint v = values.poll();
            if (!v.toAdd) continue;
            if (v.x > 0 && matr[v.x - 1][v.y] != null && !matr[v.x - 1][v.y].toAdd ||
                    v.x < Constants.N1 && matr[v.x + 1][v.y] != null && !matr[v.x + 1][v.y].toAdd ||
                    v.y > 0 && matr[v.x][v.y - 1] != null && !matr[v.x][v.y - 1].toAdd ||
                    v.y < Constants.N1 && matr[v.x][v.y + 1] != null &&!matr[v.x][v.y + 1].toAdd) {
                // BufferedImage img = imageSettable.getImage();
                v.toAdd = false;
                // MarkBorder(v.x, v.y, null);
                  /*  img.setRGB(v.x,v.y,Color.YELLOW.getRGB());
                    imageSettable.setImage(img);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                   */

            }
            if (v.toAdd) {
                BufferedImage img = imageSettable.getImage();
                //if (Constants.withinBorders(v.x, v.y))
                MarkBorder(v.x, v.y, img);
                points.add(v);
              /*  imageSettable.setImage(img);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //   ImageIO.write(CreateImage(matr, min, max), "png", new File(String.format("img\\temp\\map%d_%d.png", v.x, v.y)));

               */
            }
        }
        return points;
    }

    public ArrayList<Vector3f> updateVertexes(ArrayList<Vector3f> vertexes){
        float half = (maxHeight - minHeight) / 2.0f;
        ArrayList<Vector3f> colors = new ArrayList<>(vertexes.size());
        for (Vector3f v: vertexes){
            if ((int) v.x < Constants.N && (int) v.z < Constants.N ) {
                float val = half;
                if (matr[(int) v.x][(int) v.z]!=null)
                val = matr[(int) v.x][(int) v.z].height;
                v.y = (val-minHeight) /half - 1.0f;
            }
            else v.y = 0.0f;
            v.x = v.x*2.0f/Constants.N - 1.0f;
            v.z = -v.z*2.0f/Constants.N+1.0f;
            float red=1.0f, green=1.0f, blue=1.0f;
            float value = v.y;
            if (value < 0.0){
                green = value +1.0f;
                blue = 1.0f - green;
                red = 0.0f;
            }
            else if (value < Constants.lim16) {
                //value -= half;
                red = value;
                green = 1.0f - red;
                blue = 0.0f;
            }
            colors.add(new Vector3f(red, green, blue));
            v.y *= 0.3;
            // Change view from side to up
            /*float temp = vertexes[i+1];
            vertexes[i+1] = vertexes[i+2];
            vertexes[i+2] = temp;
            */

        }
        return colors;
    }

    public float[] updateVertexes(float[]vertexes){
        int num = vertexes.length;
        float half = (maxHeight - minHeight) / 2.0f;
        float[] colors = new float[num];
        for (int i=0; i<num; i += 3){
            if ((int) vertexes[i] < Constants.N && (int) vertexes[i+2] < Constants.N ) {
                int val = matr[(int) vertexes[i]][(int) vertexes[i + 2]].height;
                vertexes[i + 1] = (val-minHeight) /half - 1.0f;
            }
            else vertexes[i + 1] = 0.0f;
            vertexes[i] = vertexes[i]*2.0f/Constants.N - 1.0f;
            vertexes[i+2] = -vertexes[i+2]*2.0f/Constants.N+1.0f;
            float red=1.0f, green=1.0f, blue=1.0f;
            float value = vertexes[i+1];
            if (value < 0.0){
                green = value +1.0f;
                blue = 1.0f - green;
                red = 0.0f;
            }
            else if (value < Constants.lim16) {
                //value -= half;
                red = value;
                green = 1.0f - red;
                blue = 0.0f;
            }
            colors[i] = red;
            colors[i+1] = green;
            colors[i+2] = blue;
            vertexes[i+1] *= 0.05;
            // Change view from side to up
            /*float temp = vertexes[i+1];
            vertexes[i+1] = vertexes[i+2];
            vertexes[i+2] = temp;
            */

        }
        return colors;
    }
}
