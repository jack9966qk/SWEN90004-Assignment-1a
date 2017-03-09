import java.util.Random;

/**
 * Created by Jack on 8/3/2017.
 */
public class Inspector extends BicycleHandlingThread {

    protected Bicycle bicycle;
    protected Robot robot;
    protected boolean inspected = false;

    public Inspector(Robot robot) {
        this.robot = robot;
    }

    public synchronized Bicycle getInspectedBicycle() throws InterruptedException {
        while (true) {
            // check if bicycle has arrived
            if (bicycle != null) {
                // check if inspection is finished on the bicycle
                if (inspected) {
                    break;
                }
            }
            wait();
        }

        Bicycle b = bicycle;
        bicycle = null;
        notify();
        return b;
    }

    public synchronized void putBicycle(Bicycle bicycle) throws InterruptedException {
        // wait until there's space for new bicycle
        while (this.bicycle != null) {
            wait();
        }

        this.bicycle = bicycle;
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                // wait for bicycle to come
                while (bicycle == null) {
                    wait();
                }

                inspected = false;
                synchronized (this) {
                    // inspect the bicycle, cannot be removed during the process
                    // TODO probably unnecessary as removal checks if inspected
                    Random random = new Random();
                    int sleepTime = random.nextInt(Params.INSPECT_TIME);
                    sleep(sleepTime);
                    if (!bicycle.isDefective()) {
                        bicycle.setNotTagged();
                    }
                    robot.moveToBelt();
                }
                inspected = true;

                notifyAll();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
