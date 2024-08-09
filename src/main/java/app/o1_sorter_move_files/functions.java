package app.o1_sorter_move_files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.HashMap;

import app.objGlobals;

public class functions {
    public static String doing;
    public static String current;
    public static String totalThreadsString;
    public static int totalThreads;
    public static String sourceGray;
    public static String sourceTiff;
    public static String targetGray;
    public static String targetTiff;
    public static String logFolder;
    public static String anomalyFile;
    public static String errorLog;
    public static String allEtichette;
    public static String allJobSorter;

    public static void variables()throws Exception{
        targetGray=objGlobals.gray;
        targetTiff=objGlobals.tiff;
        logFolder=logFolder();
    }

    public static String logFolder() throws Exception{
        File file = new File(objGlobals.workingFolder,"_LOGS");
        logFolder=file.getAbsolutePath();
        Path logPath = Paths.get(logFolder);
        if(!Files.exists(logPath)){
            Files.createDirectories(logPath);
            Files.setAttribute(logPath, "dos:hidden", true);
        }
        else{
            Files.setAttribute(logPath, "dos:hidden", true);
        }
        return logFolder;
    }

    public static void mkdir(String strPath) throws Exception{
        Path path = Paths.get(strPath);
        if(!Files.isDirectory(path)){
            File file = new File(strPath);
            String parent = file.getParent();
            path = Paths.get(parent);
        }
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
    }

    public static void error(String text, Exception e) throws Exception{
        File fileLog = new File(errorLog);
        File parent = new File(fileLog.getParent());
        if(!parent.exists()){ mkdir(parent.toString());}
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorLog,true))) {
            writer.append("----------------------------------------------------------------------------------------------------------------------------------\n");
            writer.append(text+"\n");
            writer.append(e.toString()+"\n");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            writer.append(stackTrace+"\n");
            writer.append("----------------------------------------------------------------------------------------------------------------------------------\n");
        } catch (Exception ee) { System.out.println("error log" + ee);}
    }

    public static String strPad(String inputString, int length, String append) {
        if (inputString.length() >= length) {
            return inputString;
        } else {
            StringBuilder sb = new StringBuilder();
            while(sb.length() < length - inputString.length()) {
                sb.append(append);
            }
            sb.append(inputString);
            return sb.toString();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }    

    public static HashMap<String,String> map() throws Exception{
        Path sourceTiffPath = Paths.get(sourceTiff);
        String strSourceTiff = sourceTiffPath.toString();
        Path targetTiffPath = Paths.get(targetTiff);
        String strTargetTiff = targetTiffPath.toString();
        HashMap<String,String> ret = new HashMap<>();
        Files.walkFileTree(sourceTiffPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String from = path.toString();
                String to = from.replace(strSourceTiff, strTargetTiff);
                File fileTo = new File(to);
                if(!fileTo.exists()){
                    ret.put(from, to);                  
                }
                return FileVisitResult.CONTINUE;
            }
        });

        Path sourceGrayPath = Paths.get(sourceGray);
        String strSourceGray = sourceGrayPath.toString();
        Path targetGrayPath = Paths.get(targetGray);
        String strTargetGray = targetGrayPath.toString();
        Files.walkFileTree(sourceGrayPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String from = path.toString();
                String to = from.replace(strSourceGray, strTargetGray);
                File fileTo = new File(to);
                if(!fileTo.exists()){
                    ret.put(from, to);                     
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return ret;
    }

    public static HashMap<String,String> mapCheck() throws Exception{
        Path sourceTiffPath = Paths.get(sourceTiff);
        String strSourceTiff = sourceTiffPath.toString();
        Path targetTiffPath = Paths.get(targetTiff);
        String strTargetTiff = targetTiffPath.toString();
        HashMap<String,String> ret = new HashMap<>();
        Files.walkFileTree(sourceTiffPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String from = path.toString();
                String to = from.replace(strSourceTiff, strTargetTiff);
                ret.put(from, to);
                return FileVisitResult.CONTINUE;
            }
        });

        Path sourceGrayPath = Paths.get(sourceGray);
        String strSourceGray = sourceGrayPath.toString();
        Path targetGrayPath = Paths.get(targetGray);
        String strTargetGray = targetGrayPath.toString();
        Files.walkFileTree(sourceGrayPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                String from = path.toString();
                String to = from.replace(strSourceGray, strTargetGray);
                ret.put(from, to);
                return FileVisitResult.CONTINUE;
            }
        });

        return ret;
    }

    public static long folderSize(String directoryPath) throws Exception {
        Path path = Paths.get(directoryPath);
        final long[] size = {0};
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });
        return size[0];
    }

    public static String calculateChecksum(String file) throws Exception{
        Path path = Paths.get(file);
        StringBuilder sb = new StringBuilder();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(path);
        byte[] checksum = digest.digest(bytes);
        for (byte b : checksum) { sb.append(String.format("%02x", b));}
        return sb.toString();
    }
}
