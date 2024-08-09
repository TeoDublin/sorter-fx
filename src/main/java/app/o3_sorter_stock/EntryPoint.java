package app.o3_sorter_stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class EntryPoint extends functions{
    public static void start(String[] args) {
        print("Inizio");
        try {
            setVariables(args);
            write("primo: "+stockPrefix + stockStartNumber, stockTxt);
            StepController stepController = new StepController();
            readJobSorterCSV();
            readBlackDir();
            getEtichettaXls();
            if(hasEtichettaError){
                print("Sistema tutti i errori prima di continuare");
            }
            else{
                makeStockNumber();
                makePdfMulti(stepController);
                makeSorterExport();
                moveErrors();
                write("ultimo: "+stockPrefix + objDoneStock.lastNumber, pdfFolder+"\\\\pacco_primo_e_ultimo.txt");
            }
        } catch (Exception e) {
            printError("error",e);
        }
        print("Fine");
    }

    public static void moveErrors(){
        print("Sposta Anomalie");
        ArrayList<String> allBlack = ls(inputBlackDir, "Thumbs");
        for (String file : allBlack) {
            if(objDonePdf.error.contains(file)){
                if(debug.equals("yes")){
                    moveFiles("PDFE", file);
                }
                else{
                    delete(file);
                }
            }
            else if(objDoneStockNumber.isdb.contains(file)){
                if(debug.equals("yes")){
                    moveFiles("ISDB", file);
                }
                else{
                    delete(file);
                }
            }
            else if(!objDonePdf.done.contains(file)){
                moveFilesWithJobSorterGroup("NONP", file);
            }
        }
    }

    public static void readJobSorterCSV() throws Exception{
        print("Legge i file job-sorter");
        boolean isFirst = true;
        int barcodeIndex = letterToIndex(setup.colonnaBarcode);

        try(BufferedReader txtReader = new BufferedReader(new FileReader(allJobSorter))){
            String path;
            while ((path=txtReader.readLine())!=null) {
                if(!path.isEmpty()){
                    try (CSVReader reader = new CSVReader(new FileReader(path))) {
                        String[] nextLine;
                        while ((nextLine = reader.readNext()) != null) {
                            String[] row = nextLine[0].split(";");
                            if(row.length>0){
                                if(isFirst){
                                    isFirst = false;
                                    objJobSorter.addTitles(row);
                                }
                                else{
                                    objJobSorter.add(row[barcodeIndex], row);
                                    String groupBy = getGroup(row[barcodeIndex]);
                                    objJobSorterGrouped.add(groupBy, row[barcodeIndex], row);
                                    if(row.length>7){
                                        objJobSorterGrouped.alternative(row[barcodeIndex], row[7]);
                                    }
                                }
                            }
                        }
                    } catch (IOException ee) {throw  ee;}
                }
            }
        }catch(Exception e){printError("Non riesco a leggere il file "+allJobSorter, e);}
    }

    public static void readBlackDir() {
        print("Legge i file neri");
        if (!fileExists(allBlackFiles)) {
            print("Manca il file "+allBlackFiles);
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(allBlackFiles))) {
                String line;
                while ((line = br.readLine())!=null) {
                    if(line.contains("-FRONTE.tiff")){
                        objBlackFiles.addBarcodePath(line.replace("-FRONTE.tiff", ""));
                    }
                    objBlackFiles.add(line);
                }
            } catch (Exception e) {
                printError("Legge i file neri", e);
                System.exit(1);
            }
        }
    }

    public static void getEtichettaXls() throws Exception{
        try (BufferedReader reader = new BufferedReader(new FileReader(allEtichette))) {
            String etichettaPath;
            while ((etichettaPath=reader.readLine())!=null) {
                if(!etichettaPath.isEmpty()){
                    String msg;
                    boolean isFirst = true;
                    try (FileInputStream fis = new FileInputStream(new File(etichettaPath));
                        Workbook workbook = WorkbookFactory.create(fis)) {
                        Sheet sheet = workbook.getSheetAt(0);
                        int count = 0;
                        for (Row row : sheet) {
                            if(isFirst){
                                isFirst=false;
                            }else{
                                String barcodeFrom = cellValue(row.getCell(0)).toUpperCase().replace(" ", "");
                                String barcodeTo = cellValue(row.getCell(1)).toUpperCase().replace(" ", "");
                                if(barcodeFrom.isEmpty()){
                                    hasEtichettaError = true;
                                    print("Primo barcode non puo essere vuoto. [ File: " + etichettaPath + " riga: " + count + " ]");
                                }
                                else if(barcodeTo.isEmpty()){
                                    hasEtichettaError = true;
                                    print("Ultimo barcode non puo essere vuoto. [ File: " + etichettaPath + " riga: " + count + " ]");
                                }
                                else{
                                    int indexFrom = indexMin(barcodeFrom);
                                    int indexTo = indexMax(barcodeTo);
                                    if(indexFrom==0){
                                        String alternative = objJobSorterGrouped.getAlternative(barcodeFrom);
                                        if(!alternative.isEmpty()){
                                            indexFrom = indexMin(alternative);
                                            barcodeFrom = alternative;
                                        }
                                    }
                                    if(indexTo==0){
                                        String alternative = objJobSorterGrouped.getAlternative(barcodeTo);
                                        if(!alternative.isEmpty()){
                                            indexTo = indexMax(alternative);
                                            barcodeTo = alternative;
                                        }
                                    }
                                    String groupFrom = getGroup(barcodeFrom);
                                    String groupTo = getGroup(barcodeTo);
                                    if(indexFrom==0){
                                        hasEtichettaError = true;
                                        msg = "barcode iniziale: "+barcodeFrom+" non è stato trovato nei file neri. [ File: " + etichettaPath + " riga: " + count + " ]";
                                        print(msg);write(msg, anomalyTxt);
                                    }
                                    else if(groupFrom.isEmpty()){
                                        hasEtichettaError = true;
                                        msg ="barcode iniziale: "+barcodeFrom+" non è stato trovato nei file job-sorter. [ File: " + etichettaPath + " riga: " + count + " ]";
                                        print(msg);write(msg, anomalyTxt);
                                    }

                                    if(indexTo==0){
                                        hasEtichettaError = true;
                                        msg ="barcode finale: "+barcodeTo+" non è stato trovato nei file neri. [ File: " + etichettaPath + " riga: " + count + " ]";
                                        print(msg);write(msg, anomalyTxt);
                                    }
                                    else if(groupTo.isEmpty()){
                                        hasEtichettaError = true;
                                        msg ="barcode finale: "+barcodeTo+" non è stato trovato nei file job-sorter. [ File: " + etichettaPath + " riga: " + count + " ]";
                                        print(msg);write(msg, anomalyTxt);
                                    }

                                    if(!groupFrom.isEmpty()&&!groupTo.isEmpty()&&!groupFrom.equals(groupTo)){
                                        hasEtichettaError = true;
                                        msg = "l'intervallo da:"+barcodeFrom+" a:"+barcodeTo+" non appartiene allo stesso raggruppamento nei job-sorter. [ File: " + etichettaPath + " riga: " + count + " ]";
                                        print(msg);write(msg, anomalyTxt);
                                    }

                                    if(!hasEtichettaError){
                                        if(indexTo < indexFrom){
                                            int tmp = indexTo;
                                            indexTo = indexFrom;
                                            indexFrom = tmp;
                                        }
                                        objEtichetta.add(groupFrom,indexFrom,indexTo);
                                    }
                                }
                            }
                            count++;
                        }
                    } catch (IOException ee) { throw ee;}

                }
            }
        } catch (Exception ee) { throw ee;}

    }

    public static void makeStockNumber(){
        File fileOrigin = new File(inputBlackDir);
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
                                    stock = stockPrefix + objDoneStock.getNext();
                                }
                                String[] sorterExportRow = new String[]{stock, String.valueOf(countStock), barcode+"-"+getIndexFromBlackFile(fullFilename)};
                                String sorterExportLabel = objJobSorter.rows.get(barcode)[letterToIndex(setup.sorterExportValoreDaSorter)];
                                File file = new File(fullFilename);
                                String extraFolders = file.getParent().replace(fileOrigin.getAbsolutePath(), "");
                                objSorterExport.add(extraFolders, sorterExportLabel, sorterExportRow);
                                String filenameReplaced = tagReplace(barcode, fullFilename, stock);
                                String to = pdfFolder+"\\"+filenameReplaced.replace("/", "\\");
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

    public static void makeSorterExport(){
        print("Genera sorterExport");
        for(String folder : objSorterExport.list.keySet()){
            String csvFile = sorterExport+folder+".csv";
            String[] header = {"N.Pacco-Anno", "Sequenza nel Pacco", "Barcode"};
            try (
                CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)
            ) {
                HashMap<String, ArrayList<String[]>> keys = objSorterExport.list.get(folder);
                for (String key : keys.keySet()) {
                    String[] firstRow = new String[]{setup.sorterExportTitolo+":", key};
                    writer.writeNext(firstRow);
                    writer.writeNext(header);
                    for (String[] row : keys.get(key)) {
                        writer.writeNext(row);
                    }
                    writer.writeNext(new String[]{});
                }
                writer.close();
            } catch (IOException e) {
                printError("makeSorterExport",e);
            }
        }
    }

    public static void makePdfMulti(StepController stepController){
        print("Genera pdf");
        int threadIndex = 0;
        StepPdf objStepPdf = new StepPdf();
        for (String from : objToPdf.list.keySet()) {
            String to = objToPdf.list.get(from);
            if(stepController.listIsFull()){
                stepController.printProgress("Pdf",objToPdf.list.size());
            }
            ThreadPdf Thread = new ThreadPdf( "pdf" + threadIndex, objStepPdf, from, to);
            try {
                Thread.join();
            } catch ( InterruptedException e) {
                printError("pdf from: "+from+" to "+to, e);
            }
            threadIndex++;
            Thread.start();
            stepController.stepAdd(from + " " + to);
        }
        ThreadCount.waitPdf();
    }

    public static String cellValue(Cell cell){
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

    public static String tagReplace(String barcode, String fullFileName, String stock){
        try {
            String[] jobSorterRow =  objJobSorter.rows.get(barcode);
            String barcodeFolderTag = setup.nomeCartellaPdf;
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
            printError("fileBarcodeTo", e);
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

}