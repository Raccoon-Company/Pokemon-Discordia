package game.model.enums;

public enum Meteo {
    NEUTRE(""),
    FORT_SOLEIL("meteo.soleil"),
    PLUIE("meteo.pluie"),
    GRELE("meteo.neige"),
    TEMPETE_DE_SABLE("meteo.sandstorm"),
    BROUILLARD(""),
    ;

    private final String filtre;

    Meteo(String filtre) {

        this.filtre = filtre;
    }

    public String getFiltre() {
        return filtre;
    }
}
