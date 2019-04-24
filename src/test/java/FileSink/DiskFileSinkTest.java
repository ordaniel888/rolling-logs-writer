package FileSink;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiskFileSinkTest {

    @Test
    void writeCase() throws InterruptedException {
        FileSink fileSink = DiskFileSink.instance();
        fileSink.write("hello World!");
        fileSink.write("hello World!");
        fileSink.write("hello World!");
        fileSink.write("hello World!!!!");
        fileSink.write("hello World!");
        fileSink.write("hello World!");
        Thread.sleep(6000);
    }
}