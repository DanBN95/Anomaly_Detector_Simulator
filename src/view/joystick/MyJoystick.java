package view.joystick;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
import java.util.HashMap;

public class MyJoystick extends AnchorPane {

//    public DoubleProperty aileron, elevator, rudder, throttle;
    private FloatProperty throt,rudd;
    public MyJoystickController myJoystickController;
    public HashMap<String,FloatProperty> joystickMap;
    public MyJoystick(){
        super();
        try{
            FXMLLoader fxl = new FXMLLoader();
            AnchorPane joy = (AnchorPane) fxl.load(getClass().getResource("MyJoystick.fxml").openStream());
            myJoystickController=fxl.getController();
            joystickMap = new HashMap<>();

//            double r = myJoystickController.rudder.valueProperty().get();
//            double t = myJoystickController.throttle.valueProperty().get();
//            myJoystickController.rudd.setValue((float)r);
//            myJoystickController.throt.setValue((float)t);

            joystickMap.put("aileron", myJoystickController.aileron);
            joystickMap.put("elevator", myJoystickController.elevator);
            joystickMap.put("rudder", myJoystickController.rudd);
            joystickMap.put("throttle", myJoystickController.throt);


//            aileron=myJoystickController.aileron;
//            elevator=myJoystickController.elevator;
//            rudder=myJoystickController.rudder.valueProperty();
//            throttle=myJoystickController.throttle.valueProperty();
            myJoystickController.paint();

            this.getChildren().add(joy);
            ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}