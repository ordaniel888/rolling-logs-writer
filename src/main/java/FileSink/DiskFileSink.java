package FileSink;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import Configuration.Configuration;

public class DiskFileSink implements FileSink{

    private final String filePath;
    private final ExecutorService writingExecutor;
    private final LinkedBlockingQueue<String> messagesQueue;
    private final int rollingFilesNumber;
    private final int filesMaxFileSizeBytes;

    private Optional<FileOutputStream> currLogFile;
    private RollingNumber currFileNumber;

    private static DiskFileSink instance;

    public static DiskFileSink instance() {
        if (instance == null) {
            synchronized (FileSink.class) {
                if (instance == null) {
                    return new DiskFileSink(Configuration.DIR_PATH,
                            Configuration.FILE_NAME,
                            Configuration.MAX_FILE_SIZE_BYTES,
                            Configuration.ROLLING_FILES_NUMBER);
                }
            }
        }
        return instance;
    }

    private DiskFileSink(String directoryPath, String fileName, int maxFileSizeBytes, int rollingFilesNumber) {
        this.filePath = directoryPath + fileName;
        this.filesMaxFileSizeBytes = maxFileSizeBytes;
        this.rollingFilesNumber = rollingFilesNumber;
        this.currFileNumber = new RollingNumber(rollingFilesNumber);
        this.writingExecutor = Executors.newSingleThreadExecutor();
        this.messagesQueue = new LinkedBlockingQueue<>();
        this.currLogFile = Optional.empty();
        this.startConsuming();
    }

    private void startConsuming() {
        CompletableFuture.runAsync(() -> {

            while (true) {
                try {
                    String logMessage = messagesQueue.take();
                    this.writeToFile(logMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, writingExecutor);
    }

    private void writeToFile(String logMessage) throws IOException {

        FileOutputStream outputStream = getCurrentLogFile();
        logMessage = logMessage + System.lineSeparator();
        byte[] strToBytes = logMessage.getBytes();
        outputStream.write(strToBytes);
    }

    public void write(String logMessage) {
        CompletableFuture.supplyAsync(() -> messagesQueue.add(logMessage));
    }

    public FileOutputStream getCurrentLogFile() throws IOException {
        if (!currLogFile.isPresent()) {
            this.currLogFile = createNewFile();
        } else if (new File(this.filePath).length() >= this.filesMaxFileSizeBytes){
            this.currLogFile.get().close();
            this.currLogFile = createNewFile();
        }

        return this.currLogFile.get();
    }

    private Optional<FileOutputStream> createNewFile() throws FileNotFoundException {
        //Get the file reference
        String currLogFile = String.format(filePath, currFileNumber.getAndIncrement());
        Path path = Paths.get(currLogFile);
        return Optional.of(new FileOutputStream(path.toString()));
    }
}
