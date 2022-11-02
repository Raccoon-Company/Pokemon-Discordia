package game.model.enums;

import java.util.Arrays;

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

    public static Stats getById(int idApi) {
        return Arrays.stream(values()).filter(s -> s.getId() == idApi).findAny().orElse(null);
    }

    public long getId() {
        return id;
    }
}
