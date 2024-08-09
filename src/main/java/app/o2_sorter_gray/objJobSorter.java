package app.o2_sorter_gray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class objJobSorter extends functions{
    public static ArrayList<String> list = new ArrayList<>();
    public static HashMap<String,String> alternative = new HashMap<>();
    public static void list() throws Exception{
        try (BufferedReader txtReader = new BufferedReader(new FileReader(allJobSorter))) {
            String file;
            while ((file=txtReader.readLine())!=null) {
                boolean isFirst=true;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while((line=reader.readLine())!=null){
                        if(isFirst){isFirst=false;}
                        else{
                            String[] columns = line.split(";");
                            list.add(columns[1]);
                            if(columns.length>7&&!columns[7].isEmpty()){
                                alternative.put(columns[7], columns[1]);
                            }
                        }
                    }
                } catch (Exception ee) { throw ee;}       
            }
        } catch (Exception e) {throw e;}
    }
}
