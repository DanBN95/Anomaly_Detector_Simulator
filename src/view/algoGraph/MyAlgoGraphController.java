package view.algoGraph;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import view.linechart.MyLineChartController;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class MyAlgoGraphController {

    @FXML
    private NumberAxis x ;
    @FXML
    private NumberAxis y ;

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
        series.getData().clear();
        series2.getData().clear();
        series3.getData().clear();
        series4.getData().clear();
    }

    public void set_algo_setting(List<XYChart.Series> data) {
        clear();
        if(data==null){
            System.out.println("not hava enough coraltion to use this algo_detect");
            return;
        }
        for (Object x :data.get(0).getData()) {
            series.getData().add(x);
        }
        for (Object x :data.get(1).getData()) {
            series2.getData().add(x);
        }
        series.setName(data.get(0).getName());
        Algo_line_chart.setTitle(series2.getName());
        x.setAutoRanging(false);
        float max_x,max_y,min_x,min_y;
        min_x= Float.parseFloat(((XYChart.Data<?,?>)data.get(2).getData().get(0)).getXValue().toString());
        max_x=Float.parseFloat(((XYChart.Data<?,?>)data.get(2).getData().get(0)).getYValue().toString());
        min_y=Float.parseFloat(((XYChart.Data<?,?>)data.get(2).getData().get(1)).getXValue().toString());
        max_y=Float.parseFloat(((XYChart.Data<?,?>)data.get(2).getData().get(1)).getYValue().toString());
        x.setLowerBound(min_x*(float)(1.2));
        x.setUpperBound(max_x*(float)(1.2));
        y.setLowerBound(min_y*(float)(1.2));
        y.setUpperBound(max_y*(float)(1.2));
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

//        Platform.runLater(() -> {
            if (point[2] == (float) 1)
                series4.getData().add(new XYChart.Data(point[0], point[1]));
            else
                series3.getData().add(new XYChart.Data(point[0], point[1]));
            count++;
            queue.add(point[2]);
//        });
    }

    public void clear_detect() {
        series3.getData().clear();
        series4.getData().clear();
        queue.clear();
        count=0;
    }
}