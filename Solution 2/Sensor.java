/**
 * The sensor that scans bicycles, determining if a bicycle needs inspection
 * Created by Jack on 11/3/2017.
 */
public class Sensor {

    // The robot arm to call when a bicycle needs to be moved
    protected Robot robot;

    // The destination where a bicycle is going to be moved to
    protected BicycleContainer dest;

    // The destination index where a bicycle is going to be moved to
    protected int destIndex = -1;

    /**
     * Create a new sensor object
     * @param dest
     *            The destination where a bicycle is going to be moved to
     * @param destIndex
     *            The destination index where a bicycle is going to be moved to
     * @param robot
     *            The robot arm to call when a bicycle needs to be moved
     */
    public Sensor(BicycleContainer dest, int destIndex, Robot robot) {
        this.robot = robot;
        this.dest = dest;
        this.destIndex = destIndex;
    }


    /**
     * Determine if the specified bicycle needs to be moved away,
     * call the robot arm to pick the bicycle if needed
     * @param container
     *            The container where bicycle is to be scanned
     * @param index
     *            The index of container where bicycle is to be scanned
     * @throws InterruptedException
     *            if the thread executing is interrupted
     */
    public void handleBicycle(BicycleContainer container, int index)
        throws InterruptedException {
        Bicycle bicycle = container.peek(index);
        if (bicycle == null) {
            return;
        }
        if (bicycle.isTagged() && !bicycle.isInspected()) {
            robot.pickBicycle(container, index, dest, destIndex);
        }
    }
}
