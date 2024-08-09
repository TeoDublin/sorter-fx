package app.o2_sorter_gray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class objConcurrentBlackController extends functions{
   public static final ConcurrentHashMap<String, String> barcodeStatus = new ConcurrentHashMap<>();
   public static final ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
   private static final AtomicBoolean isPrinting = new AtomicBoolean(false);
   private static int step = 0;

   public static void add(String code,HashMap<String, ArrayList<String>> barcodeFiles) {
      if(!barcodeFiles.isEmpty()){
         for(String barcode:barcodeFiles.keySet()){
            ArrayList<String> files = barcodeFiles.get(barcode);
            for (String file : files) {
               add(code,barcode,file);
            }
         }
      }
   }

   public static void add(String code,String barcode,String file) {
      if(barcodeStatus.containsKey(barcode)){
         String previous = barcodeStatus.get(barcode);
         if(!previous.equals(code)){
            if(errorMap.success.contains(previous)){
               code=previous;
            }else{
               barcodeStatus.put(barcode,code);
            }
            list.add(file+";"+code+";"+barcode);
         }
      }else{
         barcodeStatus.put(barcode, code);
         list.add(file+";"+code+";"+barcode);
      }
      if(list.size()>=PRINT_AT && isPrinting.compareAndSet(false, true)){ printLog();}
   }

   public static void printLog(){
      File output = new File(logBlack, "lb_"+step++);
      String outputPath=output.getPath();
      mkdir(outputPath);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath,true))) {
         String line;while((line=list.poll())!=null){ writer.append(line+"\n"); }
      } catch (Exception e) { error("objBlackController",e);
      } finally { isPrinting.set(false);}
   }

   public static void printAll(){ printLog();
      File output = new File(logBlack, "LOGBLACK.txt");mkdir(logBlack);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(output.getPath(),true))) {
         for (String barcode : barcodeStatus.keySet()) {
            ArrayList<String> files = objFilesBlack.barcodeFiles.get(barcode);
            String code = barcodeStatus.get(barcode);
            for (String fileStr : files) {
               File file = new File(fileStr);
               if(!errorMap.success.contains(code)&&file.exists()){
                  moveFilesBlackWithDir(code, fileStr);
               }
               String line = fileStr+";"+barcode+";"+barcodeStatus.get(barcode);
               writer.append(line+"\n");
            }
         }
      } catch (Exception e) { error("objBlackController printAll",e);}
   }
   
}
