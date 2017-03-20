/**
 * Created by Jack on 11/3/2017.
 */
public class Sensor {
    public Sensor(BicycleContainer dest, int destIndex, Robot robot) {
        this.robot = robot;
        this.dest = dest;
        this.destIndex = destIndex;
    }

    protected Robot robot;
    protected BicycleContainer dest;
    protected int destIndex = -1;

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
