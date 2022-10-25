package game.model;

import game.Save;
import game.model.enums.Dresseur;
import game.model.enums.NiveauIA;
import game.model.enums.TypeDuelliste;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Duelliste {

    private long id;

    private String nom;
    private List<Pokemon> equipe;

    private Pokemon pokemonActif;
    private NiveauIA niveauIA;
    private int potionsRestantes;

    private TypeDuelliste typeDuelliste;

    /**
     * duelliste à partir d'un joueur humain
     * @param save
     */
    public Duelliste(Save save) {
        this.id = save.getUserId();
        this.nom = save.getCampaign().getNom();
        this.niveauIA = NiveauIA.HUMAN;
        this.typeDuelliste = TypeDuelliste.JOUEUR;
        this.potionsRestantes = 0;
        this.equipe = save.getCampaign().getEquipe();
        this.pokemonActif = save.getCampaign().getEquipe().get(0);
    }

    /**
     * duelliste à partir d'un pnj
     * @param dresseur
     */
    public Duelliste(Dresseur dresseur){
        this.id = dresseur.getProgress();
        this.nom = dresseur.getNom();
        this.typeDuelliste = TypeDuelliste.PNJ;
        this.niveauIA = dresseur.getNiveauIA();
        this.potionsRestantes = niveauIA.getNbPotionsAutorisees();
        this.equipe = new ArrayList<>();

        dresseur.getEquipe().forEach(k -> {
            Pokemon pokemon = new Pokemon(k.getKey(),k.getValue(), false);
            this.equipe.add(pokemon);
        });
    }

    /**
     * Duelliste à partir d'un pokémon sauvage
     * @param pokemon
     */
    public Duelliste(Pokemon pokemon) {
        this.id = pokemon.getId();
        this.nom = pokemon.getSpecieName();
        this.typeDuelliste = TypeDuelliste.POKEMON_SAUVAGE;
        this.niveauIA = NiveauIA.RANDOM;
        this.potionsRestantes = 0;
        this.equipe = new ArrayList<>(Collections.singleton(pokemon));
        this.pokemonActif = pokemon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Pokemon> getEquipe() {
        return equipe;
    }

    public void setEquipe(List<Pokemon> equipe) {
        this.equipe = equipe;
    }

    public NiveauIA getNiveauIA() {
        return niveauIA;
    }

    public void setNiveauIA(NiveauIA niveauIA) {
        this.niveauIA = niveauIA;
    }

    public int getPotionsRestantes() {
        return potionsRestantes;
    }

    public void setPotionsRestantes(int potionsRestantes) {
        this.potionsRestantes = potionsRestantes;
    }

    public TypeDuelliste getTypeDuelliste() {
        return typeDuelliste;
    }

    public void setTypeDuelliste(TypeDuelliste typeDuelliste) {
        this.typeDuelliste = typeDuelliste;
    }

    public Pokemon getPokemonActif() {
        return pokemonActif;
    }

    public void setPokemonActif(Pokemon pokemonActif) {
        this.pokemonActif = pokemonActif;
    }

    public void soinsLeger() {
        equipe.forEach(Pokemon::postFightHeal);
    }
}
