package model;

import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import javafx.beans.property.*;
import sample.UserSettings;
import PTM1.Helpclass.TimeSeries;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Model extends Observable  {

    TimeSeries timeSeries;
    TimeSeriesAnomalyDetector anomalyDetector;
    PrintWriter out2fg;
    Socket fg;
    public HashMap<String, ArrayList<Integer>> setting_map;
    int port;
    String ip;
    Timer t = null;
    public IntegerProperty timestep;
    public volatile LongProperty time_speed;

    public Model(String settings) {

        setSettings(settings);


        // this.userSettings = new UserSettings();
       // serializeToXML(userSettings,settings);
       // userSettings = desrializeFromXML(settings);
    }

    public void csvToFg(TimeSeries ts) {
    /*
    suppose to take the timeseries, connecting to fg,
    read line by line from the csv file in the ts values separated by comma(','),
    and for every value the fg put it in the right placee
    thank to the playback_small file
     */
            System.out.println("csvToFg");
        try {
            fg = new Socket(ip,port);
            out2fg = new PrintWriter(fg.getOutputStream());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSettings(String settings) {

        setting_map = new HashMap<>();
        Scanner myScanner = null;
        try {
            myScanner = new Scanner(new BufferedReader(new FileReader(settings)));
            while (myScanner.hasNextLine()) {

                String line = myScanner.nextLine();
                String[] vec_by_row = line.split(",");
                if(vec_by_row[0].equals("ip")){
                    ip=vec_by_row[1];
                    continue;
                }
                if(vec_by_row[0].equals("port")){
                    port=Integer.parseInt(vec_by_row[1]);
                    continue;
                }
                setting_map.put(vec_by_row[0], new ArrayList<>());
                int i=0;
                for (String s : vec_by_row) {
                    if(i==0){
                        i++;
                        continue;
                    }
                    setting_map.get(vec_by_row[0]).add(Integer.parseInt(s));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        try {
//            String [] strings = InetAddress.getLocalHost().toString().split("/");
//            userSettings.setIp(strings[1]);
//            System.out.println(userSettings.getIp());
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
////        }
//        HashMap<String,Properties> hsm = new HashMap<>();
//        userSettings.setIp("127.0.0.1");
//        userSettings.setPort("5400");
//
//
//        userSettings.setHsm(hsm);
//        String [] features = {"aileron","elevator","rudder","throttle","altitude","airspeed","heading","roll","pitch","yaw"};
//        int [] index = {0,1,2,6,16,21,19,28,29,20};
//        float [] max = {1,1,1,1,1}
//        String [] min = new String[10];
//        for(String f : features) {
//            Properties p = new Properties();
//            hsm.put(f,getProp(p,))
//        }
//
//
//        Properties properties = new Properties();
//        properties.setAileron_index(0);
//        properties.setElevator_index(1);
//        properties.setRudder_index(2);
//        properties.setThrottle_index(6);
//        properties.setAltitude_index(16);
//        properties.setAirSpeed_index(21);
//        properties.setHeading_index(19);
//        properties.setRoll_index(28);
//        properties.setPitch_index(29);
//        properties.setYaw_index(20);
//
//        properties.setAileron_max(1);
//        properties.setAileron_min(-1);
//        properties.setElevator_max(1);
//        properties.setElevator_min(-1);
//        properties.setRudder_max(1);
//        properties.setRudder_min(-1);
//        properties.setThrottle_max(1);
//        properties.setThrottle_min(-1);
//        properties.setAltitude_max(1);
//        properties.setAltitude_min(-1);
//        properties.setAirSpeed_max(100000);
//        properties.setAirSpeed_min(0);
//        properties.setHeading_max(300);
//        properties.setHeading_min(100);
//        properties.setRoll_max(40);
//        properties.setRoll_min(-38);
//        properties.setPitch_max(17);
//        properties.setPitch_min(-10);
//        properties.setYaw_max(90);
//        properties.setYaw_min(-30);
//
//        userSettings.setProperties(properties);
//    }

//    public Properties getProp(Properties p,int index,float max, float min) {
//        p.setIndex(index);
//        p.setMax(max);
//        p.setMin(min);
//        return p;
//    }

//    public UserSettings desrializeFromXML(String xmlFile) {
//        try {
//            BufferedInputStream file = new BufferedInputStream(new FileInputStream(xmlFile));
//            XMLDecoder decoder = new XMLDecoder(file);
//            UserSettings decodedSettings = (UserSettings) decoder.readObject();
//            decoder.close();
//            file.close();
//            return decodedSettings;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void serializeToXML(UserSettings us,String settings) {
//
//        try {
//            FileOutputStream fos = new FileOutputStream(settings);
//            XMLEncoder encoder = new XMLEncoder(fos);
//            encoder.setExceptionListener(new ExceptionListener() {
//                @Override
//                public void exceptionThrown(Exception e) {
//                    System.out.println("Exception occurred!");
//                }
//            });
//
//            // writing to xml file
//            encoder.writeObject(us);
//            encoder.close();
//            fos.close();
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void setAileron(float aileron) {
//        String command = userSettings.getAileron();
//        out2fg.println(command+" "+aileron);
//        out2fg.flush();
//    }
//
//    public void setElevator(float elevator) {
//        String command = userSettings.getElevator();
//        out2fg.println(command+" "+elevator);
//        out2fg.flush();
//    }
//
//    public void setRudder(float rudder) {
//        String command = userSettings.getRudder();
//        out2fg.println(command+" "+rudder);
//        out2fg.flush();
//    }
//
//    public void setThrottle(float throttle) {
//        String command = userSettings.getThrottle();
//        out2fg.println(command+" "+throttle);
//        out2fg.flush();
//    }
//
//    public void setAltitude(float altitude) {
//        String command = userSettings.getAltitude();
//        out2fg.println(command+" "+altitude);
//        out2fg.flush();
//    }
//
//    public void setAirSpeed(float airSpeed) {
//        String command = userSettings.getAirSpeed();
//        out2fg.println(command+" "+airSpeed);
//        out2fg.flush();
//    }
//
//    public void setHeading(float heading) {
//        String command = userSettings.getHeading();
//        out2fg.println(command+" "+heading);
//        out2fg.flush();
//    }
    }
    public void finalize() {
        try {
            out2fg.close();
            fg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TimeSeriesAnomalyDetector getAnomalyDetector() {
        return anomalyDetector;
    }

    public void setAnomalyDetevtor(TimeSeriesAnomalyDetector ad ){
        this.anomalyDetector=ad;
        if(this.timeSeries!=null){
            this.anomalyDetector.learnNormal(this.timeSeries);
            this.anomalyDetector.detect(this.timeSeries);

        }
    }

    // Projection Functions:
    public void setTimeSeries(TimeSeries ts) {
        this.timeSeries = ts;
        if(this.anomalyDetector!=null){
            this.anomalyDetector.learnNormal(this.timeSeries);
            this.anomalyDetector.detect(this.timeSeries);
        }
    }

    public void play() {
        if(t==null){
            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long start = System.nanoTime();
                    System.out.println("sending row "+ timestep.get() + " with time speed: " + time_speed.get());
                    String row_data = timeSeries.row_array(timestep.get());
                    out2fg.println(row_data);
                    System.out.println(row_data);
                    timestep.set(timestep.get()+1);
                    long end = System.nanoTime();
                    System.out.println("********************* Time: " + (end-start)/1000000);

                }
            },0,1000);
        }
    }


    public void pause() {
        if(t != null) {
            t.cancel();
        }
        t=null;
    }


    public void stop() {
        if(t != null) {
            t.cancel();
        }
        t=null;
        timestep.set(0);
    }

    public HashMap<String, SimpleFloatProperty> showFields() {

        HashMap<String, SimpleFloatProperty> scores=new HashMap<>();
        for (String feature:this.setting_map.keySet()) {
            if(this.setting_map.get(feature).size()==3){
                scores.put(feature, new SimpleFloatProperty());
            }
        }
            System.out.println(scores.keySet());
            return scores;
    }
}