package FileSink;

public class RollingNumber {
    private int currentValue;
    private int startValue;
    private int maxValue;

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
