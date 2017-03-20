/**
 * The inspector of bicycle, checks if bicycle is defective, removes tag if not
 * Created by Jack on 11/3/2017.
 */
public class Inspector extends BicycleHandlingThread implements BicycleContainer {

    // The bicycle in the inspector
    protected Bicycle bicycle = null;

    // The destination of inspected bicycle
    protected BicycleContainer dest = null;

    // The destination index of inspected bicycle
    protected int destIndex = -1;

    // The robot arm to use after inspection
    protected Robot robot;

    /**
     * Create a new inspector
     * @param dest      The destination of inspected bicycle
     * @param destIndex The destination index of inspected bicycle
     * @param robot     The robot arm to use after inspection
     */
    public Inspector(BicycleContainer dest, int destIndex, Robot robot) {
        this.dest = dest;
        this.destIndex = destIndex;
        this.robot = robot;
    }

    /**
     * Get the string representation of inspector
     * @return          String representation of the inspector
     */
    public String toString() {
        return "Inspector: [ " + (bicycle != null ? bicycle.toString() : "") + " ]";
    }

    /**
     *
     * @param bicycle
     *            the bicycle to be put into the inspector
     * @param index
     *            the index of inspector where bicycle is put,
     *            supports only 0 since inspector contains only one bicycle
     * @throws InterruptedException
     */
    @Override
    public synchronized void put(Bicycle bicycle, int index) throws InterruptedException {
        while (this.bicycle != null) {
            wait();
        }
        System.out.println(bicycle + " arrives at inspector");
        this.bicycle = bicycle;
        notifyAll();
    }

    /**
     *
     * @param index
     *            the index at which to peek,
     *            supports only 0 since inspector contains only one bicycle
     * @return
     *            the bicycle at the index (or null if empty)
     */
    @Override
    public Bicycle peek(int index) {
        return bicycle;
    }

    /**
     *
     * @param index
     *            the index where bicycle is removed from container,
     *            supports only 0 since inspector contains only one bicycle
     * @return    the removed bicycle
     * @throws InterruptedException
     *            if the thread executing is interrupted
     */
    @Override
    public synchronized Bicycle remove(int index) throws InterruptedException {
        while (this.bicycle == null || !this.bicycle.isInspected()) {
            wait();
        }
        System.out.println(bicycle + " left inspector");
        Bicycle bicycle = this.bicycle;
        this.bicycle = null;
        notifyAll();
        return bicycle;
    }

    /**
     * Gets the size of inspector
     * @return   size of the inspector
     */
    @Override
    public int length() {
        return 1;
    }

    /**
     * Do inspection on bicycle as soon as it arrives.
     * When finished, call robot arm to move the bicycle to destination,
     * and wait for the next bicycle to arrive
     */
    public synchronized void run() {
        while (!interrupted()) {
            try {
                // inspected bicycle should be removed already
                if (bicycle != null) {
                    assert (bicycle.isInspected());
                }
                // wait for new bicycle to arrive
                while (bicycle == null) {
                    wait();
                }

                // Start inspecting
                Debug.println("started inspecting " + bicycle.toString());
                sleep(Params.INSPECT_TIME);
                bicycle.setInspected(true);
                if (bicycle.isDefective()) {
                    bicycle.setTagged();
                } else {
                    bicycle.setNotTagged();
                }
                Debug.println("finished inspecting " + bicycle.toString());
                robot.pickBicycle(this, 0, dest, destIndex);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
