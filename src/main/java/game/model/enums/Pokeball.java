package game.model.enums;

import java.util.Arrays;

public enum Pokeball {
    POKEBALL(4,1),
    GREATBALL(3,1.5),
    ULTRABALL(2,2),
    MASTERBALL(1,1);


    private final int idItemApi;
    private final double efficacite;

    Pokeball(int idItemApi, double efficacite) {

        this.idItemApi = idItemApi;
        this.efficacite = efficacite;
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
