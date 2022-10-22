package game.model.enums;

public enum Regions {
    KANTO(1, "Kanto", 10),
    ;

    private final int id;
    private final String nom;

    private final Integer versionGroupId;

    Regions(int id, String nom, Integer versionGroupId) {
        this.id = id;
        this.nom = nom;
        this.versionGroupId = versionGroupId;
    }

    public Integer getVersionGroupId() {
        return versionGroupId;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
}
