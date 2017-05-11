package simulator;

import estimator.Estimator;

import java.util.List;

/**
 * Stores the simulations information.
 *
 * @author Pedro Henrique
 */
public class SimulationResult {

    /**
     * The estimator used in the simulation.
     */
    public Estimator estimator;

    /**
     * The number of tags used in the simulation.
     */
    public int tagCount;

    //

    /**
     * The number of created frames.
     */
    public int createdFrames;

    /**
     * The number of created slots.
     */
    public int createdSlots;

    /**
     * The number of idle slots.
     */
    public int idleSlots;

    /**
     * The number of success slots.
     */
    public int successSlots;

    /**
     * The number of collision slots.
     */
    public int collisionSlots;

    /**
     * The number of internal iterations of the complete simulation (not only frame size calculation) of the estimator.
     * Some estimators does not contains this info.
     */
    public int iterations;

    //

    /**
     * The simulation (not estimator) execution time in milliseconds.
     */
    public float executionTime;

    /**
     * Calculates the average result of the received simulations, this method does not check if the simulations uses the
     * same {@link Estimator} and contains the same tag count, this data is get from the first simulation. At least one
     * simulation result is expected, otherwise {@link NullPointerException} is throw.
     *
     * @param simulationResults the list of simulation results
     * @return the average result
     */
    public static SimulationResult average(List<SimulationResult> simulationResults) {
        SimulationResult average = new SimulationResult();
        average.estimator = simulationResults.get(0).estimator;
        average.tagCount = simulationResults.get(0).tagCount;

        for (SimulationResult simulationResult : simulationResults) {
            average.createdFrames += simulationResult.createdFrames;
            average.createdSlots += simulationResult.createdSlots;
            average.idleSlots += simulationResult.idleSlots;
            average.successSlots += simulationResult.successSlots;
            average.collisionSlots += simulationResult.collisionSlots;
            average.executionTime += simulationResult.executionTime;
            average.iterations += simulationResult.iterations;
        }
        average.createdFrames /= simulationResults.size();
        average.createdSlots /= simulationResults.size();
        average.idleSlots /= simulationResults.size();
        average.successSlots /= simulationResults.size();
        average.collisionSlots /= simulationResults.size();
        average.executionTime /= simulationResults.size();
        average.iterations /= simulationResults.size();

        return average;
    }

    /**
     * Calculates the min result of the received simulations, this method does not check if the simulations uses the
     * same {@link Estimator} and contains the same tag count, this data is get from the first simulation. At least one
     * simulation result is expected, otherwise {@link NullPointerException} is throw.
     *
     * @param simulationResults the list of simulation results
     * @return the min result
     */
    public static SimulationResult min(List<SimulationResult> simulationResults) {
        SimulationResult min = new SimulationResult();
        min.estimator = simulationResults.get(0).estimator;
        min.tagCount = simulationResults.get(0).tagCount;
        min.createdFrames = Integer.MAX_VALUE;
        min.createdSlots = Integer.MAX_VALUE;
        min.idleSlots = Integer.MAX_VALUE;
        min.successSlots = Integer.MAX_VALUE;
        min.collisionSlots = Integer.MAX_VALUE;
        min.executionTime = Integer.MAX_VALUE;
        min.iterations = Integer.MAX_VALUE;

        for (SimulationResult simulationResult : simulationResults) {
            min.createdFrames = min.createdFrames > simulationResult.createdFrames ? simulationResult.createdFrames : min.createdFrames;
            min.createdSlots = min.createdSlots > simulationResult.createdSlots ? simulationResult.createdSlots : min.createdSlots;
            min.idleSlots = min.idleSlots > simulationResult.idleSlots ? simulationResult.idleSlots : min.idleSlots;
            min.successSlots = min.successSlots > simulationResult.successSlots ? simulationResult.successSlots : min.successSlots;
            min.collisionSlots = min.collisionSlots > simulationResult.collisionSlots ? simulationResult.collisionSlots : min.collisionSlots;
            min.executionTime = min.executionTime > simulationResult.executionTime ? simulationResult.executionTime : min.executionTime;
            min.iterations = min.iterations > simulationResult.iterations ? simulationResult.iterations : min.iterations;
        }
        return min;
    }

    /**
     * Calculates the max result of the received simulations, this method does not check if the simulations uses the
     * same {@link Estimator} and contains the same tag count, this data is get from the first simulation. At least one
     * simulation result is expected, otherwise {@link NullPointerException} is throw.
     *
     * @param simulationResults the list of simulation results
     * @return the max result
     */
    public static SimulationResult max(List<SimulationResult> simulationResults) {
        SimulationResult max = new SimulationResult();
        max.estimator = simulationResults.get(0).estimator;
        max.tagCount = simulationResults.get(0).tagCount;
        max.createdFrames = Integer.MIN_VALUE;
        max.createdSlots = Integer.MIN_VALUE;
        max.idleSlots = Integer.MIN_VALUE;
        max.successSlots = Integer.MIN_VALUE;
        max.collisionSlots = Integer.MIN_VALUE;
        max.executionTime = Integer.MIN_VALUE;
        max.iterations = Integer.MIN_VALUE;

        for (SimulationResult simulationResult : simulationResults) {
            max.createdFrames = max.createdFrames < simulationResult.createdFrames ? simulationResult.createdFrames : max.createdFrames;
            max.createdSlots = max.createdSlots < simulationResult.createdSlots ? simulationResult.createdSlots : max.createdSlots;
            max.idleSlots = max.idleSlots < simulationResult.idleSlots ? simulationResult.idleSlots : max.idleSlots;
            max.successSlots = max.successSlots < simulationResult.successSlots ? simulationResult.successSlots : max.successSlots;
            max.collisionSlots = max.collisionSlots < simulationResult.collisionSlots ? simulationResult.collisionSlots : max.collisionSlots;
            max.executionTime = max.executionTime < simulationResult.executionTime ? simulationResult.executionTime : max.executionTime;
            max.iterations = max.iterations < simulationResult.iterations ? simulationResult.iterations : max.iterations;
        }
        return max;
    }
}
