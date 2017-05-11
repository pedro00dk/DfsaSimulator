package estimator;

import simulator.SimulationResult;

/**
 * Chen estimator implementation.
 *
 * @author Pedro Henrique
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

        // If has no collisions return 0
        if (collision == 0) {
            return 0;
        }

        double i = idle;
        double s = success;
        double c = collision;

        double l = i + s + c;
        double n = s + c * 2;

        double next = 0;
        double previous = -1;

        //
        double not1_LpowN1 = Math.pow(1 - 1 / l, n - 1);
        double not1_LpowN = not1_LpowN1 * (1 - 1 / l);

        double fatL_fatIfatSfatC = MathUtils.factAndDiv(l, i, s, c);

        while (previous < next) {
            simulationResult.iterations++;

            double pI = not1_LpowN;
            double pS = (n / l) * not1_LpowN1;
            double pC = 1 - pI - pS;

            not1_LpowN1 = not1_LpowN;
            not1_LpowN *= (1 - 1 / l);

            previous = next;
            n++;

            next = fatL_fatIfatSfatC * Math.pow(pI, i) * Math.pow(pS, s) * Math.pow(pC, c);
        }
        int nextFrameSize = (int) (n - 2d) - success;
        return nextFrameSize > 0 ? nextFrameSize : 2;
    }

    /**
     * Saves the simulation result internally to count the number of iterations.
     */
    private SimulationResult simulationResult;

    @Override
    public void setSimulationResult(SimulationResult simulationResult) {
        this.simulationResult = simulationResult;
    }

    @Override
    public Estimator copy() {
        return new Chen(initialFrameSize);
    }
}
