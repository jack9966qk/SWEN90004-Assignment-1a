/**
 * The driver of the simulation 
 */

public class Sim {
    public static void debugPrint(String string) {
//        System.out.println("\u001B[36m" + string + "\u001B[0m");
    }


    /**
     * Create all components and start all of the threads.
     */
    public static void main(String[] args) {

        int index = 2;
        
        Belt belt = new Belt(index);
        Producer producer = new Producer(belt);
        Consumer consumer = new Consumer(belt);
        BeltMover mover = new BeltMover(belt);
        Sensor sensor = new Sensor(belt, null, index);
        Inspector inspector = new Inspector(null);
        Robot robot = new Robot(belt, inspector, index);
        sensor.robot = robot;
        inspector.robot = robot;


        System.out.println("sensor thread: " + sensor.toString());
        System.out.println("robot thread: " + robot.toString());
        System.out.println("inspector thread: " + inspector.toString());
        System.out.println("consumer thread: " + consumer.toString());
        System.out.println("producer thread: " + producer.toString());
        System.out.println("mover thread: " + mover.toString());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sensor.start();
        robot.start();
        inspector.start();
        consumer.start();
        producer.start();
        mover.start();


        while (consumer.isAlive() && 
               producer.isAlive() && 
               mover.isAlive() &&
               robot.isAlive() &&
               sensor.isAlive() &&
               inspector.isAlive()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                BicycleHandlingThread.terminate(e);
            }
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
