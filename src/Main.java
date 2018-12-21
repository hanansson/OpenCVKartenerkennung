import org.opencv.core.Core;

public class Main {
    /**
     * Erzeugt eine Instanz der Klasse Aufnahme.
     * @param args
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Aufnahme aufnahme = new Aufnahme();
    }

}
