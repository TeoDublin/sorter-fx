package app.o2_sorter_gray;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

public class EntryPoint extends functions {
   public static void main(String[] args) {
      try {
         setVariables(args);grayFiles();blackFiles();
         if(resume.equals("yes")){ recalcToBarcodeReader(); }
         objJobSorter.list();
         readBarcodes();
         moveGFNF();
         prints();
      } catch (Exception e) { printError(e);}
   }

   private static void prints(){
      printTime();
      objConcurrentGrayController.printAll();
      objConcurrentBlackController.printAll();
      print("Fine");      
   }

   private static void moveGFNF(){
      for(String barcode:objFilesBlack.barcodeFiles.keySet()){
         if(!objConcurrentBlackController.barcodeStatus.containsKey(barcode)){
            ArrayList<String> files = objFilesBlack.barcodeFiles.get(barcode);
            for (String file : files) {
               moveFilesBlackWithDir("GFNF", file);
               objConcurrentBlackController.add("GFNF",barcode,file);
            }
         }
      }
   }

   private static void readBarcodes() {
      int threadIndex = 0;
      for(String path : objFilesGray.toBarcodeReader) {
         Threads.start(new ThreadReadBarcode("bcr_"+threadIndex++, new ThreadObjReadBarcode(), path), objFilesGray.toBarcodeReader.size());
      }
      Threads.waitRunning();
   }

   private static void blackFiles(){
      objFilesBlack.list();
      if (objFilesBlack.all.isEmpty()) { print("Nessun File da lavorare");} 
      else {
         print("Validando i file neri");
         for(String file : objFilesBlack.list) {
            String front = file + "-FRONTE.tiff";
            String back = file + "-RETRO.tiff";
            try {
               if (!objFilesBlack.all.contains(front)) {
                  objConcurrentBlackController.add("CGNF",barcode(back),back);
                  moveFilesBlackWithDir("CGNF", back);  
               } else if (!objFilesBlack.all.contains(back)) {
                  objConcurrentBlackController.add("CGNF",barcode(front),front);
                  moveFilesBlackWithDir("CGNF", front);
               } else if (Files.size(Paths.get(back)) < 300L) {
                  objConcurrentBlackController.add("FSEC",barcode(back),back);
                  objConcurrentBlackController.add("CGNF",barcode(front),front);
                  moveFilesBlackWithDir("FSEC", back);
                  moveFilesBlackWithDir("CGNF", front);
               } else if (Files.size(Paths.get(front)) < 300L) {
                  objConcurrentBlackController.add("FSEC",barcode(front),front);
                  objConcurrentBlackController.add("FSEC",barcode(back),back);
                  moveFilesBlackWithDir("FSEC", front);
                  moveFilesBlackWithDir("FSEC", back);
               }
            } catch (IOException e) {
               error("blackFiles", e);
            } 
         }
         for(String barcode:objFilesBlack.barcodeFiles.keySet()){
            ArrayList<String>files=objFilesBlack.barcodeFiles.get(barcode);
            if(files.size()>2){
               int bigger = 0;
               HashMap<Integer, ArrayList<String>> byProgSorter = new HashMap<>();
               for (String file : files) {
                  String[] split = file.split("-");
                  int progSorter = Integer.parseInt(split[split.length-3]+split[split.length-2]);
                  if(progSorter > bigger){ bigger = progSorter;}
                  if(byProgSorter.containsKey(bigger)){
                     byProgSorter.get(bigger).add(file);
                  }else{
                     ArrayList<String> newList = new ArrayList<>();
                     newList.add(file);
                     byProgSorter.put(bigger, newList);
                  }
               }
               for(int progSorter:byProgSorter.keySet()){
                  for(String file:byProgSorter.get(progSorter)){
                     if(progSorter!=bigger){
                        objConcurrentBlackController.add("ISMT",barcode,file);
                        moveFilesBlackWithDir("ISMT",file);
                     }
                  }
               }
            }
         }
         objFilesBlack.refreshBarcodeFiles();
      }
   }

   private static void grayFiles(){
      objFilesGray.list();
      if (objFilesGray.all.isEmpty()) {
         print("Nessun File da lavorare");
      } else {
         for (String file : objFilesGray.files) {
            String front = file.replace("PREFIX", "Camera");
            String back = file.replace("PREFIX", "BacksideCamera");
            if(!objFilesGray.all.contains(front)){ objConcurrentGrayController.add(back,"CFNF",null);
            } else if(!objFilesGray.all.contains(back)){ objConcurrentGrayController.add(front,"CFNF",null);
            } else{ objFilesGray.toBarcodeReader.add(file);}
         }
      }
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(toBarcodeReaderFile))) {
         for (String file : objFilesGray.toBarcodeReader) { writer.append(file+"\n");}
      } catch (Exception e) { error("grayFiles", e); }
   }

   private static void recalcToBarcodeReader(){
      ArrayList<String> barcodeRead = new ArrayList<>();
      try {
         Files.walkFileTree(Paths.get(stepControl), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {  
               try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                  String line;while((line=reader.readLine())!=null){barcodeRead.add(line);}
               } catch (Exception e) {error("buildObjectsFromFiles",e);}
               return FileVisitResult.CONTINUE;
            }
         });    
      } catch (IOException e) { error("buildObjectsFromFiles", e);}   

      for(String path:objFilesGray.toBarcodeReader){ if(barcodeRead.contains(path)){ objFilesGray.toBarcodeReader.remove(path);}}
   }

}
