package view_model;


import PTM1.AnomalyDetector.AnomalyReport;
import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import PTM1.Helpclass.Point;
import PTM1.Helpclass.TimeSeries;

import javafx.application.Platform;
import javafx.beans.property.*;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.scene.chart.XYChart;
import model.Model;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


public class ViewModel implements Observer {

    Model model;
    TimeSeries timeSeries,detect_timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;

    public File file;

    public final Runnable play,pause,stop,forward,backward;
    private ScheduledFuture<?> futureTask;
    public double x;


    public IntegerProperty time_step;
    public LongProperty time_speed;
    public DoubleProperty time;


    private HashMap<String,SimpleFloatProperty> displayVariables;
    public FloatProperty aileron,elevator,rudder,throttle,altitude,airSpeed,heading;
    public StringProperty selected_feature;
    public BooleanProperty check_settings;
    private ScheduledExecutorService scheduledExecutorService;

    public float[] selected_feature_vector;
    public float[] Best_c_feature_vector;
    public List<AnomalyReport> AnomalyReport;
    /// for the popup alert in case the settings file of the user is not good
    public BooleanProperty check_for_settings = new SimpleBooleanProperty(true);
    public BooleanProperty settings_ok = new SimpleBooleanProperty(false);
    public BooleanProperty display_bool_features;

    public HashMap<String, ArrayList<Integer>> setting_map;


    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);
        scheduledExecutorService = Executors.newScheduledThreadPool(5);

        check_settings=new SimpleBooleanProperty(false);
        display_bool_features = new SimpleBooleanProperty(false);
        check_settings.setValue(true);
        selected_feature = new SimpleStringProperty();
        time = new SimpleDoubleProperty();

        time_step = new SimpleIntegerProperty(0);
        time_speed = new SimpleLongProperty((long)model.time_default);
        this.time.setValue(1);
        model.timestep = this.time_step;
        model.time_speed = this.time_speed;
        x = 1;
        model.setSettings(model.settings);
        displayVariables = new HashMap<>();
        displayVariables = this.model.showFields();


        Best_c_feature_vector=null;
        time_step.addListener((o, ov, nv) -> {
            Platform.runLater(() -> setTimeStep((int) nv));
        });

        play = ()-> model.play();
        stop = ()-> model.stop();
        pause = ()-> model.pause();
        forward = ()-> this.changeTimeSpeed(0.25);
        backward = ()-> this.changeTimeSpeed(-0.25);

    }


    synchronized public void changeTimeSpeed(double time) {
        if(time > 0) {
            if (this.time.get() >= 0.25 && this.time.get() < 2) {
                System.out.println("change time speed ");
                this.time.setValue(this.time.get() + time);
                model.pause();

                time_speed.set((long) (200 / this.time.get()));
                model.play();
            }
        }
        else if(time < 0) {
            if (this.time.get() > 0.25 && this.time.get() <= 2) {
                System.out.println("change time speed ");
                this.time.setValue(this.time.get() + time);
                model.pause();
                time_speed.set((long) (100 / this.time.get()));
                model.play();
            }

            }
    }

    public void connect2fg(){
        model.csvToFg(this.timeSeries);
    }

    public void set_detect_TimeSeries(File f) {
        detect_timeSeries = new TimeSeries(f.getPath());
        model.set_detect_TimeSeries(detect_timeSeries);
    }


    public void setTimeStep(int time_step) {
        this.model.timestep.set(time_step);
        if(timeSeries !=null){
            for (String feature : this.displayVariables.keySet()) {
                displayVariables.get(feature).setValue(timeSeries.valueAtIndex(time_step,this.model.setting_map.get(feature).get(0)));
            }
        }
    }

    public HashMap<String, SimpleFloatProperty> getDisplayVariables() {
        return displayVariables;
    }


    @Override
    public void update(Observable o, Object arg) {
        timeSeries = model.getTimeSeries();
        System.out.println("Time series learn successed");
        display_bool_features.set(true);
    }

    public TimeSeriesAnomalyDetector getAnomalyDetector() {
        return anomalyDetector;
    }

    public void setAnomalyDetector(TimeSeriesAnomalyDetector anomalyDetector) {
        this.model.setAnomalyDetevtor(anomalyDetector);
    }


    ///  the function send the settings file to the model for checking validation and setting the properties
    public void sendSettingsToModel(File f){
        if(this.model.CheckSettings(f)){
            System.out.println("the settings check succeeded");
            this.model.setSettings(f.getPath());
            this.setting_map=this.model.setting_map;
            settings_ok.set(!settings_ok.get());
        }
        else{
            System.out.println("the settings check failed");
            System.out.println(check_for_settings);
            check_for_settings.set(!check_for_settings.get());
            System.out.println(check_for_settings);
        }
    }
    public List<XYChart.Series> getpaintAlgo(){
        selected_feature_vector = model.getSelected_vector(selected_feature.getValue());
        Best_c_feature_vector = model.getBest_cor_Selected_vector(selected_feature.getValue());
        AnomalyReport= model.getAnomalyReports(selected_feature.getValue());
        return this.model.paintAlgo(selected_feature.getValue());
    }

    public float[] get_detect_point(){
        int cur_timestep =time_step.getValue();
        float x_val = selected_feature_vector[cur_timestep];
        if(Best_c_feature_vector==null){return null;}
        float y_val = Best_c_feature_vector[cur_timestep];
        if(AnomalyReport!=null){
          for(int i=0;i<AnomalyReport.size();i++){
                if(AnomalyReport.get(i).timeStep==cur_timestep){
                    float[] detect_point= {x_val,y_val, (float) 1};
                 return detect_point;
                 }
          }
        }
        float[] detect_point= {x_val,y_val,(float)2};
        return detect_point;
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }
}