package game.model.enums;

import java.util.Arrays;

public enum Item {
    POKEBALL(4, ItemCategorie.STANDARD_BALLS),
    SUPERBALL(3, ItemCategorie.STANDARD_BALLS),
    HYPERBALL(2, ItemCategorie.STANDARD_BALLS),
    MASTERBALL(1, ItemCategorie.STANDARD_BALLS),
    POTION(17, ItemCategorie.HEALING),
    SUPER_POTION(26, ItemCategorie.HEALING),
    HYPER_POTION(25, ItemCategorie.HEALING),
    MAX_POTION(24, ItemCategorie.HEALING),
    ;


    private final int idApi;
    private final ItemCategorie categorie;

    Item(int idApi, ItemCategorie categorie) {
        this.idApi = idApi;
        this.categorie = categorie;
    }

    public static Item getById(int idItemApi) {
        return Arrays.stream(values()).filter(a -> a.getIdApi() == idItemApi).findAny().orElse(null);
    }

    public int getIdApi() {
        return idApi;
    }

    public ItemCategorie getCategorie() {
        return categorie;
    }
}
