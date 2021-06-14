package view.clocks;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.HashMap;

public class Clocks extends AnchorPane {

    public ClockController controller;
    public HashMap<String, FloatProperty> clocksMap;

    public Clocks() {
        FXMLLoader fxl = new FXMLLoader();
        GridPane gridPane = null;
        clocksMap = new HashMap<>();
        setClocksMap();

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
    public void setValues(String featureName, float newVal) {
//        clocksMap.get(featureName).setValue((double)newVal);
        System.out.println("set value to gauge");
        controller.gaugeMap.get(featureName).setValue((double)newVal);
    }
    private void setClocksMap() {

        clocksMap.put("airSpeed",new SimpleFloatProperty());
        clocksMap.put("altitude",new SimpleFloatProperty());
        clocksMap.put("heading",new SimpleFloatProperty());
        clocksMap.put("yaw",new SimpleFloatProperty());
        clocksMap.put("roll",new SimpleFloatProperty());
        clocksMap.put("pitch",new SimpleFloatProperty());
    }
}