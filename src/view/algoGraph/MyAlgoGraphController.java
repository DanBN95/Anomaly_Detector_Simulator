package view.algoGraph;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import view.linechart.MyLineChartController;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class MyAlgoGraphController {

    @FXML
    LineChart Algo_line_chart;

    XYChart.Series series;
    XYChart.Series series2;
    XYChart.Series series3;
    XYChart.Series series4;
    Queue<Float> queue;
    int count;
    public MyAlgoGraphController() {
        series = new XYChart.Series();
        series2 = new XYChart.Series();
        series3 = new XYChart.Series();
        series4 = new XYChart.Series();
        queue = new ArrayDeque<>(30);
        count=0;
    }

    public void add_series(){
        Algo_line_chart.getData().addAll(series,series2,series3,series4);
    }

    public void clear() {
//        if(Algo_line_chart.getData().size()>0) {
//            Algo_line_chart.getData().clear();
//        }
        Algo_line_chart.getData().clear();
        series.getData().clear();
        series2.getData().clear();
        series3.getData().clear();
        series4.getData().clear();
    }

    public void add_data(List<XYChart.Series> data) {
        series=data.get(0);
        series2=data.get(1);
       add_series();
    }

    synchronized public void add_p_paint(float[] point){
        if(point==null){return;}
        float step;
        if(count==30){
            count--;
            step =queue.poll();
            if(step==(float)1)
                series4.getData().remove(0);
            else
                series3.getData().remove(0);
        }

        Platform.runLater(() -> {
            if (point[2] == (float) 1)
                series4.getData().add(new XYChart.Data(point[0], point[1]));
            else
                series3.getData().add(new XYChart.Data(point[0], point[1]));
            count++;
            queue.add(point[2]);
        });
    }

    public void clear_detect() {
        series3.getData().clear();
        series4.getData().clear();
        queue.clear();
        count=0;
    }
}