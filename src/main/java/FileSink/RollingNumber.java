package FileSink;

public class RollingNumber {
    private final int startValue;
    private final int maxValue;
    private int currentValue;

    public RollingNumber(int maxValue) {
        this(1, maxValue);
    }

    public RollingNumber(int startValue, int maxValue) {
        this.currentValue = startValue;
        this.startValue = startValue;
        this.maxValue = maxValue;
    }

    public int currentValue() {
        return currentValue;
    }

    public int increment() {
        if (currentValue < maxValue) {
            currentValue++;
        } else {
            currentValue = startValue;
        }
        return currentValue;
    }
}
