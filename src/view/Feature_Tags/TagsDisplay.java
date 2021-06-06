package view.Feature_Tags;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import view.open.OpenController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TagsDisplay extends Pane {

    public ObservableList features;

    public TagsDisplay(List<String> list) {
        features = FXCollections.observableArrayList();
        features.removeAll(features);
        list.forEach((e)->features.add(e));

        FXMLLoader fxl = new FXMLLoader();
        try {
            Pane fList = fxl.load(getClass().getResource("tags.fxml").openStream());
            TagsController tags = fxl.getController();

            this.getChildren().add(fList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
