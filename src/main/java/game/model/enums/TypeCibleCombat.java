package game.model.enums;

import java.util.Arrays;

public enum TypeCibleCombat {
    SPECIFIC_MOVE(1),
    SELECTED_POKEMON_ME_FIRST(2),
    ALLY(3),
    USERS_FIELD(4),
    USER_OR_ALLY(5),
    OPPONENTS_FIELD(6),
    USER(7),
    RANDOM_OPPONENT(8),
    ALL_OTHER_POKEMON(9),
    SELECTED_POKEMON(10),
    ALL_OPPONENTS(11),
    ENTIRE_FIELD(12),
    USER_AND_ALLIES(13),
    ALL_POKEMON(14),
    ALL_ALLIES(15);

    private final int idApi;

    TypeCibleCombat(int idApi) {

        this.idApi = idApi;
    }

    public int getIdApi() {
        return idApi;
    }

    public static TypeCibleCombat getById(int idApi){
        return Arrays.stream(values()).filter(t -> t.getIdApi() == idApi).findAny().orElse(null);
    }
}
