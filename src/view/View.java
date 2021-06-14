package view;

import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import PTM1.Helpclass.Line;
import PTM1.Helpclass.Point;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import view.clocks.Clocks;
import view.featureList.MyFeatureList;
import view.linechart.MyLineChart;
import view.pannel.Pannel;
import view.joystick.MyJoystick;
import view_model.ViewModel;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class View{

        @FXML
        Clocks clocks;
        @FXML
        Button open;
        @FXML
        Button fly;
        @FXML
        Pannel pannel;
        @FXML
    MyFeatureList myFeatureList;
        @FXML
        ListView<String> fList;
        @FXML
         MyLineChart myLineChart;
        @FXML
        MyJoystick myJoystick;
        ViewModel vm;
        StringProperty selected_feature;
        BooleanProperty check_settings;



        public View() {
            selected_feature = new SimpleStringProperty();
            check_settings = new SimpleBooleanProperty();
        }


        public void init(ViewModel vm) {
            this.vm = vm;

            myJoystick.joystickMap.forEach((f,p)-> {
                myJoystick.joystickMap.get(f).bind(vm.getDisplayVariables().get(f));
            });
            myJoystick.joystickMap.forEach((f,p) -> {
                    myJoystick.joystickMap.get(f).addListener((ob,ov,nv)->myJoystick.myJoystickController.paint());
            });


            //binding clocks params: airspeed, altitude, heading, yaw, roll, pitch to vm
           clocks.clocksMap.forEach((f,p) -> clocks.clocksMap.get(f).bind(vm.getDisplayVariables().get(f)));
           clocks.clocksMap.forEach((f,p) -> clocks.clocksMap.get(f).addListener((ob,ov,nv)->clocks.setValues(f,(float)nv)));

            pannel.controller.time_speed.bind(vm.time_speed);
            this.vm.time.addListener((ob,ov,nv) ->
                    pannel.controller.text_speed.setText("x" + nv.toString()));



            pannel.controller.time_step.bindBidirectional(vm.time_step);
            pannel.controller.time_step.addListener((ob,ov,nv) -> pannel.changeTimeStep());


            vm.check_for_paint.addListener((o,ov,nv)->
                  myLineChart.paint(vm.selected_feature_vector,vm.Best_c_feature_vector)
           );

            vm.selected_feature.bind(this.selected_feature);

            ///  a listener in case the settings file uploaded succeed
            vm.check_for_settings.addListener((o,ov,nv)->popupSettings());
            vm.settings_ok.addListener((o,ov,nv)->popupToOpenFile());


            pannel.controller.onPlay = vm.play;
            pannel.controller.onPause = vm.pause;
            pannel.controller.onStop = vm.stop;
            pannel.controller.runForward = vm.forward;
            pannel.controller.runBackward = vm.backward;
            initFeature();


        }

    public void setVariables(){
            System.out.println("setVar check line 165");
            clocks.clocksMap.forEach((f,p) -> {
                int i = 1;
                String gaugeI = "gauge" + i;
                System.out.println("gaugeI is : " + gaugeI);
                clocks.controller.gaugeMap.get(gaugeI).setValue((double)(clocks.clocksMap.get(f).get()));
                i++;
            });

    }




    // the function checks if the flightgear is running
    // and send notification to the view-model to connect
    public void connectFg() {
        if(checkFlightGearProcess()==true){
            vm.connect2fg();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Please Open FlightGear App!");
            alert.showAndWait();
        }
    }

    ///
    public void openBtnPreesed() {

        Stage stage = new Stage();
        stage.setTitle("File chooser sample");

        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                if (file.getPath().endsWith(".csv")) {
                    System.out.println("File ends with csv");
                    this.fly.setDisable(false);
                    vm.setTimeSeries(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /// the function checks if the flightGear process is running in the background
    public static boolean checkFlightGearProcess() {
        String line;
        String pidInfo ="";
        Process p = null;

        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                pidInfo+=line;
            }
            input.close();

            if(pidInfo.contains("fgfs")) {
                System.out.println("Found FlightGear process.");
                return true;
            } else {
                System.out.println("Couldn't find FlightGear process");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /// the function open a dialog with the user to upload settinngs file and send it to the view-model
    public void uploadSettings(){
        Stage stage = new Stage();
        stage.setTitle("File chooser sample");

        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);

        if(file!=null){
            if (file.getPath().endsWith(".txt")){
                System.out.println("accepting the props file");
                vm.sendSettingsToModel(file);
            }
            else{
                popupSettings();
            }
        }
    }

    /// the function alert that there is a problem with the settings file that uplaoded
    public void popupSettings(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("There is a problem with the settings file");
        alert.setContentText("Using default settings");
        alert.showAndWait();
    }


    /// the function responsible to alert the user to upload csv file after uploaded the settings file
    public void popupToOpenFile(){

    }


        public void LoadAlgo() {
        }





        public void initFeature() {


            List<String> features = new LinkedList<>();
            for (String feature : this.vm.getDisplayVariables().keySet()) {
                features.add(feature);
            }

            fList.getItems().addAll(features);

            fList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    selected_feature.setValue(fList.getSelectionModel().getSelectedItem());
                    System.out.println(fList.getSelectionModel().getSelectedItem() + " was selected");
                    vm.setBest_c_feature(selected_feature.getValue());
                }


            });
        }





}


