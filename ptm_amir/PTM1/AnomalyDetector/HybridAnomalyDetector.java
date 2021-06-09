package PTM1.AnomalyDetector;


import PTM1.CorrelatedFeatures.*;
import PTM1.Helpclass.*;

import java.util.*;


//need to import the other two algo beacuse it is posiblie to use it if needed.

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector {

    HashMap<String,LineCorrelatedFeatures> LinecorrelatedFeaturesList = new HashMap<>();
    HashMap<String,CircleCorrelatedFeatures> CirclecorrelatedFeaturesList = new HashMap<>();
    private HashMap<String,Float> zscoremap=new HashMap<String,Float>();
    HashMap<String,String> best_corlation_couples = new HashMap<>();

    @Override
    public void learnNormal(TimeSeries ts) {
        //find the best correlation
        float best_correlated = 0, check_correlated = 0;
        String save_through_feature = "";
        String[] features = ts.FeaturesList();
        String feature_check = null;
        float[] v_check = null;
        float[] through_v = null;

        for (int i = 0; i < ts.getHashMap().size() - 1; i++) { //for every feature checking cov with the other features
            feature_check = features[i];
            v_check = ts.getHashMap().get(feature_check);
            for (int j = 0; j < ts.getHashMap().size(); j++) {
                if(i==j){continue;}
                String through_feature = features[j];
                through_v = ts.getHashMap().get(through_feature);
                check_correlated = Math.abs(StatLib.pearson(v_check, through_v));
                if (check_correlated > best_correlated) {
                    best_correlated = check_correlated; //set the best cor
                    save_through_feature = through_feature;
                }
            }
            this.best_corlation_couples.put(feature_check, save_through_feature);
            //ues linear regration
            if (best_correlated >= 0.95) {

                if(best_corlation_couples.containsKey(save_through_feature)){continue;}
                Point[] p = new Point[ts.getSizeOfVector()];
                for (int k = 0; k < ts.getSizeOfVector(); k++)
                    p[k] = new Point(ts.valueAtIndex(k, feature_check), ts.valueAtIndex(k, save_through_feature));
                Line reg_line = StatLib.linear_reg(p);
                float max_dev = -1;
                for (Point point : p) {
                    float result = StatLib.dev(point, reg_line);
                    if (result > max_dev)
                        max_dev = result;

                }
                max_dev *= (float) 1.1;
                this.LinecorrelatedFeaturesList.put(feature_check, new LineCorrelatedFeatures(feature_check, save_through_feature, best_correlated, reg_line, max_dev));
                best_correlated = 0;


            } else if (best_correlated < 0.5) {

                Vector<Float> curArrayList = new Vector<Float>();
                float curAvg = 0, curStiya = 0, curZscore = 0, maxZscore = 0;
                Float[] curArray = null;
                curStiya = (float) Math.sqrt(StatLib.var(v_check));
                curArrayList.add(v_check[0]);

                for (int j = 1; j < v_check.length; j++) {
                    curArray = curArrayList.toArray(new Float[0]);
                    curStiya = (float) Math.sqrt(StatLib.var(curArray));
                    curAvg = StatLib.avg(curArray);
                    curArrayList.add(v_check[j]);
                    if (curStiya == 0) {
                        continue;
                    }
                    curZscore = Math.abs(v_check[j] - curAvg) / curStiya;
                    if (curZscore > maxZscore) {
                        maxZscore = curZscore;
                    }

                }
                this.zscoremap.put(feature_check, maxZscore);
                best_correlated = 0;
                // if the best cor is under 0.5 then use use z sccore


            } else {
                if(best_corlation_couples.containsKey(save_through_feature)){continue;}
                Vector<Point> point_for_cercle = new Vector<>();
                //otherwise circle
                for (int t = 0; t < v_check.length; t++) {
                    point_for_cercle.add(new Point(v_check[t], through_v[t]));
                }
                //need tgo complete the circle algo
                // Circle data = new AlgorithmMinCircle().welzl(point_for_cercle);
                // this.CirclecorrelatedFeaturesList.put(feature_check, new CircleCorrelatedFeatures(feature_check, save_through_feature, best_correlated, data.center, data.radius));
                best_correlated = 0;
            }


        }
    }

    public float checkZScore(float num,Float[] curColToCheck){
        float curAvg=0, curStiya=0, curZscore=0;
        curStiya= (float) Math.sqrt(StatLib.var(curColToCheck));
        if(curStiya==0){return 0; }
        curAvg=StatLib.avg(curColToCheck);
        curZscore=Math.abs(num-curAvg)/curStiya;
        return curZscore;

    }
    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {

        List<AnomalyReport> anomalyReportList = new LinkedList<>();
        float[] curColToCheck;
        Float[] curArray;
        String[] features = ts.FeaturesList();
        for (LineCorrelatedFeatures correlatedFeatures : this.LinecorrelatedFeaturesList.values()) {

            for (int i = 0; i < ts.getSizeOfVector(); i++) {
                Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
                if (StatLib.dev(p,correlatedFeatures.lin_reg) > correlatedFeatures.threshold)
                    anomalyReportList.add(new AnomalyReport( correlatedFeatures.feature1 + "-"
                            + correlatedFeatures.feature2, (long)i+1));

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
                curZscore=checkZScore(curColToCheck[i], curArray);
                curArrayList.add(curColToCheck[i]);
                if ( curZscore > this.zscoremap.get(name_feature)) {
                    anomalyReportList.add(new AnomalyReport("division in col " + name_feature, (long) i + 1));
                }
            }
        }

        for (CircleCorrelatedFeatures correlatedFeatures : this.CirclecorrelatedFeaturesList.values()) {
            Circle check_in_circle = new Circle (correlatedFeatures.center,correlatedFeatures.radius);
            for (int i = 0; i < ts.getSizeOfVector(); i++) {
                Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
                if (!check_in_circle.containsPoint(p))
                    anomalyReportList.add(new AnomalyReport( correlatedFeatures.feature1 + "-"
                            + correlatedFeatures.feature2, (long)i+1));
            }
        }

        return anomalyReportList;




    }

    @Override
    public HashMap<String,String> paint(TimeSeries ts,String feature) {

        return this.best_corlation_couples;


//        float[] feature_to_point1,feature_to_point2;
//        String[] features=ts.FeaturesList();
//        HashMap<String,List<Point[]>> paint_map=new HashMap<>();
//        List<Point> point_per_f_list = new LinkedList<>();
//        List<Point> point_best_cor_list = new LinkedList<>();
//        List<Point[]> point_list = new LinkedList<>();
//
//        for(int i=0;i< ts.getHashMap().size();i++){
//            feature_to_point1= ts.getHashMap().get(features[i]);
//            String best_cor = this.best_corlation_couples.get(features[i]);
//            feature_to_point2 = ts.getHashMap().get(best_cor);
//            for(int j=0;j<ts.getSizeOfVector();j++){
//                point_per_f_list.add(new Point((float)j,feature_to_point1[j]));
//                point_per_f_list.add(new Point((float)j,feature_to_point2[j]));
//                point_best_cor_list.add(new Point(feature_to_point2[j],feature_to_point1[j]));
//            }
//            point_list.add(point_per_f_list.toArray(new Point[0]));
//            point_list.add(point_best_cor_list.toArray(new Point[0]));
//            paint_map.put(features[i],point_list );
//        }
//        return paint_map;
    }



}




//    double dist(Point a,Point b) {
//        return sqrt(pow(a.x - b.x, 2)
//                + pow(a.y - b.y, 2));
//    }
//    Point get_circle_center(double bx, double by, double cx, double cy)
//    {
//        double B = bx * bx + by * by;
//        double C = cx * cx + cy * cy;
//        double D = bx * cy - by * cx;
//        return new Point((float) ((cy * B - by * C) / (2 * D)), (float)((bx * C - cx * B) / (2 * D)) );
//    }
//    Circle circle_from3(Point A,Point B,Point C)
//    {
//        Point I = get_circle_center(B.x- A.x, B.y - A.y,
//                C.x - A.x, C.y - A.y);
//
//        I.x += A.x;
//        I.y += A.y;
//        return new Circle( I, dist(I, A));
//    }
//    Circle circle_from(Point A,Point B)
//    {
//        // Set the center to be the midpoint of A and B
//        Point C = new Point((float) ((A.x + B.x) / 2.0), (float)((A.y + B.y) / 2.0 ));
//
//        // Set the radius to be half the distance AB
//        return new Circle( C, dist(A, B) / 2.0 );
//    }
//    // Function to check whether a circle
//// encloses the given points
//    boolean is_valid_circle(Circle c,Vector<Point> P)
//    {
//
//        // Iterating through all the points
//        // to check  whether the points
//        // lie inside the circle or not
//        for (Point p : P)
//            if (!c.is_inside(p))
//                return false;
//        return true;
//    }
//    // Function to return the minimum enclosing
//// circle for N <= 3
//    Circle min_circle_trivial(Vector<Point> P)
//    {
//        assert(P.size() <= 3);
//        if (P.size()==0) {
//            return new Circle(new Point( 0, 0 ), 0 );
//        }
//        else if (P.size() == 1) {
//            return  new Circle( P.firstElement(), 0 );
//        }
//        else if (P.size() == 2) {
//            return circle_from(P.firstElement(), P.get(1));
//        }
//
//        // To check if MEC can be determined
//        // by 2 points only
//        for (int i = 0; i < 3; i++) {
//            for (int j = i + 1; j < 3; j++) {
//
//                Circle c = circle_from(P.get(i), P.get(j));
//                if (is_valid_circle(c, P))
//                    return c;
//            }
//        }
//        return circle_from3(P.get(0), P.get(1), P.get(2));
//    }
//    Circle welzl_helper(Vector<Point> P, Vector<Point> R, int n)
//    {
//        // Base case when all points processed or |R| = 3
//        if (n == 0 || R.size() == 3) {
//            return min_circle_trivial(R);
//        }
//        Random rand = new Random();
//        // Pick a random point randomly
//        int idx = rand.nextInt(50) % n;
//        Point p = P.get(idx);
//
//        // Put the picked point at the end of P
//        // since it's more efficient than
//        // deleting from the middle of the vector
//        System.out.println("before swap"+P.get(n - 1).x);
//        swap(P.get(idx), P.get(n - 1));
//        System.out.println("after swap"+P.get(n - 1).x);
//
//        // Get the MEC circle d from the
//        // set of points P - {p}
//        Circle d = welzl_helper(P, R, n - 1);
//
//        // If d contains p, return d
//        if (d.is_inside(p)) {
//            return d;
//        }
//        // Otherwise, must be on the boundary of the MEC
//        R.add(p);
//        // Return the MEC for P - {p} and R U {p}
//        return welzl_helper(P, R, n - 1);
//    }
//    private void swap(Point point,Point point1) {
//
//        Point temp=point;
//        point=point1;
//        point1=temp;
//    }
//    Circle welzl(Vector<Point> P)
//    {
//        Vector<Point> P_empty= new Vector<Point>();
//        Vector<Point> P_copy = P;
//        random_shuffle(P_copy.firstElement(), P_copy.get(P_copy.size()-1));
//        return welzl_helper(P_copy , P_empty, P_copy.size());
//    }
//    private void random_shuffle(Point firstElement,Point point) { }
//