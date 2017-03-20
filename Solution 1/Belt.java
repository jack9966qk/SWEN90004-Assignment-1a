/**
 * The bicycle quality control belt
 */
public class Belt implements BicycleContainer {

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength = 5;

    // to help format output trace
    final private static String indentation = "                  ";

    // NOTE
    protected Sensor[] sensors;

    // NOTE
    public String toString() {
        String str = "Belt: [ ";
        for (int i = 0; i < segment.length; i++) {
            String sensorLabel = sensors[i] != null ? "(s)" : "";
            String bicycle = segment[i] != null ? segment[i].toString() : "";
            str += String.format("%12s,", bicycle + sensorLabel);
        }
        str += " ]";
        return str;
    }

    /**
     * Create a new, empty belt, initialised as empty
     */
    public Belt() {
        segment = new Bicycle[beltLength];
        for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }

        // NOTE
        sensors = new Sensor[beltLength];
        for (int i = 0; i < sensors.length; i++) {
            sensors[i] = null;
        }
    }

    // NOTE
    public void putSensor(Sensor sensor, int index) {
        sensors[index] = sensor;
    }

    /**
     * Put a bicycle on the belt.
     * 
     * @param bicycle
     *            the bicycle to put onto the belt.
     * @param index
     *            the place to put the bicycle
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void put(Bicycle bicycle, int index)
            throws InterruptedException {

    	// while there is another bicycle in the way, block this thread
        while (segment[index] != null) {
            wait();
        }

        // insert the element at the specified location
        segment[index] = bicycle;

        // make a note of the event in output trace
        System.out.println(bicycle + " arrived at segment " + (index+1));

        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    // NOTE
    /**
     * Take a bicycle off the belt
     * @param index
     *          the place to remove the bicycle
     * @return the removed bicycle
     * @throws InterruptedException
     *          if the thread executing is interrupted
     */
    public synchronized Bicycle remove(int index) throws InterruptedException {
        while (segment[index] == null) {
            wait();
        }

        Bicycle bicycle = segment[index];
        segment[index] = null;

        System.out.print(indentation);
        System.out.println(bicycle + " [ s" + (index+1) + " ] removed");

        notifyAll();
        return bicycle;
    }

    /**
     * Take a bicycle off the end of the belt
     * 
     * @return the removed bicycle
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Bicycle getEndBelt() throws InterruptedException {

        Bicycle bicycle;

        // while there is no bicycle at the end of the belt, block this thread
        while (segment[segment.length-1] == null) {
            wait();
        }

        // get the next item
        bicycle = segment[segment.length-1];
        segment[segment.length-1] = null;

        // make a note of the event in output trace
        System.out.print(indentation + indentation);
        System.out.println(bicycle + " departed");

        // notify any waiting threads that the belt has changed
        notifyAll();
        return bicycle;
    }

    /**
     * Move the belt along one segment
     * 
     * @throws OverloadException
     *             if there is a bicycle at position beltLength.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void move() 
            throws InterruptedException, OverloadException {
        // if there is something at the end of the belt, 
    	// or the belt is empty, do not move the belt
        while (isEmpty() || segment[segment.length-1] != null) {
            wait();
        }

        // double check that a bicycle cannot fall of the end
        if (segment[segment.length-1] != null) {
            String message = "Bicycle fell off end of " + " belt";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = segment.length-1; i > 0; i--) {
            if (this.segment[i-1] != null) {
                System.out.println(
                		indentation +
                		this.segment[i-1] +
                        " [ s" + (i) + " -> s" + (i+1) +" ]");
            }
            segment[i] = segment[i-1];
        }
        segment[0] = null;
//        System.out.println(indentation + this);


        // NOTE
        for (int i = 0; i < sensors.length; i++) {
            Sensor sensor = sensors[i];
            if (sensor != null) {
                sensor.handleBicycle(this, i);
            }
        }
        
        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * @return the maximum size of this belt
     */
    public int length() {
        return beltLength;
    }

    /**
     * Peek at what is at a specified segment
     * 
     * @param index
     *            the index at which to peek
     * @return the bicycle in the segment (or null if the segment is empty)
     */
    public Bicycle peek(int index) {
        Bicycle result = null;
        if (index >= 0 && index < beltLength) {
            result = segment[index];
        }
        return result;
    }

    /**
     * Check whether the belt is currently empty
     * @return true if the belt is currently empty, otherwise false
     */
    private boolean isEmpty() {
        for (int i = 0; i < segment.length; i++) {
            if (segment[i] != null) {
                return false;
            }
        }
        return true;
    }

//    public String toString() {
//        return java.util.Arrays.toString(segment);
//    }

    /*
     * @return the final position on the belt
     */
    public int getEndPos() {
        return beltLength-1;
    }
}
