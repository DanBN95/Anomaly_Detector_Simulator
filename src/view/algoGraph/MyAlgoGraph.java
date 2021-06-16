package view.algoGraph;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import view.linechart.MyLineChartController;

import java.io.IOException;
import java.util.Collection;

public class MyAlgoGraph extends Pane {
    public MyAlgoGraphController myAlgoGraphController;
    public MyAlgoGraph() {
        try {
            FXMLLoader fxl = new FXMLLoader();
            Pane algo = fxl.load(getClass().getResource("MyAlgoGraph.fxml").openStream());
            algo.getStylesheets().add(getClass().getResource("AlgoStyle.css").toExternalForm());
            myAlgoGraphController = fxl.getController();

            this.getChildren().add(algo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
