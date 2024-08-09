package app.o2_sorter_gray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class Threads extends functions{
    private static int runningThreads=0;
    private static boolean donePrinting=false;
    private static int doneThreads=0;
    private static int printed=0;
    private static int step=0;
    private static int total;
    private static ArrayList<String> barcodeRead = new ArrayList<>();
    public static final ArrayList<ThreadReadBarcode> toRetry = new ArrayList<>();
    private static final ArrayList<ThreadReadBarcode> list = new ArrayList<>();

    public static void start(ThreadReadBarcode thread, int _total){
        total=_total;
        int fraction = Math.round(_total/10);
        PRINT_AT = (fraction>1&&fraction<PRINT_AT) ? fraction : PRINT_AT;
        refresh();
        ArrayList<ThreadReadBarcode> toDelete = new ArrayList<>();
        while(runningThreads>=totalThreads){
            for(ThreadReadBarcode runningThread:list){
                refresh();
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {}
                    else if (runningThread.hasError()) { error("Thread error "+runningThread.path, runningThread.getException());}
                    else { error("Thread error dont know why "+runningThread.path, new Exception());}
                    toDelete.add(runningThread);runningThreads--;doneThreads++;
                }
            }
        }
        for(ThreadReadBarcode done:toDelete){
            list.remove(done);
            if(done.isCompletedSuccessfully()){
                barcodeRead.add(done.path);
            }else{
                toRetry.add(done);
            }
        }
        thread.start();
        list.add(thread);
        runningThreads++;
    }

    private static void refresh(){
        if((doneThreads - printed)>PRINT_AT){ progress(doneThreads, total);printed += PRINT_AT;printStepDone();}
        if(doneThreads==0&&printed==0){ progress(doneThreads, total);printed=1;}
        if(doneThreads==total&&!donePrinting){ progress(1, 1);donePrinting=true;printStepEnd();}
    }

    public static void waitRunning(){ refresh();
        ArrayList<ThreadReadBarcode> toDelete = new ArrayList<>();
        while(runningThreads>0){
            for(ThreadReadBarcode runningThread:list){
                refresh();
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {
                    } else if (runningThread.hasError()) { error("Thread error "+runningThread.path, runningThread.getException());
                    } else { error("Thread error dont know why "+runningThread.path, new Exception());}
                    toDelete.add(runningThread);runningThreads--;doneThreads++;
                }
            }
        }
        for(ThreadReadBarcode done:toDelete){
            list.remove(done);
            if(done.isCompletedSuccessfully()){
                barcodeRead.add(done.path);
            }else{
                toRetry.add(done);
            }
        }
        refresh();
    }

    private static void progress(int count, int total) {
        double percent = round( (double)count/total, 2);
        String line = strPad("Legge i barcode", 20, " ");
        String progress = strPad("", 10 - (int)round(percent * 10.0D, 2), "_");
        line = line + ": " + strPad(progress, 10, "#");
        line = line + " (" + round(percent * 100.0D, 2) + "%)";
        print(line);
    }

    private static void printStepDone(){ printStep("bcr_"+step);}

    private static void printStepEnd(){ printStep("bcr_end");}

    private static void printStep(String filename){
        File stepControlFile = new File(stepControl, filename);
        String output = stepControlFile.getPath();
        mkdir(output);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output,true))) {
            for(String path:barcodeRead){ writer.append(path+"\n");}
            barcodeRead = new ArrayList<>();
            step++;
        } catch (Exception e) {
            error("progress",e);
        }
    }

    public static void progressEnd(String text) {
        print(strPad(text, 20, " ") + ": ########## (100.0%)");
    }

}