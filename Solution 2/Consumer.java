import java.util.Random;

/**
 * A consumer continually tries to take bicycles from the end of a quality control belt
 */

public class Consumer extends BicycleHandlingThread {

    // the belt from which the consumer takes the bicycles
    protected Belt belt;

    // TODO
    public String getMyName() {
        return "Consumer";
    }


    /**
     * Create a new Consumer that consumes from a belt
     */
    public Consumer(Belt belt) {
        super();
        this.belt = belt;
    }

    /**
     * Loop indefinitely trying to get bicycles from the quality control belt
     */
    public void run() {
        System.out.println(BicycleHandlingThread.getCurrentThreadName() + " started");
        while (!isInterrupted()) {
            try {
                Bicycle b = belt.getEndBelt();

                // check if bicycle is correctly labeled and inspected
                if (b.isDefective()) {
                    assert (b.isTagged());
                    assert (b.isInspected());
                } else {
                    assert(!b.isTagged());
                }

                // let some time pass ...
                Random random = new Random();
                int sleepTime = Params.CONSUMER_MIN_SLEEP + 
                		random.nextInt(Params.CONSUMER_MAX_SLEEP - 
                				Params.CONSUMER_MIN_SLEEP);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Consumer terminated");
    }
}
