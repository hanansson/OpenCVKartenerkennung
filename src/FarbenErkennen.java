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
    public int formDetect(Mat b, CascadeClassifier herzCascade, CascadeClassifier karoCascade, CascadeClassifier kreuzCascade, CascadeClassifier pikCascade, int erkannt) {

        Mat frameGray = b;

        /*Mat frameGray=new Mat();
        Imgproc.cvtColor(b, frameGray, COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);*/
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

        Mat frameGray = b;
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
}
