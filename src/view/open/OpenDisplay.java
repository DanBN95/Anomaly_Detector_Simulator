package view.open;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;


import java.awt.*;
import java.io.IOException;

public class OpenDisplay extends AnchorPane {

    public Button btn;

    public OpenDisplay() {
        super();

        FXMLLoader fxl = new FXMLLoader();
        try {
            AnchorPane open_btn = fxl.load(getClass().getResource("open_view.fxml").openStream());
            OpenController openController = fxl.getController();

            btn = openController.button;
            this.getChildren().add(open_btn);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
