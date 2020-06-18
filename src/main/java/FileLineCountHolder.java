import java.nio.file.Path;
import java.util.Map;

public class FileLineCountHolder {
    private Path dir;
    private long fileCount;
    private Map<Path, Integer> fileToLineCount;

    public Path getDir() {
        return dir;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

    public Map<Path, Integer> getFileToLineCount() {
        return fileToLineCount;
    }

    public void setFileToLineCount(Map<Path, Integer> fileToLineCount) {
        this.fileToLineCount = fileToLineCount;
    }


}
