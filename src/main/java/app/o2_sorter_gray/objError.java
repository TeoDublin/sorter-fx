package app.o2_sorter_gray;

import java.io.File;

public class objError {
    public String path;
    public boolean moveFile;

    public objError(String baseDir, String folder, boolean moveFile) {
        File pathFile = new File(baseDir,folder);
        this.path = pathFile.getPath();
        this.moveFile = moveFile;
    }
}
