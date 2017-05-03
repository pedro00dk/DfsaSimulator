package estimator;

/**
 * Q estimator implementation.
 */
public class QAlgorithm implements Estimator {

    /**
     * The initial q param, this param too defines the initial frame size.
     */
    private final float q;

    /**
     * The q fluctuation parameter.
     */
    private final float c;

    /**
     * The current q value.
     */
    private float currentQ;

    /**
     * Initializes the Q with the received Q and C parameters.
     *
     * @param q the q parameter (floating point)
     * @param c the c parameter
     */
    public QAlgorithm(float q, float c) {
        this.q = q;
        this.c = c;
        currentQ = q;
    }

    @Override
    public String toString() {
        return getName() + " q=" + q + " c=" + c;
    }

    @Override
    public Type getType() {
        return Type.COMPLEX_DFSA;
    }

    @Override
    public int initialFrameSize() {
        return (int) Math.pow(2, q);
    }

    @Override
    public int nextFrameSize(int idle, int success, int collision) {
        return (int) Math.pow(2, Math.round(currentQ = (idle > 0) ? Math.max(currentQ - c, 0) : (collision > 0) ? Math.min(currentQ + c, 15) : currentQ));
    }

    @Override
    public Estimator copy() {
        return new QAlgorithm(q, c);
    }
}
