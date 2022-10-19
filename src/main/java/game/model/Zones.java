package game.model;

import com.github.oscar0812.pokeapi.models.locations.Location;
import com.github.oscar0812.pokeapi.utils.Client;
import executable.MyBot;
import utils.ImageManager;

import java.awt.image.BufferedImage;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static game.model.Regions.*;
import static game.model.Structure.*;
import static game.model.ZoneTypes.*;

public enum Zones {
    BOURG_PALETTE(86, 0, Arrays.asList(MAISON_DEPART), VILLAGE, Arrays.asList(), KANTO, "zones.bourg-palette", 60,100),
    ;

    private int idZone;
    private int progressNeeded;
    private List<Structure> listeBatimentsSpeciaux;

    private List<Zones> listeZonesAccessibles;
    private ZoneTypes typeZone;
    private List<PNJ> pnjs;

    private Regions region;

    private String background;
    private int x;
    private int y;


    Zones(int idZone, int progressNeeded, List<Structure> listeBatimentsSpeciaux, ZoneTypes typeZone, List<PNJ> pnjs, Regions region, String background, int x, int y) {
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

    static {
        BOURG_PALETTE.setListeZonesAccessibles(Collections.emptyList());
    }

    public static Zones getById(String id) {
        return Arrays.stream(values()).filter(s -> id.equals(String.valueOf(s.getIdZone()))).findAny().orElse(null);
    }

    public List<Zones> getListeZonesAccessibles() {
        return listeZonesAccessibles;
    }

    public void setListeZonesAccessibles(List<Zones> listeZonesAccessibles) {
        this.listeZonesAccessibles = listeZonesAccessibles;
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

    public int getIdZone() {
        return idZone;
    }

    public void setIdZone(int idZone) {
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

    public void getPokeApiZone(){
        Location loc = Client.getLocationById(idZone);
    }
}
