package model;

import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import javafx.beans.InvalidationListener;
import sample.Properties;
import sample.UserSettings;

import PTM1.Helpclass.TimeSeries;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Observable;

public class Model extends Observable implements Controller {

    TimeSeries timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;
    PrintWriter out2fg;
    UserSettings userSettings;
    Socket fg;

    //יקבל נקודות אם אפשר לצייר
    List<float[]> to_paint;


    public Model(String settings) {

        this.userSettings = new UserSettings();
        setSettings(userSettings);
        serializeToXML(userSettings,settings);
        userSettings = desrializeFromXML(settings);

    }


    public void csvToFg(TimeSeries ts) {
    /*
    suppose to take the timeseries, connecting to fg,
    read line by line from the csv file in the ts values separated by comma(','),
    and for every value the fg put it in the right place
    thank to the playback_small file
     */
        try {
            String line;
            InetAddress ia = InetAddress.getByName(userSettings.getIp());
            System.out.println(userSettings.getIp());
            fg = new Socket(userSettings.getIp(), Integer.parseInt(userSettings.getPort()));
            out2fg = new PrintWriter(fg.getOutputStream());
//            for(int i=1; i<ts.getNumOfFeatures(); i++) {
//                line = Arrays.toString(ts.row_array(i));
//                out2fg.println(Arrays.toString(ts.row_array(i)));
//            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSettings(UserSettings settings) {

        //set Deafault locations
//        try {
//            String [] strings = InetAddress.getLocalHost().toString().split("/");
//            userSettings.setIp(strings[1]);
//            System.out.println(userSettings.getIp());
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        userSettings.setIp("127.0.0.1");
        userSettings.setPort("5400");
        userSettings.setAileron("/controls/flight/aileron[0]");
        userSettings.setElevator("/controls/flight/elevator");
        userSettings.setRudder("/controls/flight/elevator");
        userSettings.setThrottle("/controls/engines/engine[0]/throttle");
        userSettings.setAltitude("/position/altitude-ft");
        userSettings.setAirSpeed("/instrumentation/airspeed-indicator/indicated-speed-kt");
        userSettings.setHeading("/orientation/heading-deg");

        Properties properties = new Properties();
        properties.setAileron_index(0);
        properties.setElevator_index(1);
        properties.setRudder_index(2);
        properties.setThrottle_index(6);
        properties.setAltitude_index(16);
        properties.setAirSpeed_index(21);
        properties.setHeading_index(19);

        properties.setAileron_max(1);
        properties.setAileron_min(-1);
        properties.setElevator_max(1);
        properties.setElevator_min(-1);
        properties.setRudder_max(1);
        properties.setRudder_min(-1);
        properties.setThrottle_max(1);
        properties.setThrottle_min(-1);
        properties.setAltitude_max(1);
        properties.setAltitude_min(-1);
        properties.setAirSpeed_max(100000);
        properties.setAirSpeed_min(0);
        properties.setHeading_max(300);
        properties.setHeading_min(100);
        userSettings.setProperties(properties);
    }

    public UserSettings desrializeFromXML(String xmlFile) {
        try {
            BufferedInputStream file = new BufferedInputStream(new FileInputStream(xmlFile));
            XMLDecoder decoder = new XMLDecoder(file);
            UserSettings decodedSettings = (UserSettings) decoder.readObject();
            decoder.close();
            file.close();
            return decodedSettings;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void serializeToXML(UserSettings us,String settings) {

        try {
            FileOutputStream fos = new FileOutputStream(settings);
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.setExceptionListener(new ExceptionListener() {
                @Override
                public void exceptionThrown(Exception e) {
                    System.out.println("Exception occurred!");
                }
            });

            // writing to xml file
            encoder.writeObject(us);
            encoder.close();
            fos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setAileron(float aileron) {
        String command = userSettings.getAileron();
        out2fg.println(command+" "+aileron);
        out2fg.flush();
    }

    public void setElevator(float elevator) {
        String command = userSettings.getElevator();
        out2fg.println(command+" "+elevator);
        out2fg.flush();
    }

    public void setRudder(float rudder) {
        String command = userSettings.getRudder();
        out2fg.println(command+" "+rudder);
        out2fg.flush();
    }

    public void setThrottle(float throttle) {
        String command = userSettings.getThrottle();
        out2fg.println(command+" "+throttle);
        out2fg.flush();
    }

    public void setAltitude(float altitude) {
        String command = userSettings.getAltitude();
        out2fg.println(command+" "+altitude);
        out2fg.flush();
    }

    public void setAirSpeed(float airSpeed) {
        String command = userSettings.getAirSpeed();
        out2fg.println(command+" "+airSpeed);
        out2fg.flush();
    }

    public void setHeading(float heading) {
        String command = userSettings.getHeading();
        out2fg.println(command+" "+heading);
        out2fg.flush();
    }

    public void finalize() {
        try {
            out2fg.close();
            fg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setAnomalyDetevtor(TimeSeriesAnomalyDetector ad ){
        this.anomalyDetector=ad;
    }


    // Projection Functions:
    @Override
    public void setTimeSeries(TimeSeries ts) { this.timeSeries = ts; }

    @Override
    public void play(int start, int rate) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }



    //add start to anomlydetector to start search detector
}