package app;

import java.util.concurrent.ConcurrentLinkedQueue;

import app.o1_sorter_move_files.functions;

public class objAnomalies extends functions{
    public static String noSpace="";
    public static ConcurrentLinkedQueue<String> moveFiles = new ConcurrentLinkedQueue<>();
    public static void addMoveFiles(String anomalyFile){
        if(!moveFiles.contains(anomalyFile)){
            moveFiles.add(anomalyFile);
        }
    }
}
