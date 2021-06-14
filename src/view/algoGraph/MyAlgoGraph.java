package view.algoGraph;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import view.linechart.MyLineChartController;

import java.io.IOException;

public class MyAlgoGraph extends Pane {
    public MyAlgoGraph() {
        try {
            FXMLLoader fxl = new FXMLLoader();
            Pane algo = fxl.load(getClass().getResource("MyAlgoGraph.fxml").openStream());
            MyAlgoGraphController myAlgoGraphController = fxl.getController();
            this.getChildren().add(algo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
