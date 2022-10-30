package utils;

import executable.MyBot;
import game.model.ElementUI;
import game.model.ImageUI;
import game.model.RectangleUI;
import game.model.TextUI;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class ImageManager {

    private final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private MyBot bot;
    private FileManager fileManager;

    public ImageManager(MyBot bot) {
        this.bot = bot;
        this.fileManager = new FileManager(bot);
    }

    public String composeImageCombat(BufferedImage background, List<ElementUI> elementUIList, int largeur, int hauteur) {

        BufferedImage combined = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(background, 0, 0, null);

        for (ElementUI elementUI : elementUIList) {
            if (elementUI instanceof ImageUI) {
                ImageUI imageUI = ((ImageUI) elementUI);
                g.drawImage(imageUI.getImage(), imageUI.getX(), imageUI.getY(), null);
            } else if (elementUI instanceof TextUI) {
                TextUI textUI = ((TextUI) elementUI);
                g.setFont(textUI.getFont());
                g.setColor(textUI.getColor());
                g.drawString(textUI.getText(), textUI.getX(), textUI.getY());
            } else if (elementUI instanceof RectangleUI) {
                RectangleUI rectangleUI = ((RectangleUI) elementUI);
                g.setColor(rectangleUI.getColor());
                g.fillRect(rectangleUI.getX(), rectangleUI.getY(), rectangleUI.getLargeur(), rectangleUI.getHauteur());
            }
        }

        g.dispose();

        URL tempImgPath = getClass().getClassLoader().getResource("images/temp");

// Save as new image
        if (tempImgPath != null) {
            try {
                long date = new Date().getTime();
                String nomTempFile = date + ".png";
                ImageIO.write(combined, "PNG", new File(tempImgPath.getPath(), date + ".png"));
                return nomTempFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.error("Dossier images temp. non trouvé : " + tempImgPath);
        }

        return "";
    }

    public String merge(String imageBack, String meteo, String imageFront, int x, int y, int largeur, int hauteur) {
        boolean drawMeteo = StringUtils.isNotEmpty(meteo);
        String pathBack = fileManager.getFullPathToImage(imageBack);
        String pathFront = fileManager.getFullPathToImage(imageFront);
        String pathFrontMeteo = fileManager.getFullPathToImage(meteo);
        // load source images
        BufferedImage background = null;
        BufferedImage overlay = null;
        BufferedImage overlayMeteo = null;
        try {
            background = ImageIO.read(new File(pathBack));
            overlay = ImageIO.read(new File(pathFront));
            if(drawMeteo){
                overlayMeteo = ImageIO.read(new File(pathFrontMeteo));
            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        // create the new image, canvas size is the max. of both image sizes
//        int w = Math.max(background.getWidth(), overlay.getWidth());
//        int h = Math.max(background.getHeight(), overlay.getHeight());
        BufferedImage combined = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        if(drawMeteo){
            g.drawImage(overlayMeteo,0,0,null);
        }
        g.drawImage(overlay, x, y, null);

        g.dispose();

        URL tempImgPath = getClass().getClassLoader().getResource("images/temp");

// Save as new image
        if (tempImgPath != null) {
            try {
                long date = new Date().getTime();
                String nomTempFile = date + ".png";
                ImageIO.write(combined, "PNG", new File(tempImgPath.getPath(), date + ".png"));
                return nomTempFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.error("Dossier images temp. non trouvé : " + tempImgPath);
        }

        return "";
    }
}
