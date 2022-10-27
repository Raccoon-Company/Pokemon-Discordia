package game.model;

import game.Save;
import game.model.enums.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Duelliste {

    private long id;

    private String nom;
    private List<Pokemon> equipe;

    private Pokemon pokemonActif;
    private Pokemon pokemonActifBis;
    private NiveauIA niveauIA;
    private int potionsRestantes;

    private TypeDuelliste typeDuelliste;

    /**
     * duelliste à partir d'un joueur humain
     *
     * @param save
     */
    public Duelliste(Save save) {
        this.id = save.getUserId();
        this.nom = save.getCampaign().getNom();
        this.niveauIA = NiveauIA.HUMAN;
        this.typeDuelliste = TypeDuelliste.JOUEUR;
        this.potionsRestantes = 0;
        this.equipe = save.getCampaign().getEquipe();
        this.pokemonActif = equipe.get(0);

        if(equipe.size()>1){
            this.pokemonActif = equipe.get(1);
        }
    }

    /**
     * duelliste à partir d'un pnj
     *
     * @param dresseur
     */
    public Duelliste(Dresseur dresseur) {
        this.id = dresseur.getProgress();
        this.nom = dresseur.getNom();
        this.typeDuelliste = TypeDuelliste.PNJ;
        this.niveauIA = dresseur.getNiveauIA();
        this.potionsRestantes = niveauIA.getNbPotionsAutorisees();
        this.equipe = new ArrayList<>();

        dresseur.getEquipe().forEach(k -> {
            Pokemon pokemon = new Pokemon(k.getKey(), k.getValue(), false);
            this.equipe.add(pokemon);
        });

        this.pokemonActif = equipe.get(0);

        if(equipe.size()>1){
            this.pokemonActif =equipe.get(1);
        }
    }

    /**
     * Duelliste à partir d'un pokémon sauvage
     *
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
        //pas de double combat contre des pokémons sauvages
        this.pokemonActifBis = null;
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

    public Pokemon getPokemonActifBis() {
        return pokemonActifBis;
    }

    public void setPokemonActifBis(Pokemon pokemonActifBis) {
        this.pokemonActifBis = pokemonActifBis;
    }

    public Pokemon getPokemonChoixCourant(int turn){
        if(pokemonActifBis.getActionsCombat().get(turn) != null){
            return pokemonActif;
        }else if(pokemonActif.getActionsCombat().get(turn) != null){
            return pokemonActifBis;
        }else{
            return pokemonActif.getCurrentSpeed() >= pokemonActifBis.getCurrentSpeed() ? pokemonActif : pokemonActifBis;
        }
    }

    public boolean estALui(Pokemon pokemon){
        return pokemon.equals(pokemonActif) || pokemon.equals(pokemonActifBis);
    }

    public void soinsLeger() {
        equipe.forEach(Pokemon::soinLegerApresCombat);
    }

    public void decrementerAlterations() {
        getPokemonActif().getAlterations().stream().filter(a -> a.getAlterationEtat().equals(AlterationEtat.GEL) || a.getAlterationEtat().equals(AlterationEtat.SOMMEIL) || !a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE)).forEach(v -> {
            v.setToursRestants(v.getToursRestants() - 1);
        });
        getPokemonActif().enleveAlterationsPerimees();
    }

    public long racketter() {
        if(typeDuelliste.equals(TypeDuelliste.PNJ)){
            return equipe.stream().map(Pokemon::getLevel).max(Integer::compare).orElse(0) * 80;
        }else{
            return 0;
        }
    }
}
