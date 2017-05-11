package simulator;

import estimator.Estimator;

/**
 * Class to test the simulators.
 *
 * @author Pedro Henrique
 */
public class Simulator {

    /**
     * Prevents instantiation.
     */
    private Simulator() {
    }

    /**
     * Simulates an estimator with the received tag count.
     *
     * @param estimator the estimator to test
     * @param tagCount  the number of tags
     * @return a {@link SimulationResult} with the simulation data
     */
    public static SimulationResult simulate(Estimator estimator, int tagCount) {

        // Starting the simulator execution time
        long executionStartTime = System.nanoTime();

        // Creating tags
        Tag[] tags = new Tag[tagCount];
        for (int i = 0; i < tags.length; i++) {
            (tags[i] = new Tag()).readerStarting();
        }

        // Creating the simulation result, it will save the execution information
        SimulationResult simulationResult = new SimulationResult();
        simulationResult.estimator = estimator;
        estimator.setSimulationResult(simulationResult);
        simulationResult.tagCount = tagCount;

        // Setting the first frame size
        int currentFrameSize = estimator.initialFrameSize();

        boolean allTagsRead = false; // Indicates when all tags are read
        while (!allTagsRead) { // Frame loop
            simulationResult.createdFrames++;

            switch (estimator.getType()) {
                case SIMPLE_DFSA:

                    // Crating the frame
                    int[] frame = new int[currentFrameSize];
                    Tag[] frameTags = new Tag[currentFrameSize];

                    simulationResult.createdSlots += currentFrameSize;

                    // Getting the slot positions of all tags
                    for (Tag tag : tags) {
                        int slot = tag.calculateAndGetSlot(currentFrameSize);
                        if (slot == -1) { // Silenced tags respond -1
                            continue;
                        }
                        frame[slot]++;
                        frameTags[slot] = tag;
                    }

                    // Counting idle, success and collision slots
                    int frameIdleSlots = 0;
                    int frameSuccessSlots = 0;
                    int frameCollisionSlots = 0;
                    for (int i = 0; i < currentFrameSize; i++) {
                        switch (frame[i]) {
                            case 0:
                                frameIdleSlots++;
                                break;
                            case 1:
                                frameSuccessSlots++;
                                frameTags[i].communicationSuccessful(); // Silences the tag if success
                                break;
                            default:
                                frameCollisionSlots++;
                                break;
                        }
                    }
                    simulationResult.idleSlots += frameIdleSlots;
                    simulationResult.successSlots += frameSuccessSlots;
                    simulationResult.collisionSlots += frameCollisionSlots;

                    // Calculating next frame size
                    currentFrameSize = estimator.nextFrameSize(frameIdleSlots, frameSuccessSlots, frameCollisionSlots);

                    // Stops if the frame size is 0
                    if (currentFrameSize == 0) {
                        allTagsRead = true;
                    }
                    break;
                case COMPLEX_DFSA:

                    // Stop conditions
                    boolean frameSizeChanged = true;
                    boolean collisionsInFrame = false;

                    // Iterates over all slots in the frame
                    int tagsInSlot = 0;
                    Tag lastTag = null;
                    for (int i = 0; i < currentFrameSize; i++) {
                        simulationResult.createdSlots++;
                        for (Tag tag : tags) {
                            if (frameSizeChanged ? tag.queryAdjust(currentFrameSize) : tag.queryRep()) {
                                tagsInSlot++;
                                lastTag = tag;
                            }
                        }

                        // Checks what happened in the slot (idle, success, collision)
                        int slotIdle = tagsInSlot == 0 ? 1 : 0;
                        int slotSuccess = tagsInSlot == 1 ? 1 : 0;
                        int slotCollision = tagsInSlot >= 2 ? 1 : 0;

                        simulationResult.idleSlots += slotIdle;
                        simulationResult.successSlots += slotSuccess;
                        simulationResult.collisionSlots += slotCollision;

                        // If success, silences the tag
                        if (slotSuccess == 1) {
                            lastTag.communicationSuccessful();
                        }

                        // If collision, sets the collision flag
                        if (slotCollision == 1) {
                            collisionsInFrame = true;
                        }

                        // Stop condition
                        if (i == currentFrameSize - 1 && !collisionsInFrame) {
                            allTagsRead = true;
                        } else {
                            int nextFrameSize = estimator.nextFrameSize(slotIdle, slotSuccess, slotCollision);

                            // Checks if the frame size changes, if false, finishes the current frame
                            if (nextFrameSize == currentFrameSize) {
                                frameSizeChanged = false;
                                tagsInSlot = 0;
                                lastTag = null;
                            } else {
                                currentFrameSize = nextFrameSize;
                                break;
                            }
                        }
                    }
                    break;
            }
        }

        // Calculates the execution end time
        long executionEndTime = System.nanoTime();
        simulationResult.executionTime = (executionEndTime - executionStartTime) / 1e6f;
        return simulationResult;
    }
}
