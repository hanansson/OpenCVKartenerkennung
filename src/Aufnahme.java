import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import static org.opencv.imgproc.Imgproc.*;

public class Aufnahme extends JFrame {

    private BufferedImagePanel imgPanel1;
    public FarbenErkennen farbenErkennen = new FarbenErkennen();

    Mat frame;

    int anzahlSymboleEinerFarbe;
    int[] anzahlUndThreshType = new int[2];
    int anzahlSymbole;
    int i = 0;

    String symbol;
    ArrayList<Mat> kartenImages = new ArrayList<>();

    /**
     * Der Konstruktor der Klasse Aufnahme ruft die Methode createLayout auf, sobald die main-Methode eine Instanz der Klasse Aufnahme erzeugt.
     */
    public Aufnahme() {
        creatLayout();
    }

    /**
     * In createLayout wird die GUI für das Program erzeugt.
     * Es wird ein Bildaufnahmebereich erzeugt, das Layout des zusehenden Fensters bestimmt, ein Beenden-Button mit Funktion erzeugt
     * und ein weiteres Fenster für die Ausgabe des Ergebnisses erstellt.
     */
    public void creatLayout() {

        setTitle("Video stream");

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new FlowLayout());

        imgPanel1 = new BufferedImagePanel();
        contentPane.add(imgPanel1);

        JButton beenden = new JButton();
        beenden.setText("Beenden");
        contentPane.add(beenden);

        JLabel ausgabe = new JLabel();
        ausgabe.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(ausgabe);
        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        beenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        String ausgabeWert;
        ausgabeWert = videoProcess();

        /**
         * Hier wird nach dem die Methode videoProcess die Kartenfarbe und den Kartenwert in Form eines Strings zurückgegeben hat, ein neues Fenster erzeugt
         * Das Fenster beinhaltet die ermittelte Karte, in Textform.
         */

        ausgabe.setText(ausgabeWert);
        JFrame ergebnis = new JFrame();
        ergebnis.setLayout(new BoxLayout(ergebnis.getContentPane(), BoxLayout.Y_AXIS));
        ergebnis.setVisible(true);
        ergebnis.setSize(200, 100);
        ergebnis.invalidate();
        ergebnis.validate();
        ergebnis.setContentPane(ausgabe);
        ergebnis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * In der Methode videoProcess werden die wesentlichen Funktionen des Programmes ausgeführt, um die aufgenommene Karte zu identifizieren.
     * @return Die gefundene Karte wird als String Variable zurückgegeben.
     */

    public String videoProcess() {

        Mat img;

        String filenameHerzCascade = "cascade/herz_cascade.xml";
        String filenameKaroCascade = "cascade/karo_cascade.xml";
        String filenamePikCascade = "cascade/pik_cascade.xml";
        String filenameKreuzCascade = "cascade/kreuz_cascade.xml";

        CascadeClassifier herzCascade = new CascadeClassifier();
        CascadeClassifier karoCascade = new CascadeClassifier();
        CascadeClassifier pikCascade = new CascadeClassifier();
        CascadeClassifier kreuzCascade = new CascadeClassifier();

        if (!herzCascade.load(filenameHerzCascade)) {
            System.err.println("--Error loading herz cascade: " + filenameHerzCascade);
            System.exit(0);
        }
        if (!karoCascade.load(filenameKaroCascade)) {
            System.err.println("--Error loading karo cascade: " + filenameKaroCascade);
            System.exit(0);
        }
        if (!pikCascade.load(filenamePikCascade)) {
            System.err.println("--Error loading pik cascade: " + filenamePikCascade);
            System.exit(0);
        }
        if (!kreuzCascade.load(filenameKreuzCascade)) {
            System.err.println("--Error loading kreuz cascade: " + filenameKreuzCascade);
            System.exit(0);
        }

        /**
         * Hier wird mit dem Videocapturing begonnen.
         */
        VideoCapture capture = new VideoCapture(0);
        frame = new Mat();

        if (!capture.isOpened())
            throw new CvException("The Video File or the Camera could not be opened!");
        capture.read(frame);

        /**
         * In dieser while-Schleife wird jeder Frame auf einen bestimmten Bildbereich begrenzt.
         * Einmal visuell für den Nutzer und einmal für die Weiterverarbeitung
         * Im ausgewählte Bildbereich wird durch die Methode formDetect nach den 4 Kartenfarben gesucht.
         * Werden auf 70 Frames Kartenfarben gefunden, wird die Aufnahme beendet.
         * Es kann davon ausgegangen werden, dass sich eine Karte im vorher bestimmten Bildbereich befindet
         * und der letzte Frame kann weiterverarbeitet werden, um die genaue Karte zu identifizieren.
         */
        while (capture.read(frame)) {

            img = rahmenBegrenzen(frame);

            imgPanel1.setImage(Mat2BufferedImage(rahmenBegrenzenVisualisiert(frame)));
            anzahlSymbole = farbenErkennen.formDetect(img, herzCascade, karoCascade, pikCascade, kreuzCascade, anzahlSymbole);

            if (anzahlSymbole > 70) {
                Mat aufnahmeImg = rahmenBegrenzen(frame);
                kartenImages.add(aufnahmeImg);
                break;
            }

            pack();

            if (!capture.read(frame)) {
                break;
            }
        }

        capture.release();

        /**
         * Der Frame der die Karte enthält wird nun im HSV-Farbraum danach untersucht, ob er die Farbe rot in einer bestimmten Menge enthält.
         */
        for (i = 0; i < 1; i++) {

            Mat processedImage = new Mat();
            Mat threshedImage = new Mat();

            Imgproc.GaussianBlur(kartenImages.get(i), processedImage, new Size(9, 9), 0, 0);
            Imgproc.cvtColor(processedImage, processedImage, Imgproc.COLOR_BGR2HSV);
            Core.inRange(processedImage, new Scalar(160, 100, 100), new Scalar(179, 255, 255), threshedImage);
            Imgproc.medianBlur(threshedImage, threshedImage, 5);
            int n = Core.countNonZero(threshedImage);
            System.out.println("roter Anteil:" + n);

            /**
             * In der If-Verzweigung wird nach dem Zaehlen der weißen Pixel entschieden, welche Karte rot und welche Karte schwarz ist.
             * Auf die roten Karten wird ein Methode angewendet die nur nach Herz und Karo sucht-
             * Auf die schwarzen Karten wird ein Methode angewendet die nur nach Kreuz und Pik sucht.
             */

            if (n > 4500) {
                System.out.println("rot");

                int[] anzahlUndThreshType = formenZaehlen(kartenImages.get(i), 120);
                anzahlSymboleEinerFarbe = anzahlUndThreshType [0];

                int thresh = anzahlUndThreshType [1];

                symbol = farbenErkennen.detectRed(kartenImages.get(i), herzCascade, karoCascade, thresh);
            } else {
                System.out.println("schwarz");
                /**
                 * Der auskommentierte Code guckt sich einen weiteren Bereiech im HSV-Farbraum an, um bei schlechten Lichtverhältnissen rot zu identifizieren.
                 * Diese Funktion ist noch nicht präzise genug.
                 */
                /*Core.inRange(processedImage, new Scalar(130, 0, 0), new Scalar(150, 255, 255), threshedImage);
                Imgproc.medianBlur(threshedImage, threshedImage, 5);
                n = Core.countNonZero(threshedImage);
                System.out.println("lila Anteil:" + n);
                if (n > 200 && n < 1000) {
                    System.out.println("rot2");
                    int[] anzahlUndThreshType = formenZaehlen(kartenImages.get(i), 120);

                    anzahlSymboleEinerFarbe = anzahlUndThreshType [0];
                    int thresh = anzahlUndThreshType [1];

                    symbol = farbenErkennen.detectRed(kartenImages.get(i), herzCascade, karoCascade, thresh);
                }*/


                int[] anzahlUndThreshType = formenZaehlen(kartenImages.get(i), 120);
                anzahlSymboleEinerFarbe = anzahlUndThreshType [0];
                int thresh = anzahlUndThreshType [1];

                symbol = farbenErkennen.detectBlack(kartenImages.get(i), pikCascade, kreuzCascade, thresh);
            }

        }

        /**
         * Hier wird das gefunden Ergebnis zurückgegeben.
         */
        System.out.println("Die Karte ist " + symbol + " " + anzahlSymboleEinerFarbe);
        String ausgabeWerte = symbol + " " + anzahlSymboleEinerFarbe;
        return ausgabeWerte;
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

    /**
     * Diese Funktion bestand zum Austesten. Von verschiedenen Filtern.
     * @param img
     * @return
     */
    public Mat pruefen(Mat img) {

        Mat grayImg = new Mat();
        Mat blurImg = new Mat();
        Mat outputImg = img;
        Mat thresholdImg = new Mat();
        Mat cannyEdges = new Mat();
        Size size = new Size(5, 5);

        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImg, blurImg, size, 7, 7);
        Imgproc.threshold(blurImg, thresholdImg, 120, 255, Imgproc.THRESH_BINARY);
        Imgproc.Canny(thresholdImg, cannyEdges, 1, 255, 3, false);

        return cannyEdges;

    }

    /**
     * Die Methode sollte im aufgenommenen Frame an beliebiger Position die Karte ausfinding machen.
     * Hierzu wurden im aufgenommenen Frame Konturen gesucht.
     * Um die größte gefundene Kontur wurde ein Rechteck gezeichnet.
     * Wenn Breite und Höhe in etwa dem Maßstab der Karte entsprachen, dann sollte das Rechteck den Bildbereich den die Karte umfasst begrenzen.
     * @param img
     * @return Bildausschnitt mit Karte
     */
    public Mat karteEingegrenztFreistellen(Mat img) {

        Mat grayImg = new Mat();
        Mat blurImg = new Mat();
        Mat thresholdImg = new Mat();
        Mat img2 = new Mat();
        Mat cannyEdges = new Mat();
        Mat rotiert = img;
        Mat cropped = img;

        Rect rectA = new Rect(100, 100, 300, 450);

        img2 = new Mat(img, rectA);

        Size size = new Size(3, 3);

        Imgproc.cvtColor(img2, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(grayImg, blurImg, size);

        Imgproc.GaussianBlur(grayImg, blurImg, size, 7, 7);
        //Imgproc.bilateralFilter(grayImg, blurImg, 15,20,20);
        Imgproc.threshold(blurImg, thresholdImg, 180, 255, Imgproc.THRESH_BINARY); //maxval vorher 255
        //Imgproc.adaptiveThreshold(img,thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_OTSU,45,15);
        Imgproc.Canny(thresholdImg, cannyEdges, 1, 255, 3, false);


        ArrayList<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(cannyEdges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.size() > 1) {

            ArrayList<RotatedRect> rects = new ArrayList<>();

            for (int i = 0; i < contours.size(); i++) {
                MatOfPoint points = contours.get(i);
                MatOfPoint2f contours2D = new MatOfPoint2f(points.toArray());
                RotatedRect rotatedRect = Imgproc.minAreaRect(contours2D);
                rects.add(rotatedRect);
            }

            RotatedRect rect = new RotatedRect();

            for (int i = 0; i < rects.size(); i++) {
                RotatedRect rect1 = rects.get(i);
                if (rect.size.width < rect1.size.width && rect.size.height < rect1.size.height) {
                    rect = rect1;
                }
            }

            double heightmin = rect.size.width * 1.3;
            double heightmax = rect.size.width * 2.0;

            if (rect.size.width > 40 && rect.size.width < 400 && rect.size.height > heightmin && rect.size.height < heightmax) {
                //Karte drehen und zuschneiden.
            }
        }

        return thresholdImg;
    }

    /**
     * Hier wird der Frame zurecht geschnitten.
     * @param img die Matrix die den aufgenommen Frame enthält
     * @return der ausgeschnittene Bildbereich
     */
    public Mat rahmenBegrenzen(Mat img) {

        Rect rect = new Rect(img.width() / 2 - 150, img.height() / 2 - 225, 300, 450);
        img = new Mat(img, rect);

        return img;
    }

    /**
     * Hier wird ein Rechteck in rot für den Nutzer, als Orientierung eingezeichnet.
     * @param img die Matrix die den aufgenommen Frame enthält
     * @return der komplette Frame mit einem eingezeichnet roten Rechteck
     */
    public Mat rahmenBegrenzenVisualisiert(Mat img) {
        Mat ausgabeImg = img;

        Point p1 = new Point(img.width() / 2 - 150, img.height() / 2 - 225);
        Point p2 = new Point(img.width() / 2 + 150, img.height() / 2 + 225);

        Imgproc.rectangle(ausgabeImg, p1, p2, new Scalar(0, 0, 255));

        return ausgabeImg;
    }

    /**
     * Auf der Karte werden nach Anwenden mehrerer Filter Konturen erkannt und abgespeichert.
     * Um die Konturen werden Rechtecke gezeichnet.
     * Rechtecke mit einer bestimmten Größe werden gesammelt und sollen die Anzahl an Farbsymbolen ausmachen.
     * @param imgMate der bereits begrenzte Frame
     * @param thresh ein Thresholdwert der gut bei Tageslicht für das Herausfiltern der Kartenelemente geeignet ist.
     * @return Hier wird in einem IntegerArray die Anzahl an gefundenen Farbsymbolen und der funktionierende Thresholdwert für die Weiterverarbeitung zurückgegeben.
     */
    public int[] formenZaehlen(Mat imgMate, int thresh) {

        int threshType = 0;
        Mat grayImg = new Mat();
        Mat blurImg = new Mat();
        Mat thresholdImg = new Mat();
        Size size = new Size(3, 3);

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        ArrayList<Rect> rects = new ArrayList<>();

        Imgproc.cvtColor(imgMate, grayImg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImg, blurImg, size, 7, 7);
        Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY);

        Imgproc.findContours(thresholdImg, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.size() > 0) {
            for (int i = 0; i < contours.size(); i++) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                System.out.println("RechteckSize" + rect.size());

                if (rect.width > 40 && rect.width < 100) {
                    rects.add(rect);
                }
            }
        }

        anzahlUndThreshType[0] = rects.size();
        anzahlUndThreshType[1] = thresh;

        return anzahlUndThreshType;
    }
}
