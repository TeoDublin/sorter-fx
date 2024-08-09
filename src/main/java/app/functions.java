package app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
    public static void printError(Exception e){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(e.toString());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        System.out.println(stackTrace);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        alert("ERROR",stackTrace);
    }
}
