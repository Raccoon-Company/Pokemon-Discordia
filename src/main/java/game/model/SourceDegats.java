package game.model;

import game.model.enums.TypeSourceDegats;

public class SourceDegats {
    private TypeSourceDegats typeSourceDegats;
    private Pokemon pokemonSource;

    public SourceDegats(TypeSourceDegats typeSourceDegats, Pokemon pokemonSource) {
        this.typeSourceDegats = typeSourceDegats;
        this.pokemonSource = pokemonSource;
    }

    public SourceDegats(TypeSourceDegats typeSourceDegats) {
        this.typeSourceDegats = typeSourceDegats;
    }

    public TypeSourceDegats getTypeSourceDegats() {
        return typeSourceDegats;
    }

    public void setTypeSourceDegats(TypeSourceDegats typeSourceDegats) {
        this.typeSourceDegats = typeSourceDegats;
    }

    public Pokemon getPokemonSource() {
        return pokemonSource;
    }

    public void setPokemonSource(Pokemon pokemonSource) {
        this.pokemonSource = pokemonSource;
    }
}
