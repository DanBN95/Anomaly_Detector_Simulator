package view;

import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import sample.anomaly_errors.AnomalyErrors;
import view.algoGraph.MyAlgoGraph;
import view.clocks.Clocks;
import view.linechart.MyLineChart;
import view.pannel.Pannel;
import view.joystick.MyJoystick;
import view_model.ViewModel;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

public class View {

        @FXML
        Clocks clocks;
        @FXML
        Button open;
        @FXML
        Button fly;
        @FXML
        Pannel pannel;

        /*@FXML
        ListView<String> fList;*/
        @FXML
         MyLineChart myLineChart;
        @FXML
        MyJoystick myJoystick;
         @FXML
         MyAlgoGraph myAlgoGraph;


        ViewModel vm;
//        StringProperty selected_feature;
        BooleanProperty check_settings;
        AnomalyErrors anomalyErrors;
        String classname;
        BooleanProperty display_bool_features;



        public View() {
//            selected_feature = new SimpleStringProperty();
            check_settings = new SimpleBooleanProperty();
            anomalyErrors = new AnomalyErrors(this);
            display_bool_features = new SimpleBooleanProperty();

        }


        public void init(ViewModel vm) {
            this.vm = vm;

            this.display_bool_features.bind(vm.display_bool_features);

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
            pannel.controller.time_step.addListener((ob,ov,nv) -> {
                Platform.runLater(() -> {
                    pannel.changeTimeStep();
                });
                if(myLineChart.myLineChartController.selected_feature.getValue()!=null) {
                        myLineChart.myLineChartController.add_p_paint((int) ov, (int) nv);
                        if(((int) ov)+1==((int) nv)){
                            myAlgoGraph.myAlgoGraphController.add_p_paint(vm.get_detect_point());
                        }else{
                            myAlgoGraph.myAlgoGraphController.clear_detect();
                        }
                }
            });



            vm.selected_feature.bind(myLineChart.myLineChartController.selected_feature);

            ///  a listener in case the settings file uploaded succeed
            vm.check_for_settings.addListener((o,ov,nv)->popupSettings());
            vm.settings_ok.addListener((o,ov,nv)-> popupToOpenFile());


            pannel.controller.onPlay = vm.play;
            pannel.controller.onPause = vm.pause;
            pannel.controller.onStop = vm.stop;
            pannel.controller.runForward = vm.forward;
            pannel.controller.runBackward = vm.backward;
            initFeature();


        }

    public ViewModel getVm() {
        return vm;
    }

//     the function checks if the flightgear is running
//     and send notification to the view-model to connect
    public void connectFg() {
//        if(checkFlightGearProcess()==true){
//            vm.connect2fg();
//        }
//        else{
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Error");
//            alert.setHeaderText("Please Open FlightGear App!");
//            alert.showAndWait();
//        }
    }

    ///
    public void Open_csv_butten() {

        Stage stage = new Stage();
        stage.setTitle("File chooser sample");

        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                if (file.getPath().endsWith(".csv")) {
                    System.out.println("File ends with csv");
                    this.fly.setDisable(false);
                    vm.set_detect_TimeSeries(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    / the function checks if the flightGear process is running in the background
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
            if (file.getPath().endsWith(".txt") || file.getPath().endsWith(".xml")){
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
        this.open.setStyle("-fx-background-color: #f5855b");
        System.out.println(this.vm.setting_map);
        clocks.controller.updateMinMax(this.vm.setting_map);
    }


    public void LoadAlgo() {
            //לבדוק אם עובד ולעשות עצור.
        Stage stage = new Stage();
        stage.setTitle("Select Algo Class File");
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {

                String str = file.getPath();
                String[] path_parts = str.split("bin");
                StringBuffer path_b = new StringBuffer();
                StringBuffer class_b = new StringBuffer();
                path_b.append("file://");
                for (char x : path_parts[0].toCharArray()) {
                    if(x == '\\'){
                        path_b.append('/');
                    }else{
                        path_b.append(x);
                    }
                }
                path_b.append("bin/");
                String algo_path = path_b.toString();
                String[] name1 = path_parts[1].split(".java");
                Boolean check = false;
                for (char x : name1[0].toCharArray()) {
                    if(x == '\\'){
                        if(check) {
                            class_b.append('.');
                        }else{
                            check =true;
                        }
                    }else{
                        class_b.append(x);
                    }
                }
//                if(classname != null) {
//                    System.out.println("View 267: clear");
//                    Platform.runLater(() -> {
//                        myAlgoGraph.myAlgoGraphController.set_algo_setting(vm.getpaintFunc());
//                    });
//                }
                classname = class_b.toString();
                URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{
                        new URL(algo_path)
                });
                Class<?> c = urlClassLoader.loadClass(classname);
                TimeSeriesAnomalyDetector Ts = (TimeSeriesAnomalyDetector) c.newInstance();
                vm.setAnomalyDetector(Ts);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void initFeature() {
            List<String> features = new LinkedList<>();

            if(vm.display_bool_features.get()) { ;
                for (String feature : this.vm.getTimeSeries().FeaturesList) {
                    features.add(feature);
                }
                myLineChart.myLineChartController.fList.getItems().addAll(features);
            }

            myLineChart.myLineChartController.fList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    myLineChart.myLineChartController.selected_feature.setValue(myLineChart.myLineChartController.fList.getSelectionModel().getSelectedItem());
                    vm.set_selected_vectors();
                    myAlgoGraph.myAlgoGraphController.set_algo_setting(vm.getpaintFunc());
                    myLineChart.myLineChartController.set_setting(pannel.controller.time_step.getValue(),vm.selected_feature_vector, vm.Best_c_feature_vector);
                    System.out.println(myLineChart.myLineChartController.fList.getSelectionModel().getSelectedItem() + " was selected");
               }
            });
        }





}


