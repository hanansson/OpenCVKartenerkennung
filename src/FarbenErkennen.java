import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;

public class FarbenErkennen {
    public void formDetect(Mat b, CascadeClassifier herzCascade, CascadeClassifier karoCascade, CascadeClassifier kreuzCascade, CascadeClassifier pikCascade){

        Mat frameGray = b;

        /*Mat frameGray=new Mat();
        Imgproc.cvtColor(b, frameGray, COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);*/

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
        for (Rect herz : listOfHerzen){
            Point center = new Point(herz.x + herz.width / 2, herz.y + herz.height / 2);
            Imgproc.circle(b, center,herz.height/4, new Scalar(255,0,0), 2);//blau
            Mat herzROI = frameGray.submat(herz);
        }

        List<Rect> listOfKaros = karos.toList();
        for (Rect karo : listOfHerzen){
            Point center = new Point(karo.x + karo.width / 2, karo.y + karo.height / 2);
            Imgproc.circle(b, center, karo.height/4, new Scalar(0,255,0), 2);//grün
            Mat herzROI = frameGray.submat(karo);
        }

        List<Rect> listOfPiks = piks.toList();
        for (Rect pik : listOfHerzen){
            Point center = new Point(pik.x + pik.width / 2, pik.y + pik.height / 2);
            Imgproc.circle(b, center, pik.width/4, new Scalar(0,0,255),2);//rot
            Mat herzROI = frameGray.submat(pik);
        }

        List<Rect> listOfKreuze = kreuze.toList();
        for (Rect kreuz : listOfHerzen){
            Point center = new Point(kreuz.x + kreuz.width / 2, kreuz.y + kreuz.height / 2);
            Imgproc.circle(b, center, kreuz.height/4, new Scalar(100,0,100), 2);//cyan
            Mat herzROI = frameGray.submat(kreuz);
        }

        System.out.println("Herz: " + listOfHerzen.size());
        System.out.println("Karo: " + listOfKaros.size());
        System.out.println("Pik: " + listOfPiks.size());
        System.out.println("Kreuz: " + listOfKreuze.size());

    }
}
