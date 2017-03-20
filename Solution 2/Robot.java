/**
 * The robot arm that is responsible for moving bicycle from one place to another
 * Created by Jack on 11/3/2017.
 */
public class Robot extends BicycleHandlingThread {

    // The bicycle that the robot currently holds
    protected Bicycle bicycle = null;

    // Destination that the bicycle is being moved to
    protected BicycleContainer dest = null;

    // Destination index that the bicycle is being moved to
    protected int destIndex = -1;

    /**
     * Get a string representation of the robot
     * @return    String representation of the robot
     */
    public String toString() {
        return "Robot: [ " + (bicycle != null ? bicycle.toString() : "") + " ]";
    }

    /**
     * Pick bicycle from one container, which is going to be moved to another container
     * @param from
     *            The container where bicycle is to be taken away
     * @param fromIndex
     *            The container index where bicycle is to be taken away
     * @param to
     *            The container where bicycle is to be moved to
     * @param toIndex
     *            The container index where bicycle is to be moved to
     * @throws InterruptedException
     *            if the thread executing is interrupted
     */
    public synchronized void pickBicycle(BicycleContainer from, int fromIndex, BicycleContainer to, int toIndex)
        throws InterruptedException {
        // wait until current job is finished
        while (dest != null) {
            wait();
        }

        // take bicycle and assign destination
        bicycle = from.remove(fromIndex);
        dest = to;
        destIndex = toIndex;

        // notify that there is a new task
        notifyAll();
    }

    /**
     * Wait until new bicycle moving command appears,
     * start moving when bicycle arrives at robot arm and destination is properly set,
     * wait for the next task when bicycle successfully arrives at destination
     */
    public synchronized void run() {
        while(!interrupted()) {
            try {
                while (dest == null) {
                    // wait for task
                    wait();
                }

                // start moving
                Debug.println("Robot started moving");
                sleep(Params.ROBOT_MOVE_TIME);

                // finish moving
                Debug.println("Moving finished, putting bicycle to " + dest.toString());
                Bicycle b = this.bicycle;
                dest.put(b, destIndex);
                this.bicycle = null;
                dest = null;
                destIndex = -1;

                // notify that current task is finished, available for new tasks
                notifyAll();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
