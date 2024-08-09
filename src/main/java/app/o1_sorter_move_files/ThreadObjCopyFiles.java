package app.o1_sorter_move_files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ThreadObjCopyFiles extends functions{
    public void start(String from, String to) throws Exception{
        File fileTarget = new File(to);
        if (!fileTarget.exists()) {
            File parentFileTarget = new File(fileTarget.getParent());
            if (!parentFileTarget.exists()) {
                Files.createDirectories(parentFileTarget.toPath());
            }
            copyNio(from,to);
        } 
    }

    public void copyBuffered(String from, String to) throws Exception{
        try(BufferedReader reader = new BufferedReader(new FileReader(new File(from))); BufferedWriter writer = new BufferedWriter(new FileWriter(new File(to)))){
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }catch(IOException e){throw e;}
    }

    public void copyChannel(String from, String to) throws Exception{
        try (FileInputStream fis = new FileInputStream(from);
            FileOutputStream fos = new FileOutputStream(to);
            FileChannel sourceChannel = fis.getChannel();
            FileChannel destChannel = fos.getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            throw e;
        }
    }

    public void copyNio(String from, String to) throws Exception{
        Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
    }   
    
}
