package ui.frames;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        this.add(mainPanel);
        JPanel upPanel = new UpPanel();
        JPanel downPanel = new JPanel();
        downPanel.setLayout(new BoxLayout(downPanel, BoxLayout.X_AXIS));
        JPanel rightPanel = new RightPanel();
        JPanel leftPanel = new LeftPanel((FileGettable) upPanel, (ImageSettable)rightPanel);
        mainPanel.add(upPanel);
        mainPanel.add(downPanel);
        downPanel.add(leftPanel);
        downPanel.add(rightPanel);
        pack();
    }
}
