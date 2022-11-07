package game.model.enums;

import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Arrays;

public enum Pokeball {
    POKEBALL(4, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),
    GREATBALL(3, 1.5, Emoji.fromCustom("superball", 1034421567570063400L, false)),
    ULTRABALL(2, 2, Emoji.fromCustom("hyperball", 1034421564210429993L, false)),
    MASTERBALL(1, 1, Emoji.fromCustom("masterball", 1034421566244659280L, false)),
//    SAFARIBALL(5, 1.5, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),
//    NETBALL(6, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)), //"Tries to catch a wild Pokémon. Success rate is 3× for water and bug Pokémon."
//    DIVEBALL(7, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//"Tries to catch a wild Pokémon. Success rate is 3.5× when underwater, fishing, or surfing."
//    NESTBALL(8, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)), //Used in battle : Attempts to catch a wild Pokémon. Has a catch rate of given by `(40 - level) / 10`, where `level` is the wild Pokémon's level, to a maximum of 3.9× for level 1 Pokémon.
//    // If the wild Pokémon's level is higher than 30, this ball has a catch rate of 1×.
//    // If used in a trainer battle, nothing happens and the ball is lost."
//    REPEATBALL(9, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//Tries to catch a wild Pokémon. Success rate is 3× for previously-caught Pokémon."
//    TIMERBALL(10, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//"Tries to catch a wild Pokémon. Success rate increases by 0.1× (Gen V: 0.3×) every turn, to a max of 4×."
//    LUXURYBALL(11, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//"Tries to catch a wild Pokémon. Caught Pokémon start with 200 happiness."
//    PREMIERBALL(12, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//no effect
//    DUSKBALL(13, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//"Tries to catch a wild Pokémon. Success rate is 3.5× at night and in caves."
//    HEALBALL(14, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//"Tries to catch a wild Pokémon. Caught Pokémon are immediately healed."
//    QUICKBALL(15, 1, Emoji.fromCustom("pokeball", 1032561600701399110L, false)),//"Tries to catch a wild Pokémon. Success rate is 4× (Gen V: 5×), but only on the first turn."
//    ;


    private final int idItemApi;
    private final double efficacite;
    private final CustomEmoji emoji;

    Pokeball(int idItemApi, double efficacite, CustomEmoji emoji) {

        this.idItemApi = idItemApi;
        this.efficacite = efficacite;
        this.emoji = emoji;
    }

    public CustomEmoji getEmoji() {
        return emoji;
    }

    public static Pokeball getById(int idPokeball) {
        return Arrays.stream(values()).filter(a -> a.getIdItemApi() == idPokeball).findAny().orElse(null);
    }

    public int getIdItemApi() {
        return idItemApi;
    }

    public double getEfficacite() {
        return efficacite;
    }
}
