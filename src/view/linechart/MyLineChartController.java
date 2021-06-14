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
    }

    public void addseries() {
        FeatureLineChart.getData().add(series);
        series.setName("Feature");
        CorrelatedFeatureLineChart.getData().add(series2);
        series2.setName("Correlation");

    }

    public void paint(List<Float> vals, List<Float> vals2) {
        if (vals != null) {
            series.getData().add(new XYChart.Data(vals.size() - 1, vals.get(vals.size() - 1)));
        }
        if (vals2 != null) {
            series2.getData().add(new XYChart.Data(vals2.size() - 1, vals2.get(vals.size() - 1)));
        }
    }
}
