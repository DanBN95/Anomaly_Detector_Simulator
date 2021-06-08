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
    public List<List<Point>> point_to_p = new LinkedList<>();

    IntegerProperty time_step;
    TimeSeries timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;

    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);
        displayVariables = new HashMap<>();

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


        aileron.addListener((o,val,newval)->model.setAileron((float)newval));
        elevator.addListener((o,val,newval)->model.setElevator((float)newval));
        rudder.addListener((o,val,newval)->model.setRudder((float)newval));
        throttle.addListener((o,val,newval)->model.setThrottle((float)newval));
        airSpeed.addListener((o,val,newval)->model.setAirSpeed((float)newval));
        heading.addListener((o,val,newval)->model.setHeading((float)newval));


        time_step.addListener((o,ov,nv) -> setTimeStep((int) nv));

    }


    public void setTimeSeries(File f) {
        this.file = f;
        this.timeSeries = new TimeSeries(file.getPath());
        System.out.println("Vector timeseries size: " + timeSeries.getVector_size());
        model.csvToFg(this.timeSeries);
    }




    public void setSelected_feature(String new_selected_feature ){
        this.selected_feature=new_selected_feature;
    }

    public void setTimeStep(int time_step){

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
        return ()->this.model.getAnomalyDetector().paint(this.timeSeries,selected_feature);
    }
}