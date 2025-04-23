package ui.frames;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpPanel extends JPanel implements FileGettable {
    File file;
    @Override
    public File getFile(){return file;}
    public UpPanel(){
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(boxLayout);
        JTextField textField = new JTextField();
        textField.setSize(new Dimension(200, 30));
        JButton button = new JButton();
        button.setText(StringConstants.strFileChoose);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path currentRelativePath = Paths.get("");
                String s = currentRelativePath.toAbsolutePath().toString();
                JFileChooser fileChooser = new JFileChooser(s);
                if (fileChooser.showDialog(null,StringConstants.strOK)==JFileChooser.APPROVE_OPTION){
                    file = fileChooser.getSelectedFile();
                    textField.setText(file.getAbsolutePath());
                }
            }
        });
        add(textField);
        add(button);
    }
}
