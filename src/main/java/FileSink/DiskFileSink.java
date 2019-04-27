package FileSink;

import Configuration.Configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class DiskFileSink implements FileSink{

    private RollingFile rollingFile;
    private final ExecutorService writingExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<String> messagesQueue = new LinkedBlockingQueue<>();

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
        rollingFile = new RollingFile(directoryPath, fileName, maxFileSizeBytes, rollingFilesNumber);
        startConsuming();
    }

    private void startConsuming() {
        writingExecutor.execute(() -> {
            while (true) {
                try {
                    String logMessage = messagesQueue.take();
                    this.writeToFile(logMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void writeToFile(String logMessage) throws IOException {

        FileOutputStream outputStream = this.rollingFile.get();
        logMessage = logMessage + System.lineSeparator();
        byte[] strToBytes = logMessage.getBytes();
        outputStream.write(strToBytes);
    }

    @Override
    public void write(String logMessage) {
        CompletableFuture.runAsync(() -> messagesQueue.add(logMessage));
    }
}
