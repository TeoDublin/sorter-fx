package app.o2_sorter_gray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class objConcurrentGrayController extends functions{
   public static ConcurrentHashMap<String, Integer> count = new ConcurrentHashMap<>();
   public static ConcurrentHashMap<String, String> all = new ConcurrentHashMap<>();
   public static ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
   private static final AtomicBoolean isPrinting = new AtomicBoolean(false);
   private static int step = 0;

   public static void add(String path, String status, String result) {
      if(!all.containsKey(path)){
         count.compute(status, (key, val) -> val == null ? 1 : val + 1);
         list.add(path+";"+status+";"+result);
         all.put(path, status+";"+result);
         if(list.size()>=PRINT_AT && isPrinting.compareAndSet(false, true)){ printLog();}
      }
   }

   public static void printLog(){
      File output = new File(logGray, "lg_"+step++);
      String outputPath=output.getPath();
      mkdir(outputPath);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath,true))) {
         String line;while((line=list.poll())!=null){ writer.append(line+"\n"); }
      } catch (Exception e) { error("objGrayController",e);
      } finally { isPrinting.set(false);}
   }

   public static void printCount(){
      File output = new File(countFile);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(output,true))) {
         writer.append("GRAY TOTAL"+": "+all.size()+"\n");
         for(String status:count.keySet()){ writer.append("GRAY "+status+": "+count.get(status)+"\n"); }
      } catch (Exception e) { error("objGrayController printCount", e);}
   }

   public static void printAll(){ printLog();printCount();
      File output = new File(logGray, "LOGGRAY.txt");mkdir(logGray);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(output.getPath(),true))) {
         for(String file : all.keySet()){ 
            String line = file+";"+all.get(file);
            writer.append(line+"\n");
         }
      } catch (Exception e) { error("objGrayController printAll",e);}
   }

}
