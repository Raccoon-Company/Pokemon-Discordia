package game.model.enums;

import com.github.oscar0812.pokeapi.models.locations.Location;
import com.github.oscar0812.pokeapi.utils.Client;
import game.Game;
import utils.APIUtils;
import utils.ImageManager;
import utils.PropertiesManager;

import java.util.Arrays;
import java.util.List;

import static game.model.enums.Regions.*;
import static game.model.enums.ZoneTypes.*;

public enum Zones {
    BOURG_PALETTE(86, Meteo.NEUTRE, 0, Arrays.asList(2), VILLE, Arrays.asList(PNJ.RAOULT), KANTO, "zones.bourg-palette", 80,50),
    ROUTE_1(88,  Meteo.TEMPETE_DE_SABLE, 0, Arrays.asList(),ROUTE, Arrays.asList(PNJ.ECOLIER), KANTO, "zones.route-1-kanto", 145,80 ),
    JADIELLE(154,  Meteo.GRELE, 0, Arrays.asList(3,4),VILLE, Arrays.asList(), KANTO, "zones.jadielle", 70,85),
    ROUTE_22(102,  Meteo.NEUTRE, 0, Arrays.asList(),ROUTE, Arrays.asList(), KANTO, "zones.route-22-kanto", 170,90),
    ROUTE_2(99,  Meteo.FORT_SOLEIL, 0, Arrays.asList(),ROUTE, Arrays.asList(), KANTO, "zones.route-2-kanto", 60,90),
    FORET_DE_JADE(155,  Meteo.NEUTRE, 0, Arrays.asList(),ROUTE, Arrays.asList(), KANTO, "zones.foret-de-jade", 30,100),
    ARGENTA(231,  Meteo.NEUTRE, 0, Arrays.asList(3,4),VILLE, Arrays.asList(), KANTO, "zones.argenta", 15,70),

    ;

    private final int idZone;

    private final Meteo meteo;
    private final int progressNeeded;
    private final List<Integer> listeIdStructures;

    private List<Zones> listeZonesAccessibles;
    private final ZoneTypes typeZone;
    private final List<PNJ> pnjs;

    private final Regions region;

    private final String background;
    private final int x;
    private final int y;


    Zones(int idZone, Meteo meteo, int progressNeeded, List<Integer> listeIdStructures, ZoneTypes typeZone, List<PNJ> pnjs, Regions region, String background, int x, int y) {
        this.idZone = idZone;
        this.meteo = meteo;
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
        FORET_DE_JADE.setListeZonesAccessibles(Arrays.asList(ROUTE_2,ARGENTA));
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

    public Meteo getMeteo() {
        return meteo;
    }

    public Regions getRegion() {
        return region;
    }

    public int getIdZone() {
        return idZone;
    }

    public int getProgressNeeded() {
        return progressNeeded;
    }

    public List<Integer> getListeIdStructures() {
        return listeIdStructures;
    }

    public ZoneTypes getTypeZone() {
        return typeZone;
    }

    public String getBackground() {
        return background;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getBackground(ImageManager imageManager, String front){
        return imageManager.merge(background, PropertiesManager.getInstance().getImage(meteo.getFiltre()),front, x, y, Game.LARGEUR_FOND, Game.HAUTEUR_FOND);
    }

    public String getCombatBackground(){
        return "zones.combat." + background.split("\\.")[1];
    }

    public Location getPokeApiZone(){
        return Client.getLocationById(idZone);
    }

    public String getNom() {
        return APIUtils.getFrName(getPokeApiZone().getNames());
    }
}
