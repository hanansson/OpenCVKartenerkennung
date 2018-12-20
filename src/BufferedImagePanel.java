import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImagePanel extends JComponent {


    private int width, height;
    private BufferedImage panelImage;


    public BufferedImagePanel()
    {
        width = 320;    // arbitrary size for empty panel
        height = 240;
        panelImage = null;
    }

    public void setImage(BufferedImage image)
    {
        if(image != null) {
            width = image.getWidth();
            height = image.getHeight();
            panelImage = image;
            repaint();
        }
    }

    public void clearImage()
    {
        Graphics imageGraphics = panelImage.getGraphics();
        imageGraphics.setColor(Color.LIGHT_GRAY);
        imageGraphics.fillRect(0, 0, width, height);
        repaint();
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }


    @Override
    public void paintComponent(Graphics g) {

        Dimension size = getSize();
        g.clearRect(0, 0, size.width, size.height);
        if(panelImage != null) {
            g.drawImage(panelImage, 0, 0, null);
        }
    }
}
