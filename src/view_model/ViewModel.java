package view_model;


import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import PTM1.Helpclass.Point;
import PTM1.Helpclass.TimeSeries;

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
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class ViewModel implements Observer {

    Model model;
    private HashMap<String,FloatProperty> displayVariables;
    public FloatProperty aileron,elevator,rudder,throttle,altitude,airSpeed,heading;
    public File file;
    public String selected_feature;

    // Attach video slider to timestep : timestep.bind(video_timestep)
    IntegerProperty time_step;
    TimeSeries timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;

    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);
        displayVariables = new HashMap<>();


        /* Set the critical features so we can bind them to the same
           features in the View Section
         */
        aileron = new SimpleFloatProperty();
        elevator = new SimpleFloatProperty();
        rudder = new SimpleFloatProperty();
        throttle = new SimpleFloatProperty();
        altitude = new SimpleFloatProperty();
        airSpeed = new SimpleFloatProperty();
        heading = new SimpleFloatProperty();

        selected_feature = new String();

        time_step = new SimpleIntegerProperty();

        this.model.timestep.bind(this.time_step);


        //  When those features are changing, it evoke a change in the model
        aileron.addListener((o,val,newval)->model.setAileron((float)newval));
        elevator.addListener((o,val,newval)->model.setElevator((float)newval));
        rudder.addListener((o,val,newval)->model.setRudder((float)newval));
        throttle.addListener((o,val,newval)->model.setThrottle((float)newval));
        airSpeed.addListener((o,val,newval)->model.setAirSpeed((float)newval));
        heading.addListener((o,val,newval)->model.setHeading((float)newval));

        //  Change in the time step evoke setTime_step function with the new value as a parameter
        time_step.addListener((o,ov,nv) -> setTimeStep((int) nv));

    }

    /*
    Here we want to get the specific algorithm from the user,
    and afterwards update that value in the model,
    so when the getPaint() func is activated ==> we just need to use
    anomaly detector interface's functions.
     */



    /*
        The csv here is build (X) . we should make a function
         that in the moment the user load csv file, the View
         deliver the csv to the function in the vm
      */



    public void setTimeSeries(File f) {
        this.file = f;
        this.timeSeries = new TimeSeries(file.getPath());
        System.out.println("Vector timeseries size: " + timeSeries.getVector_size());
        model.csvToFg(this.timeSeries);
    }




    public void setSelected_feature(String new_selected_feature ){
        this.selected_feature=new_selected_feature;

        aileron.setValue(timeSeries.valueAtIndex(time_step, "aileron"));
        elevator.setValue(timeSeries.valueAtIndex(time_step, "elevator"));
        rudder.setValue(timeSeries.valueAtIndex(time_step, "rudder"));
        throttle.setValue(timeSeries.valueAtIndex(time_step, "throttle"));
        altitude.setValue(timeSeries.valueAtIndex(time_step, "altitude"));
        airSpeed.setValue(timeSeries.valueAtIndex(time_step, "air speed"));
        heading.setValue(timeSeries.valueAtIndex(time_step, "heading"));
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

    public Runnable getpainter(){
        return ()->this.model.getAnomalyDetector().paint(this.timeSeries);
    }
}