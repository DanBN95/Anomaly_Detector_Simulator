package view.joystick;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

public class MyJoystick extends AnchorPane {

    public DoubleProperty aileron, elevator, rudder, throttle;
    public MyJoystickController myJoystickController;
    public MyJoystick(){
        super();
        try{
            FXMLLoader fxl = new FXMLLoader();
            AnchorPane joy = (AnchorPane) fxl.load(getClass().getResource("MyJoystick.fxml").openStream());
            myJoystickController=fxl.getController();

            aileron=myJoystickController.aileron;
            elevator=myJoystickController.elevator;
            rudder=myJoystickController.rudder.valueProperty();
            throttle=myJoystickController.throttle.valueProperty();
            myJoystickController.paint();

            this.getChildren().add(joy);
            ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}