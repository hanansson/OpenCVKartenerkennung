import org.opencv.core.Core;

/**
 * Main class to start the program
 */

/**
 * @author Karsten Lehn
 * @version 21.10.2016
 *
 */
public class Main {
    /**
     * Erzeugt eine Instanz der Klasse Aufnahme.
     * @param args
     * Modified by Niels Hansen
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Aufnahme aufnahme = new Aufnahme();
    }

}
