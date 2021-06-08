package view;

import javafx.beans.property.*;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import view.Tags.TagsController;
import view.Tags.TagsDisplay;
import view.pannel.Pannel;
import view_model.ViewModel;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;


public class View {


        @FXML
        Canvas joystick;
        @FXML
        Slider rudder,throttle;
        @FXML
        Button open, connect;
        @FXML
        Slider slider;
        @FXML
        Pannel pannel;
        @FXML
        ListView<String> fList;


        ViewModel vm;
        double mx,my;
        FloatProperty aileron,elevator,altitude,airSpeed,heading;
        IntegerProperty time_step;
        StringProperty selectedFeature;
        BooleanProperty check_settings;

        public View () {
            aileron = new SimpleFloatProperty();
            elevator = new SimpleFloatProperty();
            altitude = new SimpleFloatProperty();
            airSpeed = new SimpleFloatProperty();
            heading = new SimpleFloatProperty();
            time_step = new SimpleIntegerProperty();

            selectedFeature = new SimpleStringProperty();

            check_settings = new SimpleBooleanProperty();

        }


        public void init(ViewModel vm) {
            this.vm = vm;
            this.rudder.valueProperty().bind(vm.getDisplayVariables().get("rudder"));
            this.throttle.valueProperty().bind(vm.getDisplayVariables().get("throttle"));
            this.aileron.bind(vm.getDisplayVariables().get("aileron"));
            this.elevator.bind(vm.getDisplayVariables().get("elevator"));
            this.altitude.bind(vm.getDisplayVariables().get("altitude"));
            this.airSpeed.bind(vm.getDisplayVariables().get("airSpeed"));
            this.heading.bind(vm.getDisplayVariables().get("heading"));
            this.time_step.bindBidirectional(vm.time_step);
            vm.selected_feature.bind(this.selectedFeature);
//            this.check_settings.bind(vm.check_settings);
//            check_settings.addListener((o,ov,nv)->initFeature());

            pannel.controller.onPlay=vm.play;
            pannel.controller.onPause=vm.pause;
            pannel.controller.onStop=vm.stop;
            initFeature();
            paint();



    }

    public void paint() { // To do: attach joystick to features: aileron,elevators
        GraphicsContext gc = joystick.getGraphicsContext2D();

        mx = joystick.getWidth() / 2;
        my = joystick.getHeight() / 2;

        gc.strokeOval(mx - 50, my - 50, 100, 100); //painting a circle

    }

    public void paint_2G(){
        //הפונקציות של הדר


    }

    public void getPaintFunc(){

       // vm.getpainter();


    }

    public void connectFg(){
            vm.connect2fg();
    }
    public void openBtnPreesed() {

        Stage stage = new Stage();
        stage.setTitle("File chooser sample");


        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {

                if(file.getPath().endsWith(".csv")){
                    connect.setDisable(false);
                    vm.setTimeSeries(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void LoadAlgo() {
//
//
//        Stage stage = new Stage();
//        stage.setTitle("choose Algorithem");
//        final FileChooser fileChooser = new FileChooser();
//
//        File file = fileChooser.showOpenDialog(stage);
//        if (file != null) {
////        String input, className;
////        System.out.println("enter a class directory");
//            try {
////            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
////            input = in.readLine(); // get user input
////            System.out.println("enter the class name");
////            className = in.readLine();
////            in.close();
////// load class directory
//                URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{
//                        new URL(file.getPath())
//                });
//                String[] classnames = file.getPath().split("/");
//                String className = classnames[classnames.length-1];
//                Class<?> c = urlClassLoader.loadClass(className);
//                TimeSeriesAnomalyDetector Ts = (TimeSeriesAnomalyDetector) c.newInstance();
//                vm.setAnomalyDetector(Ts);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }


    public void changeTimeStep(MouseEvent mouseEvent) {
        System.out.println("time step has changed");
        this.time_step.set((int) slider.getValue());
        System.out.println(this.time_step);
    }



    public void initFeature() {


            List<String> features = new LinkedList<>();
        for (String feature:this.vm.getDisplayVariables().keySet()) {
            features.add(feature);
        }

//        ObservableList features = FXCollections.observableArrayList();
//        features.removeAll(features);
//
//        features.add("aileron");
//        features.add("elevator");
//        features.add("rudder");
//        features.add("throttle");
//        features.add("altimeter");
//        features.add("altitude");
//        features.add("airSpeed");
//        features.add("heading");
//        features.add("roll");
//        features.add("pitch");
//        features.add("yaw");

        fList.getItems().addAll(features);

        fList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedFeature.setValue(fList.getSelectionModel().getSelectedItem());
                System.out.println(fList.getSelectionModel().getSelectedItem() + " was selected");
            }
        });
    }
}


