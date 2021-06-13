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
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
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


    private HashMap<String,SimpleFloatProperty> displayVariables;
    public FloatProperty aileron,elevator,rudder,throttle,altitude,airSpeed,heading;
    public StringProperty selected_feature;
    public BooleanProperty check_settings;
    private ScheduledExecutorService scheduledExecutorService;



    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);
        scheduledExecutorService = Executors.newScheduledThreadPool(5);

        displayVariables = this.model.showFields();
        check_settings=new SimpleBooleanProperty(false);
        check_settings.setValue(true);
        selected_feature=new SimpleStringProperty();

        time_step = new SimpleIntegerProperty(0);
        time_speed = new SimpleLongProperty(1);

        model.timestep = this.time_step;
        model.time_speed = this.time_speed;
        x = 1;


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
        forward = ()-> this.changeReadInterval(0.25);
        backward = ()-> this.changeReadInterval(-0.25);

    }

    //  to do: set timer to thread here
    public void changeReadInterval(double time)
    {
        if(x >= 0.25 && x <= 2)
        {
            if (futureTask != null)
            {
                x += time;
                this.time_speed.set((long)time + time_speed.getValue());
                System.out.println("current time: " + time);
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<< change read Interval function " + x);

                futureTask.cancel(true);
            }

            futureTask = scheduledExecutorService.scheduleAtFixedRate(play, 0,1000/(long) x, TimeUnit.MILLISECONDS );
        }
    }


    // 500, 750, 1000, 1750 2000
    public void changeTimeSpeed(int change) {
        if(time_speed.get() == 500 && change > 0)
            this.time_speed.set(750);
        else if(time_speed.get() == 750)
            this.time_speed.set(time_speed.get() - (long)change);
        else if(time_speed.get() == 1000 && change > 0)
            this.time_speed.set(300);
        else if(time_speed.get() == 1000 && change < 0)
            this.time_speed.set(1750);
        else if(time_speed.get() == 1750 && change > 0)
            this.time_speed.set(1000);
        else if(time_speed.get() == 1750 && change < 0)
            this.time_speed.set(2000);
        else if(time_speed.get() == 2000 && change < 0) {
            this.time_speed.set(1750);
            System.out.println("Minimum time speed: " + time_speed.get() + "!");
        }
        System.out.println("current time speed is: " + time_speed.get());
    }

    public void connect2fg(){
        model.csvToFg(this.timeSeries);
    }

    public void setTimeSeries(File f) {
        this.file = f;
        this.timeSeries = new TimeSeries(file.getPath());
        model.setTimeSeries(this.timeSeries);
    }


    public void setTimeStep(int time_step) {
        System.out.println("timestep from slider: " + time_step);
        this.model.timestep.set(time_step);
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



//    public Runnable getpainter(){
//        return ()->this.model.getAnomalyDetector().paint(this.timeSeries);
//    }
}