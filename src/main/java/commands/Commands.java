package commands;

import java.util.Arrays;

public enum Commands {
    HELP("help","Affiche une liste de commandes valides.",0),
    CHANGES("changes","Affiche l'historique des changements", 0),
    START("start", "Commence à jouer !",0);

    private String texte;
    private String description;
    private int minArgs;

    Commands(String texte, String description, int minArgs) {
        this.texte = texte;
        this.minArgs = minArgs;
        this.description = description;
    }

    public static Commands getByTexte(String texte) {
        return Arrays.stream(values()).filter(c -> c.getTexte().equals(texte)).findAny().orElse(null);
    }

    public int getMinArgs() {
        return minArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }
}
