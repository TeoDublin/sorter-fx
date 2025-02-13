package app.o2_sorter_gray;

import java.util.ArrayList;
import java.util.HashMap;


public class ThreadObjReadBarcode extends functions {

   public void start(String path) {
      String pathFront = path.replace("PREFIX", "Camera");
      String pathBack = path.replace("PREFIX", "BacksideCamera");
      objReadBarcode objReadBarcode = new objReadBarcode(pathFront, pathBack);
      objReadBarcode.readBarCodesDoingWhites();
      ArrayList<String> barcodesJobsorter = checkInJobSorter(objReadBarcode.resultsFront, objReadBarcode.resultsBack);
      if(barcodesJobsorter.isEmpty()){
         objReadBarcode.logResults("NOIS");
      }if(barcodesJobsorter.size()>1){
         objReadBarcode.logResults("ISDB");
      }else{
         if(!objReadBarcode.equals.isEmpty()){
            objReadBarcode.logResults("FOUND");
            objConcurrentBlackController.add("FOUND", barcodeFiles(objReadBarcode.equals));
         }
         else if(!objReadBarcode.resultsFront.isEmpty()&&!objReadBarcode.resultsBack.isEmpty()){
            objReadBarcode.logResults("FOUND NOT EQUAL");
            objConcurrentBlackController.add("BBNE", barcodeFiles(objReadBarcode.resultsFront, objReadBarcode.resultsBack));
         }
         else if(!objReadBarcode.resultsFront.isEmpty()||!objReadBarcode.resultsBack.isEmpty()){
            objReadBarcode.logResults("FOUND ONE EMPTY");
            HashMap<String, ArrayList<String>> barcodeFiles = barcodeFiles(objReadBarcode.resultsFront, objReadBarcode.resultsBack);
            if(!barcodeFiles.isEmpty()){
               for (String barcodeFile : barcodeFiles.keySet()) {
                  ArrayList<String> files = barcodeFiles.get(barcodeFile);
                  for (String file : files) {
                     if(isActCompiutaGiacenza(file, barcodeFile)){
                        objConcurrentBlackController.add("FOUND_ACTCG",barcodeFile,file);
                     }else{
                        objConcurrentBlackController.add("BBNE",barcodeFile,file);
                     }
                  }
               }
            }
         }
         else{
            objReadBarcode.logResults("FOUND NOTHING");
         }
      }
   }

   public static HashMap<String, ArrayList<String>> barcodeFiles(ArrayList<String> barcodesA,ArrayList<String> barcodesB){
      HashMap<String, ArrayList<String>> ret = new HashMap<>();
      HashMap<String, ArrayList<String>> retA = barcodeFiles(barcodesA);
      HashMap<String, ArrayList<String>> retB = barcodeFiles(barcodesA);
      for (String barcode : retA.keySet()) {
         if(!ret.containsKey(barcode)){
            ret.put(barcode, retA.get(barcode));
         }
      }
      for (String barcode : retB.keySet()) {
         if(!ret.containsKey(barcode)){
            ret.put(barcode, retB.get(barcode));
         }
      }
      return ret;
   }

   public static HashMap<String, ArrayList<String>> barcodeFiles(ArrayList<String> barcodes){
      HashMap<String, ArrayList<String>> barcodeFiles = new HashMap<>();
      for (String barcode : barcodes) {
         String alternative = objJobSorter.alternative.get(barcode);
         if(objFilesBlack.barcodeFiles.containsKey(barcode)){
            ArrayList<String> files = objFilesBlack.barcodeFiles.get(barcode);
            barcodeFiles.put(barcode, files);
         }
         else if(objFilesBlack.barcodeFiles.containsKey(alternative)){
            ArrayList<String> files = objFilesBlack.barcodeFiles.get(alternative);
            barcodeFiles.put(alternative, files);
         }
      }
      return barcodeFiles;
   }

   public static ArrayList<String> checkInJobSorter(ArrayList<String> barcodesA, ArrayList<String> barcodesB){
      ArrayList<String> ret = new ArrayList<>();
      ArrayList<String> retA = checkInJobSorter(barcodesA);
      ArrayList<String> retB = checkInJobSorter(barcodesB);
         
      for (String barcode : retA) {
         String alternative = objJobSorter.alternative.get(barcode);
         if(!ret.contains(barcode)&&!ret.contains(alternative)){
            ret.add(barcode);
         }
      }
      
      for (String barcode : retB) {
         String alternative = objJobSorter.alternative.get(barcode);
         if(!ret.contains(barcode)&&!ret.contains(alternative)){
            ret.add(barcode);
         }
      }
      
      return ret;
   }

   public static ArrayList<String> checkInJobSorter(ArrayList<String> barcodes){
      ArrayList<String> ret = new ArrayList<>();
      for (String barcode : barcodes) {
         if(objJobSorter.list.contains(barcode)){
            ret.add(barcode);
         }
      }
      return ret;
   }


}
