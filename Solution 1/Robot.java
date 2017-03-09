import java.util.Random;

/**
 * Created by Jack on 8/3/2017.
 */
public class Robot extends BicycleHandlingThread {
    protected Belt belt;
    protected Inspector inspector;

    protected String moveTo = null;

    protected int index;

    public Robot(Belt belt, Inspector inspector, int index) {
        this.belt = belt;
        this.inspector = inspector;
        this.index = index;
    }

    protected synchronized void assignTask(String moveTo) throws InterruptedException {
        while (this.moveTo != null) {
            wait();
        }
        this.moveTo = moveTo;
        notifyAll();
    }

    public synchronized void moveToInspector() throws InterruptedException {
        assignTask("inspector");
    }

    public synchronized void moveToBelt() throws InterruptedException {
        assignTask("belt");
    }

    public void run() {
        while (!interrupted()) {
            try {
                while (moveTo == null) {
                    wait();
                }
                if (moveTo.equals("belt")) {
                    Bicycle bicycle = belt.getAt(index);
                    Random random = new Random();
                    int sleepTime = random.nextInt(Params.ROBOT_MOVE_TIME);
                    sleep(sleepTime);
                    inspector.putBicycle(bicycle);
                } else if (moveTo.equals("inspector")) {
                    Bicycle bicycle = inspector.getInspectedBicycle();
                    Random random = new Random();
                    int sleepTime = random.nextInt(Params.ROBOT_MOVE_TIME);
                    sleep(sleepTime);
                    belt.put(bicycle, index);
                }
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
