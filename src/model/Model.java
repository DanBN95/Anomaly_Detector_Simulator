package model;

import PTM1.AnomalyDetector.AnomalyReport;
import PTM1.AnomalyDetector.TimeSeriesAnomalyDetector;
import javafx.beans.property.*;
import javafx.scene.chart.XYChart;
import sample.Properties;
import sample.UserSettings;
import PTM1.Helpclass.TimeSeries;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Model extends Observable  {

    public TimeSeries timeSeries,detect_timeSeries;
    public TimeSeriesAnomalyDetector anomalyDetector;

    PrintWriter out2fg;
    Socket fg;

    public HashMap<String, ArrayList<Integer>> setting_map;
    public HashMap<String,List<AnomalyReport>> AnomalyReports;

    int port;
    String ip;
    public String settings;
    Timer t = null;

    public IntegerProperty timestep;
    public volatile LongProperty time_speed;
    public float time_default;

    public Model(String settings) {
        this.settings = settings;
    }

    public void csvToFg() {
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
        time_default = 200;
        setting_map = new HashMap<>();
        if (settings.contains(".txt"))
            loadSettingsFromTxt(settings);
        else if(settings.contains(".xml")) {
            serializeToXML(settings);
            desrializeFromXML(settings);
        }


    }

    public void loadSettingsFromTxt(String settings) {
        Scanner myScanner = null;
        try {
            myScanner = new Scanner(new BufferedReader(new FileReader(settings)));
            while (myScanner.hasNextLine()) {
                String line = myScanner.nextLine();
                String[] vec_by_row = line.split(",");
                if (vec_by_row[0].equals("ip")) {
                    ip = vec_by_row[1];
                    continue;
                }
                else if (vec_by_row[0].equals("port")) {
                    port = Integer.parseInt(vec_by_row[1]);
                    continue;
                }
                else if (vec_by_row[0].equals("run speed")) {
                    time_speed.set(Long.parseLong(vec_by_row[1]));
                    time_default = Float.parseFloat(vec_by_row[1]);
                    continue;
                }
                else if (vec_by_row[0].equals("flight path")) {
                    timeSeries = new TimeSeries(vec_by_row[1]);
                    setChanged();
                    notifyObservers();
                    continue;
                }
                setting_map.put(vec_by_row[0], new ArrayList<>());
                int i = 0;
                for (String s : vec_by_row) {
                    if (i == 0) {
                        i++;
                        continue;
                    }
                    setting_map.get(vec_by_row[0]).add(Integer.parseInt(s));
                }
            }

            myScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void desrializeFromXML(String xmlFile) {
        try {
            BufferedInputStream file = new BufferedInputStream(new FileInputStream(xmlFile));
            XMLDecoder decoder = new XMLDecoder(file);
            UserSettings decodedSettings = (UserSettings) decoder.readObject();
            decoder.close();
            file.close();

            for(String s : decodedSettings.getHsm().keySet()) {
                ArrayList<Integer>list = new ArrayList();
                int index = decodedSettings.getHsm().get(s).getIndex();
                int max = decodedSettings.getHsm().get(s).getMax();
                int min = decodedSettings.getHsm().get(s).getMin();

                list.add(0,index);
                list.add(1,max);
                list.add(2,min);

                setting_map.put(s,list);

                port = decodedSettings.getPort();
                ip = decodedSettings.getIp();
                time_speed.set((long)decodedSettings.getRun_speed());
                time_default = decodedSettings.getRun_speed();
                //timeSeries = new TimeSeries()
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void serializeToXML(String settings) {

        UserSettings us = new UserSettings();
        HashMap<String, Properties> userProp = new HashMap<>();
        String [] features = {"aileron","elevator","rudder","throttle","altitude","airSpeed","heading","roll","pitch","yaw"};
        int [] index = {0,1,2,6,16,21,19,28,29,20};
        int [] max = {1,1,0,1,860,100,350,40,17,90};
        int [] min = {-1,-1,0,0,0,0,80,-38,-10,-30};
        int i = 0;
        for(String s : features) {
            Properties p = new Properties();
            p.setIndex(index[i]);
            p.setMax(max[i]);
            p.setMin(min[i]);
            userProp.put(s,p);
            i++;
        }
        us.setHsm(userProp);
        us.setIp("127.0.0.1");
        us.setPort(5400);
        us.setRun_speed(200);
        try {
            FileOutputStream fos = new FileOutputStream(settings);
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.setExceptionListener(new ExceptionListener() {
                @Override
                public void exceptionThrown(Exception e) {
                    System.out.println("Exception occurred encode to XML(Model,line 285)!");
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
        }
        if(this.detect_timeSeries!=null){
            this.anomalyDetector.detect(detect_timeSeries);
        }
    }

    /// the function checks the validation of the settings file the user uploaded
    public boolean CheckSettings(File f){
        boolean answer = true;
        int count_rows = 0;
        Scanner scanner = null;
        try{
            scanner = new Scanner(new BufferedReader(new FileReader(f.getPath())));
            while (scanner.hasNextLine()){
                count_rows++;
                String line = scanner.nextLine();
                String[] line_in_array = line.split(",");
                if(line_in_array[0].equals("ip")){
                    if(!validateIP(line_in_array[1])){
                        System.out.println("the validation of the ip failed");
                        answer=false;
                        break;
                    }
                }
                else if(line_in_array[0].equals("port")){
                    if(!validatePort(line_in_array[1])){
                        System.out.println("the validation of the port failed");
                        answer=false;
                        break;
                    }
                }
                else if(line_in_array[0].equals("run speed")) {
                    float range = Float.parseFloat(line_in_array[1]);
                    if(range <= 0 || range >= 3000 || range % 100 != 0) {
                        System.out.println("run speed out of range!");
                        answer = false;
                        break;
                    }
                }
                //*********************************************
                // Add else if for rum speed validation

                else if(!(line_in_array.length==4)){
                    System.out.println("the length of the array is not 4");
                    answer=false;
                    break;
                }
                else{
                    for(int i=1; i<=3; i++){
                        if(!(isNumeric(line_in_array[i]))){
                            System.out.println("the string is not a number");
                            answer=false;
                            break;
                        }
                    }
                }
            }
            if(count_rows != 14) {
                System.out.println("Missing/Too many settings!");
                answer = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return answer;
    }

    ///  the function checks if a string is a number
    public static boolean isNumeric(String s){
        if (s == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    ///  the function checks if the IP is valid
    public static boolean validateIP(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    ///  the function checks if the port is valid
    public static boolean validatePort(final String ip) {
        String PATTERN = "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
        return ip.matches(PATTERN);
    }

    public void setTimeSeries(TimeSeries ts) {
        this.timeSeries = ts;
        if(anomalyDetector!=null){
            anomalyDetector.learnNormal(this.timeSeries);
        }
    }
    public void set_detect_TimeSeries(File f) {
        detect_timeSeries = new TimeSeries(f.getPath());
        if(anomalyDetector!=null){
            AnomalyReports=anomalyDetector.detect(detect_timeSeries);
        }
    }

    public void play() {
        if(t==null){
            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                   // System.out.println("sending row "+ timestep.get() + " with time speed: " + time_speed.get());
                    String row_data = timeSeries.row_array(timestep.get());
                    out2fg.println(row_data);
                    timestep.set(timestep.get()+1);

                }
            },0,time_speed.get());
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
            return scores;
    }

    public float[] getSelected_vector(String selected_feature){
        String feature_name = setting_map.containsKey(selected_feature) ? timeSeries.getFeaturesList()[setting_map.get(selected_feature).get(0)] : selected_feature;
        float[] selected_feature_vals = timeSeries.getHashMap().get(feature_name);
        return selected_feature_vals;
    }

    public float[] getBest_cor_Selected_vector(String selected_feature){
        String best_c_feature = setting_map.containsKey(selected_feature)? timeSeries.getbest_c_feature(setting_map.get(selected_feature).get(0)) : timeSeries.getbest_c_feature(selected_feature);
        float[] selected_feature_vals=null;
        if(!best_c_feature.equals(""))
             selected_feature_vals = timeSeries.getHashMap().get(best_c_feature);
        return selected_feature_vals;
    }

    public List<XYChart.Series> paintAlgo(String selected_featureX){
        String slected_feature_N = setting_map.containsKey(selected_featureX)? timeSeries.getFeaturesList()[setting_map.get(selected_featureX).get(0)]:selected_featureX;
        return anomalyDetector.paint(timeSeries,slected_feature_N);
    }


    public TimeSeries getTimeSeries() {
        return timeSeries;
    }
}