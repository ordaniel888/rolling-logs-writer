package FileSink;

import Configuration.Configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DiskFileSink implements FileSink{

    private RollingFile rollingFile;
    private final ExecutorService writingExecutor;
    private final LinkedBlockingQueue<String> messagesQueue;

    private static DiskFileSink instance;

    public static DiskFileSink instance() throws IOException {
        if (instance == null) {
            synchronized (FileSink.class) {
                if (instance == null) {
                    instance = new DiskFileSink(Configuration.DIR_PATH,
                            Configuration.FILE_NAME,
                            Configuration.MAX_FILE_SIZE_BYTES,
                            Configuration.ROLLING_FILES_NUMBER);
                }
            }
        }
        return instance;
    }

    private DiskFileSink(String directoryPath, String fileName, int maxFileSizeBytes, int rollingFilesNumber) throws IOException {
        this.rollingFile = new RollingFile(directoryPath, fileName, maxFileSizeBytes, rollingFilesNumber);
        this.writingExecutor = Executors.newSingleThreadExecutor();
        this.messagesQueue = new LinkedBlockingQueue<>();
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

        FileOutputStream outputStream = this.rollingFile.get();
        logMessage = logMessage + System.lineSeparator();
        byte[] strToBytes = logMessage.getBytes();
        outputStream.write(strToBytes);
    }

    public void write(String logMessage) {
        CompletableFuture.supplyAsync(() -> messagesQueue.add(logMessage));
    }
}
