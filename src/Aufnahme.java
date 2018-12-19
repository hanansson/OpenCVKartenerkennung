import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.*;

public class Aufnahme extends JFrame {

    private BufferedImagePanel imgPanel1;
    private BufferedImagePanel imgPanel2;
    public FarbenErkennen farbenErkennen = new FarbenErkennen();

    Mat frame;
    int erkannt;
    int a11 = 0;
    int a12 = 0;
    int a13 = 0;
    int a14 = 0;
    //ArrayList<ArrayList<List<Rect>>> alleErkanntenKartenfarbenAllerImages;
    ArrayList<List<Rect>> alleErkanntenKartenfarben;
    ArrayList<Mat> kartenImages = new ArrayList<>();

    public Aufnahme() {
        creatLayout();
    }

    public void creatLayout() {

        setTitle("Video stream");

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new FlowLayout());

        imgPanel1 = new BufferedImagePanel();
        contentPane.add(imgPanel1);
        /*imgPanel2 = new BufferedImagePanel();
        contentPane.add(imgPanel2);*/

        ActionEvent event = null;
        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        videoProcess();
    }

    public void videoProcess() {

        Mat a;
        Mat b;

        String filenameHerzCascade = "/Users/nielshansen/IdeaProjects/OpenCVKamera/cascade/herz_cascade.xml";
        String filenameKaroCascade = "/Users/nielshansen/IdeaProjects/OpenCVKamera/cascade/karo_cascade.xml";
        String filenamePikCascade = "/Users/nielshansen/IdeaProjects/OpenCVKamera/cascade/pik_cascade.xml";
        String filenameKreuzCascade = "/Users/nielshansen/IdeaProjects/OpenCVKamera/cascade/kreuz_cascade.xml";

        CascadeClassifier herzCascade = new CascadeClassifier();
        CascadeClassifier karoCascade = new CascadeClassifier();
        CascadeClassifier pikCascade = new CascadeClassifier();
        CascadeClassifier kreuzCascade = new CascadeClassifier();

        if (!herzCascade.load(filenameHerzCascade)){
            System.err.println("--Error loading herz cascade: " + filenameHerzCascade);
            System.exit(0);
        }
        if (!karoCascade.load(filenameKaroCascade)){
            System.err.println("--Error loading karo cascade: " + filenameKaroCascade);
            System.exit(0);
        }
        if (!pikCascade.load(filenamePikCascade)){
            System.err.println("--Error loading pik cascade: " + filenamePikCascade);
            System.exit(0);
        }
        if (!kreuzCascade.load(filenameKreuzCascade)){
            System.err.println("--Error loading kreuz cascade: " + filenameKreuzCascade);
            System.exit(0);
        }

        VideoCapture capture = new VideoCapture(0);
        frame = new Mat();

        if (!capture.isOpened())
            throw new CvException("The Video File or the Camera could not be opened!");
        capture.read(frame);

        while (capture.read(frame)) {

            //*****Hier werden die Methoden ausgeführt.*****

            //a = pruefen(frame);
            b = rahmenBegrenzen(frame);

            imgPanel1.setImage(Mat2BufferedImage(rahmenBegrenzen(frame)));
            //imgPanel2.setImage(Mat2BufferedImage(rahmenBegrenzen(frame)));

            erkannt = farbenErkennen.formDetect(b, herzCascade, karoCascade, pikCascade, kreuzCascade, erkannt);

            if(erkannt>50){
                System.out.println("test");
                //Hier noch 10 durchläufe
                for(a11 = 0; a11<10; a11 ++){
                    Mat aufnahmeImg = rahmenBegrenzen(frame);
                    kartenImages.add(aufnahmeImg);
                }
                break;
            }

            pack();

            if (!capture.read(frame)) {
                break;
            }
        }
        capture.release();
        System.out.println(kartenImages.size());
        for(a11 = 0; a11<kartenImages.size(); a11++) {
            alleErkanntenKartenfarben = farbenErkennen.formDetect2(kartenImages.get(a11), herzCascade, karoCascade, pikCascade, kreuzCascade);
            for(a12 = 0; a12<alleErkanntenKartenfarben.size(); a12 ++){
                List<Rect>ObjektederKartenFarbe = alleErkanntenKartenfarben.get(a12);
                //System.out.println(ObjektederKartenFarbe.size());
                    for(a13 = 0; a13<ObjektederKartenFarbe.size(); a13++){
                        System.out.println("1");
                    }
                    System.out.println("*****");
                }
                System.out.println("!!!!!!!");
            }//*******
            //alleErkanntenKartenfarbenAllerImages.add(alleErkanntenKartenfarben);
        //System.out.println(alleErkanntenKartenfarbenAllerImages.size());
        /*for(a11 = 0; a11<4; a11 ++){
            for(a12 = 0; a12<alleErkanntenKartenfarben.get(a11).size(); a12 ++){
            }
        }*/

    }


    public BufferedImage Mat2BufferedImage(Mat imgMat) {
        int bufferedImageType = 0;
        switch (imgMat.channels()) {
            case 1:
                bufferedImageType = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                bufferedImageType = BufferedImage.TYPE_3BYTE_BGR;
                break;
            default:
                throw new IllegalArgumentException("Unknown matrix type. Only one byte per pixel (one channel) or three bytes pre pixel (three channels) are allowed.");
        }
        BufferedImage bufferedImage = new BufferedImage(imgMat.cols(), imgMat.rows(), bufferedImageType);
        final byte[] bufferedImageBuffer = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        imgMat.get(0, 0, bufferedImageBuffer);
        return bufferedImage;

    }

    public Mat pruefen(Mat img) {

            Mat grayImg = new Mat();
            Mat blurImg = new Mat();
            Mat outputImg = img;
            Mat thresholdImg = new Mat();
            Mat cannyEdges = new Mat();
            Size size = new Size(5, 5);

        /*Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImg, blurImg, size, 5, 5);
        Imgproc.threshold(blurImg, thresholdImg, 100, 255, Imgproc.THRESH_BINARY);
        Imgproc.Canny(thresholdImg, cannyEdges, 50, 200, 5, false);*/


            Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
            //Imgproc.blur(grayImg, blurImg, size);
            Imgproc.GaussianBlur(grayImg, blurImg, size, 7,7);
            Imgproc.threshold(blurImg, thresholdImg, 120, 255, Imgproc.THRESH_BINARY); //thresh!
            //Imgproc.adaptiveThreshold(blurImg,thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_OTSU,75,10);
            Imgproc.Canny(thresholdImg, cannyEdges, 1, 255, 3, false);

        return cannyEdges;

    }

    public Mat karteFreistellenHoughlines (Mat img){

        Mat grayImg = new Mat();
        Mat cannyEdges = new Mat();
        Mat blurImg = new Mat();
        Mat thresholdImg = new Mat();
        Size size = new Size(5, 5);

        Mat lines = new Mat();

        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImg, blurImg, size, 5, 5);
        Imgproc.threshold(blurImg, thresholdImg, 100, 255, Imgproc.THRESH_BINARY);
        Imgproc.Canny(thresholdImg, cannyEdges, 50, 200, 3, false);

        Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI/180, 50, 300, 25);

        MatOfPoint points = new MatOfPoint();

        for (int x = 0; x < lines.rows(); x++) {
            double[] l = lines.get(x, 0);
            Imgproc.line(img, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }

        //System.out.println(points.size());

        Mat ausgabe = img;

        /*Rect rect = new Rect(0,0,img.width(),img.height());

        if(!points.empty()) {
            rect = Imgproc.boundingRect(points);
        }

        ausgabe = new Mat(img, rect);*/

        return ausgabe;
    }

    public Mat karteEingegrenztFreistellen (Mat img){
        //Rect rect = new Rect(100,100,300,450);

        //Mat croppedImg = new Mat(img, rect);
        Mat grayImg = new Mat();
        Mat blurImg = new Mat();
        Mat thresholdImg = new Mat();
        Mat img2 = new Mat();
        Mat cannyEdges = new Mat();
        Mat hsvImg = new Mat();
        Mat rotiert = img;
        Mat cropped = img;

        Rect rectA = new Rect(100,100,300,450);

        img2 = new Mat(img, rectA);

        Size size = new Size(3,3);

            Imgproc.cvtColor(img2, grayImg, Imgproc.COLOR_BGR2GRAY);
            /*for(int i=0; i<grayImg.rows(); i++) {
                for (int j = 0; j < grayImg.cols(); j++) {
                    double[] pixel = grayImg.get(i, j);
                    System.out.println(pixel[i]);
                }
            }*/

            Imgproc.blur(grayImg, blurImg, size);

            Imgproc.GaussianBlur(grayImg, blurImg, size, 7,7);
            //Imgproc.bilateralFilter(grayImg, blurImg, 15,20,20);
            Imgproc.threshold(blurImg, thresholdImg, 180, 255, Imgproc.THRESH_BINARY); //maxval vorher 255
            //Imgproc.adaptiveThreshold(img,thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_OTSU,45,15);
            Imgproc.Canny(thresholdImg, cannyEdges, 1, 255, 3, false);


            ArrayList<MatOfPoint> contours = new ArrayList<>();

            Imgproc.findContours(cannyEdges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if (contours.size() > 1) {

                ArrayList<RotatedRect> rects = new ArrayList<>();

                //Imgproc.drawContours(img, contours, -1, new Scalar(0, 0, 255));

                //funktioniert!!!!!!!!

                for (int i = 0; i < contours.size(); i++) {
                    MatOfPoint points = contours.get(i);
                    MatOfPoint2f contours2D = new MatOfPoint2f(points.toArray());
                    RotatedRect rotatedRect = Imgproc.minAreaRect(contours2D);
                    rects.add(rotatedRect);
                    //System.out.println("test1");
                }

                RotatedRect rect = new RotatedRect();

                for (int i = 0; i < rects.size(); i++) {
                    RotatedRect rect1 = rects.get(i);
                    if (rect.size.width < rect1.size.width && rect.size.height < rect1.size.height) {
                        rect = rect1;
                        //System.out.println("test2");
                    }
                }

                //double height1 = rect.size.width*1.5;

                //Vielleicht noch andere Größenverhältnisse. Vieleicht ist das auch Unsinn!

                double heightmin = rect.size.width * 1.3;
                double heightmax = rect.size.width * 2.0;


                if (rect.size.width > 40 && rect.size.width < 400 && rect.size.height > heightmin && rect.size.height < heightmax) {

                    float angle = (float) rect.angle;

                    Size rect_size = rect.size;

                    if (rect.angle < -45.) {
                        angle += 90.0;
                        rect.size.height = rect_size.width;
                        rect.size.width = rect_size.height;
                    }

                    //vielleicht irgendwie noch trimmen!

                    Mat m = Imgproc.getRotationMatrix2D(rect.center, angle, 1.0);
                    Imgproc.warpAffine(img2, rotiert, m, img2.size(), INTER_CUBIC);
                    Imgproc.getRectSubPix(rotiert, rect.size, rect.center, cropped);
                    //System.out.println(cropped.width() + " " + cropped.height());
                }
            }

            return thresholdImg;
    }

    public Mat rahmenBegrenzen (Mat img){

        Mat grayImg = new Mat();
        Mat thresholdImg = new Mat();

        Rect rect = new Rect(img.width()/2-150,img.height()/2-225,300,450);

        Mat ausgabe = img;

        Point p1 = new Point(img.width()/2-150,img.height()/2-225);

        Point p2 = new Point(img.width()/2+150, img.height()/2+225);

        //Imgproc.rectangle(ausgabe, p1,p2,new Scalar(0, 0, 255));

        img = new Mat(img, rect);
        //Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.threshold(grayImg, thresholdImg, 120, 255, Imgproc.THRESH_BINARY);

        //Wenn der CascadeClassifier etwas findet Aufnahme beenden. (alle Classifier)
        //Wenn die Aufnahme beendet wird 10 frames abspeichern über alle Klassifier laufen lassen, wenn 2-3 Listen gleich sind erfolgreiche Suche.
        //wenn nicht Camera wieder starten.

         return img;
    }
}
