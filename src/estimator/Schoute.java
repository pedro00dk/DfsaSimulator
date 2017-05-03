package estimator;

/**
 * Schoute estimator implementation.
 */
public class Schoute implements Estimator {

    /**
     * The estimator initial frame size.
     */
    private final int initialFrameSize;

    /**
     * Initializes the Schoute estimator with the received initial frame size.
     *
     * @param initialFrameSize the initial frame size
     */
    public Schoute(int initialFrameSize) {
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
        return (int) Math.ceil(2.39f * collision);
    }

    @Override
    public Estimator copy() {
        return new Schoute(initialFrameSize);
    }
}
