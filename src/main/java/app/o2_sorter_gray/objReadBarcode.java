package app.o2_sorter_gray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class objReadBarcode extends functions{
    public ArrayList<String> resultsFront;
    public ArrayList<String> resultsBack;
    public ArrayList<String> equals;
    public String pathFront;
    public String pathBack;

    public objReadBarcode(String pathFront, String pathBack){
        this.pathFront = pathFront;
        this.pathBack = pathBack;
    }

    public ArrayList<String> equals(ArrayList<String> listA, ArrayList<String> listB){
        ArrayList<String> ret = new ArrayList<>();
        for (String barcode:listA){ 
            if(listB.contains(barcode)&&!ret.contains(barcode)){ ret.add(barcode); }
            else{
                String alternative = objJobSorter.alternative.get(barcode);
                if(alternative!=null){
                    if(!alternative.isBlank()&&listB.contains(alternative)&&!ret.contains(alternative)){
                        ret.add(barcode);
                    }
                }
            }
        }
        for (String barcode:listB){ 
            if(listA.contains(barcode)&&!ret.contains(barcode)){ ret.add(barcode); }
            else{
                String alternative = objJobSorter.alternative.get(barcode);
                if(alternative!=null){
                    if(!alternative.isBlank()&&listA.contains(alternative)&&!ret.contains(alternative)){
                        ret.add(barcode);
                    }
                }
            }
        }
        return ret;
    }

    private String barcodeClean(String barcode){
        String ret = barcode.replace("-BACK", "").replace(" ","").replace("\"", "");
        ArrayList<String> m = preg_match("([0-9a-zA-Z]+)/.+",barcode,1);
        if(!m.isEmpty()){ ret = m.get(0);}
        return ret;
    }
  
    public void readBarCodesDoingWhites(){
        resultsFront = readBarCodes(pathFront, frontBcType);
        resultsBack = readBarCodes(pathBack, backBcType);
        equals = equals(resultsFront, resultsBack);
        if( (equals==null || equals.isEmpty()) && !isActCompiutaGiacenza(pathFront,resultsFront,pathBack,resultsBack)){
            objWhiteFile bwA = new objWhiteFile(pathFront, "front");
            objWhiteFile bwB = new objWhiteFile(pathBack, "back");
            while ((equals==null||equals.isEmpty()) && bwA.hasNext() && bwB.hasNext() && !isActCompiutaGiacenza(pathFront,resultsFront,pathBack,resultsBack)) {
                resultsFront = readBarCodes(bwA.next(), frontBcType, resultsFront);
                resultsBack = readBarCodes(bwB.next(), backBcType, resultsBack);
                equals = equals(resultsFront, resultsBack);
            }
            bwA.close();bwB.close();
        }
    }

    public ArrayList<String> readBarCodes(String path, String bcType, ArrayList<String> current){
        ArrayList<String> ret = readBarCodes(path, bcType);
        for (String barcode : ret) {
            if(!current.contains(barcode)){
                current.add(barcode);
            }
        }
        return current;
    }
    
    public ArrayList<String> readBarCodes(String path, String bcType){
        ArrayList<String> ret = new ArrayList<>();
        char unity = path.charAt(0);
        String wslPath = path.replace(unity+":", "/mnt/"+Character.toLowerCase(unity)).replace("\\","/");
        String command = "wsl /srv/scandit_multiple/"+scanditProgram+" /srv/scandit_multiple/"+scanditKey+" /srv/scandit_multiple/codes_"+bcType+".settings \""+wslPath+"\"";
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                error("path:"+path+" Command execution failed with exit code: " + exitCode +" "+command, new Exception());
            }
            else{
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        ret=readLine(line,ret);
                    }           
                } catch (Exception ee) { error("path:"+path+"readBarCodes",ee);}
            }
        } catch (IOException | InterruptedException e) {
            error("path:"+path+" Command failed: "+command, e);
        }
        return ret;
    }

    public ArrayList<String>readLine(String line,  ArrayList<String>ret){
        ArrayList<String>m = preg_match("\"value\":\"(.+)\"",line,1);
        if(!m.isEmpty()){
            String barcode = barcodeClean(m.get(0));
            if(!ret.contains(barcode)){
                ret.add(barcode);
            }
        }
        return ret;
    }

    public void logResults(String code){
        logResultGray(code,pathFront,line(resultsFront));
        logResultGray(code,pathBack,line(resultsBack));
    }
             
    public String line(ArrayList<String> results){
        String line = "";
        for (String result : results) { line+=result+";";} 
        return line;
    }

}
