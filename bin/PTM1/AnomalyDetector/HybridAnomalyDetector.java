package PTM1.AnomalyDetector;


import PTM1.CorrelatedFeatures.*;
import PTM1.Helpclass.*;
import javafx.scene.chart.XYChart;

import java.util.*;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector {

    HashMap<String, LineCorrelatedFeatures> LinecorrelatedFeaturesList = new HashMap<>();
    HashMap<String, CircleCorrelatedFeatures> CirclecorrelatedFeaturesList = new HashMap<>();
    private HashMap<String, Float> zscoremap = new HashMap<String, Float>();
    HashMap<String, String> best_corlation_couples = new HashMap<>();

    @Override
    public void learnNormal(TimeSeries ts) {
        float best_correlated = 0;
        String[] features = ts.getFeaturesList();
        String feature_check = null;
        float[] v_check = null;
        float[] through_v = null;

        for (int i = 0; i < ts.getHashMap().size() ; i++) { //for every feature checking cov with the other features
            feature_check = features[i];
            v_check = ts.getHashMap().get(feature_check);
            String through_feature = ts.getbest_c_feature(feature_check);
            if(through_feature.equals("")){this.zscoremap.put(feature_check, (float)0);
            continue;}
            through_v = ts.getHashMap().get(through_feature);
            best_correlated = Math.abs(StatLib.pearson(v_check, through_v));
            this.best_corlation_couples.put(feature_check, through_feature);
            if (best_correlated >= 0.95) {
                Point[] p = new Point[ts.getSizeOfVector()];
                for (int k = 0; k < ts.getSizeOfVector(); k++)
                    p[k] = new Point(ts.valueAtIndex(k, feature_check), ts.valueAtIndex(k, through_feature));
                Line reg_line = StatLib.linear_reg(p);
                float max_dev = -1;
                for (Point point : p) {
                    float result = StatLib.dev(point, reg_line);
                    if (result > max_dev)
                        max_dev = result;
                }
                max_dev *= (float) 1.1;
                this.LinecorrelatedFeaturesList.put(feature_check, new LineCorrelatedFeatures(feature_check, through_feature, best_correlated, reg_line, max_dev));


                //zscore->
            }
            else if (best_correlated < 0.5) {
                Vector<Float> curArrayList = new Vector<Float>();
                float curAvg = 0, curStiya = 0, curZscore = 0, maxZscore = 0;
                Float[] curArray = null;
                curArrayList.add(v_check[0]);
                for (int j = 1; j < v_check.length; j++) {
                    curArray = curArrayList.toArray(new Float[0]);
                    curZscore = StatLib.checkZScore(v_check[i], curArray);
                    curArrayList.add(v_check[i]);
                    if (curZscore > maxZscore) {
                        maxZscore = curZscore;
                    }
                }
                this.zscoremap.put(feature_check, maxZscore);


                //wizel's algo->
            }
            else {
                Vector<Point> point_for_cercle = new Vector<>();
                for (int t = 0; t < v_check.length; t++) {
                    point_for_cercle.add(new Point(v_check[t], through_v[t]));
                }
                Circle data = new AlgorithmMinCircle().minidisk(point_for_cercle);
                this.CirclecorrelatedFeaturesList.put(feature_check, new CircleCorrelatedFeatures(feature_check, through_feature, best_correlated, data.center, data.radius));

            }
        }
    }

    @Override
    public HashMap<String,List<AnomalyReport>> detect(TimeSeries ts) {

        HashMap<String,List<AnomalyReport>> anomalyReportList = new HashMap<>();
        float[] curColToCheck;
        Float[] curArray;
        for (LineCorrelatedFeatures correlatedFeatures : this.LinecorrelatedFeaturesList.values()) {
            for (int i = 0; i < ts.getSizeOfVector(); i++) {
                Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
                if (StatLib.dev(p,correlatedFeatures.lin_reg) > correlatedFeatures.max_div) {
                    if (anomalyReportList.containsKey(correlatedFeatures.feature1) == false) {
                        anomalyReportList.put(correlatedFeatures.feature1, new LinkedList<>());
                    }
                    anomalyReportList.get(correlatedFeatures.feature1).add(new AnomalyReport(correlatedFeatures.feature1 + "-"
                            + correlatedFeatures.feature2, correlatedFeatures.feature2, (long) i + 1));
                }
             }
        }
        for (String name_feature:this.zscoremap.keySet()) {
            float curZscore;
            int hashSize=ts.getHashMap().size();
            curColToCheck = ts.getHashMap().get(name_feature);
            ArrayList<Float> curArrayList=new ArrayList<>();
            curArrayList.add(curColToCheck[0]);
            for (int i=1;i<hashSize-1;i++) {
                curArray=curArrayList.toArray(new Float[0]);
                curZscore=StatLib.checkZScore(curColToCheck[i], curArray);
                curArrayList.add(curColToCheck[i]);
                if ( curZscore > this.zscoremap.get(name_feature)) {
                    if(anomalyReportList.containsKey(name_feature)==false){
                        anomalyReportList.put(name_feature,new LinkedList<>());
                    }
                    anomalyReportList.get(name_feature).add(new AnomalyReport( name_feature, name_feature,(long)i+1));
                }
            }
        }

        for (CircleCorrelatedFeatures correlatedFeatures : this.CirclecorrelatedFeaturesList.values()) {
            Circle check_in_circle = new Circle (correlatedFeatures.center,correlatedFeatures.radius);
            for (int i = 0; i < ts.getSizeOfVector(); i++) {
                Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
                if (!check_in_circle.containsPoint(p)) {
                    if (anomalyReportList.containsKey(correlatedFeatures.feature1) == false) {
                        anomalyReportList.put(correlatedFeatures.feature1, new LinkedList<>());
                    }
                    anomalyReportList.get(correlatedFeatures.feature1).add(new AnomalyReport(correlatedFeatures.feature1 + "-"
                            + correlatedFeatures.feature2, correlatedFeatures.feature2, (long) i + 1));
                }
            }
        }

        return anomalyReportList;



//        XYChart.Series series = new XYChart.Series();
//        XYChart.Series series2= new XYChart.Series();
//        List<XYChart.Series> points =new LinkedList<>();
//        float[] feature_vals = ts.getHashMap().get(feature);
//        if (zscoremap.containsKey(feature)) {
//            ArrayList<Float> curArrayList = new ArrayList<>();
//            float curZscore, correlated_zscore;
//            Float[] curArray;
//            feature_vals = ts.getHashMap().get(feature);
//            correlated_zscore = zscoremap.get(feature);
//            curArrayList.add(feature_vals[0]);
//            curArrayList.add(feature_vals[1]);
//            series.getData().add(new XYChart.Data(0, feature_vals[0]));
//            series.getData().add(new XYChart.Data(1, feature_vals[1]));
//            for (int i = 2; i < feature_vals.length; i++) {
//                curArray = curArrayList.toArray(new Float[0]);
//                curZscore = StatLib.checkZScore(feature_vals[i], curArray);
//                curArrayList.add(feature_vals[i]);
//                if (curZscore > correlated_zscore) {
//                    series2.getData().add(new XYChart.Data(i, feature_vals[i],"Bad"));
//                } else {
//                    series.getData().add(new XYChart.Data(i, feature_vals[i],"Good"));
//                }
//            }
//        }
//        else if (LinecorrelatedFeaturesList.containsKey(feature)) {
//            LineCorrelatedFeatures correlatedFeature = LinecorrelatedFeaturesList.get(feature);
//            float[] best_c_vals = ts.getHashMap().get(best_corlation_couples.get(feature));
//            for (int s = 0; s < best_c_vals.length; s++) {
//                if (StatLib.dev(new Point(feature_vals[s], best_c_vals[s]), correlatedFeature.lin_reg) > correlatedFeature.max_div) {
//                    series2.getData().add(new XYChart.Data(feature_vals[s], best_c_vals[s],"Bad"));
//                } else {
//                    series.getData().add(new XYChart.Data(feature_vals[s], best_c_vals[s],"Good"));
//                }
//            }
//        }
//        else if (CirclecorrelatedFeaturesList.containsKey(feature)){
//            CircleCorrelatedFeatures CircleCorrelatedFeature = CirclecorrelatedFeaturesList.get(feature);
//            Circle check_in_circle = new Circle(CircleCorrelatedFeature.center, CircleCorrelatedFeature.radius);
//            float[] best_c_vals = ts.getHashMap().get(best_corlation_couples.get(feature));
//            Point p = new Point(0, 0);
//            for (int s = 0; s < best_c_vals.length; s++) {
//                p.x = feature_vals[s];
//                p.y = best_c_vals[s];
//                if (!check_in_circle.containsPoint(p)) {
//                    series2.getData().add(new XYChart.Data(p.x, p.y,"Bad"));
//                } else {
//                    series.getData().add(new XYChart.Data(p.x, p.y,"Good"));
//                }
//            }
//        }
//        else{
//            System.out.println("problem with learn function");
//        }
//        points.add(series);
//        points.add(series2);
//        return points;

    }

    @Override
    public List<XYChart.Series> paint(TimeSeries ts, String feature) {
        List<XYChart.Series> points = new LinkedList<>();
        float[] selected_f_vals = ts.getHashMap().get(feature);
        XYChart.Series learning_points = new XYChart.Series<Number, Number>();
        XYChart.Series Algo_points = new XYChart.Series<Number, Number>();
        XYChart.Series setting_algo = new XYChart.Series();
        float min = StatLib.min(selected_f_vals);
        float max = StatLib.max(selected_f_vals);
        learning_points.setName("Lerning Points");
        if (zscoremap.containsKey(feature)) {
            float zscore = zscoremap.get(feature);
            Algo_points.setName("Zscore-Algo");
            Algo_points.getData().add(new XYChart.Data(0, zscore));
            Algo_points.getData().add(new XYChart.Data(selected_f_vals.length, zscore));
            setting_algo.getData().add(new XYChart.Data(0,selected_f_vals.length));
            setting_algo.getData().add(new XYChart.Data(0,zscore));

        }
        else if (LinecorrelatedFeaturesList.containsKey(feature)) {
            float[] best_c_f_vals = ts.getHashMap().get(best_corlation_couples.get(feature));
            Line line = new Line(this.LinecorrelatedFeaturesList.get(feature).lin_reg.a, this.LinecorrelatedFeaturesList.get(feature).lin_reg.a);
            for (int i = 0; i < selected_f_vals.length; i++) {
                learning_points.getData().add(new XYChart.Data(selected_f_vals[i], best_c_f_vals[i]));
            }
            Algo_points.getData().add(new XYChart.Data(min, line.a * min + line.b));
            Algo_points.getData().add(new XYChart.Data(max, line.a * max + line.b));
            setting_algo.getData().add(new XYChart.Data(min,max));
            setting_algo.getData().add(new XYChart.Data(line.a * min + line.b,line.a * max + line.b));
            Algo_points.setName("Line-Reg-Algo");
        }
        else if (CirclecorrelatedFeaturesList.containsKey(feature)) {
            float[] best_c_f_vals = ts.getHashMap().get(best_corlation_couples.get(feature));
            for (int i = 0; i < selected_f_vals.length; i++) {
                learning_points.getData().add(new XYChart.Data(selected_f_vals[i], best_c_f_vals[i]));
            }
            Circle circle = new Circle(this.CirclecorrelatedFeaturesList.get(feature).center, this.CirclecorrelatedFeaturesList.get(feature).radius);
            double x, y;
            for (double s = 0; s < 360; ) {
                x = (circle.radius * Math.cos(s) + circle.center.x);
                y = (circle.radius * Math.sin(s) + circle.center.y);
                s += 0.5;
                Algo_points.getData().add(new XYChart.Data(x, y));
            }
            Algo_points.setName("Circle-Algo");
            setting_algo.getData().add(new XYChart.Data((circle.center.x-circle.radius),(circle.center.x+circle.radius)));
            setting_algo.getData().add(new XYChart.Data(circle.center.y-circle.radius,circle.center.y+circle.radius));

        }
        else {
            System.out.println("problem with learn function");
            return null;
        }
        setting_algo.setName("setting-Algo");
        learning_points.setName("learning-points");
        points.add(learning_points);
        points.add(Algo_points);
        points.add(setting_algo);

        return points;

    }

}