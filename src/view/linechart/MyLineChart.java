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
            //ap.getStylesheets().add(getClass().getResource("LineChartStyle.css").toExternalForm());

            myLineChartController=fxl.getController();
            myLineChartController.add_series();
            this.getChildren().add(ap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}