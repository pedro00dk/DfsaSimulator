package simulator;

import java.util.Random;

/**
 * Represents generic tags that can be used as type 1 or 3 tags.
 */
public class Tag {

    /**
     * The current slot of the tag (used in type 3 methods).
     */
    private int slot;

    /**
     * Indicates if the tag is silenced.
     */
    private boolean silenced;

    /**
     * The tag unique prng.
     */
    private Random prng;

    /**
     * Initializes the tag silenced.
     */
    public Tag() {
        prng = new Random();
        silenced = true;
        slot = -1;
    }

    // Starting and finishing methods

    /**
     * Indicates to the tag that a reader is starting a communication. This method let the tag respond to the reader.
     * The internal silence state is set to false.
     */
    public void readerStarting() {
        silenced = false;
    }

    /**
     * Indicates to the data that the reader could understand the tag response, the tag will not more respond. The
     * internal silence state is set to true.
     */
    public void communicationSuccessful() {
        silenced = true;
    }

    // Type 1 communication methods

    /**
     * Requires to the thread the current slot number, each time this method is called, a new slot number is calculated.
     * If the thread is silenced -1 is returned.
     * <p>
     * This is a tag type 1 method (should not be used with tag type 3 methods).
     *
     * @param frameSize the size of the reader frame
     * @return the slot number, between 0 and frameSize (exclusive)
     */
    public int calculateAndGetSlot(int frameSize) {
        return !silenced ? prng.nextInt(frameSize) : -1;
    }

    // Type 3 communication methods

    /**
     * Sets in this tag the reader frame size, this tag will calculate a random number between 0 and the frame size
     * (exclusive) and will save internally. If the calculated value is 0, true is returned, false otherwise. If the tag
     * is silenced, false is returned.
     *
     * @param frameSize the size of the reader frame
     * @return true if the current slot number is 0
     */
    public boolean queryAdjust(int frameSize) {
        return !silenced && (slot = prng.nextInt(frameSize)) == 0;
    }

    /**
     * Indicates to the tag to decrement 1 from the slot number,if the current slot number value is 0, true is returned,
     * false otherwise. If the tag is silenced, false is returned.
     *
     * @return true if the current slot number is 0
     */
    public boolean queryRep() {
        return !silenced && --slot == 0;
    }
}
