package view.joystick;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import view_model.ViewModel;

public class MyJoystickController {

    @FXML
    Canvas joystick;
    @FXML
    Slider rudder,throttle;

    public DoubleProperty aileron,elevator,altitude,airSpeed,heading;

    double mx,my,jx,jy;

    public MyJoystickController(){
        aileron=new SimpleDoubleProperty();
        elevator=new SimpleDoubleProperty();

    }

    public void paint() { // To do: attach joystick to features: aileron,elevators
        GraphicsContext gc = joystick.getGraphicsContext2D();
        mx = joystick.getWidth()/2;
        my = joystick.getHeight()/2;
        gc.clearRect(0,0,joystick.getWidth(), joystick.getHeight());
        gc.strokeOval(mx-50,my-40,80,80); //painting a circle
        aileron.set(1);
        elevator.set(1);
    }
}
