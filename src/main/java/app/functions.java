package app;

import static app.o3_sorter_stock.functions.delete;
import static app.o3_sorter_stock.functions.fileExists;
import static app.o3_sorter_stock.functions.getBarcodeFromBlackFile;
import static app.o3_sorter_stock.functions.getGroup;
import static app.o3_sorter_stock.functions.getIndexFromBlackFile;
import static app.o3_sorter_stock.functions.letterToIndex;
import static app.o3_sorter_stock.functions.moveFiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import app.o3_sorter_stock.objBlackFiles;
import app.o3_sorter_stock.objDonePdf;
import app.o3_sorter_stock.objDoneStock;
import app.o3_sorter_stock.objDoneStockNumber;
import app.o3_sorter_stock.objEtichetta;
import app.o3_sorter_stock.objJobSorter;
import app.o3_sorter_stock.objSorterExport;
import app.o3_sorter_stock.objToPdf;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class functions {
    public static void alert(String title, String text) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            Label label = new Label(text);
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
        });
    }

    public static void alert(String title, ArrayList<String> texts){
        StringBuilder text = new StringBuilder();
        for (String msg : texts) {
            text.append(msg).append("\n");
        }
        alert(title,text.toString());
    }

    public static boolean confirm(String title, String message, HashMap<String,ArrayList<String>> items) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        ButtonType yesButton = new ButtonType("SI");
        ButtonType noButton = new ButtonType("NO");
        VBox vbox = new VBox(10);
        Label labelMessage = new Label(message);
        vbox.getChildren().add(labelMessage);
        String labelStyle = "-fx-background-color: #f0f0f0; " +
            "-fx-padding: 5px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #333333; ";

        for (String item : items.keySet()) {
            Label label = new Label("• " + item);
            label.setStyle(labelStyle);
            vbox.getChildren().add(label);
        }
        alert.getButtonTypes().setAll(yesButton, noButton);
        alert.getDialogPane().setContent(vbox);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
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

    public static void printError(Exception e, Boolean shouldStopPropagation){
        if(shouldStopPropagation){
            objGlobals.stop=true;
        }
        alert("ERROR",e.toString());
        logError("ERROR",e);
    }

    public static void printError(String text, Exception e, Boolean shouldStopPropagation){
        if(shouldStopPropagation){
            objGlobals.stop=true;
        }
        alert("ERROR",text+"\n"+e.toString());
        logError(text,e);
    }

    public static void logError(String text, Exception e){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(objGlobals.errorLog,true))) {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(text+";"+e.toString());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            System.out.println(text+";"+stackTrace);
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");

            writer.append(text+";"+e.toString());
        } catch (Exception ee) {
            alert("ERROR LOG",ee.toString());
        }
    }

    public static void writeOnce(File file, String line){
        if(file.exists()){
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(line);
        } catch (Exception e) {
            printError(e,true);
        }
    }

    public static void writeOnce(File file, ArrayList<String> lines){
        if(file.exists()){
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.append(line).append(System.lineSeparator());
            }
        } catch (Exception e) {
            printError(e,true);
        }
    }

    public static void mkdir(String strPath){
        try {
            Path path = Paths.get(strPath);
            if(!Files.isDirectory(path)){
                File file = new File(strPath);
                String parent = file.getParent();
                path = Paths.get(parent);
            }
            if(!Files.exists(path)){
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            printError("mkdir "+strPath, e,true);
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

    public static ArrayList<String> preg_match(String _pattern, String string, boolean returnMatches) {
        Pattern pattern = Pattern.compile(_pattern);
        Matcher matcher = pattern.matcher(string);
        ArrayList<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        } 
        return matches;
    }

    public static void load(@SuppressWarnings("exports") Stage _stage, String view, double w, double h) {
        try {
            objGlobals.hasLoadError = false;
            objGlobals.stage = _stage;
            objGlobals.fxmlLoader = new FXMLLoader(App.class.getResource(view + ".fxml"));
            objGlobals.parent = objGlobals.fxmlLoader.load();
            if(!objGlobals.stop){
                objGlobals.scene = new Scene(objGlobals.parent, w, h);
                objGlobals.scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                objGlobals.stage.setWidth(w);
                objGlobals.stage.setHeight(h);
                objGlobals.stage.setX((screenBounds.getWidth() - w) / 2);
                objGlobals.stage.setY((screenBounds.getHeight() - h) / 2);
                objGlobals.stage.setScene(objGlobals.scene);
                objGlobals.stage.show();
            }
        } catch (IOException e) {
            printError(e,true);
            objGlobals.hasLoadError = true;
        }
    }

    public static void writeEtichette() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("PRIMO BARCODE");
        headerRow.createCell(1).setCellValue("ULTIMO BARCODE");
        headerRow.createCell(2).setCellValue("SCATOLO");
        headerRow.createCell(3).setCellValue("PEDANA");
        headerRow.createCell(4).setCellValue("GRUPPO");
        headerRow.createCell(5).setCellValue("PROG INIZIO");
        headerRow.createCell(6).setCellValue("PROG FINE");
        int rowNum = 1;
        for (Map.Entry<Integer, objFileEtichette> entry : objGlobals.fileEtichette.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            objFileEtichette data = entry.getValue();
            row.createCell(0).setCellValue(data.firstBarcode);
            row.createCell(1).setCellValue(data.lastBarcode);
            row.createCell(2).setCellValue(data.reference);
            row.createCell(3).setCellValue(data.obs);
            row.createCell(4).setCellValue(data.group);
            row.createCell(5).setCellValue(data.progStart);
            row.createCell(6).setCellValue(data.progEnd);
        }
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
        String filename = objGlobals.newTargetEtichetta();
        try (FileOutputStream fileOut = new FileOutputStream(filename)) {
            workbook.write(fileOut);
            objGlobals.newtargetEtichette(filename);
        } catch (IOException e) {
            printError(e,true);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                printError(e,true);
            }
        }
    }

    public static void readEtichette() throws Exception{
        objGlobals.fileEtichette.clear();
        try (FileInputStream fis = new FileInputStream(new File(objGlobals.targetEtichette()));
            Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;
            boolean isFirst=true;
            for (Row row : sheet) {
                if(isFirst){
                    isFirst=false;
                }else{
                    String firstBarcode = cellValue(row.getCell(0)).toUpperCase().replace(" ", "");
                    String lastBarcode = cellValue(row.getCell(1)).toUpperCase().replace(" ", "");
                    String reference = cellValue(row.getCell(2)).toUpperCase();
                    String obs = cellValue(row.getCell(3)).toUpperCase();
                    if(!(firstBarcode.isEmpty()&&lastBarcode.isEmpty()&&reference.isEmpty()&&obs.isEmpty())){
                        objGlobals.fileEtichette.put(count, new objFileEtichette(count,firstBarcode,lastBarcode,reference,obs));
                    }
                }
                count++;
            }
        } catch (IOException ee) { throw ee;}
    }

    public static String cellValue(@SuppressWarnings("exports") Cell cell){
        String cellValue;
        if (cell != null) {
            if (null == cell.getCellType()) {
                cellValue = "";
            } else{
                switch (cell.getCellType()) {
                    case NUMERIC -> {
                        double numericValue = cell.getNumericCellValue();
                        BigDecimal bigDecimalValue = new BigDecimal(numericValue);
                        cellValue = bigDecimalValue.toPlainString();
                    }
                    case STRING -> cellValue = cell.getStringCellValue();
                    default -> cellValue = "";
                }
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

    public static void loadFullscreen(String view) {
        try {
            objGlobals.hasLoadError = false;
            objGlobals.fxmlLoader = new FXMLLoader(App.class.getResource(view + ".fxml"));
            objGlobals.parent = objGlobals.fxmlLoader.load();
            objGlobals.scene = new Scene(objGlobals.parent);
            objGlobals.scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
            objGlobals.stage.setScene(objGlobals.scene);
            objGlobals.stage.setFullScreen(true);
            objGlobals.stage.show();
        } catch (IOException e) {
            printError(e,true);
            objGlobals.hasLoadError=true;
        }
    }

    public static void load(@SuppressWarnings("exports") Stage _stage, String view){
        load(_stage,view,600,600);
    }

    public static void load(String view){
        load(objGlobals.stage,view,600,600);
    }

    public static void load(String view,double w, double h){
        load(objGlobals.stage,view,w,h);
    }

    public static void deleteFolder(String folder) {
        try {
            Path directory = Paths.get(folder);
            Files.walk(directory)
                .sorted((path1, path2) -> path2.compareTo(path1)) 
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete " + path + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            printError(e,true);
        }
    }

    public static ArrayList<String>ls(String folderPath, String get){
        ArrayList<String> ret = new ArrayList<>();
        File folderFile = new File(folderPath);
        if(folderFile.exists()){
            try {
                Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>()  {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        String strPath=path.toString();
                        if(get.isEmpty()){
                            ret.add(strPath);
                        }
                        else if(strPath.endsWith(get)){
                            ret.add(strPath);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                printError(e,true);
            }
        }
        return ret;
    }

    public synchronized static void updateProgressGray(double value) {
        objGlobals.progressGray = value;
    }

    public synchronized static double getProgressStock() {
        return objGlobals.progressStock;
    }

    public synchronized static double getProgressGray() {
        return objGlobals.progressGray;
    }

    public static void moveErrors(){
        ArrayList<String> allBlack = ls(objGlobals.targetTiff, ".tiff");
        for (String file : allBlack) {
            if(objDonePdf.error.contains(file)){
                if(objGlobals.errorMap.get("PDFE").moveFile){
                    moveFiles("PDFE", file);
                }
                else{
                    delete(file);
                }
            }
            else if(objDoneStockNumber.isdb.contains(file)){
                if(objGlobals.errorMap.get("ISDB").moveFile){
                    moveFiles("ISDB", file);
                }
                else{
                    delete(file);
                }
            }
            else{
                String filename = file.replace("-FRONTE.tiff", "").replace("-RETRO.tiff", "");
                if(!objToPdf.list.containsKey(filename)){
                    if(fileExists(file)){
                        File fileDir = new File(objGlobals.errorMap.get("NONP").path,getGroup(getBarcodeFromBlackFile(file)));
                        String to = file.replace(objGlobals.targetTiff, fileDir.toString());
                        moveFile(file,to);
                        String fullName = to.replace("-FRONTE.tiff", "").replace("-RETRO.tiff", "");
                        if(!objAnomalies.stock2List.contains(fullName)){
                            objAnomalies.stock2List.add(fullName);
                        }                        
                    }
                }
            }
        }
    }

    public static void moveFile(String from, String to){
        File file = new File(from);
        if(file.exists()){
            try {
                mkdir(to);
                Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                printError(e,false);
            }
        }
    }

    public static void makeStockNumber(){
        boolean isFirst = true;
        File fileOrigin = new File(objGlobals.targetTiff);
        for (String group : objEtichetta.list.keySet()) {
            for (int indexFrom : objEtichetta.list.get(group).keySet()) {
                String stock = "";
                int indexTo = objEtichetta.list.get(group).get(indexFrom);
                int index = indexFrom;
                int countStock = 0;
                while (index <= indexTo) {
                    if(objBlackFiles.groupIndex.containsKey(group)&&objBlackFiles.groupIndex.get(group).containsKey(index)){
                        countStock++;
                        String fullFilename = objBlackFiles.groupIndex.get(group).get(index);
                        String barcode = getBarcodeFromBlackFile(fullFilename);
                        if(objDoneStockNumber.list.contains(barcode)){
                            objDoneStockNumber.isdb(fullFilename);
                        }
                        else{
                            if(fileExists(fullFilename+"-FRONTE.tiff")&&fileExists(fullFilename+"-RETRO.tiff")){
                                if(stock.isEmpty()){
                                    if(isFirst){
                                        isFirst=false;
                                        stock=objGlobals.stockPrefix + objGlobals.stockNumber;
                                    }
                                    else{
                                        stock = objGlobals.stockPrefix + objDoneStock.getNext();
                                    }
                                }
                                String[] sorterExportRow = new String[]{stock, String.valueOf(countStock), barcode+"-"+getIndexFromBlackFile(fullFilename)};
                                String sorterExportLabel = objJobSorter.rows.get(barcode)[letterToIndex(objGlobals.sorterExportValoreDaSorter)];
                                File file = new File(fullFilename);
                                String extraFolders = file.getParent().replace(fileOrigin.getAbsolutePath(), "");
                                objSorterExport.add(extraFolders, sorterExportLabel, sorterExportRow);
                                String filenameReplaced = tagReplace(barcode, fullFilename, stock);
                                File fileTo = new File(objGlobals.pdfFolder,filenameReplaced);
                                String to = fileTo.toString();
                                objToPdf.add(fullFilename, to);
                                objDoneStockNumber.add(barcode);
                            }
                        }
                    }
                    index++;
                }
            }
        }
    }

    public static String tagReplace(String barcode, String fullFileName, String stock){
        try {
            String[] jobSorterRow =  objJobSorter.rows.get(barcode);
            String barcodeFolderTag = objGlobals.nomeCartellaPdf;
            String ret = barcodeFolderTag;
            ArrayList<String> matches = preg_match("\\{([_a-zA-Z0-9::]+)\\}*", barcodeFolderTag, true);
            for (String tag : matches) {
                String _tag = tag.replace("{", "").replace("}", "");
                String[] functions = _tag.split("::");
                String origin = origin(_tag);
                String value;
                Integer position;
                String[] splitFile;
                switch (origin) {
                    case "nowYear" -> {
                        LocalDate currentDate = LocalDate.now();
                        int currentYear = currentDate.getYear();
                        value = String.valueOf(currentYear);
                    }
                    case "stockNumber" -> value = stock;
                    case "S" -> {
                        String column = _tag.replace(origin, "");
                        value = jobSorterRow[letterToIndex(column)];
                    }
                    case "D" -> {
                        String[] pathSplit = fullFileName.split("\\\\");
                        position = position(tag, origin);
                        value = pathSplit[(pathSplit.length - position -1)];
                    }
                    case "F" -> {
                        splitFile = fullFileName.split("-");
                        position = position(tag, origin);
                        value = splitFile[position];
                    }
                    default -> value = "error";
                }
                for (String function : functions) {
                    switch (function) {
                        case "uppercase" -> value = value.toUpperCase();
                        case "noNumber" -> {
                            ArrayList<String> noNumberMatches = preg_match("([a-zA-Z]+)", value, true);
                            value = noNumberMatches.get(0);
                        }
                        case "left4" -> value = value.substring(0,4);
                        default -> {
                        }
                    }
                }
                ret = ret.replace(tag, value);
            }
            return ret;
        } catch (NumberFormatException e) {
            logError("fileBarcodeTo", e);
        }
        return null;
    }

    public static Integer position(String tag, String origin){
        ArrayList<String> match = preg_match("[a-zA-Z0-9]+", tag, true);
        String _tag = match.get(0);
        String position = _tag.replace(origin, "");
        return Integer.valueOf(position);
    }

    public static String origin(String tag){
        if(preg_match("NUMERO_PACCO", tag)){
            return "stockNumber";
        }
        else if(preg_match("ANNO", tag)){
            return "nowYear";
        }
        else{
            ArrayList<String> match = preg_match("[a-zA-Z0-9]+", tag, true);
            String _tag = match.get(0);
            return _tag.substring(0,1);
        }
    }

    public static void moveStock2(String group, ArrayList<String> fileList, String where){
        for (String file : fileList) {
            moveStock2(group, file, where);
        }
    }

    public static void moveStock2(String group, String from, String where){
        String to=from;
        switch (where) {
            case "target" -> to = to.replace(new File(objGlobals.anomalyFolderStock2, group).toString(),objGlobals.targetTiff);
            case "log" -> to = to.replace(objGlobals.anomalyFolderStock2,objGlobals.notExpectedFolder);
        }
        moveFile(from+"-FRONTE.tiff", to+"-FRONTE.tiff");
        moveFile(from+"-RETRO.tiff", to+"-RETRO.tiff");
    }

}
