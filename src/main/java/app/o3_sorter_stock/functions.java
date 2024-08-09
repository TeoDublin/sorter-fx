package app.o3_sorter_stock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.gson.Gson;

public class functions{
    public static boolean hasEtichettaError=false;
    public static String inputBlackDir="";
    public static String logFolder="";
    public static String pdfFolder="";
    public static int totalThreads;
    public static String rotateHorizontal="";
    public static String jsonPath="";
    public static String stockPrefix="";
    public static String stockStartNumber="";
    public static String allBlackFiles="";
    public static String anomalyFolder="";
    public static String debug="no";
    public static String sorterExport="";
    public static String anomalyTxt="";
    public static String stockTxt="";
    public static String stepControl="";
    public static String errorFile="";
    public static String errorISDB="";
    public static String errorNONP="";
    public static String errorPDFE="";
    public static String allEtichette="";
    public static String allJobSorter="";

    public static int frontAngle=90;
    public static int backAngle=270;
    public static objSetupJson setup;
   
    public static void setVariables(String[] entries) throws IOException{
        for(String entry:entries){
            String[] split = entry.split(",");
            String key = split[0];
            String value = split[1].replace("\\", "\\\\");
            
            switch (key) {    
                case "inputBlackDir" -> inputBlackDir=value;
                case "logFolder" -> logFolder=value;
                case "pdfFolder" -> pdfFolder=value;
                case "totalThreads" -> totalThreads = Integer.parseInt(value);
                case "rotateHorizontal" -> rotateHorizontal=value;
                case "jsonPath" -> jsonPath=value;
                case "stockPrefix" -> stockPrefix=value;
                case "stockStartNumber" -> stockStartNumber=value;
                case "allBlackFiles" -> allBlackFiles=value;
                case "anomalyFolder" -> anomalyFolder=value;
                case "debug" -> debug=value;
                case "sorterExport" -> sorterExport=value;
                case "allEtichette" -> allEtichette=value;
                case "allJobSorter" -> allJobSorter=value;
                default -> throw new IOException( "Input non previsto -> "+key);  
            }
        }

        stepControl=file(logFolder,"stepControl");
        errorFile = file(logFolder,"STOCK_ERRORS.log");
        errorISDB=file(anomalyFolder, "doppioni");
        errorNONP=file(anomalyFolder, "non previsto nel tracciato_etichetta");
        errorPDFE=file(anomalyFolder, "errore generazione pdf");
        anomalyTxt=file(anomalyFolder,"SISTEMA_TRACCIATO_ETICHETTA.txt");
        stockTxt=file(pdfFolder,"pacco_primo_e_ultimo.txt");

        if(rotateHorizontal.equals("yes")){
            backAngle=90;
        }
        if(inputBlackDir.isEmpty()||!isDirectory(inputBlackDir)){
            throw new IOException("Imposta una Cartella di entrata valida per i file neri!");   
        }
        if(pdfFolder.isEmpty()){throw new IOException("Imposta una Cartella di uscita valida!");}
        else if(!isDirectory(pdfFolder)){createDirectory(pdfFolder);}
       
        if(stockPrefix.isEmpty()||stockStartNumber.isEmpty()){
            throw new IOException("Imposta un valore valido per il calcolo dei pacchi");   
        }
        else{
            int intStockStartNumber = Integer.parseInt(stockStartNumber) - 1;
            String startAt = strPad(String.valueOf(intStockStartNumber),4,"0");
            objDoneStock.add(startAt);                
        }
        if(allEtichette.isEmpty()){
            throw new IOException("Imposta un valore valido per allEtichette");   
        }
        print(jsonPath);
        if(jsonPath.isEmpty()||!isFile(jsonPath)){
            throw new IOException( "Imposta un percorso valido per il file json!");  
        }
        else{
            setup = setupJson(jsonPath);
        } 
        if(allJobSorter.isEmpty()){
            throw new IOException("Imposta un valore valido per allJobSorter");   
        } 

        deleteAll(pdfFolder);
    }    

    public static String file(String a, String b){
        File file = new File(a,b);
        return file.getPath();
    }
    
    public static String getFileName(String filePath){
        File file = new File(filePath);
        return file.getName();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static boolean isDirectory(String path){
        File directory = new File(path);
        return directory.isDirectory();
    }

    public static void delete(String path){
        File directory = new File(path);
        deleteFile(directory);
    }

    public static void deleteFile(File directory) {
        if(directory.isDirectory()) {
          File[] files = directory.listFiles();
          if(files != null) {
            for(File file : files) {
              deleteFile(file);
            }
          }
        }
        directory.delete();
    }

    public static void moveFile(String from, String to){
        try {
            createDirectory(to);
            Files.move(Paths.get(from), Paths.get(to), REPLACE_EXISTING);
        } catch (IOException e) {
            printError("moveFile from "+from+" to "+to,e);
        }
    }

    public static void copyFile(String from, String to){
        try {
            Files.copy(Paths.get(from), Paths.get(to), REPLACE_EXISTING);
        } catch (IOException e) {
            printError("copyFile from "+from+" to "+to, e);
        }
    }   

    public static String getFrontPath(String path){
        return path.replace("Backside", "");
    }

    public static String getBackPath(String path){
        return getFrontPath(path).replace("Camera", "BacksideCamera");
    }

    public static String getErrorDir(String code){
        String dir;
        switch (code) {            
            case "ISDB" -> dir = errorISDB;
            case "PDFE" -> dir = errorPDFE;
            case "NONP" -> dir = errorNONP;
            default -> throw new AssertionError();
        }
        return dir;
    }   

    public static void copyFiles(String code, String from){
        String filename = getFileName(from);
        String dir;
        dir=getErrorDir(code);
        try {
            createDirectory(dir);
        } catch (IOException e) {
            printError("copyFiles code "+code+" from "+from, e);
        }
        copyFile(from,dir+"\\\\"+filename);
    }

    public static void moveFiles(String code, String from){
        String filename = getFileName(from);
        String dir;
        dir=getErrorDir(code);
        try {
            createDirectory(dir);
        } catch (IOException e) {
            printError("moveFiles code "+code+" from "+from, e);
        }
        moveFile(from,dir+"\\\\"+filename);        
    }

    public static void moveFilesWithJobSorterGroup(String code, String from){
        if(fileExists(from)){
            String dir;
            dir=getErrorDir(code)+"\\\\"+getGroup(getBarcodeFromBlackFile(from));
            try {
                createDirectory(dir);
                Path path = Paths.get(from);
                Path inputPath = Paths.get(inputBlackDir);
                String to = path.toString().replace(inputPath.toString(),dir);
                try {
                    createDirectory(to);
                    moveFile(from,to);
                } catch (IOException e) {
                    printError("moveFilesWithDir code "+code+" from "+from, e);
                }
            } catch (IOException e) {
                printError("moveFilesWithDir code "+code+" from "+from, e);
            }
        }
    }

    public static void moveFilesWithDir(String code, String from){
        if(fileExists(from)){
            String dir;
            dir=getErrorDir(code);
            try {
                createDirectory(dir);
                Path path = Paths.get(from);
                Path inputPath = Paths.get(inputBlackDir);
                String to = path.toString().replace(inputPath.toString(),dir);
                try {
                    createDirectory(to);
                    moveFile(from,to);
                } catch (IOException e) {
                    printError("moveFilesWithDir code "+code+" from "+from, e);
                }
            } catch (IOException e) {
                printError("moveFilesWithDir code "+code+" from "+from, e);
            }
        }
    }
    
    public static void printStepControl(String file, String line){
        if(!fileExists(file)){
            try {
                createDirectory(file);
            } catch (IOException e) {
                printError("printStepControl "+file, e);
            }
        }        
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.append(line+"\n");
            }
        } catch (IOException e) {
            printError("error putting Step",e);
        }
    }

    public static void printLog(String file, String line){
        if(!fileExists(file)){
            try {
                createDirectory(file);
            } catch (IOException e) {
                printError("printLog "+file, e);
            }
        }        
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.append(line+"\n");
            }
        } catch (IOException e) {
            printError("error putting log", e);
        }
    }

    public static boolean preg_match(String _pattern, String string){
        Pattern pattern = Pattern.compile(_pattern);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
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

    public static ArrayList<String>ls(String folderPath){
        ArrayList<String> ret = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath), Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> ret.add(path.toString())); // You can process each file here
        } catch (IOException e) {
            printError("ls "+folderPath, e);
        }
        return ret;
    }

    public static ArrayList<String>ls(String folderPath, String not){
        ArrayList<String> ret = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath), Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                    if(!preg_match(".+"+not+".+", path.toString())){
                        ret.add(path.toString());
                    }
                });
        } catch (IOException e) {
            printError("ls "+folderPath, e);
        }
        return ret;
    }

    public static ArrayList<String>getFilesBack(String path, ArrayList<String> foundFiles){
        try {
            File f = new File(path);
            File[] files = f.listFiles();
            for (File file : files) {
                if(file.isDirectory()){
                    getFilesBack(file.getAbsolutePath(), foundFiles);
                }else if(file.getName().toLowerCase().contains("backside")){
                    foundFiles.add(file.getCanonicalPath());
                }
            }
            return foundFiles;            
        } catch (IOException e) {
            printError("getFiles.back path: "+path, e);
        }
        return null;

    }    

    public static String strPad(String inputString, int length, String append) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(append);
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static boolean isBack(String path){
        return path.contains("Backside")||path.contains("-RETRO");
    }

    public static boolean fileExists(String path){
        String filePath = path;
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean folderExists(String filePath){
        File file = new File(filePath);
        Boolean ret;
        if(file.isDirectory()){
            ret = file.exists();
        }else{
            File parent = new File(file.getParent());
            ret = parent.exists();
        }
        return ret;
    }

    public static void createDirectory(String path) throws IOException {
        if(!fileExists(path)){
            String[] split = path.split("\\\\");
            String splitPath="";
            for(String dir:split){
                 if(!dir.isEmpty()){
                    if(!isFile(splitPath+dir)){
                        splitPath+=dir+"\\\\";
                        if(!fileExists(splitPath)){
                            Files.createDirectories(Paths.get(splitPath));
                        }                
                    }                
                }
            }        
        }
    }

    public static void mkDir(String path) throws IOException {
        File file = new File(path);
        if (file.getParentFile() != null) {
            Files.createDirectories(file.getParentFile().toPath());
        }
    }

    public static void write(String row, String file) {
        if(!fileExists(file)){
            try {
                mkDir(file);
            } catch (IOException ee) {
                printError("write", ee);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            try {
                writer.append(row+ "\n");
            } catch (IOException var5) {
                try {
                    writer.close();
                } catch (IOException var4) {
                    var5.addSuppressed(var4);
                }
                throw var5;
            }
        } catch (IOException var6) {
           printError("write", var6);
        }
    }

    public static void printError(String error, Exception e) {
        if(!fileExists(logFolder)){
            try {
                mkDir(logFolder);
            } catch (IOException ee) {
                System.err.println("printError "+ee.getMessage());
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile, true))) {
            try {
                writer.append("----------------------------------------------------------------\n");
                writer.append(error + "\n");
                writer.append(e.getMessage() + "\n");
                writer.append(Arrays.toString(e.getStackTrace()) + "\n");
            } catch (IOException var5) {
                try {
                    writer.close();
                } catch (IOException var4) {
                    var5.addSuppressed(var4);
                }
                
                throw var5;
            }
        } catch (IOException var6) {
           System.err.println(var6+" "+e.getMessage());
        }
    }

    public static boolean isFile(String path){
        Path filePath = Paths.get(path);
        return Files.isRegularFile(filePath);
    }

    public static void deleteAll(String path){
        try {
            Path directory = Paths.get(path);
            Files.walk(directory)
                 .filter(Files::isRegularFile)
                 .forEach((Path file) -> {
                     try {
                         Files.delete(file);
                     } catch (IOException e) {
                         printError("deleteAll "+path, e);
                     }
                 });
        } catch (IOException e) {
            printError("deleteAll "+path, e);
        }finally{}  
    }

    public static objSetupJson setupJson(String path){
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(path)) {
            objSetupJson data = gson.fromJson(reader, objSetupJson.class);
            return data;
        } catch (IOException e) {
            printError("setupJson", e);
        }
        return null;
    }

    public static int letterToIndex(String letter) {
        return Character.toUpperCase(letter.charAt(0)) - 'A';
    }

    public static String[] blackPathSplit(String fileStr){
        File file = new File(fileStr);
        String filename = file.getName();
        filename = filename.replace("RAC-EST", "RAC_EST");   
        return filename.split("-");
    }

    public static String getBarcodeFromBlackFile(String filename){
        String[] blackPathSplit = blackPathSplit(filename); 
        return blackPathSplit[ 6 ];
    }
    
    public static String getIndexFromBlackFile(String filename){
        String[] blackPathSplit = blackPathSplit(filename); 
        return blackPathSplit[ 9 ] + "-" + blackPathSplit[ 10 ];
    }

    public static String getGroup(String barcode) {
        String[] row = objJobSorter.rows.get(barcode);
        String[] groupBy = setup.raggruppamentoJobSorter;
        String ret = "";
        for (String group : groupBy) {
            try {
                ret+=row[letterToIndex(group)];
            } catch (Exception e) {}
        }
        return ret;
    }

    public static int indexMin(String barcode){
        int ret=0;
        try {
            String filename =  objBlackFiles.barcodePathMin.get(barcode.toUpperCase());
            ret = index(barcode, filename);
        } catch (Exception e) {}
        return ret;
    }

    public static int indexMax(String barcode){
        int ret=0;
        try {
            String filename =  objBlackFiles.barcodePathMax.get(barcode.toUpperCase());
            ret = index(barcode, filename);
        } catch (Exception e) {}
        return ret;
    } 

    public static int index(String barcode, String filename) throws Exception{
        int ret = 0;
        try {
            ret= Integer.parseInt(getIndexFromBlackFile(filename).replace("-", ""));
        } catch (NumberFormatException e) {
            printError("il barcode: "+barcode+" non è stato trovato", e);
            throw e;
        }
        return ret;
    }  

    public static void printProgress(double percent, String text){
        if(percent>1.0){
            percent=1.0;
        }
        String line = strPad(text,20," ");
        String progress = strPad("",10-(int)round(percent*10.00,2),"_");
        line+=": "+strPad(progress,10,"#");
        line+=" ("+round(percent*100.0,2)+"%)";
        print(line);
    }

    public static void print(String text){
        System.out.println(text);
    }
    
}