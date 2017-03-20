/**
 * Created by Jack on 11/3/2017.
 */
public class Inspector extends BicycleHandlingThread implements BicycleContainer {
    protected Bicycle bicycle = null;
    protected BicycleContainer dest = null;
    protected int destIndex = -1;

    public Inspector(BicycleContainer dest, int destIndex, Robot robot) {
        this.dest = dest;
        this.destIndex = destIndex;
        this.robot = robot;
    }

    protected Robot robot;

    public String toString() {
        return "Inspector: [ " + (bicycle != null ? bicycle.toString() : "") + " ]";
    }

    @Override
    public synchronized void put(Bicycle bicycle, int index) throws InterruptedException {
        while (this.bicycle != null) {
            wait();
        }
        System.out.println(bicycle + " arrives at inspector");
        this.bicycle = bicycle;
        notifyAll();
    }

    @Override
    public Bicycle peek(int index) {
        return bicycle;
    }

    @Override
    public synchronized Bicycle remove(int index) throws InterruptedException {
        while (this.bicycle == null || !this.bicycle.isInspected()) {
            wait();
        }
        System.out.println(bicycle + " left inspector");
        Bicycle bicycle = this.bicycle;
        this.bicycle = null;
        notifyAll();
        return bicycle;
    }

    @Override
    public int length() {
        return 1;
    }

    public synchronized void run() {
        while (!interrupted()) {
            try {
                // inspected bicycle should be removed already
                if (bicycle != null) {
                    assert (bicycle.isInspected());
                }
                // wait for new bicycle to arrive
                while (bicycle == null) {
                    wait();
                }

                // inspect
                Debug.println("started inspecting " + bicycle.toString());
                sleep(Params.INSPECT_TIME);
                bicycle.setInspected(true);
                if (bicycle.isDefective()) {
                    bicycle.setTagged();
                } else {
                    bicycle.setNotTagged();
                }
                Debug.println("finished inspecting " + bicycle.toString());
                robot.pickBicycle(this, 0, dest, destIndex);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }
}
