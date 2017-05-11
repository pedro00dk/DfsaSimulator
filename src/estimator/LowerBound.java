package estimator;

/**
 * Lower bound estimator implementation.
 *
 * @author Pedro Henrique
 */
public class LowerBound implements Estimator {

    /**
     * The estimator initial frame size.
     */
    private final int initialFrameSize;

    /**
     * Initializes the lower bound with the received initial frame size.
     *
     * @param initialFrameSize the initial frame size
     */
    public LowerBound(int initialFrameSize) {
        this.initialFrameSize = initialFrameSize;
    }

    @Override
    public String toString() {
        return getName() + " i=" + initialFrameSize;
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
        return 2 * collision;
    }

    @Override
    public Estimator copy() {
        return new LowerBound(initialFrameSize);
    }
}
