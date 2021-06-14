package view.joystick;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import view_model.ViewModel;

public class MyJoystickController {

    @FXML
    Canvas joystick;
    @FXML
    Slider rudder,throttle;

    public FloatProperty aileron,elevator,rudd,throt;

    double mx,my;

    public MyJoystickController(){

        aileron=new SimpleFloatProperty();
        elevator=new SimpleFloatProperty();
        throt = new SimpleFloatProperty(0);
        rudd = new SimpleFloatProperty(0);

    }

    public void paint() { // To do: attach joystick to features: aileron,elevators

        System.out.println("painting my joystick");
        GraphicsContext gc = joystick.getGraphicsContext2D();

        gc.clearRect(mx-50 , my-50, 100, 100);
        mx = joystick.getWidth() / 2 + aileron.getValue()*100;
        my = joystick.getHeight() / 2 + elevator.getValue()*100;
        System.out.println("mx: " + mx + ", my: " + my);



        // gc.strokeOval(mx - 50, my - 50, 100, 100); //painting a circle
        var stops1 = new Stop[] { new Stop(0, Color.ALICEBLUE),
                new Stop(1, Color.BLACK)};

        var lg1 = new RadialGradient(0, 0, 0.5, 0.5, 0.8, true,
                CycleMethod.NO_CYCLE, stops1);
        gc.setFill(lg1);


        gc.fillOval(mx-25 , my-25, 50, 50);

        rudder.setValue((double)rudd.get());
        System.out.println("rudder change to: " + rudd.get());
        throttle.setValue((double)throt.get());
        System.out.println("throttle changed to: " + throt.get());
    }
}