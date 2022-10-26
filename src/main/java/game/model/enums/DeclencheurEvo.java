package game.model.enums;

public enum DeclencheurEvo {
    LEVEL_UP(1),
    TRADE(2),
    USE_ITEM(3),
    SHED(4),
    SPIN(5),
    TOWER_OF_DARKNESS(6),
    TOWER_OF_WATERS(7),
    CRITS(8),
    TAKE_DAMAGE(9),
    OTHER(10);

    private final int idTrigger;

    DeclencheurEvo(int idTrigger) {

        this.idTrigger = idTrigger;
    }

    public int getIdTrigger() {
        return idTrigger;
    }
}
