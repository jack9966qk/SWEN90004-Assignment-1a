/**
 * Created by Jack on 11/3/2017.
 */
public class Robot extends BicycleHandlingThread {
    protected Bicycle bicycle = null;
    protected BicycleContainer dest = null;
    protected int destIndex = -1;

    public String toString() {
        return "Robot: [ " + (bicycle != null ? bicycle.toString() : "") + " ]";
    }

    public synchronized void pickBicycle(BicycleContainer from, int fromIndex, BicycleContainer to, int toIndex)
        throws InterruptedException {
        while (dest != null) {
            wait();
        }
        bicycle = from.remove(fromIndex);
        dest = to;
        destIndex = toIndex;
        notifyAll();
    }

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

                notifyAll();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
