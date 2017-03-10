/**
 * Created by Jack on 8/3/2017.
 */
public class Sensor extends BicycleHandlingThread {

    // the belt to be handled
    protected final Belt belt;

    // TODO
    public String getMyName() {
        return "Sensor";
    }

    // robot arm
    protected Robot robot;

    protected int index;

    public Sensor(Belt belt, Robot robot, int index) {
        this.belt = belt;
        this.robot = robot;
        this.index = index;
    }


    public void run() {
        Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " started");
        try {
            while (!isInterrupted()) {

//                Sim.debugPrint("a");
//                waitForTaggedBicycle();
//                Sim.debugPrint("b");
//                robot.moveBicycleFromBeltToInspector();
//                Sim.debugPrint("c");

                synchronized (belt) {
                    Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " acquires lock");
                    while (true) {
                        Bicycle b = belt.peek(index);
                        if (b != null && b.isTagged() && !b.isInspected()) {
                            break;
                        }
                        Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for tagged bicycle to arrive");
                        belt.wait(); // wait for belt mover to bring new bicycle
                    }

                    Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " found tagged bicycle, call robot to move to inspector");
                    robot.moveBicycleFromBeltToInspector();
                }

            }
        } catch (InterruptedException e) {
            this.interrupt();
        } catch (OverloadException e) {
            e.printStackTrace();
        }
    }
}
