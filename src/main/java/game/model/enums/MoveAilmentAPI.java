package game.model.enums;

import java.util.Arrays;

public enum MoveAilmentAPI {
    UNKNOWN(-1, null),
    NONE(0,null),
    PARALYSIS(1, AlterationEtat.PARALYSIE),
    SLEEP(2, AlterationEtat.SOMMEIL),
    FREEZE(3, AlterationEtat.GEL),
    BURN(4, AlterationEtat.BRULURE),
    POISON(5,AlterationEtat.POISON),
    CONFUSION(6, AlterationEtat.CONFUSION),
    INFATUATION(7,AlterationEtat.CHARME),
    TRAP(8,AlterationEtat.LIEN),
    NIGHTMARE(9,AlterationEtat.CAUCHEMAR),
    TOURMENT(12,AlterationEtat.TOURMENT),
    DISABLE(13,null),
    YAWN(14, AlterationEtat.SOMNOLENCE),
    HEAL_BLOCK(15,AlterationEtat.ANTISOIN),
    NO_TYPE_IMMUNITY(17,null),
    LEECH_SEED(18,AlterationEtat.VAMPIGRAINE),
    EMBARGO(19,AlterationEtat.EMBARGO),
    PERISH_SONG(20,AlterationEtat.REQUIEM),
    INGRAIN(21,AlterationEtat.RACINES)

    ;

    private final int idApi;
    private final AlterationEtat alterationEtat;

    MoveAilmentAPI(int idApi, AlterationEtat alterationEtat) {

        this.idApi = idApi;
        this.alterationEtat = alterationEtat;
    }

    public static MoveAilmentAPI getById(int id) {
        return Arrays.stream(values()).filter(a -> a.getIdApi() == id).findAny().orElse(null);
    }

    public int getIdApi() {
        return idApi;
    }

    public AlterationEtat getAlterationEtat() {
        return alterationEtat;
    }
}
