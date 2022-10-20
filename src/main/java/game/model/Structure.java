package game.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Structure {
    CHAMBRE(1,"Ma chambre - 1er étage", "structures.chambre", 70,40, Arrays.asList()),
    MAISON_DEPART(2,"Chez moi - Rez de chaussée", "structures.maison-depart", 120,24, Arrays.asList(PNJ.MOM)),
    CENTRE_POKEMON(3, "Centre Pokémon", "structures.centre-pokemon", 90,40,Arrays.asList(PNJ.INFIRMIERE)),

    ;

    private final Logger logger = LoggerFactory.getLogger(Structure.class);
    private int id;

    private String nom;
    private String background;
    //position en x de l'affichage du sprite joueur
    private int x;
   //position en y de l'affichage du sprite joueur
    private int y;

    private List<Structure> structuresAccessibles;
    private List<PNJ> pnjs;

    Structure(int id, String nom, String background, int x, int y, List<PNJ> pnjs) {
        this.nom = nom;
        this.x = x;
        this.id = id;
        this.y = y;
        this.background = background;
        this.pnjs = pnjs;
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

    public void setId(int id) {
        this.id = id;
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

    public void setPnjs(List<PNJ> pnjs) {
        this.pnjs = pnjs;
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
