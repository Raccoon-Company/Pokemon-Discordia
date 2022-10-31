package game.model.enums;

public enum StatutsTerrain {
    MIST(true, false),
    LIGHT_SCREEN(true, false),
    REFLECT(true, false),
    SAFEGUARD(true, false),
    TAILWIND(true, false),
    LUCKY_CHANT(true, false),
    WIDE_GUARD(true, false),
    QUICK_GUARD(true, false),
    MAT_BLOCK(true, false),
    CRAFTY_SHIELD(true, false),
    HAPPY_HOUR(false, false),
    AURORA_VEIL(true, false),


    SPIKES(false, false),
    TOXIC_SPIKES(false, false),
    STEALTH_ROCK(false, false),
    STICKY_WEB(false, false),


    HAZE(true, true),
    SANDSTORM(true, true),
    RAIN_DANCE(true, true),
    SUNNY_DAY(true, true),
    HAIL(true, true),

    MUD_SPORT(true, false),
    WATER_SPORT(true, false),
    GRAVITY(true, false),
    TRICK_ROOM(true, false),
    WONDER_ROOM(true, false),
    MAGIC_ROOM(true, false),
    ION_DELUGE(true, false),
    GRASSY_TERRAIN(true, false),
    MISTY_TERRAIN(true, false),
    FAIRY_LOCK(true, false),
    ELECTRIC_TERRAIN(true, false),
    PSYCHIC_TERRAIN(true, false);

    private final boolean temporaire;
    private final boolean meteo;

    StatutsTerrain(boolean temporaire, boolean meteo) {
        this.temporaire = temporaire;
        this.meteo = meteo;
    }

    public static StatutsTerrain getStatutFromMeteo(Meteo meteo) {
        if(meteo == null){
            return null;
        }
        switch (meteo){
            case FORT_SOLEIL:
                return SUNNY_DAY;
            case PLUIE:
                return RAIN_DANCE;
            case GRELE:
                return HAIL;
            case TEMPETE_DE_SABLE:
                return SANDSTORM;
            case BROUILLARD:
                return HAZE;
            default:
                return null;
        }
    }

    public static Meteo getMeteoFromStatut(StatutsTerrain statutsTerrain) {
        switch (statutsTerrain){
            case SUNNY_DAY:
                return Meteo.FORT_SOLEIL;
            case RAIN_DANCE:
                return Meteo.PLUIE;
            case HAIL:
                return Meteo.GRELE;
            case SANDSTORM:
                return Meteo.TEMPETE_DE_SABLE;
            case HAZE:
                return Meteo.BROUILLARD;
            default:
                return Meteo.NEUTRE;
        }
    }

    public boolean isTemporaire() {
        return temporaire;
    }

    public boolean isMeteo() {
        return meteo;
    }
}
