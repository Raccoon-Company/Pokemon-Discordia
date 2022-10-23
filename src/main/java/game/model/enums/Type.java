package game.model.enums;

import java.util.Arrays;

public enum Type {

    NORMAL(1, "normal", 1033875566874611722L),
    FIGHTING(2,"combat", 1033875557202530346L),
    FLYING(3,"vol", 1033875560180486207L),
    POISON(4,"poison", 1033875568179028018L),
    GROUND(5,"sol", 1033875564630655026L),
    ROCK(6,"roche", 1033875570888556594L),
    BUG(7,"insecte", 1033875550818799657L),
    GHOST(8,"spectre", 1033875561266810921L),
    STEEL(9,"acier", 1033875571983257600L),
    FIRE(10,"feu", 1033875558649577533L),
    WATER(11,"eau", 1033875573199618098L),
    GRASS(12,"plante", 1033875563225563198L),
    ELECTRIC(13,"elec", 1033875554849529976L),
    PSYCHIC(14,"psy", 1033875569600901170L),
    ICE(15,"glace", 1033875565817638953L),
    DRAGON(16,"dragontype", 1033875553700298874L),
    DARK(17,"tenebres", 1033875552442011729L),
    FEE(18, "fee", 1033875555981983855L);

    private final int id;
    private final String emoji;
    private final long idDiscordEmoji;

    Type(int idPokeApi, String emoji, long idDiscordEmoji) {
        this.id = idPokeApi;
        this.emoji = emoji;
        this.idDiscordEmoji = idDiscordEmoji;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getId() {
        return id;
    }

    public long getIdDiscordEmoji() {
        return idDiscordEmoji;
    }

    public static Type getById(int id){
        return Arrays.stream(values()).filter(a -> a.getId() == id).findAny().orElse(null);
    }
}
