import java.util.function.Predicate;

/**
 * Created by Jack on 8/3/2017.
 */
public class Sensor extends BicycleHandlingThread {

    // the belt to be handled
    protected Belt belt;

    // robot arm
    protected Robot robot;

    protected int index;

    public Sensor(Belt belt, Robot robot, int index) {
        this.belt = belt;
        this.robot = robot;
        this.index = index;
    }

//    private static Predicate<Bicycle> isTaggedAndNotInspected =
//            bicycle ->!bicycle.isInspected() && bicycle.isTagged();

    public void run() {
        try {
            while (!isInterrupted()) {

                synchronized (belt) {
                    Bicycle b = belt.peek(index);
                    while (true) {
                        if (b != null && b.isTagged()) {
                            break;
                        }
                        wait();
                    }
                    robot.moveToInspector();
                }

//                Bicycle b = belt.getIfTrue(isTaggedAndNotInspected, index);
//                // wait for a bicycle to arrive at segment
//                while (belt.peek(index) == null) {
//                    wait();
//                }
//
//                Bicycle bicycle = belt.peek(index);
//                if (bicycle.isTagged()) {
//
//                } else {
//                    // don't care about this bicycle, wait for belt to move
//                    wait();
//                }
            }
        } catch (InterruptedException e) {
            this.interrupt();
        }
    }
}
