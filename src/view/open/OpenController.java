package view.open;

import javafx.fxml.FXML;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenController extends JFrame implements ActionListener {

    @FXML
    Button button;

    public OpenController() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        button = new Button("Open csv file");
        button.addActionListener(this);

        this.add(button);
        this.pack();
        this.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == button) {

            JFileChooser file = new JFileChooser();

            file.showOpenDialog(null);
        }
    }
}
