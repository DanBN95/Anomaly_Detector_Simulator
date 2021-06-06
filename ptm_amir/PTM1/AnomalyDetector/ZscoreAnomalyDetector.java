package PTM1.AnomalyDetector;

import PTM1.Helpclass.Point;
import PTM1.Helpclass.StatLib;
import PTM1.Helpclass.TimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ZscoreAnomalyDetector implements TimeSeriesAnomalyDetector {

    private HashMap<String, Float> zscoremap = new HashMap<String, Float>();
    public HashMap<String, List<Point[]>> to_paint_map = new HashMap<>();


    public float MaxcheckZScore(float[] curColToCheck) {
        ArrayList<Float> curArrayList = new ArrayList<>();
        float curZscore = 0, maxZscore = 0;
        Float[] curArray;
        curArrayList.add(curColToCheck[0]);
        for (int j = 1; j < curColToCheck.length; j++) {
            curArray = curArrayList.toArray(new Float[0]);
            curZscore = checkZScore(curColToCheck[j], curArray);
            curArrayList.add(curColToCheck[j]);
            if (curZscore > maxZscore) {
                maxZscore = curZscore;
            }
        }
        return maxZscore;
    }


    public float checkZScore(float num, Float[] curColToCheck) {
        float curAvg = 0, curStiya = 0, curZscore = 0;
        curStiya = (float) Math.sqrt(StatLib.var(curColToCheck));
        if (curStiya == 0) {
            return 0;
        }
        curAvg = StatLib.avg(curColToCheck);
        curZscore = Math.abs(num - curAvg) / curStiya;
        return curZscore;

    }


    @Override
    public void learnNormal(TimeSeries ts) {
        int hashSize = ts.getHashMap().size();
        String[] features;
        features = ts.FeaturesList();
        for (int i = 0; i < hashSize; i++) {
            float[] curColToCheck = ts.getHashMap().get(features[i]);
            this.zscoremap.put(features[i], MaxcheckZScore(curColToCheck));
        }
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        ts.setvalue("A", 30, 400);
        List<AnomalyReport> anomalyReportList = new LinkedList<>();
        float curAvg = 0, curStiya = 0, curZscore = 0;
        Float[] curArray;
        int hashSize = ts.getVector_size();
        String[] features = ts.FeaturesList();
        for (int j = 0; j < features.length - 1; j++) {
            float[] curColToCheck = ts.getHashMap().get(features[j]);
            ArrayList<Float> curArrayList = new ArrayList<>();
            curArrayList.add(curColToCheck[0]);
            for (int i = 1; i < hashSize; i++) {
                //find the the z score and comper to the high z score
                curArray = curArrayList.toArray(new Float[0]);
                curZscore = checkZScore(curColToCheck[i], curArray);
                curArrayList.add(curColToCheck[i]);
                if (curZscore > this.zscoremap.get(features[j])) {
                    anomalyReportList.add(new AnomalyReport("division in col " + features[j], (long) i + 1));
                }
            }
        }
        return anomalyReportList;
    }


    @Override
    public HashMap<String, String> paint(TimeSeries ts) {
        HashMap<String, String> best_corlation_couples = new HashMap<>();
        float best_correlated = 0;
        String save_through_feature = "";
        String[] features = ts.FeaturesList();
        for (int i = 0; i < ts.getHashMap().size(); i++) { //for every feature checking cov with the other features
            String feature_check = features[i];
            float[] v_check = ts.getHashMap().get(feature_check);
            for (int j = 0; j < ts.getHashMap().size(); j++) {
                if (i == j) {
                    continue;
                }
                String through_feature = features[j];

                float[] through_v = ts.getHashMap().get(through_feature);
                if (Math.abs(StatLib.pearson(v_check, through_v)) > best_correlated) {
                    best_correlated = Math.abs(StatLib.pearson(v_check, through_v)); //set the best cor
                    save_through_feature = through_feature;
                }

            }

            best_corlation_couples.put(feature_check, save_through_feature);


//        float[] feature_to_point;
//        String[] features=ts.FeaturesList();
//        HashMap<String,List<Point[]>> paint_map=new HashMap<>();
//        List<Point> point_per_f_list = new LinkedList<>();
//        List<Point[]> point_list = new LinkedList<>();
//        for(int i=0;i< ts.getHashMap().size();i++){
//            feature_to_point= ts.getHashMap().get(features[i]);
//            for(int j=0;j<ts.getSizeOfVector();j++){
//                point_per_f_list.add(new Point((float)j,feature_to_point[j]));
//            }
//            point_list.add(point_per_f_list.toArray(new Point[0]));
//            paint_map.put(features[i],point_list );
//        }
//        return paint_map;
        }
        return best_corlation_couples;

    }
}