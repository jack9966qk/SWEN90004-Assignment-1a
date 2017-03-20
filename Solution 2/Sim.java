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
        Belt belt = new Belt(5, "Belt");
        Belt shortBelt = new Belt(2, "ShortBelt");
        Inspector inspector = new Inspector(shortBelt, 0, robot);
        Producer producer = new Producer(belt);
        Consumer consumer = new Consumer(belt);
        Consumer shortConsumer = new Consumer(shortBelt);
        BeltMover mover = new BeltMover(belt);
        BeltMover shortMover = new BeltMover(shortBelt);
        belt.putSensor(new Sensor(inspector, 0, robot), sensorIdx);

        consumer.start();
        shortConsumer.start();
        producer.start();
        mover.start();
        shortMover.start();
        robot.start();
        inspector.start();

        while (consumer.isAlive() && 
               producer.isAlive() && 
               mover.isAlive() &&
               robot.isAlive() &&
               inspector.isAlive() &&
               shortMover.isAlive() &&
               shortConsumer.isAlive())
            try {
                Thread.sleep(50);
                Printer.clearAndPrint(belt.toString() + "\n" + shortBelt.toString() + "\n" + robot.toString() + "\n" + inspector.toString());
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
