/**
 * Created by Jack on 11/3/2017.
 */
public interface BicycleContainer {

    public void put(Bicycle bicycle, int index) throws InterruptedException;

    public Bicycle remove(int index) throws InterruptedException;

    public int length();

    public Bicycle peek(int index);

}
