package view.linechart;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;

import java.util.List;

public class MyLineChartController {

    @FXML
    private LineChart FeatureLineChart;
    @FXML
    private LineChart CorrelatedFeatureLineChart;
    @FXML
    public ListView<String> fList;
    public StringProperty selected_feature;

    XYChart.Series series;
    XYChart.Series series2;
    float[] f_vals,c_vals;
    public MyLineChartController() {
        series = new XYChart.Series();
        series2 = new XYChart.Series();
        selected_feature= new SimpleStringProperty();

    }
    public void add_series(){
        FeatureLineChart.getData().add(series);
        CorrelatedFeatureLineChart.getData().add(series2);
    }

    public void set_fNames() {
        series.setName("Feature: " + selected_feature.get());
        series2.setName("Best Correlated Feature: ");
    }

    public void getpaint(float[] vals,float[] vals2) {
            f_vals=vals;
            c_vals=vals2;
    }
    synchronized public void add_p_paint(int old_time,int new_time){
        if(old_time<new_time) {
            for(int i = old_time+1;i<=new_time;i++) {
                final int j = i;
                Platform.runLater(() -> {
                    series.getData().add(new XYChart.Data(j, f_vals[j]));
                if (c_vals != null) {
                    series2.getData().add(new XYChart.Data(j, c_vals[j]));
                }
            });
            }
        }else{
            go_back(new_time);
        }
    }
    public void go_back(int index){
        series.getData().clear();
        series2.getData().clear();
            for(int i=0;i<index;i++) {
                series.getData().add(new XYChart.Data(i, f_vals[i]));
                if (c_vals != null) {
                series2.getData().add(new XYChart.Data(i, c_vals[i]));
            }
        }
    }
}