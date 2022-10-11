package game.model;

public enum PNJ {
    SYSTEM("System", "system.png"),
    RAOULT("Prof. Didier Raoult", "raoult.jpg");

    //nom du pnj
    private String nom;
    //url du fichier icone du pnj
    private String iconPath;

    PNJ(String nom, String path) {
        this.iconPath = path;
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
