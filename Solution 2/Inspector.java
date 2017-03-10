import java.util.Random;

/**
 * Created by Jack on 8/3/2017.
 */
public class Inspector extends BicycleHandlingThread {

    protected Bicycle bicycle;
    protected Robot robot;
    final private static String indentation = "                  ";


    // TODO
    public String getMyName() {
        return "Inspector";
    }
    public Inspector(Robot robot) {
        this.robot = robot;
    }

    public synchronized Bicycle removeInspectedBicycle() throws InterruptedException {
        while (true) {
            // check if bicycle has arrived
            if (bicycle != null) {
                // check if inspection is finished on the bicycle
                if (bicycle.isInspected()) {
                    break;
                }
            }
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for bicycle to be inspected");
            wait();
        }

        Bicycle b = bicycle;
        bicycle = null;
        notifyAll();
        return b;
    }

    public synchronized void putBicycle(Bicycle bicycle) throws InterruptedException {
        // wait until there's space for new bicycle
        while (this.bicycle != null) {
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for space at inspector");
            wait();
        }

        this.bicycle = bicycle;
        notifyAll();
    }


    private synchronized void waitForBicycle() throws InterruptedException {
        // wait for bicycle to come
        while (true) {
            if (bicycle == null) {
                Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for bicycle arrive at inspector");
                wait();
            } else if (bicycle.isInspected()) {
                Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for inspected bicycle to removed");
                wait();
            } else {
                break;
            }
        }
    }

    // inspect the bicycle, cannot be removed during the process
    private synchronized void inspect(Bicycle bicycle) throws InterruptedException {
        Sim.debugPrint("inspector starts inspecting");
        Random random = new Random();
        int sleepTime = random.nextInt(Params.INSPECT_TIME);
        Thread.sleep(sleepTime);
        if (!bicycle.isDefective()) {
            bicycle.setNotTagged();
        }
        bicycle.setInspected(true);
        System.out.println(indentation + "\u001B[32m" + bicycle + " inspected" + "\u001B[0m");
        Sim.debugPrint("inspector finishes inspecting");
    }

    public void run() {
        Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " started");
        while (!isInterrupted()) {
            try {
                waitForBicycle();
                inspect(bicycle);
                robot.moveBicycleFromInspectorToBelt();
            } catch (InterruptedException e) {
                this.interrupt();
            } catch (OverloadException e) {
                e.printStackTrace();
            }
        }
    }
}
