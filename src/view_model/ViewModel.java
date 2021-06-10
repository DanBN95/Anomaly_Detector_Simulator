package view_model;


import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import PTM1.Helpclass.TimeSeries;

import javafx.application.Platform;
import javafx.beans.property.*;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

import model.Model;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class ViewModel implements Observer {

    Model model;


    public FloatProperty aileron, elevator, rudder, throttle, altitude, airSpeed, heading;
    public File file;
    public StringProperty selected_feature;

    public final Runnable play,pause,stop;

    // Attach video slider to timestep : timestep.bind(video_timestep)
    public IntegerProperty time_step;
    TimeSeries timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;
    private HashMap<String, SimpleFloatProperty> displayVariables;
    public BooleanProperty check_settings;

    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);
        check_settings = new SimpleBooleanProperty(false);


        displayVariables = this.model.showFields();
        check_settings.setValue(true);

        selected_feature = new SimpleStringProperty();

        time_step = new SimpleIntegerProperty(0);
        //this.model.timestep = new SimpleIntegerProperty(this.time_step.getValue());
        this.model.timestep = this.time_step;

        //this.model.timestep.bind(this.time_step);


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

        play=()->model.play();
        stop=()->model.stop();
        pause=()->model.pause();

    }

    public void connect2fg(){
        model.csvToFg(this.timeSeries);
    }

    public void setTimeSeries(File f) {
        this.file = f;
        this.timeSeries = new TimeSeries(file.getPath());
        this.model.setTimeSeries(this.timeSeries);
    }


    public void setTimeStep(int time_step) {
        System.out.println("timestep from slider: " + time_step);
        //this.model.timestep.set(time_step);
        if(timeSeries!=null){

            for (String feature : this.displayVariables.keySet()) {
                    displayVariables.get(feature).setValue(timeSeries.valueAtIndex(time_step,this.model.setting_map.get(feature).get(0)));
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


    public HashMap<String, SimpleFloatProperty> getDisplayVariables() {
        return displayVariables;
    }


    @Override
    public void update(Observable o, Object time_step) {

    }

    public TimeSeriesAnomalyDetector getAnomalyDetector() {
        return anomalyDetector;
    }

    public void setAnomalyDetector(TimeSeriesAnomalyDetector anomalyDetector) {
        this.model.setAnomalyDetector(anomalyDetector);
    }



//    public Runnable getpainter(){
//        return ()->this.model.getAnomalyDetector().paint(this.timeSeries);
//    }
}