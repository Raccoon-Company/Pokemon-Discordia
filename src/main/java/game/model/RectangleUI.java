package game.model;

import java.awt.*;

public class RectangleUI extends ElementUI{

    private Color color;
    private int largeur;

    private int hauteur;

    public RectangleUI(int x, int y, Color color, int largeur, int hauteur) {
        super(x, y);
        this.color = color;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }
}
