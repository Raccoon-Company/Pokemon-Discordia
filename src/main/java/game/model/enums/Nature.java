package game.model.enums;

import utils.Utils;

public enum Nature {
    //neutrals
    HARDY("Hardi",100,100,100,100,100),
    DOCILE("Docile", 100,100,100,100,100),
    BASHFUL("Pudique", 100,100,100,100,100),
    QUIRKY("Bizarre", 100,100,100,100,100),
    SERIOUS("Sérieux", 100,100,100,100,100),

    //alterations
    LONELY("Solo", 110,90,100,100,100),
    ADAMANT("Rigide", 110,100,90,100,100),
    NAUGHTY("Mauvais", 110,100,100,90,100),
    BRAVE("Brave", 110,100,100,100,90),
    BOLD("Assuré", 90,110,100,100,100),
    IMPISH("Malin", 100,110,90,100,100),
    LAX("Lâche", 100,110,100,90,100),
    RELAXED("Relax", 100,110,100,100,90),
    MODEST("Modeste", 90,100,110,100,100),
    MILD("Doux", 100,90,110,100,100),
    RASH("Foufou", 100,100,110,90,100),
    QUIET("Discret", 100,100,110,100,90),
    CALM("Calme", 90,100,100,110,100),
    GENTLE("Gentil", 100,90,100,110,100),
    CAREFUL("Prudent", 100,100,90,110,100),
    SASSY("Malpoli", 100,100,100,110,90),
    TIMID("Timide", 90,100,100,100,110),
    HASTY("Pressé", 100,90,100,100,110),
    JOLLY("Jovial", 100,100,90,100,110),
    NAIVE("Naïf", 100,100,100,90,110);

    private final String libelle;
    private final int atkPhy;
    private final int defPhy;
    private final int atkSpe;
    private final int defSpe;
    private final int speed;

    Nature(String libelle, int atkPhy, int defPhy, int atkSpe, int defSpe, int speed) {
        this.libelle = libelle;
        this.atkPhy = atkPhy;
        this.defPhy = defPhy;
        this.atkSpe = atkSpe;
        this.defSpe = defSpe;
        this.speed = speed;
    }

    public String getLibelle() {
        return libelle;
    }

    public static Nature random() {
        return values()[Utils.getRandom().nextInt(values().length)];
    }

    public int getAtkPhy() {
        return atkPhy;
    }

    public int getDefPhy() {
        return defPhy;
    }

    public int getAtkSpe() {
        return atkSpe;
    }

    public int getDefSpe() {
        return defSpe;
    }

    public int getSpeed() {
        return speed;
    }
}
