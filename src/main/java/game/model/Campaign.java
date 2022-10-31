package game.model;

import com.github.oscar0812.pokeapi.utils.Client;
import game.model.enums.Item;
import game.model.enums.Meteo;
import game.model.enums.Structure;
import game.model.enums.Zones;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Campaign implements Serializable {

    private static final long ARGENT_DEPART = 5000;
    private final Logger logger = LoggerFactory.getLogger(Campaign.class);
    private String nom; //set à la création de la save
    //déso pas déso y a que 2 genres (true pour gars, false pour fille)
    private boolean gender;//set à la création de la save

    //nom du rival
    private String nomRival;//set à la création de la save
    //id du pokemon starter (1,4,7)
    private int idStarter;//set à la création de la save

    private List<Pokemon> equipe;

    private List<Pokemon> reserve;

    private Pokedex pokedex;
    private Inventaire inventaire;

    private long pokedollars;

    private int progress;
    private Zones currentZone;

    private Zones zoneCentrePokemon;

    @Nullable
    private Structure currentStructure;
    private Meteo currentMeteo;

    public Campaign() {
    }

    public Campaign(String nom, boolean gender, String nomRival, int idStarter) {
        this.nom = nom;
        this.gender = gender;
        this.pokedollars = ARGENT_DEPART;
        this.nomRival = nomRival;
        this.currentZone = Zones.BOURG_PALETTE;
        this.currentStructure = Structure.LABO;
        this.idStarter = idStarter;
        this.reserve = new ArrayList<>();
        this.equipe = new ArrayList<>();
        this.progress = 0;
        this.pokedex = new Pokedex();
        this.pokedex.captured(idStarter);
        this.zoneCentrePokemon = Zones.BOURG_PALETTE;
        this.inventaire = new Inventaire();
        inventaire.ajoutItem(Item.POKEBALL, 10);
        inventaire.ajoutItem(Item.POTION,2);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getPokedollars() {
        return pokedollars;
    }

    public void setPokedollars(long pokedollars) {
        this.pokedollars = pokedollars;
    }

    public void gagnerArgent(long montant){
        pokedollars += montant;
    }

    public void depenserArgent(long montant){
        if(montant > pokedollars){
            logger.error("Pas assez d'argent ("+pokedollars+") pour dépenser "+montant+" !");
            throw new IllegalStateException("Problème d'argent !");
        }else{
            pokedollars -= montant;
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getNomRival() {
        return nomRival;
    }

    public void setNomRival(String nomRival) {
        this.nomRival = nomRival;
    }

    public int getIdStarter() {
        return idStarter;
    }

    public void setIdStarter(int idStarter) {
        this.idStarter = idStarter;
    }

    public List<Pokemon> getEquipe() {
        return equipe;
    }

    public void setEquipe(List<Pokemon> equipe) {
        this.equipe = equipe;
    }

    public List<Pokemon> getReserve() {
        return reserve;
    }

    public void setReserve(List<Pokemon> reserve) {
        this.reserve = reserve;
    }

    public Zones getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(Zones currentZone) {
        this.currentZone = currentZone;
    }

    public Pokedex getPokedex() {
        return pokedex;
    }

    public void setPokedex(Pokedex pokedex) {
        this.pokedex = pokedex;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }

    @Nullable
    public Structure getCurrentStructure() {
        return currentStructure;
    }

    public void setCurrentStructure(@Nullable Structure currentStructure) {
        this.currentStructure = currentStructure;
    }

    public Zones getZoneCentrePokemon() {
        return zoneCentrePokemon;
    }

    public void setZoneCentrePokemon(Zones zoneCentrePokemon) {
        this.zoneCentrePokemon = zoneCentrePokemon;
    }

    public Pokemon getTeamPokemonById(long id) {
        return getEquipe().stream().filter(s -> s.getId() == id).findAny().orElse(null);
    }

    public void setCurrentMeteo(Meteo currentMeteo) {
        this.currentMeteo = currentMeteo;
    }

    public Meteo getCurrentMeteo() {
        return currentMeteo;
    }
}
