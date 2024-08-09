package app.o3_sorter_stock;

import java.io.IOException;
import java.util.ArrayList;

public class StepController extends functions{
    public int count;
    public int countFile;
    public int emptyAt;
    public int minEmptyAt;
    public ArrayList<String> stepList;
    public String workingPrefix;

    public StepController(){

        minEmptyAt=1000;
        count=0;
        countFile=0;
        stepList=new ArrayList<>();
        workingPrefix="";

        try {
            createDirectory(stepControl);
        } catch (IOException e) {
            printError("StepController createDirectories", e);
        }
    }
    public void checkLimit(int test){
        if((test/10)<minEmptyAt&&test>0){
            emptyAt=(test/10);
        }else{
            emptyAt=minEmptyAt;
        }
    }
    public void stepAdd(String text){
        stepList.add(text);
        count++;
    }
    public boolean listIsFull(){
        if(emptyAt==0){
            checkLimit(0);
        }
        return count>=emptyAt;
    }

    public void end(String prefix){
        printStepControl(stepControl+"\\\\"+prefix+"_end.txt","1");
        stepList = new ArrayList<>();
        countFile=0;
        count=0;
    }
    public void printProgress(String text, int listSize){
        double percent = ((double)countFile*(double)emptyAt)/(double)listSize;
        printProgress(percent, text);
    }
    public void progressEnd(String text){
        print(strPad(text,20," ")+": ########## (100.0%)");
    }
}