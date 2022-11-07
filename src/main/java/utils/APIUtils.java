package utils;

import com.github.oscar0812.pokeapi.models.utility.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class APIUtils {

    public static List<String> FAUSSES_VERSIONS = Arrays.asList("xd", "colosseum", "lets-go-pikachu-lets-go-eevee");

    public static String getFrName(List<Name> apiObjects){
        return apiObjects.stream().filter(o -> o.getLanguage().getName().equals("fr")).map(Name::getName).findAny().orElse(apiObjects.get(0).getName());
    }

    public static Optional<Object> getPokemonByName(String nom){
        Object result = Client.getPokemonByName(nom);
        if(result == null){
            //TODO erreur api à gérer
        }
        return Optional.ofNullable(result);
    }

}
