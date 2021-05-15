package view;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import view.open.OpenController;
import view_model.ViewModel;

import javax.swing.*;



import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;



public class View {

        @FXML
        Canvas joystick;
        @FXML
        Slider rudder,throttle;
        @FXML
        Button open;



        ViewModel vm;
        boolean mousePushed;
        double mx,my;
        FloatProperty aileron,elevator,altitude,airSpeed,heading;

        public View () {
            aileron = new SimpleFloatProperty();
            elevator = new SimpleFloatProperty();
            altitude = new SimpleFloatProperty();
            airSpeed = new SimpleFloatProperty();
            heading = new SimpleFloatProperty();
            mousePushed = false;
            open = new Button();
            open.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    System.out.println(event);
                    JFileChooser file = new JFileChooser();
                    file.showOpenDialog(null);
                }
            });

        }

        void init(ViewModel vm) {
            this.vm = vm;
            vm.rudder.bind(rudder.valueProperty());
            vm.throttle.bind(throttle.valueProperty());
            vm.aileron.bind(aileron);
            vm.elevator.bind(elevator);
            vm.altitude.bind(altitude);
            vm.airSpeed.bind(airSpeed);
            vm.heading.bind(heading);

        }

        public void paint() { // To do: attach joystick to features: aileron,elevators
            GraphicsContext gc = joystick.getGraphicsContext2D();
            System.out.println(joystick.getWidth());
            mx = joystick.getWidth()/2;
            my = joystick.getHeight()/2;

            gc.strokeOval(mx-50,my-50,100,100); //painting a circle

        }

        /*
            1. create an EventListner that updating the video scroll bar
            (the timestep on vm change according to it by binding)
         */

        /*
            2. EventListner that after selecting one of the csv feature,its updating
            the vm to active paint function
         */

        /*
            3. EventListner that after algorithm has been selected, updating
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

//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//        if(e.getSource() == open) {
//
//            JFileChooser file = new JFileChooser();
//
//            file.showOpenDialog(null);
//        }
//    }
}


