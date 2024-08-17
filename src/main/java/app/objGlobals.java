package app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class objGlobals {
    public static String version = "SORTER-FX 1.0.0";
    @SuppressWarnings("exports")
    public static Scene scene;
    @SuppressWarnings("exports")
    public static FXMLLoader fxmlLoader;
    @SuppressWarnings("exports")
    public static Parent parent;
    @SuppressWarnings("exports")
    public static Stage stage;
    public static int totalThreadsMoveFiles=1;
    public static String workingFolder="C:\\Users\\teodu\\Desktop\\TESTS";
    public static String sourceEtichette="C:\\Users\\teodu\\Desktop\\SOURCE\\Tracciato per produzione etichetta.xls";
    public static ArrayList<String> sourceJobSorter=new ArrayList<>(Arrays.asList("C:\\Users\\teodu\\Desktop\\SOURCE\\job-sorter.csv"));
    public static String sourceGray="C:\\Users\\teodu\\Desktop\\SOURCE\\GRIGI\\DM2024001";
    public static String sourceTiff="C:\\Users\\teodu\\Desktop\\SOURCE\\TIFF\\DM2024001";
    public static String stockPrefix="TEST";
    public static String masterCode;
    public static int stockNumber=1;
    public static String targetGray;
    public static String targetTiff;
    public static String logFolder;
    public static String anomalyFolder;
    public static String anomalyLog;
    public static String errorLog;
    public static String jogSorterFolder;
    public static String etichetteFolder;
    public static ArrayList<String> tiffFiles = new ArrayList<>();
    public static ArrayList<String> grayFiles = new ArrayList<>();

    public static void variables() throws Exception{
        try{
            targetGray=targetGray();
            targetTiff=targetTiff();
            logFolder=logFolder();
            anomalyLog=anomalyLog();
            errorLog=errorLog();
            jogSorterFolder=jogSorterFolder();
            etichetteFolder=etichetteFolder();
            anomalyFolder=anomalyFolder();
        } catch (Exception e) { throw e;}
    }

    public static String logFolder() throws Exception{
        try{
            File file = new File(objGlobals.workingFolder,"_LOGS");
            logFolder=file.getAbsolutePath();
            Path logPath = Paths.get(logFolder);
            if(!Files.exists(logPath)){
                Files.createDirectories(logPath);
                Files.setAttribute(logPath, "dos:hidden", true);
            }
            else{
                Files.setAttribute(logPath, "dos:hidden", true);
            }
        } catch (IOException e) { throw e;}
        return logFolder;
    }

    public static String targetGray() {
        File file = new File(new File(workingFolder, "FILE_TEMPORANEI"), "GRIGI");
        return file.toString();
    }

    public static String targetTiff() {
        File file = new File(new File(workingFolder, "FILE_TEMPORANEI"), "TIFF");
        return file.toString();
    }

    public static String anomalyLog() {
        File file = new File(logFolder, "ANOMALIE.txt");
        return file.toString();
    }

    public static String errorLog() {
        File file =new File(logFolder, "error.log");
        return file.toString();        
    }

    public static String anomalyFolder() {
        File file =new File(workingFolder, "ANOMALIE");
        return file.toString();        
    }

    public static String jogSorterFolder() {
        File file =new File(new File(workingFolder, "FILE_TEMPORANEI"),"JOB-SORTER");
        return file.toString();        
    }

    public static String etichetteFolder() {
        File file =new File(new File(workingFolder, "FILE_TEMPORANEI"),"ETICHETTE");
        return file.toString();        
    }

}
