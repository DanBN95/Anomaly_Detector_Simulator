package view.linechart;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;

public class MyLineChartController {

    @FXML
    private LineChart FeatureLineChart;
    @FXML
    private LineChart CorrelatedFeatureLineChart;

    XYChart.Series series;
    XYChart.Series series2;

    public MyLineChartController() {
        series = new XYChart.Series();
        series2 = new XYChart.Series();
        setName("Feature","Correlation");
    }

    public void addseries() {
        FeatureLineChart.getData().add(series);
        series.setName(" ");
        CorrelatedFeatureLineChart.getData().add(series2);
        series2.setName(" ");
    }

    public void paint(List<Float> vals, List<Float> vals2) {
        if (vals != null) {
            series.getData().add(new XYChart.Data(vals.size() - 1, vals.get(vals.size() - 1)));
        }
        if (vals2 != null) {
            series2.getData().add(new XYChart.Data(vals2.size() - 1, vals2.get(vals.size() - 1)));
        }
    }

    public void setName(String ser_name1,String ser_name2) {
        if (ser_name1 == null || ser_name2 == null) {
            System.out.println("set name line 44");
            series.setName("Feature: ");
            series2.setName("Correlation Feature: ");
        } else {
            series.setName("Feature: " + ser_name1);
            series2.setName("Correlation Feature: " + ser_name2);
        }
    }
}
