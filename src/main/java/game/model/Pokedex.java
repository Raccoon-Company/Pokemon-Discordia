package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.oscar0812.pokeapi.utils.Client;

import java.util.HashMap;

public class Pokedex {

    //<idSpecie,status> status : 0 for unseen, 1 for seen, 2 for captured
    private HashMap<Integer, Integer> avancement;

    public Pokedex() {
        this.avancement = new HashMap<>();
        int n = Client.getPokemonSpeciesList(151, 1).getCount();
        for (int i = 1; i <= n; i++) {
            //init
            this.avancement.put(i, 0);
        }
    }

    public HashMap<Integer, Integer> getAvancement() {
        return avancement;
    }

    public void setAvancement(HashMap<Integer, Integer> avancement) {
        this.avancement = avancement;
    }

    public void captured(int idPokemonSpecie) {
        avancement.replace(idPokemonSpecie, 2);
    }

    public void saw(int idPokemonSpecie) {
        avancement.replace(idPokemonSpecie, 1);
    }

    @JsonIgnore
    public int getNombrePokemonsCaptures(){
        return Math.toIntExact(avancement.entrySet().stream().filter(e -> e.getValue() == 2).count());
    }

    @JsonIgnore
    public int getNombrePokemonsVus(){
        return Math.toIntExact(avancement.entrySet().stream().filter(e -> e.getValue() == 1 || e.getValue() == 2).count());
    }
}
