package game.model;

public class Campaign {

    private String nom; //set à la création de la save
    //déso pas déso y a que 2 genres (true pour gars, false pour fille)
    private boolean gender;//set à la création de la save

    //nom du rival
    private String nomRival;//set à la création de la save
    //id du pokemon starter (1,4,7)
    private int idStarter;//set à la création de la save

    public Campaign() {
    }

    public Campaign(String nom, boolean gender, String nomRival, int idStarter) {
        this.nom = nom;
        this.gender = gender;
        this.nomRival = nomRival;
        this.idStarter = idStarter;
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
}
