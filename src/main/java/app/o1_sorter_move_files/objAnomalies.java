package app.o1_sorter_move_files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class objAnomalies extends functions{
    public static ConcurrentHashMap<String, ArrayList<String>> list = new ConcurrentHashMap<>();
    public static void add(String anomalyFile, String anomaly){
        if(list.contains(anomalyFile)&&!list.get(anomalyFile).isEmpty()){
            list.get(anomalyFile).add(anomaly);
        }else{
            ArrayList<String> newList = new ArrayList<>();
            newList.add(anomaly);
            list.put(anomalyFile, newList);
        }
    }
    public static void print() throws Exception{
        for (String file : list.keySet()) {
            mkdir(file);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (String line : list.get(file)) { writer.append(line+"\n");}
            } catch (Exception e) {error("objAnomalies "+file, e);}
        }
    }
}
