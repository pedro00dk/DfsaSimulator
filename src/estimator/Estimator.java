package estimator;

/**
 * Interface for estimator implementations.
 */
public interface Estimator {

    /**
     * Returns the estimator getName.
     *
     * @return the estimator getName
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Returns the estimator type, if is {@link Type#SIMPLE_DFSA} the {@link #nextFrameSize(int, int, int)} is called
     * in the end of each frame, otherwise ({@link Type#COMPLEX_DFSA}) the {@link #nextFrameSize(int, int, int)} is
     * called after each frame block.
     *
     * @return the estimator type
     */
    Type getType();

    /**
     * Gets the initial frame size
     *
     * @return the initial frame size
     */
    int initialFrameSize();

    /**
     * Calculates the next frame size based on the received frame ({@link Type#SIMPLE_DFSA}) or block
     * ({@link Type#COMPLEX_DFSA}) information.
     *
     * @param idle      number of idle slots
     * @param success   number of success slots
     * @param collision number of collision slots
     * @return the new frame size
     */
    int nextFrameSize(int idle, int success, int collision);

    /**
     * Returns a copy of the estimator, is used to do simulations in parallel.
     *
     * @return a copy of the estimator
     */
    Estimator copy();

    @Override
    String toString();

    /**
     * The types of the estimators, is used in the simulator.
     */
    enum Type {

        /**
         * Represents simple DFSA estimators like Lower Bound and Eom-Lee, the {@link #nextFrameSize(int, int, int)} is
         * called after each frame.
         */
        SIMPLE_DFSA,

        /**
         * Represents more complex DFSA estimators like Q algorithm, the {@link #nextFrameSize(int, int, int)} is
         * called after each block.
         */
        COMPLEX_DFSA
    }
}
