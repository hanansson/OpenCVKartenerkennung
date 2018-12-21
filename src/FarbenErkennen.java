import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;

/**
 * Diese Klasse enthält Methoden, die spezielle Kartenfarben erkennen soll.
 */

public class FarbenErkennen {

    /**
     * Diese Methode sucht den ausgewählten Bildbereich beziehungsweise die mitgegebene Matrix nach allen vier Kartenfarben ab.
     * Dazu wird die Matrix in Grauwerte umgewandelt und es findet eine Histogrammequalization statt.
     * Mit den selbsttrainierten Classifieren wird der Bildbereich den die Matrix beinhaltet nach Kartenfarben durchsucht.
     * Die gefundenen Kartenfarben(einzelne Symbole) werden separat in Listen gespeichert.
     * @param img
     * @param herzCascade
     * @param karoCascade
     * @param kreuzCascade
     * @param pikCascade
     * @param erkannt
     * @return Wurde mindestens ein Symbol einer Farbe gefunden, dann wird durch die Erhöhung der Integervariable erkanntausgabe markiert,
     *         dass auf einem weiteren Frame Kartenfarben identifiziert wurden.
     *
     * @made by Svenja Wiehen
     * @made by Niels Hansen
     */
    public int formDetect(Mat img, CascadeClassifier herzCascade, CascadeClassifier karoCascade, CascadeClassifier kreuzCascade, CascadeClassifier pikCascade, int erkannt) {

        Mat frameGray = new Mat();
        Imgproc.cvtColor(img, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);

        int erkanntausgabe = erkannt;

        MatOfRect herzen = new MatOfRect();
        herzCascade.detectMultiScale(frameGray, herzen);
        MatOfRect karos = new MatOfRect();
        karoCascade.detectMultiScale(frameGray, karos);
        MatOfRect piks = new MatOfRect();
        pikCascade.detectMultiScale(frameGray, piks);
        MatOfRect kreuze = new MatOfRect();
        kreuzCascade.detectMultiScale(frameGray, kreuze);

        List<Rect> listOfHerzen = herzen.toList();
        List<Rect> listOfKaros = karos.toList();
        List<Rect> listOfPiks = piks.toList();
        List<Rect> listOfKreuze = kreuze.toList();

        int mengeErkannterZeichen = listOfHerzen.size() + listOfKaros.size() + listOfKreuze.size() + listOfPiks.size();

        if (mengeErkannterZeichen > 0) {
            erkanntausgabe++;
        }

        return erkanntausgabe;
    }

    /**
     * Es wurde bereits bestimmt, dass die Karte rot ist, wenn diese Methode ausgeführt wird.
     * Daher wird in dieser Methode getestet, ob es sich bei der Karte um Herz oder Karo handelt.
     * Hierzu wird der Herz und Karo Classifier angewendet.
     * @param img
     * @param herzCascade
     * @param karoCascade
     * @param thresh
     * @return Die Variable vom Typ String bekommt entweder denn Wert Karo oder Herz zugewiesen.
     *         Die Kartenfarbe von der mehr Symbole gefunden werden, wird zurückgegeben.
     *
     * @made by Svenja Wiehen
     */
    public String detectRed(Mat img, CascadeClassifier herzCascade, CascadeClassifier karoCascade, int thresh){

        Mat grayImg = new Mat();
        Mat blurImg = new Mat();
        Mat thresholdImg = new Mat();
        Size size = new Size(3, 3);

        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImg, blurImg, size, 7, 7);
        Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY);

        String symbol = null;

        MatOfRect herzen = new MatOfRect();
        herzCascade.detectMultiScale(thresholdImg, herzen);
        MatOfRect karos = new MatOfRect();
        karoCascade.detectMultiScale(thresholdImg, karos);

        List<Rect> listOfHerzen = herzen.toList();
        List<Rect> listOfKaros = karos.toList();

        if (listOfHerzen.size()>0 && listOfHerzen.size()> listOfKaros.size()){
            System.out.println("Herz: " + listOfHerzen.size());
            symbol = "Herz";
        }else if(listOfKaros.size()>0 && listOfKaros.size()>listOfHerzen.size()){
            System.out.println("Karo: " + listOfKaros.size());
            symbol = "Karo";
        }

        System.out.println("Herz: " + listOfHerzen.size());
        System.out.println("Karo: " + listOfKaros.size());

        return symbol;
    }

    /**
     * Es wurde bereits bestimmt, dass die Karte schwarz ist, wenn diese Methode ausgeführt wird.
     * Daher wird in dieser Methode getestet, ob es sich bei der Karte um Kreuz oder Pik handelt.
     * Hierzu wird der Kreuz und Pik Classifier angewendet.
     * @param img
     * @param kreuzCascade
     * @param pikCascade
     * @param thresh
     * @return Die Variable vom Typ String bekommt entweder denn Wert Kreuz oder Pik zugewiesen.
     *         Die Kartenfarbe von der mehr Symbole gefunden werden, wird zurückgegeben.
     *
     * @made by Svenja Wiehen
     */
    public String detectBlack(Mat img, CascadeClassifier kreuzCascade, CascadeClassifier pikCascade, int thresh){

        Mat grayImg = new Mat();
        Mat blurImg = new Mat();
        Mat thresholdImg = new Mat();
        Size size = new Size(3, 3);

        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImg, blurImg, size, 7, 7);
        Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY);

        String symbol = null;

        MatOfRect piks = new MatOfRect();
        pikCascade.detectMultiScale(thresholdImg, piks);
        MatOfRect kreuze = new MatOfRect();
        kreuzCascade.detectMultiScale(thresholdImg, kreuze);

        List<Rect> listOfPiks = piks.toList();
        List<Rect> listOfKreuze = kreuze.toList();


        if (listOfPiks.size()>0 && listOfPiks.size()>listOfKreuze.size()){
            System.out.println("Pik: " + listOfPiks.size());
            symbol = "Pik";
        }else if (listOfKreuze.size()>0 && listOfKreuze.size()>listOfPiks.size()){
            System.out.println("Kreuz: " + listOfKreuze.size());
            symbol = "Kreuz";
        }
            System.out.println("Pik: " + listOfPiks.size());
            System.out.println("Kreuz: " + listOfKreuze.size());

        return symbol;
    }
}