import java.io.IOException;

/**
 * Created by Jack on 11/3/2017.
 */
public class Printer {
    public static String last = "";
    public static void clearAndPrint(String str) {
        if (str.equals(last)) {return;}
        try {
            Runtime.getRuntime().exec("clear");
        } catch (IOException e) {}
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(str);
    }
}
