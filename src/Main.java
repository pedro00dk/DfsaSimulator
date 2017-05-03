/**
 * Created by pedro on 5/1/2017.
 */

import gui.SimulatorScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SimulatorScreen.class.getResource("SimulatorScreen.fxml"));
        Pane simulatorScreen = loader.load();
        primaryStage.setScene(new Scene(simulatorScreen));
        primaryStage.show();
    }
}
