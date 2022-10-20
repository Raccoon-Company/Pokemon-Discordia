package utils;

import com.github.oscar0812.pokeapi.models.utility.Name;

import java.util.List;

public class APIUtils {

    public static String getFrName(List<Name> apiObjects){
        return apiObjects.stream().filter(o -> o.getLanguage().getName().equals("fr")).map(Name::getName).findAny().orElse(apiObjects.get(0).getName());
    }

}
