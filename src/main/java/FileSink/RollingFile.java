package FileSink;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RollingFile {

    private final String filePath;
    private final int filesMaxFileSizeBytes;
    private FileOutputStream currFile;
    private RollingNumber rollingNumber;

    public RollingFile(String directoryPath, String fileName, int maxFileSizeBytes, int rollingFilesNumber) throws IOException {
        this.filePath = directoryPath + fileName;
        this.filesMaxFileSizeBytes = maxFileSizeBytes;
        this.rollingNumber = new RollingNumber(rollingFilesNumber);
        this.currFile = createNewFile();
    }

    private long size() throws IOException {
        return Files.size(getFilePathByNumber(rollingNumber.currentValue()));
    }

    private void roll() throws IOException {
        this.currFile.close();
        rollingNumber.increment();

        currFile = createNewFile();
    }

    private FileOutputStream createNewFile() throws FileNotFoundException {
        String filePath = getFilePathByNumber(rollingNumber.currentValue()).toString();
        return new FileOutputStream(filePath);
    }

    private Path getFilePathByNumber(int fileNumber) {
        return Paths.get(String.format(filePath, fileNumber));
    }

    public FileOutputStream get() throws IOException {
        if (size() > filesMaxFileSizeBytes) {
            roll();
        }

        return currFile;
    }
}
