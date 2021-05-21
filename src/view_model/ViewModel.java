package view_model;


import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import PTM1.Helpclass.TimeSeries;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import model.Model;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Observable;
import java.util.Observer;

public class ViewModel implements Observer {

    Model model;
    public FloatProperty aileron,elevator,rudder,throttle,altitude,airSpeed,heading;
    public File file;

    // To Do: attach video slider to timestep : timestep.bind(video_timestep)
    int time_step;

    TimeSeries timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;

    public ViewModel(Model m) {
        this.model = m;
        this.model.addObserver(this);

        /* set the critical features so we can bind them to the same
        features in the View Section
         */
        aileron = new SimpleFloatProperty();
        elevator = new SimpleFloatProperty();
        rudder = new SimpleFloatProperty();
        throttle = new SimpleFloatProperty();
        altitude = new SimpleFloatProperty();
        airSpeed = new SimpleFloatProperty();
        heading = new SimpleFloatProperty();


    }



    /*
    Here we want to get the specific algorithm from the user,
    and afterwards update that value in the model,
    so when the getPaint() func is activated ==> we just need to use
    anomaly detector interface's functions.
     */
    public void LoadAlgo() {
        //need to deal with exceptions
        String input,className;
        System.out.println("enter a class directory");
        try {
            BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
            input=in.readLine(); // get user input
            System.out.println("enter the class name");
            className=in.readLine();
            in.close();
// load class directory
            URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {
                    new URL("file://"+input)
            });
            Class<?> c = urlClassLoader.loadClass(className);
            TimeSeriesAnomalyDetector Ts = (TimeSeriesAnomalyDetector) c.newInstance();
            this.anomalyDetector= Ts;
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /*
    the csv here is build (X) . we should make a function
         that in the moment the user load csv file, the View
         deliver the csv to the function in the vm
      */
    public void setTimeSeries(File f) {
        this.file = f;
        this.timeSeries = new TimeSeries(file.getPath());
        model.csvToFg(this.timeSeries);


             /* EXAMPLE
        for every feature get the valueAtIndex(timestep,feature) and update the addListner
         */
//        float value = timeSeries.valueAtIndex(10,"aileron");


        /*
        when we change this features in the vm, we set the new values in
        the Model, and the View change also because they binding.
         */
        aileron.addListener((o,val,newval)->model.setAileron((float)newval));
        elevator.addListener((o,val,newval)->model.setElevator((float)newval));
        rudder.addListener((o,val,newval)->model.setRudder((float)newval));
        throttle.addListener((o,val,newval)->model.setThrottle((float)newval));
        airSpeed.addListener((o,val,newval)->model.setAirSpeed((float)newval));
        heading.addListener((o,val,newval)->model.setHeading((float)newval));
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}