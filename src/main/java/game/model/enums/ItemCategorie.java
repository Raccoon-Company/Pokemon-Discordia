package game.model.enums;

public enum ItemCategorie {
    STANDARD_BALLS(34),
    HEALING(27),

    ;


    private final int idCategorieItem;

    ItemCategorie(int idCategorieItem) {

        this.idCategorieItem = idCategorieItem;
    }

    public int getIdCategorieItem() {
        return idCategorieItem;
    }
}
