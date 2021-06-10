package view.mylines;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MycharLineController implements Initializable {
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;
    @FXML
    private javafx.scene.chart.LineChart<?,?> FeatureLineChart;

    //CorrelatedFeatureLineChart

    public MycharLineController(){

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] x={"1","2","3","4","5"};
        int[] y={23,10,30,14,50};
        int[] y1={50,2,12,10,34};

        FeatureGraphPaint(x,y);
        //CorrelatedFeatureGraphPaint(x,y1);

    }
    public void paint(){



    }
    public void FeatureGraphPaint(String [] x,int [] y){
        XYChart.Series series = new XYChart.Series();
        for(int i=0; i<x.length; i++){
            series.getData().add(new XYChart.Data(x[i],y[i]));
        }
        FeatureLineChart.getData().addAll(series);
    }

//    public void CorrelatedFeatureGraphPaint(String [] x,int [] y){
//        XYChart.Series series = new XYChart.Series();
//        for(int i=0; i<x.length; i++){
//            series.getData().add(new XYChart.Data(x[i],y[i]));
//        }
//        CorrelatedFeatureLineChart.getData().addAll(series);
//    }
}
