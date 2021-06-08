package sample;

import view.View;
import com.sun.webkit.Timer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Model;
import view_model.ViewModel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


        System.out.println("Application started");
        FXMLLoader fxl = new FXMLLoader();
        AnchorPane root = fxl.load(getClass().getResource("sample.fxml").openStream());
        Scene scene = new Scene(root);
        View view = fxl.getController();
        Model model = new Model("prop.txt");
        ViewModel vm = new ViewModel(model);
//        scene.getStylesheets().add(getClass().getResource("C:\\Users\\User\\Desktop\\Dan\\Team_Project_PTM2\\src\\view\\application.css").toExternalForm());
        primaryStage.setTitle("Flight GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
        view.init(vm);
    }


    public static void main(String[] args) {

        launch(args);
    }
}