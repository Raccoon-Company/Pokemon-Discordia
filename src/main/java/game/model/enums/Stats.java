package game.model.enums;

public enum Stats {
    HP(1),
    ATTACK(2),
    DEFENSE(3),
    SPECIAL_ATTACK(4),
    SPECIAL_DEFENSE(5),
    SPEED(6),
    ACCURACY(7),
    EVASION(8);

    private final long id;

    Stats(long id) {

        this.id = id;
    }

    public long getId() {
        return id;
    }
}
