package game.model;

import game.model.enums.TypeActionCombat;
import game.model.enums.TypeCibleCombat;

public class ActionCombat {
    private TypeActionCombat typeActionCombat;
    private Attaque attaque;
    private TypeCibleCombat typeCibleCombat;
    private Pokemon pokemonCible;
    private Terrain terrainCible;

    public ActionCombat(TypeActionCombat typeActionCombat) {
        this.typeActionCombat = typeActionCombat;
        this.attaque = null;
        this.typeCibleCombat = null;
        this.terrainCible = null;
        this.pokemonCible = null;
    }

    public ActionCombat(TypeActionCombat typeActionCombat, Attaque attaque, TypeCibleCombat typeCibleCombat, Pokemon pokemonCible) {
        this.typeActionCombat = typeActionCombat;
        this.attaque = attaque;
        this.typeCibleCombat = typeCibleCombat;
        this.pokemonCible = pokemonCible;
        this.terrainCible = null;
    }

    public ActionCombat(TypeActionCombat typeActionCombat, Attaque attaque, TypeCibleCombat typeCibleCombat, Terrain terrainCible) {
        this.typeActionCombat = typeActionCombat;
        this.attaque = attaque;
        this.typeCibleCombat = typeCibleCombat;
        this.terrainCible = terrainCible;
        this.pokemonCible = null;
    }

    public ActionCombat(TypeActionCombat typeActionCombat, Attaque attaque, TypeCibleCombat typeCibleCombat) {
        this.typeActionCombat = typeActionCombat;
        this.attaque = attaque;
        this.typeCibleCombat = typeCibleCombat;
        this.terrainCible = null;
        this.pokemonCible = null;
    }

    public TypeActionCombat getTypeActionCombat() {
        return typeActionCombat;
    }

    public void setTypeActionCombat(TypeActionCombat typeActionCombat) {
        this.typeActionCombat = typeActionCombat;
    }

    public Attaque getAttaque() {
        return attaque;
    }

    public void setAttaque(Attaque attaque) {
        this.attaque = attaque;
    }

    public TypeCibleCombat getTypeCibleCombat() {
        return typeCibleCombat;
    }

    public void setTypeCibleCombat(TypeCibleCombat typeCibleCombat) {
        this.typeCibleCombat = typeCibleCombat;
    }

    public Pokemon getPokemonCible() {
        return pokemonCible;
    }

    public void setPokemonCible(Pokemon pokemonCible) {
        this.pokemonCible = pokemonCible;
    }

    public Terrain getTerrainCible() {
        return terrainCible;
    }

    public void setTerrainCible(Terrain terrainCible) {
        this.terrainCible = terrainCible;
    }
}
