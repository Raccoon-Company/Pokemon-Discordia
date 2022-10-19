package game.model;

import utils.ImageManager;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public enum Structure {
    CHAMBRE("Chambre", "structures.chambre", 70,40, null),
    MAISON_DEPART("Maison", "structures.maison-depart", 120,24, Zones.BOURG_PALETTE),

    ;

    private String nom;
    private String background;
    //position en x de l'affichage du sprite joueur
    private int x;
   //position en y de l'affichage du sprite joueur
    private int y;

    private Zones zone;

    Structure(String nom, String background, int x, int y, Zones zone) {
        this.nom = nom;
        this.x = x;
        this.zone = zone;
        this.y = y;
        this.background = background;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getBackground(ImageManager imageManager, String front){
        return imageManager.merge(background, front, x, y);
    }
}
