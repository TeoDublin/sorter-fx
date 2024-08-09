package app.o1_sorter_move_files;

import java.util.ArrayList;

public class Threads extends functions{
    private static int runningThreads=0;
    private static final ArrayList<ThreaCopyFiles> list = new ArrayList<>();

    public static void start(ThreaCopyFiles thread) throws Exception{ 
        ArrayList<ThreaCopyFiles> toDelete = new ArrayList<>();
        while(runningThreads>=totalThreads){
            for(ThreaCopyFiles runningThread:list){
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) {} 
                    else if (runningThread.hasError()) { error("Thread error "+runningThread.from, runningThread.getException());} 
                    else { error("Thread error dont know why "+runningThread.from, new Exception());}
                    toDelete.add(runningThread);runningThreads--;break;
                }
            }
        }
        for(ThreaCopyFiles done:toDelete){ list.remove(done);}
        thread.start();
        list.add(thread);
        runningThreads++;
    }

    public static void waitRunning() throws Exception{
        ArrayList<ThreaCopyFiles> toDelete = new ArrayList<>();
        while(runningThreads>0){
            for(ThreaCopyFiles runningThread:list){
                if(!runningThread.isRunning()&&!toDelete.contains(runningThread)){
                    if (runningThread.isCompletedSuccessfully()) { 
                    } else if (runningThread.hasError()) { error("Thread error "+runningThread.from, runningThread.getException());
                    } else { error("Thread error dont know why "+runningThread.from, new Exception());}
                    toDelete.add(runningThread);runningThreads--;
                }
            }
        }
        for(ThreaCopyFiles done:toDelete){ list.remove(done);}
    }
    
}