package app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import static app.functions.alert;
import static app.functions.load;
import static app.functions.confirm;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
        Service<Void> moveFilesService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> {
                            lblMoveFiles.setText("iniziato");
                            imgMoveFiles.setImage(new Image(App.class.getResource("img/download.gif").toExternalForm()));
                        });
                        objGlobals.variables();
                        boolean start[] = {true};
                        CountDownLatch latch = new CountDownLatch(1); 
                        if(outOfSpace()){
                            Platform.runLater(() -> {
                                try {
                                    start[0] = confirm(objAnomalies.noSpace, "SPAZIO INSUFFICIENTE!, CONTINUA LO STESSO ?");
                                } 
                                finally {
                                    latch.countDown();
                                }    
                            });
                            latch.await();
                        }
                        if(start[0]){
                            app.o1_sorter_move_files.EntryPoint.start(true);
                        }
                        else{
                            Platform.exit();
                        }
                        return null;
                    }
                };
            }
        };
        Service<Void> postMoveFilesService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> {
                            if(!objAnomalies.moveFiles.isEmpty()){
                                lblMoveFiles.setText("anomalia");
                                imgMoveFiles.setImage(new Image(App.class.getResource("img/warning.gif").toExternalForm()));
                                alert("ANOMALIA","SISTEMA LE ANOMALIE PRIMA DI CONTINUARE");
                                load("viewMoveFilesAnomalie");
                            }else{
                                lblMoveFiles.setText("completato");
                                imgMoveFiles.setImage(new Image(App.class.getResource("img/done.gif").toExternalForm()));
                            }
                        });
                        return null;
                    }
                };
            }
        };
        Service<Void> grayService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(()->{
                            lblGray.setText("iniziato");
                            imgGray.setImage(new Image(App.class.getResource("img/download.gif").toExternalForm()));
                        });
                        return null;
                    }
                };
            }
        };

        moveFilesService.setOnSucceeded(event -> postMoveFilesService.start());
        postMoveFilesService.setOnSucceeded(event -> grayService.start());

        moveFilesService.setOnFailed(event -> handleError(lblMoveFiles, imgMoveFiles));
        postMoveFilesService.setOnFailed(event -> handleError(lblMoveFiles, imgMoveFiles));

        moveFilesService.start();
    }

    private static boolean outOfSpace() throws Exception{
        try {
            String targetGrayPartition = objGlobals.targetGray.substring(0,2);
            String targetTiffPartition = objGlobals.targetTiff.substring(0,2);
            if(targetGrayPartition.equals(targetTiffPartition)){
                File diskPartition = new File(targetGrayPartition);
                long freeSpace = diskPartition.getFreeSpace();
                long graySize = folderSize(objGlobals.sourceGray);
                long tiffSize = folderSize(objGlobals.sourceTiff);
                long sourceSize = graySize + tiffSize;
                double sizeLocal = (double) freeSpace / (1024 * 1024 * 1024);
                double sizeSource = (double) sourceSize / (1024 * 1024 * 1024);
                if(sizeLocal<sizeSource){
                    objAnomalies.noSpace="Servono: "+String.format("%.2f", sizeSource)+" GB. Sono Disponibili: "+String.format("%.2f",sizeLocal)+" GB";
                    return true;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return false;
    }
    
    public static long folderSize(String directoryPath) throws Exception {
        Path path = Paths.get(directoryPath);
        final long[] size = {0};
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });
        return size[0];
    }

    private void handleError(Label label, ImageView imageView) {
        Platform.runLater(() -> {
            label.setText("errore");
            imageView.setImage(new Image(App.class.getResource("img/error.gif").toExternalForm()));
        });
    }
}
