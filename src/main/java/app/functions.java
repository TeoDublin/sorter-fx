package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class functions {
    public static void alert(String title, String text){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void alert(String title, ArrayList<String> texts){
        StringBuilder text = new StringBuilder();
        for (String msg : texts) {
            text.append(msg).append("\n");
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text.toString());
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType yesButton = new ButtonType("SI");
        ButtonType noButton = new ButtonType("NO");
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    public static void printError(Exception e){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(e.toString());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        System.out.println(stackTrace);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        alert("ERROR",e.toString());
        logError(e.toString());
    }

    public static void printError(String text, Exception e){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(text+";"+e.toString());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        System.out.println(text+";"+stackTrace);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        alert("ERROR",text+";"+e.toString());
        logError(text+";"+e.toString());
    }

    public static void printAnomaly(String text, Exception e){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(text+";"+e.toString());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        System.out.println(text+";"+stackTrace);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        alert("ERROR",text+";"+e.toString());
    }

    public static void logError(String text){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.errorLog,true))) {
            writer.append(text);
        } catch (Exception e) {
            alert("ERROR LOG",e.toString());
        }
    }

    public static void mkdir(String strPath) throws Exception{
        Path path = Paths.get(strPath);
        if(!Files.isDirectory(path)){
            File file = new File(strPath);
            String parent = file.getParent();
            path = Paths.get(parent);
        }
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
    }

    public static boolean preg_match(String _pattern, String string){
        Pattern pattern = Pattern.compile(_pattern);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public static ArrayList<String> preg_match(String _pattern, String string, int index) {
        Pattern pattern = Pattern.compile(_pattern);
        Matcher matcher = pattern.matcher(string);
        ArrayList<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(index));
        } 
        return matches;
    }

    public static void load(@SuppressWarnings("exports") Stage _stage, String view, double w, double h){
        try {
            objGlobals.stage = _stage;
            objGlobals.fxmlLoader = new FXMLLoader(App.class.getResource(view+".fxml"));
            objGlobals.parent = objGlobals.fxmlLoader.load();
            objGlobals.scene = new Scene(objGlobals.parent, w, h);
            objGlobals.scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            objGlobals.stage.setScene(objGlobals.scene);
            objGlobals.stage.show();
        } catch (IOException e) { 
            printError(e);
            Platform.exit();
        }
        
    }

    public static void load(@SuppressWarnings("exports") Stage _stage, String view){
        load(_stage,view,600,500);
    }

    public static void load(String view){
        load(objGlobals.stage,view,600,500);
    }

    public static void load(String view,double w, double h){
        load(objGlobals.stage,view,w,h);
    }

}
