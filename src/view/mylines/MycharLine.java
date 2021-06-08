package view.mylines;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import view.joystick.MyJoystickController;

import java.io.IOException;

public class MycharLine extends AnchorPane {

   public XYChart series;


    public MycharLine() {
        super();
        try {
            FXMLLoader fxl = new FXMLLoader();
            AnchorPane char1 = (AnchorPane) fxl.load(getClass().getResource("MycharLine.fxml").openStream());
            MycharLineController MycharLineController = fxl.getController();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
