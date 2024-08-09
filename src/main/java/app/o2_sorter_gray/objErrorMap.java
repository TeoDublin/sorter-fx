package app.o2_sorter_gray;

import java.util.ArrayList;
import java.util.HashMap;

public class objErrorMap extends functions{
    public HashMap<String,objError> map;
    public ArrayList<String> success = new ArrayList<>();
    public objErrorMap(){
        map = new HashMap<>();
        map.put("BBNE", new objError(anomalyFolder,"fronte e retro probabilmente sono diversi", true));//Black barcode not equal
        map.put("CGNF", new objError(anomalyFolder,"manca il tiff fronte o retro", true));//Couple file not found
        map.put("ISMT", new objError(anomalyFolder,"doppioni (progsorter inferiori)", true));//Is Multi
        map.put("GFNF", new objError(anomalyFolder,"Servi verifica manuale", true));//Gray file not found     
        map.put("FSEC", new objError(anomalyFolder,"File corrotto", true));//File is error or corrupted

        //gray errors
        map.put("NOIS", new objError(errorDir,"barcode non trovato nel file jobsorter", false));//Not in sorter
        map.put("CFNF", new objError(errorDir,"file copia non trovato", false));//Couple file not found
        map.put("BCNF", new objError(errorDir,"barcode non trovato", false));//Barcode not found
        map.put("ISDB", new objError(errorDir,"probabilmente attaccati", true));//Is Doubled

        success.add("FOUND");
        success.add("FOUND_ACTCG");
    }
    public objError get(String code){
        return map.get(code);
    }
}
