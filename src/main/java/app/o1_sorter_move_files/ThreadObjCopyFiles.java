package app.o1_sorter_move_files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static app.functions.printError;

public class ThreadObjCopyFiles extends functions{
    public void start(String from, String to) {
        try {
            File fileTarget = new File(to);
            if (!fileTarget.exists()) {
                File parentFileTarget = new File(fileTarget.getParent());
                if (!parentFileTarget.exists()) {
                    Files.createDirectories(parentFileTarget.toPath());
                }
                copyNio(from,to);
            } 
        } catch (IOException e) {printError(e);}
    }
    
    public void copyNio(String from, String to) {
        try {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            printError("filed to copy: "+from, e);
        }
    }

}
