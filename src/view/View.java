package view;

import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import javafx.beans.property.*;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.joystick.MyJoystick;
import view_model.ViewModel;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;


public class View implements Initializable {



        @FXML
        Slider rudder,throttle;
        @FXML
        Button open;
        @FXML
        Slider slider;

    @FXML
    private CategoryAxis x,x1,algo_x;
    @FXML
    private NumberAxis y,y1,algo_y;
    @FXML
    private javafx.scene.chart.LineChart<?,?> CorrelatedFeatureLineChart, FeatureLineChart;
    @FXML
    MyJoystick myJoystick;
        ViewModel vm;
        double mx,my;
        FloatProperty aileron,elevator,altitude,airSpeed,heading;
        IntegerProperty time_step;
        StringProperty selected_feature;

        public View () {
            aileron = new SimpleFloatProperty();
            elevator = new SimpleFloatProperty();
            altitude = new SimpleFloatProperty();
            airSpeed = new SimpleFloatProperty();
            heading = new SimpleFloatProperty();
            time_step = new SimpleIntegerProperty();
            selected_feature = new SimpleStringProperty();
        }


        public void init(ViewModel vm) {
            this.vm = vm;
            this.vm = vm;
            vm.rudder.bind(myJoystick.rudder);
            vm.throttle.bind(myJoystick.throttle);
            vm.aileron.bind(myJoystick.aileron);
            vm.elevator.bind(myJoystick.elevator);


            this.altitude.bind(vm.altitude);
            this.airSpeed.bind(vm.airSpeed);
            this.heading.bind(vm.heading);


            this.time_step.set(0);
        selected_feature.addListener(((o,val,newval)->this.vm.setSelected_feature((String)newval)));


    }

    public void getPaintFunc(){

        vm.getpainter();


    }


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


    public void changeTimeStep(MouseEvent mouseEvent) {
        System.out.println("time step has changed");
        this.time_step.set((int) slider.getValue());
        System.out.println(this.time_step);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] x={"1","2","3","4","5"};
        int[] y={23,10,30,14,50};
        int[] y1={50,2,12,10,34};

        FeatureGraphPaint(x,y);
        CorrelatedFeatureGraphPaint(x,y1);

    }



    public void FeatureGraphPaint(String [] x,int [] y){
        XYChart.Series series = new XYChart.Series();
        for(int i=0; i<x.length; i++){
            series.getData().add(new XYChart.Data(x[i],y[i]));
        }
        FeatureLineChart.getData().addAll(series);
    }

    public void CorrelatedFeatureGraphPaint(String [] x,int [] y){
        XYChart.Series series = new XYChart.Series();
        for(int i=0; i<x.length; i++){
            series.getData().add(new XYChart.Data(x[i],y[i]));
        }
        CorrelatedFeatureLineChart.getData().addAll(series);
    }
}


