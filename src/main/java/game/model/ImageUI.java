package game.model;

import java.awt.image.BufferedImage;

public class ImageUI extends ElementUI{
    private BufferedImage image;

    public ImageUI(int x, int y, BufferedImage image) {
        super(x, y);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
