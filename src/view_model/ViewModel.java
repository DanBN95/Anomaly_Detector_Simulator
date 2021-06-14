package view_model;


import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import PTM1.Helpclass.Point;
import PTM1.Helpclass.TimeSeries;

import javafx.application.Platform;
import javafx.beans.property.*;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

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

    TimeSeries timeSeries;
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


    public String best_c_feature;

    public List<Float> selected_feature_vector;
    public List<Float> Best_c_feature_vector;

    public BooleanProperty check_for_paint;

    /// for the popup alert in case the settings file of the user is not good
    public BooleanProperty check_for_settings = new SimpleBooleanProperty(true);
    public BooleanProperty settings_ok = new SimpleBooleanProperty(false);



    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);
        scheduledExecutorService = Executors.newScheduledThreadPool(5);

        displayVariables = this.model.showFields();
        check_settings=new SimpleBooleanProperty(false);
        check_settings.setValue(true);
        selected_feature = new SimpleStringProperty();
        time = new SimpleDoubleProperty();

        time_step = new SimpleIntegerProperty(0);
        time_speed = new SimpleLongProperty(100);
        this.time.setValue(1);

        model.timestep = this.time_step;
        model.time_speed = this.time_speed;
        x = 1;

        check_for_paint = new SimpleBooleanProperty(true);


        //  When those features are changing, it evoke a change in the model
//        this.displayVariables.get("aileron").addListener((o, val, newval) -> model.setAileron((float) newval));
//        this.displayVariables.get("elevator").addListener((o, val, newval) -> model.setElevator((float) newval));
//        this.displayVariables.get("rudder").addListener((o, val, newval) -> model.setRudder((float) newval));
//        this.displayVariables.get("throttle").addListener((o, val, newval) -> model.setThrottle((float) newval));
//        this.displayVariables.get("airSpeed").addListener((o, val, newval) -> model.setAirSpeed((float) newval));
//        this.displayVariables.get("heading").addListener((o, val, newval) -> model.setHeading((float) newval));

          //Change in the time step evoke setTime_step function with the new value as a parameter

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
        if(this.time.get() >= 0.25 && this.time.get() <= 2) {
            System.out.println("change time speed ");
            this.time.setValue(this.time.get() + time);
            model.pause();

            time_speed.set((long)(100 / this.time.get()));
            model.play();
        }
    }

    public void connect2fg(){
        model.csvToFg(this.timeSeries);
    }

    public void setTimeSeries(File f) {
        this.file = f;
        this.timeSeries = new TimeSeries(file.getPath());
        model.setTimeSeries(this.timeSeries);
        System.out.println("VM line 131: feature list is: ");
        for(String s : timeSeries.FeaturesList())
            System.out.print(s + ", ");
    }


    public void setTimeStep(int time_step) {
        this.model.timestep.set(time_step);
        if(timeSeries !=null){
            check_for_paint.setValue(!check_for_paint.getValue());
            for (String feature : this.displayVariables.keySet()) {
                displayVariables.get(feature).setValue(timeSeries.valueAtIndex(time_step,this.model.setting_map.get(feature).get(0)));
            }
            if(selected_feature.getValue()!=null) {
                System.out.println("this is the selected feature"+selected_feature);
                selected_feature_vector = model.getSelected_vector(selected_feature.getValue());
                Best_c_feature_vector = model.getBest_cor_Selected_vector(best_c_feature);
            }
        }
    }
//        aileron.setValue(timeSeries.valueAtIndex(time_step, "aileron"));
//        elevator.setValue(timeSeries.valueAtIndex(time_step, "elevator"));
//        rudder.setValue(timeSeries.valueAtIndex(time_step, "rudder"));
//        throttle.setValue(timeSeries.valueAtIndex(time_step, "throttle"));
//        altitude.setValue(timeSeries.valueAtIndex(time_step, "altitude"));
//        airSpeed.setValue(timeSeries.valueAtIndex(time_step, "air speed"));
//        heading.setValue(timeSeries.valueAtIndex(time_step, "heading"));

//    public void setSelected_feature(String new_selected_feature ) {
//        this.selected_feature = new_selected_feature;
//    }

    public HashMap<String, SimpleFloatProperty> getDisplayVariables() {
        return displayVariables;
    }


    @Override
    public void update(Observable o, Object arg) {

    }

    public TimeSeriesAnomalyDetector getAnomalyDetector() {
        return anomalyDetector;
    }

    public void setAnomalyDetector(TimeSeriesAnomalyDetector anomalyDetector) {
        this.model.setAnomalyDetevtor(anomalyDetector);
    }

    public void setBest_c_feature(String selected_feature) {
        this.best_c_feature = this.model.getBest_c_feature(selected_feature);
    }

    ///  the function send the settings file to the model for checking validation and setting the properties
    public void sendSettingsToModel(File f){
        if(this.model.CheckSettings(f)){
            System.out.println("the settings check succeeded");
            this.model.setSettings(f.getPath());
            settings_ok.set(!settings_ok.get());
        }
        else{
            System.out.println("the settings check failed");
            check_for_settings.set(!check_for_settings.get());
        }
    }

//    public Runnable getpainter(){
//        return ()->this.model.getAnomalyDetector().paint(this.timeSeries);
//    }
}