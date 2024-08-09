module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.poi.poi;
    requires com.opencsv;
    
    opens app to javafx.fxml;
    exports app;
}
