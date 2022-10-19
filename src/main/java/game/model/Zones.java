package game.model;

import utils.ImageManager;

import java.awt.image.BufferedImage;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

import static game.model.Regions.*;
import static game.model.Structure.*;
import static game.model.ZoneTypes.*;

public enum Zones {
    BOURG_PALETTE(86, 0, Arrays.asList(MAISON_DEPART), VILLAGE, Arrays.asList(PNJ.RAOULT), KANTO, "zones.bourg-palette", 60,100),
    ;

    private long idZone;
    private int progressNeeded;
    private List<Structure> listeBatimentsSpeciaux;
    private ZoneTypes typeZone;
    private List<PNJ> pnjs;

    private Regions region;

    private String background;
    private int x;
    private int y;


    Zones(long idZone, int progressNeeded, List<Structure> listeBatimentsSpeciaux, ZoneTypes typeZone, List<PNJ> pnjs, Regions region, String background, int x, int y) {
        this.idZone = idZone;
        this.progressNeeded = progressNeeded;
        this.listeBatimentsSpeciaux = listeBatimentsSpeciaux;
        this.pnjs = pnjs;
        this.typeZone = typeZone;
        this.region = region;
        this.background = background;
        this.x = x;
        this.y = y;
    }

    public List<PNJ> getPnjs() {
        return pnjs;
    }

    public void setPnjs(List<PNJ> pnjs) {
        this.pnjs = pnjs;
    }

    public Regions getRegion() {
        return region;
    }

    public void setRegion(Regions region) {
        this.region = region;
    }

    public long getIdZone() {
        return idZone;
    }

    public void setIdZone(long idZone) {
        this.idZone = idZone;
    }

    public int getProgressNeeded() {
        return progressNeeded;
    }

    public void setProgressNeeded(int progressNeeded) {
        this.progressNeeded = progressNeeded;
    }

    public List<Structure> getListeBatimentsSpeciaux() {
        return listeBatimentsSpeciaux;
    }

    public void setListeBatimentsSpeciaux(List<Structure> listeBatimentsSpeciaux) {
        this.listeBatimentsSpeciaux = listeBatimentsSpeciaux;
    }

    public ZoneTypes getTypeZone() {
        return typeZone;
    }

    public void setTypeZone(ZoneTypes typeZone) {
        this.typeZone = typeZone;
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
