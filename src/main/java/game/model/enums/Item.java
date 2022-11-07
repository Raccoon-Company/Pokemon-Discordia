package game.model.enums;

import java.util.Arrays;

public enum Item {
    POKEBALL("Pokéball",4, ItemCategorie.STANDARD_BALLS),
    SUPERBALL("Super Ball",3, ItemCategorie.STANDARD_BALLS),
    HYPERBALL("Hyper Ball",2, ItemCategorie.STANDARD_BALLS),
    MASTERBALL("Master Ball",1, ItemCategorie.STANDARD_BALLS),
//    SAFARIBALL("Safari ball",5, ItemCategorie.STANDARD_BALLS),
//
//    NETBALL("Filet Ball",6, ItemCategorie.SPECIAL_BALLS), //"Tries to catch a wild Pokémon. Success rate is 3× for water and bug Pokémon."
//    DIVEBALL("Scuba Ball",7,ItemCategorie.SPECIAL_BALLS),//"Tries to catch a wild Pokémon. Success rate is 3.5× when underwater, fishing, or surfing."
//    NESTBALL("Faiblo Ball",8,ItemCategorie.SPECIAL_BALLS), //Used in battle : Attempts to catch a wild Pokémon. Has a catch rate of given by `(40 - level) / 10`, where `level` is the wild Pokémon's level, to a maximum of 3.9× for level 1 Pokémon.
//    // If the wild Pokémon's level is higher than 30, this ball has a catch rate of 1×.
//    // If used in a trainer battle, nothing happens and the ball is lost."
//    REPEATBALL("Bis Ball",9,ItemCategorie.SPECIAL_BALLS),//Tries to catch a wild Pokémon. Success rate is 3× for previously-caught Pokémon."
//    TIMERBALL("Chrono Ball",10,ItemCategorie.SPECIAL_BALLS),//"Tries to catch a wild Pokémon. Success rate increases by 0.1× (Gen V: 0.3×) every turn, to a max of 4×."
//    LUXURYBALL("Luxe Ball",11,ItemCategorie.SPECIAL_BALLS),//"Tries to catch a wild Pokémon. Caught Pokémon start with 200 happiness."
//    PREMIERBALL("Mémoire Ball",12,ItemCategorie.SPECIAL_BALLS),//no effect
//    DUSKBALL("Sombre Ball",13,ItemCategorie.SPECIAL_BALLS),//"Tries to catch a wild Pokémon. Success rate is 3.5× at night and in caves."
//    HEALBALL("Soin Ball",14,ItemCategorie.SPECIAL_BALLS),//"Tries to catch a wild Pokémon. Caught Pokémon are immediately healed."
//    QUICKBALL("Rapide Ball",15,ItemCategorie.SPECIAL_BALLS),//"Tries to catch a wild Pokémon. Success rate is 4× (Gen V: 5×), but only on the first turn."

    POTION("Potion",17, ItemCategorie.HEALING),
    SUPER_POTION("Super potion",26, ItemCategorie.HEALING),
    HYPER_POTION("Hyper potion",25, ItemCategorie.HEALING),
    MAX_POTION("Max potion",24, ItemCategorie.HEALING),
    ;


    private final String libelle;
    private final int idApi;
    private final ItemCategorie categorie;

    Item(String libelle,int idApi, ItemCategorie categorie) {
        this.libelle = libelle;
        this.idApi = idApi;
        this.categorie = categorie;
    }

    public static Item getById(int idItemApi) {
        return Arrays.stream(values()).filter(a -> a.getIdApi() == idItemApi).findAny().orElse(null);
    }

    public int getIdApi() {
        return idApi;
    }

    public String getLibelle() {
        return libelle;
    }

    public ItemCategorie getCategorie() {
        return categorie;
    }
}
