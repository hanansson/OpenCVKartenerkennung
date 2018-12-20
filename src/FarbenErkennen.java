import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;

public class FarbenErkennen {
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