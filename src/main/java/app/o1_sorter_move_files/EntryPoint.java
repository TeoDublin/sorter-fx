package app.o1_sorter_move_files;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EntryPoint extends functions{
    public static void start(String doing) throws Exception{
        variables();
        switch (doing) {
            case "copy" -> {
                HashMap<String, String> map = map();
                if(!map.isEmpty()){
                    checkMemory();
                    copy(map);
                }
            }
            case "check" -> {
                check();
                objAnomalies.print();
            }
            case "delete" -> {
            }              
            default -> {
            }
        }
    }

    public static void copy(HashMap<String, String> map) throws Exception{
        int threadIndex = 0;
        for(String from : map.keySet()) {     
            Threads.start(new ThreaCopyFiles("cf_"+threadIndex++, new ThreadObjCopyFiles(), from, map.get(from)));
        }
        Threads.waitRunning();
    }

    public static boolean check() throws Exception{
        HashMap<String,String> filesMap = mapCheck(); 
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        for(String file : filesMap.keySet()){
            executor.submit(() -> {
                try {
                    String sumSource = calculateChecksum(file);
                    String sumTarget = calculateChecksum(filesMap.get(file));
                    if(!sumSource.equals(sumTarget)){ objAnomalies.add(anomalyFile, file);}
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        return !objAnomalies.list.contains(anomalyFile);
    }

    private static void checkMemory() throws Exception{
        String targetGrayPartition = targetGray.substring(0,2);
        String targetTiffPartition = targetTiff.substring(0,2);
        if(targetGrayPartition.equals(targetTiffPartition)){
            File diskPartition = new File(targetGrayPartition);
            long freeSpace = diskPartition.getFreeSpace();
            long graySize = folderSize(sourceGray);
            long tiffSize = folderSize(sourceTiff);
            long sourceSize = graySize + tiffSize;
            double sizeLocal = (double) freeSpace / (1024 * 1024 * 1024);
            double sizeSource = (double) sourceSize / (1024 * 1024 * 1024);
            if(sizeLocal<sizeSource){
                throw new Exception("SPAZIO INSUFFICIENTE ! Servono: "+String.format("%.2f", sizeSource)+" GB. Sono Disponibili: "+String.format("%.2f",sizeLocal)+" GB");
            }
        }
    }

}