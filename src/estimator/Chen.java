package estimator;

/**
 * Chen estimator implementation.
 */
public class Chen implements Estimator {

    /**
     * The estimator initial frame size.
     */
    private final int initialFrameSize;

    /**
     * Initializes the Chen with the received initial frame size.
     *
     * @param initialFrameSize the initial frame size
     */
    public Chen(int initialFrameSize) {
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
        int l = idle + success + collision;
        int n = success + 2 * collision;
        float next = 0;
        float previous = -1;
        while (previous < next) {
            float pIdle = (float) Math.pow((1 - (1 / l)), n);
            float pSuccess = (n / l) * (float) Math.pow((1 - (1 / l)), n);
            float pCollision = 1 - pIdle - pSuccess;
            previous = next;
            next = (f(l) / (f(idle) * f(success) * f(collision))) * (float) (Math.pow(pIdle, idle) * Math.pow(pSuccess, success) * Math.pow(pCollision, collision));
            n += 1;
        }
        return n - 2;
    }

    private int f(int value) {
        int factorial = 2;
        for (int i = 3; i <= value; i++) {
            factorial *= value;
        }
        return factorial;
    }

    @Override
    public Estimator copy() {
        return new Chen(initialFrameSize);
    }
}
