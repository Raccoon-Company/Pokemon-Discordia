package game.model;

import com.github.oscar0812.pokeapi.models.locations.Location;
import com.github.oscar0812.pokeapi.utils.Client;
import executable.MyBot;
import utils.APIUtils;
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
    BOURG_PALETTE(86, 0, Arrays.asList(2), VILLE, Arrays.asList(PNJ.RAOULT), KANTO, "zones.bourg-palette", 80,50),
    ROUTE_1(88,0, Arrays.asList(),ROUTE, Arrays.asList(PNJ.ECOLIER), KANTO, "zones.route-1-kanto", 145,80 ),
    JADIELLE(154,0, Arrays.asList(3),VILLE, Arrays.asList(), KANTO, "zones.jadielle", 70,85),
    ROUTE_22(102,0, Arrays.asList(),ROUTE, Arrays.asList(), KANTO, "zones.route-22-kanto", 170,90),
    ROUTE_2(99,0, Arrays.asList(),ROUTE, Arrays.asList(), KANTO, "zones.route-2-kanto", 60,90),
    FORET_DE_JADE(155,0, Arrays.asList(),ROUTE, Arrays.asList(), KANTO, "zones.foret-de-jade", 30,100),
    ARGENTA(231,0, Arrays.asList(3),VILLE, Arrays.asList(), KANTO, "zones.argenta", 15,70),

    ;

    private int idZone;
    private int progressNeeded;
    private List<Integer> listeIdStructures;

    private List<Zones> listeZonesAccessibles;
    private ZoneTypes typeZone;
    private List<PNJ> pnjs;

    private Regions region;

    private String background;
    private int x;
    private int y;


    Zones(int idZone, int progressNeeded, List<Integer> listeIdStructures, ZoneTypes typeZone, List<PNJ> pnjs, Regions region, String background, int x, int y) {
        this.idZone = idZone;
        this.progressNeeded = progressNeeded;
        this.listeIdStructures = listeIdStructures;
        this.pnjs = pnjs;
        this.typeZone = typeZone;
        this.region = region;
        this.background = background;
        this.x = x;
        this.y = y;
    }

    static {
        BOURG_PALETTE.setListeZonesAccessibles(Arrays.asList(ROUTE_1));
        ROUTE_1.setListeZonesAccessibles(Arrays.asList(BOURG_PALETTE,JADIELLE));
        JADIELLE.setListeZonesAccessibles(Arrays.asList(ROUTE_1, ROUTE_22,ROUTE_2));
        ROUTE_2.setListeZonesAccessibles(Arrays.asList(FORET_DE_JADE,JADIELLE));
        ROUTE_22.setListeZonesAccessibles(Arrays.asList(JADIELLE));
        FORET_DE_JADE.setListeZonesAccessibles(Arrays.asList(ROUTE_22,ARGENTA));
        ARGENTA.setListeZonesAccessibles(Arrays.asList(FORET_DE_JADE));
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

    public List<Integer> getListeIdStructures() {
        return listeIdStructures;
    }

    public void setListeIdStructures(List<Integer> listeIdStructures) {
        this.listeIdStructures = listeIdStructures;
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

    public Location getPokeApiZone(){
        return Client.getLocationById(idZone);
    }

    public String getNom() {
        return APIUtils.getFrName(getPokeApiZone().getNames());
    }
}
