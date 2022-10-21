package game.model.enums;

import static game.model.enums.TypeAlteration.*;

public enum AlterationEtat {
    BRULURE(NON_VOLATILE, "est brûlé !", "souffre de sa brûlure !", "images.statusIcons.brulure", 0.66f),
    PARALYSIE(NON_VOLATILE, "est paralysé !", "est paralysé et ne peut pas bouger !", "images.statusIcons.paralysie", 0.5f),
    GEL(NON_VOLATILE, "est gelé !", "est gelé et ne peut pas bouger !", "images.statusIcons.gel", 0.25f),
    SOMMEIL(NON_VOLATILE, "s'endort...", "est endormi.", "images.statusIcons.sommeil", 0.25f),
    POISON(NON_VOLATILE, "est empoisonné.", "souffre du poison", "images.statusIcons.poison", 0.75f),
    POISON_GRAVE(NON_VOLATILE, "est empoisonné gravement.", "souffre du poison", "images.statusIcons.poison-grave", 0.66f),

    CONFUSION(VOLATILE, "est confus !", "est confus ! Il se blesse lui-même dans sa confusion !", "", 0.66f),
    LIEN(VOLATILE, "est entravé.", "", "", 0.66f),
    NO_ESCAPE(VOLATILE, "ne peut plus s'échapper !", "", "", 0.9f),
    MALEDICTION(VOLATILE, "est maudit !", "est affecté par la malédiction !", "", 0.4f),
    SOMNOLENCE(VOLATILE, "commence à somnoler...", "", "", 0.75f),
    EMBARGO(VOLATILE, "ne peut plus utiliser d'objets !", "ne peut plus utiliser d'objets !", "", 0.85f),
    ENCORE(VOLATILE, "en a encore !", "", "", 0.9f),
    APEURE(VOLATILE, "est effrayé !", "est effrayé et n'ose pas attaquer", "", 0.8f),
    ANTISOIN(VOLATILE, "est empêché de se soigner.", "ne peut pas se soigner !", "", 0.9f),
    IDENTIFIE(VOLATILE, "est identifié.", "", "", 0.95f),
    CHARME(VOLATILE, "est charmé !", "", "", 0.66f),
    VAMPIGRAINE(VOLATILE, "est infecté.", "est infecté !", "", 0.66f),
    CAUCHEMAR(VOLATILE, "commence à cauchemarder !", "fait des cauchemars !", "", 0.66f),
    PROVOCATION(VOLATILE, "répond à la Provoc !", "ne peut pas utiliser cette attaque après la Provoc !", "", 0.85f),
    LEVIKINESIE(VOLATILE, "est soulevé dans les airs !", "", "", 0.9f),
    REQUIEM(VOLATILE, "entend le requiem ! Les pokémons au combat seront K.O. dans 3 tours !", "", "", 0.4f),
    TOURMENT(VOLATILE, "est victime de la tourmente !", "", "", 0.85f),

    ANNEAU_HYDRO(VOLATILE_BATTLE, "s'entoure d'un voile d'eau !", " restaure ses points de vie grâce à Anneau Hydro", "", 1.25f),
    CHARGING_TURN(VOLATILE_BATTLE, "", "ne peut pas attaquer ce tour-ci !", "", 0.8f),
    ENDURE(VOLATILE_BATTLE, "endurera le prochain coup !", "tient le choc !", "", 1.1f),
    CENTRE_ATTENTION(VOLATILE_BATTLE, "devient le centre de l'attention", "", "", 1.1f),
    BOULARMURE(VOLATILE_BATTLE, "", "", "", 1.05f),
    RACINES(VOLATILE_BATTLE, "plante ses racines !", "absorbe des nutriments avec ses racines !", "", 1.15f),
    REFLET_MAGIK(VOLATILE_BATTLE, "s'entoure du reflet magik !", "", "", 1.2f),
    LEVITATION(VOLATILE_BATTLE, "lévite sur un champ magnétique !", "", "", 1.1f),
    LILLIPUT(VOLATILE_BATTLE, "augmente son esquive.", "", "", 0.9f),
    MIMIQUE(VOLATILE_BATTLE, "", "", "", 1.1f),
    PROTECTION(VOLATILE_BATTLE, "se protège !", "se protège !", "", 1.3f),
    RECHARGE(VOLATILE_BATTLE, "est fatigué et doit se reposer !", "", "", 0.8f),
    CLONAGE(VOLATILE_BATTLE, "créé un clone !", "", "", 1.5f),
    SEMI_INVULNERABLE(VOLATILE_BATTLE, "", "", "", 1.3f),
    VISEE(VOLATILE_BATTLE, "se concentre pour viser...", "", "", 1.1f),
    THRASHING(VOLATILE_BATTLE, "", "", "", 0.66f),
    TRANSFORME(VOLATILE_BATTLE, "se transforme !", "", "", 1f),
    ;

    private final TypeAlteration typeAlteration;

    private final String afflictionText;
    private final String applicationText;

    private final String icone;

    //ratio de désirabilité (utilisé par l'AI)
    private final float ratio;

    AlterationEtat(TypeAlteration typeAlteration, String applicationText, String afflictionText, String icone, float ratio) {
        this.typeAlteration = typeAlteration;
        this.afflictionText = afflictionText;
        this.applicationText = applicationText;
        this.icone = icone;
        this.ratio = ratio;
    }

    public String getApplicationText() {
        return applicationText;
    }

    public float getRatio() {
        return ratio;
    }

    public TypeAlteration getTypeAlteration() {
        return typeAlteration;
    }

    public String getAfflictionText() {
        return afflictionText;
    }

    public String getIcone() {
        return icone;
    }
}
