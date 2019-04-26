package FileSink;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class DiskFileSinkTest {

    @Test
    void writeCase() throws InterruptedException, IOException {
        FileSink fileSink = DiskFileSink.instance();
        for (int i = 1; i <= 1000; i ++) {
            fileSink.write("log record " + i);
        }
        Thread.sleep(35000);
    }
}