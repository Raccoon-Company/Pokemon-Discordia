package game.model;

import com.github.oscar0812.pokeapi.utils.Client;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Campaign implements Serializable {

    private String nom; //set à la création de la save
    //déso pas déso y a que 2 genres (true pour gars, false pour fille)
    private boolean gender;//set à la création de la save

    //nom du rival
    private String nomRival;//set à la création de la save
    //id du pokemon starter (1,4,7)
    private int idStarter;//set à la création de la save

    private List<Pokemon> equipe;

    private List<Pokemon> reserve;

    //<idSpecie,status> status : 0 for unseen, 1 for seen, 2 for captured
    private HashMap<Integer, Integer> pokedex;

    private Zones currentZone;

    @Nullable
    private Structure currentStructure;

    public Campaign() {
    }

    public Campaign(String nom, boolean gender, String nomRival, int idStarter) {
        this.nom = nom;
        this.gender = gender;
        this.nomRival = nomRival;
        this.currentZone = Zones.BOURG_PALETTE;
        this.currentStructure = Structure.CHAMBRE;
        this.idStarter = idStarter;
        this.reserve = new ArrayList<>();
        this.equipe = new ArrayList<>();
        this.pokedex = new HashMap<>();
        setUpPokedex();
    }

    private void setUpPokedex() {
        int n = Client.getPokemonSpeciesList(151, 1).getCount();
        for (int i = 1; i <= n; i++) {
            this.pokedex.put(i, 0);
        }
        this.pokedex.replace(idStarter,2);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public HashMap<Integer, Integer> getPokedex() {
        return pokedex;
    }

    public void setPokedex(HashMap<Integer, Integer> pokedex) {
        this.pokedex = pokedex;
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

    @Nullable
    public Structure getCurrentStructure() {
        return currentStructure;
    }

    public void setCurrentStructure(@Nullable Structure currentStructure) {
        this.currentStructure = currentStructure;
    }

    public Pokemon getTeamPokemonById(long id) {
        return getEquipe().stream().filter(s -> s.getId() == id).findAny().orElse(null);
    }
}
