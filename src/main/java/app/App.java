package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private static ctrlStatusBar controller;
    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("viewStatusBar.fxml"));
        Parent parent = fxmlLoader.load();
        controller = fxmlLoader.getController();
        scene = new Scene(parent, 600, 500);
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    public static ctrlStatusBar getController() {
        return controller;
    }
    public static void main(String[] args) {
        launch();
    }
}