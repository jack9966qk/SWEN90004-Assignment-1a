/**
 * Created by Jack on 11/3/2017.
 */
public interface BicycleContainer {

    /**
     * Put a bicycle to container
     * @param bicycle
     *            the bicycle to be put into container
     * @param index
     *            the index of container where bicycle is put
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public void put(Bicycle bicycle, int index) throws InterruptedException;

    /**
     * Remove a bicycle from container
     * @param index
     *            the index where bicycle is removed from container
     * @return    the removed bicycle
     * @throws InterruptedException
     *            if the thread executing is interrupted
     */
    public Bicycle remove(int index) throws InterruptedException;

    /**
     * Get the length (size) of container
     * @return the length of container
     */
    public int length();


    /**
     * Peek at what is in the container
     * @param index
     *            the index at which to peek
     * @return
     *            the bicycle at the index (or null if empty)
     */
    public Bicycle peek(int index);

}
