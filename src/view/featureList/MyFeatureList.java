package view.featureList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MyFeatureList extends Pane {

    MyFeatureListController myFeatureListController;

    MyFeatureList(){
        try{
            FXMLLoader fxl = new FXMLLoader();
            System.out.println("MY FEATURE LIST LINE 18");
            Pane fl = (Pane) fxl.load(getClass().getResource("MyFeatureList.fxml").openStream());
            myFeatureListController = fxl.getController();
            this.getChildren().add(fl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
