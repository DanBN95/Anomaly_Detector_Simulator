package PTM1.AnomalyDetector;


import PTM1.CorrelatedFeatures.LineCorrelatedFeatures;
import PTM1.Helpclass.Line;
import PTM1.Helpclass.Point;
import PTM1.Helpclass.StatLib;
import PTM1.Helpclass.TimeSeries;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

	HashMap<String,LineCorrelatedFeatures> correlatedFeaturesList = new HashMap<>();
	HashMap<String,String> best_corlation_couples = new HashMap<>();
	@Override
	public void learnNormal(TimeSeries ts) {
		float best_correlated=0;
		float threshold = (float) 0.9;
		String [] features = ts.getFeaturesList();

		for (int i=0;i<ts.getHashMap().size()-1;i++) { //for every feature checking cov with the other features
			String feature_check = features[i];
			String through_feature = ts.getbest_c_feature(feature_check);
			if(through_feature.equals("")){continue;}
			float[] v_check = ts.getHashMap().get(feature_check);
			float[] through_v = ts.getHashMap().get(through_feature);
			best_correlated = Math.abs(StatLib.pearson(v_check, through_v)); //set the best cor
			if (best_correlated < threshold)
				continue;
			this.best_corlation_couples.put(feature_check, through_feature);
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
			max_dev*=(float)1.1;
			this.correlatedFeaturesList.put(feature_check,new LineCorrelatedFeatures(feature_check, through_feature, best_correlated, reg_line, max_dev));
		}
	}
	@Override
	public HashMap<String,List<AnomalyReport>> detect(TimeSeries ts) {
		HashMap<String,List<AnomalyReport>> anomalyReportList = new HashMap<>();
		String[] features = ts.getFeaturesList();
		LineCorrelatedFeatures correlatedFeatures;
		for (int j=0;j<features.length;j++) {
			if (this.correlatedFeaturesList.containsKey(features[j])) {
				correlatedFeatures = this.correlatedFeaturesList.get(features[j]);
				for (int i = 0; i < ts.getSizeOfVector(); i++) {
					Point p = new Point(ts.valueAtIndex(i, correlatedFeatures.feature1), ts.valueAtIndex(i, correlatedFeatures.feature2));
					if (StatLib.dev(p, correlatedFeatures.lin_reg) > correlatedFeatures.max_div) {
						if (anomalyReportList.containsKey(correlatedFeatures.feature1) == false) {
							anomalyReportList.put(correlatedFeatures.feature1, new LinkedList<>());
						}
						anomalyReportList.get(correlatedFeatures.feature1).add(new AnomalyReport(correlatedFeatures.feature1 + "-"
								+ correlatedFeatures.feature2, correlatedFeatures.feature2, (long) i + 1));
					}
				}
			}
		}

		return anomalyReportList;


//
//		XYChart.Series series= new XYChart.Series();
//		XYChart.Series series2= new XYChart.Series();
//		List<XYChart.Series> points =new LinkedList<>();
//
//		float[] best_c_vals,feature_vals;
//		feature_vals = ts.getHashMap().get(feature);
//		if(correlatedFeaturesList.containsKey(feature)) {
//			LineCorrelatedFeatures correlatedFeature= this.correlatedFeaturesList.get(feature);
//			best_c_vals = ts.getHashMap().get(best_corlation_couples.get(feature));
//			for (int i = 0; i < feature_vals.length; i++) {
//				if (StatLib.dev(new Point(feature_vals[i], best_c_vals[i]), correlatedFeature.lin_reg) > correlatedFeature.max_div) {
//					series2.getData().add(new XYChart.Data(feature_vals[i], best_c_vals[i], i));
//				} else {
//					series.getData().add(new XYChart.Data(feature_vals[i], best_c_vals[i], i));
//				}
//			}
//		}else{
//			for (int i = 0; i < feature_vals.length; i++) {
//				series2.getData().add(new XYChart.Data(i,feature_vals[i],i));
//			}
//
//		}
//		points.add(series);
//		points.add(series2);
//		return points;
	}

	@Override
	public List<XYChart.Series> paint(TimeSeries ts, String feature) {
		List<XYChart.Series> points = new LinkedList<>();
		XYChart.Series series = new XYChart.Series();
		XYChart.Series series2 = new XYChart.Series();
		float min,max;
		float[] selected_f_vals = ts.getHashMap().get(feature);
		if(best_corlation_couples.containsKey(feature) ){
			float[] best_c_f_vals = ts.getHashMap().get(best_corlation_couples.get(feature));
			Line line = new Line(this.correlatedFeaturesList.get(feature).lin_reg.a, this.correlatedFeaturesList.get(feature).lin_reg.a);
			for (int i = 0; i < selected_f_vals.length; i++) {
				series.getData().add(new XYChart.Data(selected_f_vals[i], best_c_f_vals[i]));
			}
			min = StatLib.min(selected_f_vals);
			max = StatLib.max(selected_f_vals);
			series2.getData().add(new XYChart.Data(min, line.a * min + line.b));
			series2.getData().add(new XYChart.Data(max, line.a * max + line.b));
			series.setName("Lerning Points");
			series2.setName("Line-Reg-Algo");
		}

		points.add(series);
		points.add(series2);
		return points;
	}

	public List<XYChart.Series> detect_P(TimeSeries ts,String feature,int time_step){
		List<XYChart.Series> detect_points = new LinkedList<>();
		XYChart.Series series= new XYChart.Series();
		XYChart.Series series1= new XYChart.Series();
		float x = ts.valueAtIndex(time_step,feature);
		float y = ts.valueAtIndex(time_step,best_corlation_couples.get(feature));

		LineCorrelatedFeatures correlatedFeature= this.correlatedFeaturesList.get(feature);
		if(correlatedFeature!=null) {
			if (StatLib.dev(new Point(x,y), correlatedFeature.lin_reg) > correlatedFeature.max_div) {
				series1.getData().add(new XYChart.Data(x,y));
			}
		}else{
			series.getData().add(new XYChart.Data(x,y));
		}
		detect_points.add(series);
		detect_points.add(series1);
		return detect_points;
	}
}