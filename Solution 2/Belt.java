import jdk.nashorn.internal.runtime.Debug;

import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;

/**
 * The bicycle quality control belt
 */
public class Belt  {

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength = 5;

    // to help format output trace
    final private static String indentation = "                  ";

    // TODO
    protected boolean waitingForPutBack = false;

    // TODO
    protected int sensorIdx;

    /**
     * Create a new, empty belt, initialised as empty
     */
    public Belt(int sensorIdx) {
        segment = new Bicycle[beltLength];
        for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }
        this.sensorIdx = sensorIdx;
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
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for space to put bicycle at" + index);
            
            wait();
            
        }

        // insert the element at the specified location
        segment[index] = bicycle;

        // make a note of the event in output trace
        System.out.println(bicycle + " is put at " + (index+1));

        // notify any waiting threads that the belt has changed
        notifyAll();
        
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
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for bicycle to end");
            
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


    private synchronized boolean canMove() {
        if (isEmpty() || segment[segment.length-1] != null) {
            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for belt to move");
            return false;
        } else if (waitingForPutBack) {
            if (segment[sensorIdx-1] != null) {
                Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for bicycle to be put back at " + (sensorIdx + 1));
                return false;
            }
        } else if (segment[sensorIdx] != null) {
            if (segment[sensorIdx].isTagged() && !segment[sensorIdx].isInspected()) {
                Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for tagged bicycle to be removed at " + (sensorIdx + 1));
                return false;
            }
        }
        return true;
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
//        while (isEmpty() || segment[segment.length-1] != null) {
//            Sim.debugPrint(BicycleHandlingThread.getCurrentThreadName() + " waits for belt to move")
//            wait();
//        }

        // TODO
        while (!canMove()) {
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
//        Sim.debugPrint(indentation + this)
        
        // notify any waiting threads that the belt has changed
        notifyAll();

        
    }


    public synchronized Bicycle getAndWait() throws InterruptedException {
        // TODO exception for null bicycle
        Bicycle b = segment[sensorIdx];
        segment[sensorIdx] = null;

        this.waitingForPutBack = true;

        notifyAll();

        return b;
    }

    public synchronized void putAndStopWaitingFor(Bicycle bicycle)
            throws InterruptedException {
        put(bicycle, sensorIdx);
        waitingForPutBack = false;
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

    public String toString() {
        return java.util.Arrays.toString(segment);
    }

    /*
     * @return the final position on the belt
     */
    public int getEndPos() {
        return beltLength-1;
    }
}
