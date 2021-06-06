package view;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import view_model.ViewModel;

import java.io.File;



public class View {

        @FXML
        Canvas joystick;
        @FXML
        Slider rudder,throttle;
        @FXML
        Button open;
        @FXML
        Slider slider;
//        @FXML
//        OpenDisplay openDisplay;

        ViewModel vm;
        boolean mousePushed;
        double mx,my;
        FloatProperty aileron,elevator,altitude,airSpeed,heading;
        IntegerProperty time_step;

        public View () {
            aileron = new SimpleFloatProperty();
            elevator = new SimpleFloatProperty();
            altitude = new SimpleFloatProperty();
            airSpeed = new SimpleFloatProperty();
            heading = new SimpleFloatProperty();
            time_step = new SimpleIntegerProperty();
        }


        public void init(ViewModel vm) {
            this.vm = vm;
            this.rudder.valueProperty().bind(vm.rudder);
            this.throttle.valueProperty().bind(vm.throttle);
            this.aileron.bind(vm.aileron);
            this.elevator.bind(vm.elevator);
            this.altitude.bind(vm.altitude);
            this.airSpeed.bind(vm.airSpeed);
            this.heading.bind(vm.heading);
            this.time_step.set(0);

            paint();
            //System.out.println(openDisplay.file.getName());
            //vm.setTimeSeries(openDisplay.file);

        }

        public void paint() { // To do: attach joystick to features: aileron,elevators
            GraphicsContext gc = joystick.getGraphicsContext2D();
            System.out.println(joystick.getWidth());
            mx = joystick.getWidth()/2;
            my = joystick.getHeight()/2;

            gc.strokeOval(mx-50,my-50,100,100); //painting a circle

        }

        /*
            1. create an EventListener that updating the video scroll bar
            (the timestep on vm change according to it by binding)
         */

        /*
            2. EventListener that after selecting one of the csv feature,its updating
            the vm to active paint function
         */

        /*
            3. EventListener that after algorithm has been selected, updating
            the vm which algorithm to be active
        */


        public  void mouseDown(MouseEvent me) {
            if(!mousePushed) {
                mousePushed = true;
                System.out.println("mouse is down");
            }
        }

        public  void mouseUp(MouseEvent me) {
            if(!mousePushed) {
                mousePushed = false;
                System.out.println("mouse is down");
            }
        }


    public void openBtnPreesed() {
        System.out.println("btn pressed");
        Stage stage = new Stage();
        stage.setTitle("File chooser sample");

        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                System.out.println(file.getPath());
                vm.setTimeSeries(file);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void changeTimeStep(MouseEvent mouseEvent) {
        System.out.println("time step has changed");
        this.time_step.set((int) slider.getValue());
        System.out.println(this.time_step);
    }


}


