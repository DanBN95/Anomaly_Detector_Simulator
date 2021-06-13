package view.linechart;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import view.pannel.Pannel;

import java.io.IOException;
import java.util.List;

public class MyLineChart extends Pane {
    public MyLineChartController myLineChartController;
    public MyLineChart(){
        try {
            FXMLLoader fxl = new FXMLLoader();
            Pane ap = fxl.load(getClass().getResource("MyLineChart.fxml").openStream());
            myLineChartController=fxl.getController();
            myLineChartController.addseries();


            this.getChildren().add(ap);

        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    public void paint(List<Float> vals,List<Float> vals2){
        myLineChartController.paint(vals,vals2);
    }
}