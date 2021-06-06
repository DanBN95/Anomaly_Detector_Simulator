package PTM1.AnomalyDetector;


import PTM1.CorrelatedFeatures.LineCorrelatedFeatures;
import PTM1.Helpclass.Line;
import PTM1.Helpclass.Point;
import PTM1.Helpclass.StatLib;
import PTM1.Helpclass.TimeSeries;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

	List<LineCorrelatedFeatures> correlatedFeaturesList = new LinkedList<LineCorrelatedFeatures>();
	HashMap<String,String> best_corlation_couples = new HashMap<>();
	@Override
	public void learnNormal(TimeSeries ts) {
		float best_correlated = 0;
		float threshold = (float) 0.9;
		String save_through_feature = "";
		String [] features = ts.FeaturesList();
		for (int i=0;i<ts.getHashMap().size()-1;i++) { //for every feature checking cov with the other features
			String feature_check = features[i];
			for (int j=0;j<ts.getHashMap().size();j++) {
				if(i==j){continue;}
				String through_feature = features[j];
				float[] v_check = ts.getHashMap().get(feature_check);
				float[] through_v = ts.getHashMap().get(through_feature);

				if (Math.abs(StatLib.pearson(v_check, through_v)) > best_correlated) {
					best_correlated = Math.abs(StatLib.pearson(v_check, through_v)); //set the best cor
					save_through_feature = through_feature;
				}

			}
			if (best_correlated < threshold)
				continue;
			this.best_corlation_couples.put(feature_check, save_through_feature);
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
			max_dev*=(float)1.1;
			this.correlatedFeaturesList.add(new LineCorrelatedFeatures(feature_check, save_through_feature, best_correlated, reg_line, max_dev));
			best_correlated = 0;
		}
	}


	@Override
	//Input: get TimeSeries object
	//Output: returns list of all the anomalies were detected
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> anomalyReportList = new LinkedList<>();
		String[] features = ts.FeaturesList();
		for (LineCorrelatedFeatures correlatedFeatures : this.correlatedFeaturesList) {
			for (int i = 0; i < ts.getSizeOfVector(); i++) {
				Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
				if (StatLib.dev(p, correlatedFeatures.lin_reg) > correlatedFeatures.threshold)
					anomalyReportList.add(new AnomalyReport( correlatedFeatures.feature1 + "-"
							+ correlatedFeatures.feature2, (long)i+1));

			}
		}
		if (anomalyReportList.isEmpty())
			return null;
		else
			return anomalyReportList;
	}

	public List<LineCorrelatedFeatures> getNormalModel() {
		return this.correlatedFeaturesList;
	}


	@Override
	public HashMap<String,List<Point[]>> paint(TimeSeries ts) {
		float[] feature_to_point1,feature_to_point2;
		String[] features=ts.FeaturesList();
		HashMap<String,List<Point[]>> paint_map=new HashMap<>();
		List<Point> point_per_f_list = new LinkedList<>();
		List<Point> point_best_cor_list = new LinkedList<>();
		List<Point[]> point_list = new LinkedList<>();

		for(int i=0;i< ts.getHashMap().size();i++){
			feature_to_point1= ts.getHashMap().get(features[i]);
			String best_cor = this.best_corlation_couples.get(features[i]);
			feature_to_point2 = ts.getHashMap().get(best_cor);
			for(int j=0;j<ts.getSizeOfVector();j++){
				point_per_f_list.add(new Point((float)j,feature_to_point1[j]));
				point_per_f_list.add(new Point((float)j,feature_to_point2[j]));
				point_best_cor_list.add(new Point(feature_to_point2[j],feature_to_point1[j]));
			}
			point_list.add(point_per_f_list.toArray(new Point[0]));
			point_list.add(point_best_cor_list.toArray(new Point[0]));
			paint_map.put(features[i],point_list );
		}
		return paint_map;
	}


}
