package game.model.enums;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Dresseur {
    RIVAL_0(0, "Rival", NiveauIA.RANDOM, "J'ai toujours rêvé de faire ça !", new ArrayList<>()),

    TEST(1, "TEST", NiveauIA.RANDOM, "J'ai testé !", Arrays.asList(new SimpleEntry<>(1,3),new SimpleEntry<>(151,2))),

    ;

    private final int progress;
    private final String nom;
    private final NiveauIA niveauIA;

    private final String texteIntro;
    private final List<SimpleEntry<Integer, Integer>> equipe;

    /**
     * @param nom        nom du dresseur
     * @param niveauIA   méthode d'IA de choix des attaques/actions en combat
     * @param texteIntro texte affiché au début du combat
     * @param equipe     couple<idPokemon,niveauPokemon>
     */
    Dresseur(int progress, String nom, NiveauIA niveauIA, String texteIntro, List<SimpleEntry<Integer, Integer>> equipe) {
        this.progress = progress;
        this.nom = nom;
        this.niveauIA = niveauIA;
        this.texteIntro = texteIntro;
        this.equipe = equipe;
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

    public List<SimpleEntry<Integer,Integer>> getEquipe() {
        return equipe;
    }
}
