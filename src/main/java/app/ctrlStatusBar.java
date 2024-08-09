package app;

import java.net.URL;
import java.util.ResourceBundle;

import static app.functions.printError;
import app.o1_sorter_move_files.EntryPoint;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ctrlStatusBar implements Initializable {
    @FXML
    private Label title;
    @FXML
    private Label lblMoveFiles;
    @FXML
    private ImageView imgMoveFiles;
    @FXML
    private Label lblGray;
    @FXML
    private ImageView imgGray;
    @FXML
    private Label lblAnomaliaGray;
    @FXML
    private ImageView imgAnomaliaGray;
    @FXML
    private Label lblStock;
    @FXML
    private ImageView imgStock;
    @FXML
    private Label lblAnomaliaStock;
    @FXML
    private ImageView imgAnomaliaStock;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(objGlobals.version);
        try {
            lblMoveFiles.setText("iniziato");
            imgMoveFiles.setImage(new Image(App.class.getResource("download.gif").toExternalForm()));
            EntryPoint.start("copy");
            EntryPoint.start("check");
        } catch (Exception e) {printError(e);}
        
    }
    
}
