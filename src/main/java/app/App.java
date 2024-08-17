package app;

import static app.functions.load;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * JavaFX App
 */
public class App extends Application {
    
    @Override
    public void start(@SuppressWarnings("exports") Stage _stage) {
        load(_stage,"viewStatusBar");
    }
    public static void main(String[] args) {
        launch();
    }
}