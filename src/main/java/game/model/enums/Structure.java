package game.model.enums;

import game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Structure {
    CHAMBRE(1, "Ma chambre - 1er étage", "structures.chambre", 70, 40, Arrays.asList(), false),
    MAISON_DEPART(2, "Chez moi - Rez de chaussée", "structures.maison-depart", 120, 24, Arrays.asList(PNJ.MOM), true),
    CENTRE_POKEMON(3, "Centre Pokémon", "structures.centre-pokemon", 90, 40, Arrays.asList(PNJ.INFIRMIERE), true),
    BOUTIQUE(4, "Boutique", "structures.boutique", 90, 40, Arrays.asList(PNJ.VENDEUSE), true),
    ;

    private final Logger logger = LoggerFactory.getLogger(Structure.class);
    private final int id;

    private final boolean zoneAccessible;

    private final String nom;
    private final String background;
    //position en x de l'affichage du sprite joueur
    private final int x;
    //position en y de l'affichage du sprite joueur
    private final int y;

    private List<Structure> structuresAccessibles;
    private final List<PNJ> pnjs;

    Structure(int id, String nom, String background, int x, int y, List<PNJ> pnjs, boolean zoneAccessible) {
        this.nom = nom;
        this.x = x;
        this.id = id;
        this.y = y;
        this.background = background;
        this.pnjs = pnjs;
        this.zoneAccessible = zoneAccessible;
        this.structuresAccessibles = Collections.emptyList();
    }

    static {
        MAISON_DEPART.setStructuresAccessibles(Collections.singletonList(CHAMBRE));
        CHAMBRE.setStructuresAccessibles(Collections.singletonList(MAISON_DEPART));

    }

    public static Structure getById(String id) {
        return Arrays.stream(values()).filter(s -> id.equals(String.valueOf(s.getId()))).findAny().orElse(null);
    }

    public static Structure getById(int id) {
        return Arrays.stream(values()).filter(s -> id == s.getId()).findAny().orElse(null);
    }

    public int getId() {
        return id;
    }

    public List<Structure> getStructuresAccessibles() {
        return structuresAccessibles;
    }

    public void setStructuresAccessibles(List<Structure> structuresAccessibles) {
        this.structuresAccessibles = structuresAccessibles;
    }

    public List<PNJ> getPnjs() {
        return pnjs;
    }

    public String getNom() {
        return nom;
    }

    public String getBackground() {
        return background;
    }

    public boolean isZoneAccessible() {
        return zoneAccessible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getBackground(ImageManager imageManager, String front) {
        return imageManager.merge(background, front, x, y, Game.LARGEUR_FOND, Game.HAUTEUR_FOND);
    }
}
