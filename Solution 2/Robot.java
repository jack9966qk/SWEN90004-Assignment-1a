import java.util.Random;

/**
 * Created by Jack on 8/3/2017.
 */
public class Robot extends BicycleHandlingThread {
    protected Belt belt;
    protected Inspector inspector;

    protected Bicycle bicycle;

    protected String moveTo = null;
    protected int index;

    final private static String indentation = "                  ";

    // TODO
    public String getMyName() {
        return "Robot";
    }

    public Robot(Belt belt, Inspector inspector, int index) {
        this.belt = belt;
        this.inspector = inspector;
        this.index = index;
    }

    private synchronized void assignMovingTask(Bicycle bicycle, String moveTo)
            throws InterruptedException, OverloadException {

        while (this.moveTo != null) {
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for arm to be freed");
            
            wait();
            
        }
        if (this.bicycle != null) {
            throw new OverloadException("wtf");
        }

        this.bicycle = bicycle;
        this.moveTo = moveTo;
        notifyAll();
    }

    protected synchronized void moveBicycleFromBeltToInspector()
            throws OverloadException, InterruptedException {
        Bicycle bicycle = belt.getAndWait();
        System.out.println(bicycle.toString() + "[s" + (index + 1) + " -> rb]");
        assignMovingTask(bicycle, "inspector");
    }

    protected synchronized void moveBicycleFromInspectorToBelt()
            throws OverloadException, InterruptedException {
        Bicycle bicycle = inspector.removeInspectedBicycle();
        System.out.println(bicycle.toString() + "[in -> rb]");
        assignMovingTask(bicycle, "belt");
    }

    private synchronized void waitForMovingTask() throws InterruptedException {
        while (moveTo == null) {
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for moving task");
            wait();
        }
    }

    public void run() {
        Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " started");
        while (!interrupted()) {
            try {
                waitForMovingTask();

                // start moving
                Random random = new Random();
                int sleepTime = random.nextInt(Params.ROBOT_MOVE_TIME);
                sleep(sleepTime);

                synchronized (this) {
                    Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " acquires lock of robot");

                    // put bicycle to destination
                    if (moveTo.equals("inspector")) {
                        Sim.debugPrint("try moving to inspector");
                        inspector.putBicycle(bicycle);
                        System.out.println(indentation + "\u001B[33m" + bicycle.toString() + " [rb -> in]" + "\u001B[0m");
                    } else if (moveTo.equals("belt")) {
                        Sim.debugPrint("try moving to belt");
                        belt.putAndStopWaitingFor(bicycle);
                        System.out.println(indentation + "\u001B[33m" + bicycle.toString() + " [rb -> s" + (index + 1) + "]" + "\u001B[0m");
                    }
                    this.bicycle = null;
                    this.moveTo = null;
                    notifyAll();
                    
                }

            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
