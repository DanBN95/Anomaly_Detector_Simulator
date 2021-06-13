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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import view.linechart.MyLineChart;
import view.pannel.Pannel;
import view.joystick.MyJoystick;
import view_model.ViewModel;
import java.io.File;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class View implements Initializable {


        @FXML
        Canvas joystick;
        @FXML
        Slider rudder, throttle;
        @FXML
        Button open;
        @FXML
        Button fly;
        @FXML
        Slider slider;
        @FXML
        Pannel pannel;
        @FXML
        ListView<String> fList;
        @FXML
        TextField text_speed;
        @FXML
        private NumberAxis x,x1,algo_x;
        @FXML
        private NumberAxis y,y1,algo_y;
        @FXML
        private javafx.scene.chart.LineChart<?,?> CorrelatedFeatureLineChart, FeatureLineChart;
        @FXML
    MyLineChart myLineChart;




//    @FXML
//    private NumberAxis x,x1,algo_x;
//    @FXML
//    private NumberAxis y,y1,algo_y;
//    @FXML
//    private LineChart CorrelatedFeatureLineChart, FeatureLineChart,Algolinechart;
//    @FXML
//    MyJoystick myJoystick;
//    @FXML
//    MyCircleChart mycirclechart;
////    @FXML
////    private javafx.scene.chart.BubbleChart<?,?> algoFeatureLineChart;
//    @FXML
//        private Canvas co;

        ViewModel vm;
        double mx, my;
        FloatProperty aileron, elevator, altitude, airSpeed, heading;
        IntegerProperty time_step;
        DoubleProperty time_speed;
        StringProperty selected_feature;
        BooleanProperty check_settings;



        public View() {
            aileron = new SimpleFloatProperty();
            elevator = new SimpleFloatProperty();
            altitude = new SimpleFloatProperty();
            airSpeed = new SimpleFloatProperty();
            heading = new SimpleFloatProperty();
            time_step = new SimpleIntegerProperty();
            time_speed = new SimpleDoubleProperty();
            selected_feature = new SimpleStringProperty();
            check_settings = new SimpleBooleanProperty();
            text_speed = new TextField();

        }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
 /*       int[] x={1,2,3,4,5};
        int[] y={23,10,30,14,50};
        int[] y1={50,2,12,10,34};
        FeatureGraphPaint(x,y);
        CorrelatedFeatureGraphPaint(x,y1);*/
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
            this.time_speed.bind(vm.time_speed);
            this.time_speed.addListener((ob,ov,nv) -> text_speed.setText("x" + nv.toString()));

            this.time_step.addListener((ob,ov,nv) -> {
                slider.setValue(time_step.get());
            });

            vm.check_for_paint.addListener((o,ov,nv)->
                  myLineChart.paint(vm.selected_feature_vector,vm.Best_c_feature_vector)
           );

            vm.selected_feature.bind(this.selected_feature);


            pannel.controller.onPlay = vm.play;
            pannel.controller.onPause = vm.pause;
            pannel.controller.onStop = vm.stop;
            pannel.controller.runForward = vm.forward;
            pannel.controller.runBackward = vm.backward;
            initFeature();
            paint();

        }

        public void paint() { // To do: attach joystick to features: aileron,elevators
            GraphicsContext gc = joystick.getGraphicsContext2D();

            mx = joystick.getWidth() / 2;
            my = joystick.getHeight() / 2;

           // gc.strokeOval(mx - 50, my - 50, 100, 100); //painting a circle
            var stops1 = new Stop[] { new Stop(0, Color.ALICEBLUE),
                    new Stop(1, Color.BLACK)};

            var lg1 = new RadialGradient(0, 0, 0.5, 0.5, 0.8, true,
                    CycleMethod.NO_CYCLE, stops1);
            gc.setFill(lg1);

            gc.fillOval(mx - 50, my - 50, 100, 100);



        }


        public void connectFg() {
            vm.connect2fg();
        }

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


        public void LoadAlgo() {

            //        Stage stage = new Stage();
//        stage.setTitle("Select Algo Class File");
//        final FileChooser fileChooser = new FileChooser();
//
//        File file = fileChooser.showOpenDialog(stage);
//        if (file != null) {
//
//            try {
//                System.out.println(file.getPath());
//                System.out.println(file.getAbsolutePath());
//                System.out.println(file.getCanonicalPath());
//                String str = file.getPath();
//                String separator = "\\";
//                String [] path_parts = str.replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
//                String Algo_class_name = path_parts[path_parts.length-1];
//
//
//
//
//                StringBuffer sb = new StringBuffer();
//                sb.append("file://");
//                for (int i =0;i<path_parts.length-1;i++){
//                    sb.append(path_parts[i]+"/");
//               }
//                String algo_path = sb.toString();
//
//               System.out.println(algo_path);
//            try{
//                String input,className;
//                System.out.println("enter a class directory");
//                BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
//                input=in.readLine(); // get user input
//                System.out.println("enter the class name");
//                className=in.readLine();
//                in.close();
//                URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{
//                        new URL("file://"+"C:/Users/user/IdeaProjects/Anomaly_Detector_Simulator/ptm_amir/")
//                });
//                Class<?> c = urlClassLoader.loadClass("PTM1.AnomalyDetector.SimpleAnomalyDetector");
//                TimeSeriesAnomalyDetector Ts = (TimeSeriesAnomalyDetector) c.newInstance();
//                vm.setAnomalyDetector(Ts);

        }


        public void changeTimeStep(MouseEvent mouseEvent) {
            System.out.println("time step has changed by dragging slider");
            this.time_step.set((int) slider.getValue());
            System.out.println(this.time_step);
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
                }


            });
        }
       /* public void FeatureGraphPaint(int [] x,int [] y){
            XYChart.Series series = new XYChart.Series();
            for(int i=0; i<x.length; i++){
                series.getData().add(new XYChart.Data(x[i],y[i]));
            }
            series.setName("Feature");
            FeatureLineChart.getData().addAll(series);
        }

        public void CorrelatedFeatureGraphPaint(int [] x,int [] y){
            XYChart.Series series = new XYChart.Series();
            for(int i=0; i<x.length; i++){
                series.getData().add(new XYChart.Data(x[i],y[i]));
            }
            series.setName("Correlation");
            CorrelatedFeatureLineChart.getData().addAll(series);
        }*/




}


