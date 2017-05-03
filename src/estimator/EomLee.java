package estimator;

/**
 * Lower bound estimator implementation.
 */
public class EomLee implements Estimator {
    private final int initialFrameSize;

    /**
     * The Eom-Lee estimator threshold.
     */
    private float threshold;

    /**
     * Initializes the lower bound with the received initial frame size and threshold.
     *
     * @param initialFrameSize the initial frame size
     * @param threshold        the Eom-Lee algorithm threshold
     */
    public EomLee(int initialFrameSize, float threshold) {
        this.initialFrameSize = initialFrameSize;
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return getName() + " i=" + initialFrameSize + " t=" + threshold;
    }

    @Override
    public Type getType() {
        return Type.SIMPLE_DFSA;
    }

    @Override
    public int initialFrameSize() {
        return initialFrameSize;
    }

    @Override
    public int nextFrameSize(int idle, int success, int collision) {
        int l = idle + success + collision;
        float yk = 2;
        while (true) {
            float oldYk = yk;
            float bk = l / (oldYk * collision + success);
            float exp = (float) Math.exp(-1 / bk);
            yk = (1 - exp) / (bk * (1 - (1 + 1 / bk) * exp));
            if (Math.abs(oldYk - yk) < threshold) {
                break;
            }
        }
        return (int) Math.ceil(yk * collision);
    }

    @Override
    public Estimator copy() {
        return new EomLee(initialFrameSize, threshold);
    }
}
