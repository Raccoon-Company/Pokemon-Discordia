package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonSpecies;
import com.github.oscar0812.pokeapi.utils.Client;
import executable.MyBot;
import game.model.api.PokemonAPI;
import utils.Utils;

import java.util.Date;

public class Pokemon {
    private static final double BASE_CRIT_MODIFIER = 1.5;
    private static final double CRIT_MODIFIER_SNIPER = 2.25;

    private final static int MAX_EV_PER_STAT = 255;
    private final static int MAX_EV_TOTAL = 510;

    private final long id;

    private int idGender;

    private int idSpecie;

    private int idAbility;

    private boolean shiny;

    private int idItemTenu;

    private String surnom;

    private int level;

    private int xp;

    //0-255
    private int friendship;


//    private List<Attack> moveset;
//    private List<Status> statuses;

    //valeurs de combat
    private int currentHp;

    private int currentCritChance;
    @JsonIgnore
    private double critChanceStage = 0;
    private int hpIV;
    private int hpEV;
    @JsonIgnore
    private int currentAtkSpe;
    private int atkSpeIV;
    private int atkSpeEV;
    @JsonIgnore
    private double atkSpeStage = 0;
    @JsonIgnore
    private int currentAtkPhy;
    private int atkPhyIV;
    private int atkPhyEV;
    @JsonIgnore
    private double atkPhyStage = 0;
    @JsonIgnore
    private int currentDefSpe;
    private int defSpeIV;
    private int defSpeEV;
    @JsonIgnore
    private double defSpeStage = 0;
    @JsonIgnore
    private int currentDefPhy;
    private int defPhyIV;
    private int defPhyEV;
    @JsonIgnore
    private double defPhyStage = 0;
    @JsonIgnore
    private int currentSpeed;
    private int speedIV;
    private int speedEV;
    @JsonIgnore
    private double speedStage = 0;
    @JsonIgnore
    private double accuracyStage = 0;
    @JsonIgnore
    private double evasivenessStage = 0;
    @JsonIgnore
    private boolean lostHealthThisTurn;
    @JsonIgnore
    private boolean hasMovedThisTurn;
    @JsonIgnore
    private boolean isPlayerPokemon;

    public Pokemon(int idSpecie, int level, boolean canEvolve) {

        PokemonSpecies species = Client.getPokemonSpeciesById(idSpecie);


        //TODO nature et moveset
        this.id = Long.parseLong(idSpecie+""+new Date().getTime());
        this.level = level;
        this.xp = 0;
        this.shiny = Utils.getRandom().nextInt(4096) == 1;
        this.lostHealthThisTurn = false;
        this.hasMovedThisTurn = false;
        this.friendship = 0;
        //TODO gender
//        this.idGender = initGender();
        this.level = 0;
        //détermination des IVs
        this.atkSpeIV = Utils.getRandom().nextInt(32);
        this.atkPhyIV = Utils.getRandom().nextInt(32);
        this.defSpeIV = Utils.getRandom().nextInt(32);
        this.defPhyIV = Utils.getRandom().nextInt(32);
        this.hpIV = Utils.getRandom().nextInt(32);
        this.speedIV = Utils.getRandom().nextInt(32);

        //détermination des EVs
        int startingEV = Math.min(85, 0);
        this.atkSpeEV = startingEV;
        this.atkPhyEV = startingEV;
        this.defSpeEV = startingEV;
        this.defPhyEV = startingEV;
        this.hpEV = startingEV;
        this.speedEV = startingEV;

//        levelXTimes(level);
//        //si c'est un pokemon wild ou de npc, on lui donne une chance d'évoluer par lui-même même s'il a normalmeent besoin d'un item ou de bonheur
//            if (canEvolve) {
//            while (this.species.getEvolution() != null && this.species.getLvlEvo() == null && Utils.randomTest(level)) {
//                this.species = this.species.getEvolution();
//            }
//            TypePokemon finalSpecies = species;
//            List<EvoItem> usables = Arrays.stream(EvoItem.values()).filter(i -> i.getMakeEvo().contains(finalSpecies)).collect(Collectors.toList());
//            while (!usables.isEmpty() && Utils.randomTest(level)) {
//                EvoItem item = usables.get(Utils.getRandom().nextInt(usables.size()));
//                species = item.evolveKemonWithItem(this, false);
//                TypePokemon finalSpecies1 = species;
//                usables = Arrays.stream(EvoItem.values()).filter(i -> i.getMakeEvo().contains(finalSpecies1)).collect(Collectors.toList());
//            }
//        }
        //on reset le bonheur apres les levels up, sinon ca fausse car rapportent du bonheur
        this.friendship = 70;
        //talent random si disponible
//        this.talent = species.getTalents().isEmpty() ? null : species.getTalents().get(Utils.getRandom().nextInt(species.getTalents().size()));
//        this.currentCritChance = critChance;
//        this.statuses = new ArrayList<>();
//        this.currentAtkPhy = getMaxAtkPhy();
//        this.currentDefPhy = getMaxDefPhy();
//        this.currentAtkSpe = getMaxAtkSpe();
//        this.currentDefSpe = getMaxDefSpe();
//        this.currentSpeed = getMaxSpeed();
//        this.currentHp = getMaxHp();

    }
}
