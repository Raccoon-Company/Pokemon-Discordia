package game.model.enums;

public enum TypeCombatResultat {
    VICTOIRE("Victoire !"),
    DEFAITE("Tous vos pokémons sont K.O. \nVous rentrez au centre pokémon le plus proche pour soigner vos pokémons !"),
    CAPTURE("Le pokémon a été capturé !"),
    FUITE_JOUEUR("Vous vous enfuyez..."),
    FUITE_ADVERSAIRE("Votre adversaire s'est enfui..."),
    EN_COURS(""),
    ERREUR("");

    private final String description;

    TypeCombatResultat(String description) {

        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
