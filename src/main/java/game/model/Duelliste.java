package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import game.Game;
import game.Save;
import game.model.enums.*;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Duelliste implements Serializable {

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
    public Duelliste(Save save, TypeCombat typeCombat) {
        this.id = save.getUserId();
        this.nom = save.getCampaign().getNom();
        this.niveauIA = NiveauIA.HUMAN;
        this.typeDuelliste = TypeDuelliste.JOUEUR;
        this.potionsRestantes = 0;
        this.equipe = save.getCampaign().getEquipe();
        this.pokemonActif = equipe.stream().filter(Pokemon::estEnVie).findFirst().orElse(null);

        if (typeCombat.equals(TypeCombat.DOUBLE)) {
            this.pokemonActifBis = equipe.stream().filter(a -> a.estEnVie() && !a.equals(pokemonActif)).findFirst().orElse(null);
        }
    }

    /**
     * duelliste à partir d'un pnj
     *
     * @param dresseur
     */
    public Duelliste(Dresseur dresseur, Game game, boolean rival) {
        this.id = dresseur.getProgress();
        if (rival) {
            this.nom = game.getSave().getCampaign().getNomRival();
        } else {
            this.nom = dresseur.getNom();
        }

        this.typeDuelliste = TypeDuelliste.PNJ;
        this.niveauIA = dresseur.getNiveauIA();
        this.potionsRestantes = niveauIA.getNbPotionsAutorisees();
        this.equipe = new ArrayList<>();

        if (rival) {
            EquipesRival.obtenir(game.getSave().getCampaign().getProgress(), game.getSave().getCampaign().getIdStarter()).getEquipe().forEach(k -> {
                Pokemon pokemon = new Pokemon(k.getKey(), k.getValue(), false, game);
                //Cas particulier, on supprime le move stabbé pour le premier fight, sinon ca va chauffer
                if(game.getSave().getCampaign().getProgress() == 0){
                    pokemon.getMoveset().removeIf(a -> a.getIdMoveAPI() == 22 || a.getIdMoveAPI() == 55 || a.getIdMoveAPI() == 52);
                }
                this.equipe.add(pokemon);
            });
        } else {
            dresseur.getEquipe().forEach(k -> {
                Pokemon pokemon = new Pokemon(k.getKey(), k.getValue(), false, game);
                this.equipe.add(pokemon);
            });
        }

        this.pokemonActif = equipe.get(0);
        if (dresseur.getTypeCombat().equals(TypeCombat.DOUBLE)) {
            this.pokemonActifBis = equipe.stream().filter(a -> a.estEnVie() && !a.equals(pokemonActif)).findFirst().orElse(null);
        }
    }

    @JsonIgnore
    public Duelliste getCopy() {
        return (Duelliste) SerializationUtils.clone(this);
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

    public Pokemon getPokemonChoixCourant(int turn) {
        if (pokemonActifBis == null) {
            return pokemonActif;
        }
        if (pokemonActifBis.getActionsCombat().get(turn) != null) {
            return pokemonActif;
        } else if (pokemonActif.getActionsCombat().get(turn) != null) {
            return pokemonActifBis;
        } else {
            return pokemonActif.getCurrentSpeed() >= pokemonActifBis.getCurrentSpeed() ? pokemonActif : pokemonActifBis;
        }
    }

    public boolean estALui(Pokemon pokemon) {
        return pokemon.equals(pokemonActif) || pokemon.equals(pokemonActifBis);
    }

    public void soinsLeger() {
        equipe.forEach(Pokemon::soinLegerApresCombat);
    }


    public List<Pokemon> getPokemonsActifsEnVie() {
        List<Pokemon> enVie = new ArrayList<>();
        if (pokemonActif.estEnVie()) {
            enVie.add(pokemonActif);
        }
        if (pokemonActifBis != null && pokemonActifBis.estEnVie()) {
            enVie.add(pokemonActifBis);
        }
        return enVie;
    }

    public long racketter() {
        if (typeDuelliste.equals(TypeDuelliste.PNJ)) {
            return equipe.stream().map(Pokemon::getLevel).max(Integer::compare).orElse(0) * 80;
        } else {
            return 0;
        }
    }
}
