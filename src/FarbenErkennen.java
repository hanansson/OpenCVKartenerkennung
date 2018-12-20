import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.getRectSubPix;

public class FarbenErkennen {
    public int formDetect(Mat img, CascadeClassifier herzCascade, CascadeClassifier karoCascade, CascadeClassifier kreuzCascade, CascadeClassifier pikCascade, int erkannt) {

        Mat frameGray = new Mat();
        Imgproc.cvtColor(img, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);

        //Hier muss erkannt werden, ob rote oder schwarze Classifier angewendet werden sollen.

        //positive müssen in etwa gleich groß sein

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

        //Zaehlen funktioniert. mengeErkannterZeichen

        if (mengeErkannterZeichen > 0) {
            erkanntausgabe++;
        }

        return erkanntausgabe;
    }

    public ArrayList<List<Rect>> formDetect2(Mat b, CascadeClassifier herzCascade, CascadeClassifier karoCascade, CascadeClassifier kreuzCascade, CascadeClassifier pikCascade) {

        ArrayList<List<Rect>> alleErkanntenKartenfarben = new ArrayList<>();

        Mat frameGray = new Mat();
        Imgproc.cvtColor(b, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);
        //positive müssen in etwa gleich groß sein

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

        alleErkanntenKartenfarben.add(listOfHerzen);
        alleErkanntenKartenfarben.add(listOfKaros);
        alleErkanntenKartenfarben.add(listOfPiks);
        alleErkanntenKartenfarben.add(listOfKreuze);

        System.out.println("Herz: " + listOfHerzen.size());
        System.out.println("Karo: " + listOfKaros.size());
        System.out.println("Pik: " + listOfPiks.size());
        System.out.println("Kreuz: " + listOfKreuze.size());

        return alleErkanntenKartenfarben;
    }

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
            symbol = "Kreuz";
        }else if (listOfKreuze.size()>0 && listOfKreuze.size()>listOfPiks.size()){
            System.out.println("Kreuz: " + listOfKreuze.size());
            symbol = "Pik";
        }
            System.out.println("Pik: " + listOfPiks.size());
            System.out.println("Kreuz: " + listOfKreuze.size());

        return symbol;
    }
}