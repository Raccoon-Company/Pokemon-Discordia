package game.model.enums;

import com.github.oscar0812.pokeapi.models.pokemon.PokemonType;
import com.github.oscar0812.pokeapi.utils.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum Type {
    NORMAL(1, "normal", 1033875566874611722L, "#929BA3"),
    FIGHTING(2, "combat", 1033875557202530346L, "#CE3A67"),
    FLYING(3, "vol", 1033875560180486207L, "#87A3DC"),
    POISON(4, "poison", 1033875568179028018L, "#A867C7"),
    GROUND(5, "sol", 1033875564630655026L, "#D87943"),
    ROCK(6, "roche", 1033875570888556594L, "#C6B88D"),
    BUG(7, "insecte", 1033875550818799657L, "#92C22B"),
    GHOST(8, "spectre", 1033875561266810921L, "#5169AF"),
    STEEL(9, "acier", 1033875571983257600L, "#BDBDD6"),
    FIRE(10, "feu", 1033875558649577533L, "#FF9E54"),
    WATER(11, "eau", 1033875573199618098L, "#4F91D7"),
    GRASS(12, "plante", 1033875563225563198L, "#63BD5A"),
    ELECTRIC(13, "elec", 1033875554849529976L, "#F2D239"),
    PSYCHIC(14, "psy", 1033875569600901170L, "#FA737B"),
    ICE(15, "glace", 1033875565817638953L, "#75D4C4"),
    DRAGON(16, "dragontype", 1033875553700298874L, "#036DC3"),
    DARK(17, "tenebres", 1033875552442011729L, "#5A5365"),
    FEE(18, "fee", 1033875555981983855L, "#ED90E7");

    private final int id;
    private final String emoji;
    private final long idDiscordEmoji;

    private final String codeCouleur;

    Type(int idPokeApi, String emoji, long idDiscordEmoji, String codeCouleur) {
        this.id = idPokeApi;
        this.emoji = emoji;
        this.idDiscordEmoji = idDiscordEmoji;
        this.codeCouleur = codeCouleur;
    }

    public String getCodeCouleur() {
        return codeCouleur;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getId() {
        return id;
    }

    public long getIdDiscordEmoji() {
        return idDiscordEmoji;
    }

    public static Type getById(int id) {
        return Arrays.stream(values()).filter(a -> a.getId() == id).findAny().orElse(null);
    }

    public int pourcentageDegatsAttaque(ArrayList<PokemonType> types) {
        int ratio = 100;

        for (PokemonType type : types) {
            if (type.getType().getDamageRelations().getDoubleDamageFrom().stream().map(d -> Client.getTypeByName(d.getName()).getId()).collect(Collectors.toList()).contains(id)) {
                ratio = ratio * 2;
            } else if (type.getType().getDamageRelations().getHalfDamageFrom().stream().map(d -> Client.getTypeByName(d.getName()).getId()).collect(Collectors.toList()).contains(id)) {
                ratio = ratio / 2;
            } else if (type.getType().getDamageRelations().getNoDamageFrom().stream().map(d -> Client.getTypeByName(d.getName()).getId()).collect(Collectors.toList()).contains(id)) {
                ratio = 0;
            }
        }
        return ratio;
    }
}
