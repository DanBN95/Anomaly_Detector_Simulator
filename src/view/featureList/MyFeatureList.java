package view.featureList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MyFeatureList extends ListView {
    MyFeatureList(){
        try{
            FXMLLoader fxl = new FXMLLoader();
            ListView fl = (ListView) fxl.load(getClass().getResource("MyFeatureList.fxml").openStream());
            MyFeatureListController myFeatureListController=fxl.getController();
            this.getChildren().add(fl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
