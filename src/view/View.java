package view;
import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.open.OpenController;
import view.open.OpenDisplay;
import view_model.ViewModel;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;


public class View {


    @FXML
    MenuItem algo;
    @FXML
    Canvas joystick;
    @FXML
    Slider rudder, throttle;
    @FXML
    Button open;
//        @FXML
//        OpenDisplay openDisplay;

    ViewModel vm;
    double mx, my;
    FloatProperty aileron, elevator, altitude, airSpeed, heading;

    public View() {
        aileron = new SimpleFloatProperty();
        elevator = new SimpleFloatProperty();
        altitude = new SimpleFloatProperty();
        airSpeed = new SimpleFloatProperty();
        heading = new SimpleFloatProperty();
    }


    public void init(ViewModel vm) {
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
        mx = joystick.getWidth() / 2;
        my = joystick.getHeight() / 2;

        gc.strokeOval(mx - 50, my - 50, 100, 100); //painting a circle

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


    public void openBtnPreesed() {

        System.out.println("22btn pressed");
        Stage stage = new Stage();
        stage.setTitle("File chooser sample");


        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                System.out.println(file.getPath());
                vm.setTimeSeries(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void LoadAlgo() {


        Stage stage = new Stage();
        stage.setTitle("choose Algorithem");
        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
//        String input, className;
//        System.out.println("enter a class directory");
            try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            input = in.readLine(); // get user input
//            System.out.println("enter the class name");
//            className = in.readLine();
//            in.close();
//// load class directory
                URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{
                        new URL(file.getPath())
                });
                String[] classnames = file.getPath().split("/");
                String className = classnames[classnames.length-1];
                Class<?> c = urlClassLoader.loadClass(className);
                TimeSeriesAnomalyDetector Ts = (TimeSeriesAnomalyDetector) c.newInstance();
                vm.setAnomalyDetector(Ts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}


