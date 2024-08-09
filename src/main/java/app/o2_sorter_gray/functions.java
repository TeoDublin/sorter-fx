package app.o2_sorter_gray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class functions {
   public static long timingLogic;
   public static long timingLogicSum;
   public static long timingBarcode;
   public static long timingBarcodeSum;
   public static long timingBlack;
   public static long timingBlackSum;
   public static long timingBw;
   public static long timingBwSum;
   public static long timingRotate;
   public static long timingRotateSum;
   public static int totalThreads;
   public static int PRINT_AT = 10000;
   public static String resume="no";
   public static String stepControl = "";
   public static String logGray = "";
   public static String logTiming = "";
   public static String logBlack = "";
   public static String tmpDir = "";
   public static String anomalyFolder = "";
   public static String bwDir = "";
   public static String grayFolder = "";
   public static String logFolder = "";
   public static String allBlackFiles = "";
   public static String toBarcodeReaderFile = "";
   public static String blackFolder = "";
   public static String debug = "";
   public static String errorDir = "";
   public static String errorFile = "";    
   public static String anomalyFile = "";    
   public static String countFile = "";    
   public static String frontBcType = "";
   public static String backBcType = "";
   public static String scanditProgram = "";
   public static String scanditKey = "";
   public static String allEtichette = "";
   public static String allJobSorter = "";
   public static objErrorMap errorMap;
   public static void setVariables(String[] args) throws IOException{
      print("Inizio");timingLogic();
      for(String entry:args){
         String[] split = entry.split(",");
         String key = split[0];
         String value = split[1].replace("\\", "\\\\");
         switch (key) {
            case "anomalyFolder" -> anomalyFolder=value;
            case "blackFolder" -> blackFolder=value;
            case "grayFolder" -> grayFolder=value;
            case "logFolder" -> logFolder=value;
            case "totalThreads" -> totalThreads = Integer.parseInt(value);
            case "backBcType" -> backBcType=value;
            case "frontBcType" -> frontBcType=value;
            case "debug" -> debug=value;
            case "allBlackFiles" -> allBlackFiles=value;
            case "resume" -> resume=value;
            case "scanditProgram" -> scanditProgram=value;
            case "scanditKey" -> scanditKey=value;
            case "allEtichette" -> allEtichette=value;
            case "allJobSorter" -> allJobSorter=value;
            default -> throw new IOException( "Input non previsto -> "+key);  
         }
      }
      bwDir=folder(logFolder,"BW");
      stepControl=folder(logFolder,"BARCODEREAD");
      logGray=folder(logFolder,"LOGGRAY");
      logBlack=folder(logFolder,"LOGBLACK");
      errorDir=folder(logFolder,"FILES");
      errorFile=folder(logFolder,"ERRORS.txt");
      countFile=folder(logFolder,"COUNT.txt");
      logTiming=folder(logFolder,"TIMING.txt");
      toBarcodeReaderFile=folder(logFolder,"TOBARCODEREADER.txt");
      
      errorMap= new objErrorMap();

      if(frontBcType.isEmpty()){
         frontBcType = "ALL";
      }
      if(backBcType.isEmpty()){
         backBcType = "ALL";
      }  
      if(blackFolder.isEmpty()||!Files.exists(Paths.get(blackFolder))){
         throw new IOException("Imposta una Cartella di entrata valida per i file neri!");   
      }
      if(grayFolder.isEmpty()||!Files.exists(Paths.get(grayFolder))){
         throw new IOException("Imposta una Cartella di entrata valida per i file grigi!");   
      }        
      if(logFolder.isEmpty()){
         throw new IOException("Imposta una Cartella log valida!");      
      }
      createInvisibleFolder(logFolder);
   }
   
   public static String folder(String baseFolder, String folder){
      File ret = new File(baseFolder, folder);
      return ret.toString();
   }

   public static void printTime() {
      timingLogicSum();
      long totalTime = timingLogicSum;
      if (totalTime > 0L) {
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(logTiming))) {
            writer.append("TOTAL: " + timeFormated(totalTime));
         } catch (Exception e) {
            error("printTime",e);
         }
      }
   }

   public static void timingLogic() {
      timingLogic = System.nanoTime();
   }

   public static void timingLogicSum() {
      timingLogicSum += System.nanoTime() - timingLogic;
      timingBarcode();
   }

   public static void timingBarcode() {
      timingBarcode = System.nanoTime();
   }

   public static void timingBarcodeSum() {
      timingBarcodeSum += System.nanoTime() - timingBarcode;
      timingBarcode();
   }

   public static void timingBw() {
      timingBw = System.nanoTime();
   }

   public static void timingBwSum() {
      timingBwSum += System.nanoTime() - timingBw;
      timingBw();
   }

   public static double round(double value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      }

      BigDecimal bd = BigDecimal.valueOf(value);
      bd = bd.setScale(places, RoundingMode.HALF_UP);
      return bd.doubleValue();
   }

   public static void delete(String path) {
      File directory = new File(path);
      deleteFile(directory);
   }

   public static void deleteFile(File directory) {
      if (directory.isDirectory()) {
         File[] files = directory.listFiles();
         if (files != null) {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File file = var2[var4];
               deleteFile(file);
            }
         }
      }
      directory.delete();
   }

   public static String timeFormated(Long nanos) {
      long tempSec = nanos / 1000000000L;
      long sec = tempSec % 60L;
      long min = tempSec / 60L % 60L;
      long hour = tempSec / 3600L % 24L;
      long day = tempSec / 86400L % 24L;
      return String.format("%dd %dh %dm %ds", day, hour, min, sec);
   }

   public static void logErrorGray(String code, String a, String b) {
      logErrorGray(code, a);
      logErrorGray(code, b);
   }

   public static void logErrorGray(String code, String path) {
      logResultGray(code, path, null);
   }

   public static void logResultGray(String code, String from, String result) {
      objConcurrentGrayController.add(from,code,result);
   }
   
   public static void moveFilesBlackWithDir(String code, ArrayList<String> list){
      for (String from : list) { moveFilesBlackWithDir(code, from);}
   }

   public static void moveFilesBlackWithDir(String code, String from){
      objError objError = errorMap.get(code);
      Path fromPath = Paths.get(from);
      if(debug.equals("no")&&!objError.moveFile){
         delete(from);
      }else if(Files.exists(fromPath)){
         Path path = Paths.get(from);
         Path blackFolderPath = Paths.get(blackFolder);
         String to = path.toString().replace(blackFolderPath.toString(),objError.path);
         Path toPath = Paths.get(to);
         try {
            mkdir(to);
            Files.move(Paths.get(from), toPath, StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException e) {
            error("copyFiles code " + code + " from " + from, e);
         }
      }
   }

   public static void printError(Exception e){
      print("----------------------------------------------------------------------------------------------------------------------------------");
      print(e.toString());
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stackTrace = sw.toString();
      print(stackTrace);
      print("----------------------------------------------------------------------------------------------------------------------------------");
   }

   public static void error(String text, Exception e){
      File fileLog = new File(errorFile);
      File parent = new File(fileLog.getParent());
      if(!parent.exists()){ mkdir(parent.toString());}
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile,true))) {
         writer.append("----------------------------------------------------------------------------------------------------------------------------------\n");
         writer.append(text+"\n");
         writer.append(e.toString()+"\n");
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         e.printStackTrace(pw);
         String stackTrace = sw.toString();
         writer.append(stackTrace+"\n");
         writer.append("----------------------------------------------------------------------------------------------------------------------------------\n");
      } catch (Exception ee) { print("error log" + ee);}
   }

   public static void print(String msg){
      System.out.println(msg);
   }

   public static void createInvisibleFolder(String path) throws IOException {
      Path dirPath = Paths.get(path);
      if (!Files.exists(dirPath)) {
         Files.createDirectories(dirPath);
      }
      Files.setAttribute(dirPath, "dos:hidden", true);
   }

   public static void mkdir(String strPath){
      Path path = Paths.get(strPath);
      if(!Files.isDirectory(path)){
          File file = new File(strPath);
          String parent = file.getParent();
          path = Paths.get(parent);
      }
      if(!Files.exists(path)){
         try { Files.createDirectories(path);
         } catch (IOException e) { print("exists"+e);}
      }
   }

   public static String strPad(String inputString, int length, String append) {
      if (inputString.length() >= length) {
         return inputString;
      } else {
         StringBuilder sb = new StringBuilder();
         while(sb.length() < length - inputString.length()) {
            sb.append(append);
         }
         sb.append(inputString);
         return sb.toString();
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

   public static Boolean isActCompiutaGiacenza(String pathA, ArrayList<String> barcodesA, String pathB, ArrayList<String> barcodesB) {
      return isActCompiutaGiacenza(pathA,barcodesA)||isActCompiutaGiacenza(pathB,barcodesB);
   }
   public static Boolean isActCompiutaGiacenza(String path, ArrayList<String> barcodes) {
      for(String barcode:barcodes){
         if(isActCompiutaGiacenza(path,barcode)){
            return true;
         }
      }
      return false;
   }
   public static Boolean isActCompiutaGiacenza(String path, String barcode) {
      if(path.contains("1.Compiutagiacenza")){
         ArrayList<String> startsWith = new ArrayList<>();
         startsWith.add("788");
         startsWith.add("688");
         startsWith.add("386");
         startsWith.add("286");
         startsWith.add("FUAGLABCINTE");
         startsWith.add("ARAGLABCINTE");
         startsWith.add("FUAGLABSINTE");
         startsWith.add("ARAGLABSINTE");
         startsWith.add("AGB");
         startsWith.add("AGC");
         startsWith.add("AB");
         startsWith.add("AC");
         for(String prefix:startsWith){ if(barcode.startsWith(prefix)){ return true;}}
         return false;
      }
      else{
         return false;
      }
   }

   public static String barcode(String path){
      File file = new File(path);
      String filename = file.getName().replace("RAC-EST", "RAC_EST");
      String[] split = filename.split("-");
      return split[6];
   } 
}
