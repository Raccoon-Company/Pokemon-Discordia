package game.model.enums;

import java.util.Arrays;

public enum Type {

    NORMAL(1, "normal", 1033860570266484776L),
    FIGHTING(2,"combat", 1033860560149811200L),
    FLYING(3,"vol", 1033860557683572846L),
    POISON(4,"poison", 1033860572741107742L),
    GROUND(5,"sol", 1033860576713117878L),
    ROCK(6,"roche", 1033860575664541716L),
    BUG(7,"insecte", 1033860568924299364L),
    GHOST(8,"spectre", 1033860578004963398L),
    STEEL(9,"acier", 1033860558853767198L),
    FIRE(10,"feu", 1033860566151868518L),
    WATER(11,"eau", 1033860562880299108L),
    GRASS(12,"plante", 1033860571470254100L),
    ELECTRIC(13,"elec", 1033860564054720614L),
    PSYCHIC(14,"psy", 1033860574221701190L),
    ICE(15,"glace", 1033860567875715142L),
    DRAGON(16,"dragontype", 1033860561525543103L),
    DARK(17,"tenebres", 1033860556324622417L),
    FEE(18, "fee", 1033860565317193828L);

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
