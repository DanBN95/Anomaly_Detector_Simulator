package view.clocks;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class Clocks extends AnchorPane {

    public ClockController controller;

    public Clocks() {
        FXMLLoader fxl = new FXMLLoader();
        GridPane gridPane = null;

        try {
            gridPane = fxl.load(getClass().getResource("clocks.fxml").openStream());
            controller = fxl.getController();
            controller.createClocks();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            this.getChildren().add(gridPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setvalues(Double air,Double altitude,Double heading,Double yaw,Double roll,Double pitch){

    }
}