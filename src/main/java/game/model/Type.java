package game.model;

public enum Type {

    NORMAL(1),
    FIGHTING(2),
    FLYING(3),
    POISON(4),
    GROUND(5),
    ROCK(6),
    BUG(7),
    GHOST(8),
    STEEL(9),
    FIRE(10),
    WATER(11),
    GRASS(12),
    ELECTRIC(13),
    PSYCHIC(14),
    ICE(15),
    DRAGON(16),
    DARK(17);

    private final int id;

    Type(int idPokeApi) {
        this.id = idPokeApi;
    }

    public int getId() {
        return id;
    }
}
