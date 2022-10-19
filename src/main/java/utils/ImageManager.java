package utils;

import executable.MyBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class ImageManager {

    private final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private MyBot bot;
    private FileManager fileManager;

    public ImageManager(MyBot bot) {
        this.bot = bot;
        this.fileManager = new FileManager(bot);
    }


    public String merge(String imageBack, String imageFront, int x, int y) {
        String pathBack = fileManager.getFullPathToImage(imageBack);
        String pathFront = fileManager.getFullPathToImage(imageFront);
        // load source images
        BufferedImage background = null;
        BufferedImage overlay = null;
        BufferedImage overlay2 = null;
        try {
            background = ImageIO.read(new File(pathBack));
            overlay = ImageIO.read(new File(pathFront));
//        overlay2 = ImageIO.read(new File(path, "25.png"));
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        // create the new image, canvas size is the max. of both image sizes
//        int w = Math.max(background.getWidth(), overlay.getWidth());
//        int h = Math.max(background.getHeight(), overlay.getHeight());
        int w = 200;
        int h = 120;
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.drawImage(overlay, x, y, null);
//        g.drawImage(overlay2, 100, 0, null);

        g.dispose();

        URL tempImgPath = getClass().getClassLoader().getResource("images/temp");

// Save as new image
        if (tempImgPath != null) {
            try {
                long date = new Date().getTime();
                String nomTempFile = date+".png";
                ImageIO.write(combined, "PNG", new File(tempImgPath.getPath(), date+".png"));
                return nomTempFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            logger.error("Dossier images temp. non trouvé : "+tempImgPath);
        }

        return "";
    }
}
