/**
 * The driver of the simulation 
 */

public class Sim {
    /**
     * Create all components and start all of the threads.
     */
    public static void main(String[] args) {
        int sensorIdx = 2;

        Robot robot = new Robot();
        Belt belt = new Belt();
        Inspector inspector = new Inspector(belt, sensorIdx, robot);
        Producer producer = new Producer(belt);
        Consumer consumer = new Consumer(belt);
        BeltMover mover = new BeltMover(belt);
        belt.putSensor(new Sensor(inspector, 0, robot), sensorIdx);

        consumer.start();
        producer.start();
        mover.start();
        robot.start();
        inspector.start();

        while (consumer.isAlive() && 
               producer.isAlive() && 
               mover.isAlive() &&
               robot.isAlive() &&
               inspector.isAlive())
            try {
                Thread.sleep(50);
                Printer.clearAndPrint(belt.toString() + "\n" + robot.toString() + "\n" + inspector.toString());
            } catch (InterruptedException e) {
                BicycleHandlingThread.terminate(e);
            }

        // interrupt other threads
        consumer.interrupt();
        producer.interrupt();
        mover.interrupt();

        System.out.println("Sim terminating");
        System.out.println(BicycleHandlingThread.getTerminateException());
        System.exit(0);
    }
}
