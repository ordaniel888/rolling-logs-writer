package FileSink;

public class RollingNumber {
    private int value;
    private int startValue;
    private int maxValue;

    public RollingNumber(int maxValue) {
        this(1, maxValue);
    }

    public RollingNumber(int startValue, int maxValue) {
        this.value = startValue;
        this.startValue = startValue;
        this.maxValue = maxValue;
    }

    public int get() {
        return value;
    }

    public int increment() {
        if (value < maxValue) {
            value ++;
        } else {
            value = startValue;
        }
        return value;
    }
}
