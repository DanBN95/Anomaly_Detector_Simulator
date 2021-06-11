package view.pannel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Pannel extends AnchorPane {
    public final PannelController controller;


    public Pannel(){
        FXMLLoader fxl = new FXMLLoader();
        AnchorPane ap = null;
        try {
            ap = fxl.load(getClass().getResource("pannel.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ap!=null){
            controller = fxl.getController();
            System.out.println("pannel line 24");
            this.getChildren().add(ap);
        }
        else
            controller=null;

    }
}
