package ui.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RightPanel extends JPanel implements ImageSettable {
    JLabel imageLabel;
    BufferedImage image;
    public RightPanel(){
        BorderLayout borderLayout = new BorderLayout(5,5);
        setLayout(borderLayout);
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);
        BufferedImage image = new BufferedImage(800, 800, Image.SCALE_DEFAULT);
        Graphics2D gr = image.createGraphics();
        gr.setColor(Color.lightGray);
        gr.fillRect(0,0,800,800);
        imageLabel.setIcon(new ImageIcon(image));
    }
    @Override
    public void setImage(BufferedImage img) {
        image = img;
        Image img1 = img.getScaledInstance(800, 800,
                Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img1));
        repaint();
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}
