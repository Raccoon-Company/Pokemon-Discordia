package game.model;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Campaign {

    private String nom; //set à la création de la save
    //déso pas déso y a que 2 genres (true pour gars, false pour fille)
    private boolean gender;//set à la création de la save

    //nom du rival
    private String nomRival;//set à la création de la save
    //id du pokemon starter (1,4,7)
    private int idStarter;//set à la création de la save

    private List<Pokemon> equipe;

    private List<Pokemon> reserve;

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
}
