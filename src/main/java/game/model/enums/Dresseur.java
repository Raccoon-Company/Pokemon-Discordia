package game.model.enums;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Dresseur {
    RIVAL_0(0, "Rival", NiveauIA.RANDOM, new ArrayList<>(),
            "J'ai toujours rêvé de faire ça !",
            "WOAAA C'ETAIT INCROYABLE !",
            "rival.png",
            Zones.BOURG_PALETTE,Structure.CHAMBRE, TypeCombat.SIMPLE),

    TEST(1, "TEST", NiveauIA.RANDOM, Arrays.asList(new SimpleEntry<>(1, 3), new SimpleEntry<>(151, 2)),
            "J'ai capturé un pokémon super rare !",
            "J'ai testé outro !",
            "kid.png",
            Zones.ROUTE_1, null, TypeCombat.DOUBLE),

    ;

    public static final Dresseur[] rivaux = {RIVAL_0};

    private final int progress;
    private final String nom;
    private final NiveauIA niveauIA;

    private final String texteIntro;
    private final List<SimpleEntry<Integer, Integer>> equipe;
    private final String texteOutro;
    private final String iconPath;
    private final Zones zone;
    private final Structure structure;
    private final TypeCombat typeCombat;

    /**
     * @param nom        nom du dresseur
     * @param niveauIA   méthode d'IA de choix des attaques/actions en combat
     * @param equipe     couple<idPokemon,niveauPokemon>
     * @param texteIntro texte affiché au début du combat
     */
    Dresseur(int progress, String nom, NiveauIA niveauIA, List<SimpleEntry<Integer, Integer>> equipe, String texteIntro, String texteOutro,String iconPath, Zones zone, Structure structure, TypeCombat typeCombat) {
        this.progress = progress;
        this.nom = nom;
        this.niveauIA = niveauIA;
        this.texteIntro = texteIntro;
        this.equipe = equipe;
        this.texteOutro = texteOutro;
        this.iconPath = iconPath;
        this.zone = zone;
        this.structure = structure;
        this.typeCombat = typeCombat;
    }

    public String getIconPath() {
        return iconPath;
    }

    public TypeCombat getTypeCombat() {
        return typeCombat;
    }

    public Zones getZone() {
        return zone;
    }

    public Structure getStructure() {
        return structure;
    }

    public String getTexteOutro() {
        return texteOutro;
    }

    public int getProgress() {
        return progress;
    }

    public String getTexteIntro() {
        return texteIntro;
    }

    public String getNom() {
        return nom;
    }

    public NiveauIA getNiveauIA() {
        return niveauIA;
    }

    public List<SimpleEntry<Integer, Integer>> getEquipe() {
        return equipe;
    }

    public static Dresseur trouverDresseur(Zones zone, Structure structure, int progress){
       return Arrays.stream(values()).filter(a -> a.getProgress() == progress && zone.equals(a.getZone()) && structure == a.getStructure()).findAny().orElse(null);
    }
}
