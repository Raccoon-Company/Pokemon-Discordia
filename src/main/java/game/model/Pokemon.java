package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonSpecies;
import com.github.oscar0812.pokeapi.utils.Client;
import game.model.enums.AlterationEtat;
import game.model.enums.Type;
import game.model.enums.TypeAlteration;
import utils.APIUtils;
import utils.Utils;

import java.io.Serializable;
import java.util.*;

public class Pokemon implements Serializable {
    private static final double BASE_CRIT_MODIFIER = 1.5;
    private static final double CRIT_MODIFIER_SNIPER = 2.25;

    private final static int MAX_EV_PER_STAT = 255;
    private final static int MAX_EV_TOTAL = 510;
    private static final int MAX_FRIENDSHIP_VALUE = 255;
    public final static int BASE_FRIENDSHIP_VALUE = 70;

    private long id;

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

    private List<AlterationInstance> alterations;

    private List<Attaque> moveset;

    private Type type1;
    private Type type2; //nullable

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

        this.idSpecie = idSpecie;
        //TODO nature et moveset
        this.id = Long.parseLong(idSpecie + "" + new Date().getTime());
        this.level = level;
        this.xp = 0;
        this.shiny = Utils.getRandom().nextInt(4096) == 1;
        this.lostHealthThisTurn = false;
        this.hasMovedThisTurn = false;
        this.friendship = 0;
        //TODO gender
        //2 : genderless, 0 : female, 1 : male
        this.idGender = getPokemonSpeciesAPI().getGenderRate() == -1 ? 2 : Utils.getRandom().nextInt(8) > getPokemonSpeciesAPI().getGenderRate() ? 1 : 0;
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

        this.alterations = new ArrayList<>();

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
        this.friendship = getPokemonSpeciesAPI().getBaseHappiness();
        this.moveset = new ArrayList<>();
//        fillMoveset();

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

    @JsonIgnore
    public PokemonSpecies getPokemonSpeciesAPI(){
        return Client.getPokemonSpeciesById(idSpecie);
    }

    @JsonIgnore
    public com.github.oscar0812.pokeapi.models.pokemon.Pokemon getPokemonAPI(){
        return Client.getPokemonById(idSpecie);
    }

    @JsonIgnore
    public String getSpecieName(){
        return APIUtils.getFrName(getPokemonSpeciesAPI().getNames());
    }
    @JsonIgnore
    public int getTotalEV() {
        return getHpEV() + getSpeedEV() + getAtkPhyEV() + getAtkSpeEV() + getDefPhyEV() + getDefSpeEV();
    }
    @JsonIgnore
    public int getMaxHp() {
        return (((2 * getPokemonAPI().getBaseStat().getHp() + hpIV + (hpEV / 4)) * level) / 100) + level + 10;
    }

    @JsonIgnore
    public int getMaxAtkSpe() {
        return ((((2 * getPokemonAPI().getBaseStat().getAtkSpe() + atkSpeIV + (atkSpeEV / 4)) * level) / 100) + 5) * nature.getAtkSpe() / 100;
    }

    @JsonIgnore
    public int getMaxAtkPhy() {
        return ((((2 * getPokemonAPI().getBaseStat().getAtkPhy() + atkPhyIV + (atkPhyEV / 4)) * level) / 100) + 5) * nature.getAtkPhy() / 100;
    }

    @JsonIgnore
    public int getMaxDefSpe() {
        return ((((getPokemonAPI().getBaseStat().getDefSpe() + defSpeIV + (defSpeEV / 4)) * level) / 50) + 5) * nature.getDefSpe() / 100;
    }

    @JsonIgnore
    public int getMaxDefPhy() {
        return ((((2 * getPokemonAPI().getBaseStat().getDefPhy() + defPhyIV + (defPhyEV / 4)) * level) / 100) + 5) * nature.getDefPhy() / 100;
    }

    @JsonIgnore
    public int getMaxSpeed() {
        return ((((2 * getPokemonAPI().getBaseStat().getSpeed() + speedIV + (speedEV / 4)) * level) / 100) + 5) * nature.getSpeed() / 100;
    }

    public boolean hasType(Type type){
        if(type == null){
            return false;
        }
        return type.equals(this.type1) || type.equals(this.type2);
    }

    @JsonIgnore
    public boolean isMaxHappiness() {
        return MAX_FRIENDSHIP_VALUE == friendship;
    }

    @JsonIgnore
    public boolean isGrounded() { //Field field
//        if (HeldItem.IRON_BALL.equals(getHeldItem()) || field.getStatusList().contains(FieldStatus.INTENSE_GRAVITY)) {
//            return true;
//        }
        if (hasStatut(AlterationEtat.RACINES)) {
            return true;
        }
        //type vol
        if (hasType(Type.FLYING)) {
            return false;
        }
//        if (Talent.LEVITATE.equals(getTalent())) {
//            return false;
//        }
//        if (HeldItem.AIR_BALLOON.equals(getHeldItem())) {
//            return false;
//        }
        if (hasStatut(AlterationEtat.LEVITATION)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return getId() == pokemon.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public long getId() {
        return id;
    }

    public int getIdGender() {
        return idGender;
    }

    public void setIdGender(int idGender) {
        this.idGender = idGender;
    }

    public int getIdSpecie() {
        return idSpecie;
    }

    public void setIdSpecie(int idSpecie) {
        this.idSpecie = idSpecie;
    }

    public int getIdAbility() {
        return idAbility;
    }

    public void setIdAbility(int idAbility) {
        this.idAbility = idAbility;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public int getIdItemTenu() {
        return idItemTenu;
    }

    public void setIdItemTenu(int idItemTenu) {
        this.idItemTenu = idItemTenu;
    }

    public String getSurnom() {
        return surnom;
    }

    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getFriendship() {
        return friendship;
    }

    public void setFriendship(int friendship) {
        this.friendship = friendship;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getCurrentCritChance() {
        return currentCritChance;
    }

    public void setCurrentCritChance(int currentCritChance) {
        this.currentCritChance = currentCritChance;
    }

    public double getCritChanceStage() {
        return critChanceStage;
    }

    public void setCritChanceStage(double critChanceStage) {
        this.critChanceStage = critChanceStage;
    }

    public int getHpIV() {
        return hpIV;
    }

    public void setHpIV(int hpIV) {
        this.hpIV = hpIV;
    }

    public int getHpEV() {
        return hpEV;
    }

    public void setHpEV(int hpEV) {
        this.hpEV = hpEV;
    }

    public int getCurrentAtkSpe() {
        return currentAtkSpe;
    }

    public void setCurrentAtkSpe(int currentAtkSpe) {
        this.currentAtkSpe = currentAtkSpe;
    }

    public int getAtkSpeIV() {
        return atkSpeIV;
    }

    public void setAtkSpeIV(int atkSpeIV) {
        this.atkSpeIV = atkSpeIV;
    }

    public int getAtkSpeEV() {
        return atkSpeEV;
    }

    public void setAtkSpeEV(int atkSpeEV) {
        this.atkSpeEV = atkSpeEV;
    }

    public double getAtkSpeStage() {
        return atkSpeStage;
    }

    public void setAtkSpeStage(double atkSpeStage) {
        this.atkSpeStage = atkSpeStage;
    }

    public int getCurrentAtkPhy() {
        return currentAtkPhy;
    }

    public void setCurrentAtkPhy(int currentAtkPhy) {
        this.currentAtkPhy = currentAtkPhy;
    }

    public int getAtkPhyIV() {
        return atkPhyIV;
    }

    public void setAtkPhyIV(int atkPhyIV) {
        this.atkPhyIV = atkPhyIV;
    }

    public int getAtkPhyEV() {
        return atkPhyEV;
    }

    public void setAtkPhyEV(int atkPhyEV) {
        this.atkPhyEV = atkPhyEV;
    }

    public double getAtkPhyStage() {
        return atkPhyStage;
    }

    public void setAtkPhyStage(double atkPhyStage) {
        this.atkPhyStage = atkPhyStage;
    }

    public int getCurrentDefSpe() {
        return currentDefSpe;
    }

    public void setCurrentDefSpe(int currentDefSpe) {
        this.currentDefSpe = currentDefSpe;
    }

    public int getDefSpeIV() {
        return defSpeIV;
    }

    public void setDefSpeIV(int defSpeIV) {
        this.defSpeIV = defSpeIV;
    }

    public int getDefSpeEV() {
        return defSpeEV;
    }

    public void setDefSpeEV(int defSpeEV) {
        this.defSpeEV = defSpeEV;
    }

    public double getDefSpeStage() {
        return defSpeStage;
    }

    public void setDefSpeStage(double defSpeStage) {
        this.defSpeStage = defSpeStage;
    }

    public int getCurrentDefPhy() {
        return currentDefPhy;
    }

    public void setCurrentDefPhy(int currentDefPhy) {
        this.currentDefPhy = currentDefPhy;
    }

    public int getDefPhyIV() {
        return defPhyIV;
    }

    public void setDefPhyIV(int defPhyIV) {
        this.defPhyIV = defPhyIV;
    }

    public int getDefPhyEV() {
        return defPhyEV;
    }

    public void setDefPhyEV(int defPhyEV) {
        this.defPhyEV = defPhyEV;
    }

    public double getDefPhyStage() {
        return defPhyStage;
    }

    public void setDefPhyStage(double defPhyStage) {
        this.defPhyStage = defPhyStage;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getSpeedIV() {
        return speedIV;
    }

    public void setSpeedIV(int speedIV) {
        this.speedIV = speedIV;
    }

    public int getSpeedEV() {
        return speedEV;
    }

    public void setSpeedEV(int speedEV) {
        this.speedEV = speedEV;
    }

    public double getSpeedStage() {
        return speedStage;
    }

    public void setSpeedStage(double speedStage) {
        this.speedStage = speedStage;
    }

    public double getAccuracyStage() {
        return accuracyStage;
    }

    public void setAccuracyStage(double accuracyStage) {
        this.accuracyStage = accuracyStage;
    }

    public double getEvasivenessStage() {
        return evasivenessStage;
    }

    public void setEvasivenessStage(double evasivenessStage) {
        this.evasivenessStage = evasivenessStage;
    }

    public boolean isLostHealthThisTurn() {
        return lostHealthThisTurn;
    }

    public void setLostHealthThisTurn(boolean lostHealthThisTurn) {
        this.lostHealthThisTurn = lostHealthThisTurn;
    }

    public boolean isHasMovedThisTurn() {
        return hasMovedThisTurn;
    }

    public void setHasMovedThisTurn(boolean hasMovedThisTurn) {
        this.hasMovedThisTurn = hasMovedThisTurn;
    }

    public boolean isPlayerPokemon() {
        return isPlayerPokemon;
    }

    public void setPlayerPokemon(boolean playerPokemon) {
        isPlayerPokemon = playerPokemon;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<AlterationInstance> getAlterations() {
        return alterations;
    }

    public void setAlterations(List<AlterationInstance> alterations) {
        this.alterations = alterations;
    }

    public Type getType1() {
        return type1;
    }

    public void setType1(Type type1) {
        this.type1 = type1;
    }

    public Type getType2() {
        return type2;
    }

    public void setType2(Type type2) {
        this.type2 = type2;
    }

    public String getDescriptionDetaillee() {
        String res = "";
        res += getSpecieName();
        res += "Niveau " + level + " "+xp+"xp\n";
        res += currentHp + "/maxHp";
        return res;
    }

    public void completeHeal(){
//        this.currentHp = currentHp;//TODO faire les current par les max

        this.alterations.clear();
    }

    public void postFightHeal(){
        //        this.currentHp = currentHp;//TODO faire les current par les max
        this.alterations.removeIf(a-> !a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE));
    }

    public void inFightHeal(){
        this.alterations.removeIf(a -> a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.VOLATILE_BATTLE));
    }

    public boolean hasStatut(AlterationEtat alterationEtat){
        return alterations.stream().anyMatch(a -> a.getAlterationEtat().equals(alterationEtat));
    }

    public boolean hasAnyNonVolatileStatus(){
        return alterations.stream().anyMatch(a -> a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE));
    }

    public void applyStatus(AlterationEtat alterationEtat, int duree, boolean simulation) {

        //altération déjà infligée
        if (hasStatut(alterationEtat)) {
            return;
        }

        //un seul statut non-volatile à la fois
        if(alterationEtat.getTypeAlteration().equals(TypeAlteration.NON_VOLATILE) && hasAnyNonVolatileStatus()){
            return;
        }

        if (alterationEtat.equals(AlterationEtat.BRULURE) && hasType(Type.FIRE)) { //TODO ability WATER BUBBLE et talent WATER_VEIL
            return;
        }

        if (alterationEtat.equals(AlterationEtat.GEL) && hasType(Type.ICE)) { //TODO ability magma armor
            return;
        }
        if (alterationEtat.equals(AlterationEtat.PARALYSIE) && hasType(Type.ELECTRIC)) { //TODO ability limber
            return;
        }
        if ((alterationEtat.equals(AlterationEtat.POISON) || alterationEtat.equals(AlterationEtat.POISON_GRAVE)) && (hasType(Type.POISON) || hasType(Type.GROUND))) { //TODO ability immunity, et anti-immunité de type avec le talent corrosion
            return;
        }
//        if (Talent.LEAF_GUARD.equals(talent) && fight.computeWeatherEffects() && fight.getWeather().equals(Weather.HARSH_SUNLIGHT) && status.isPersistent()) {
//            if (!simulation) {
//                Utils.println(Talent.LEAF_GUARD.getLibelle() + " empêche l'altération de statut par temps ensoleillé !");
//            }
//            return;
//        }

//        if (alterationEtat.equals(Status.FEARED) && Talent.INNER_FOCUS.equals(talent)) {
//            if (!simulation) {
//                Utils.println(getLibelleColorized() + " ne flanche pas grâce à " + Talent.INNER_FOCUS.getLibelle() + " !");
//            }
//            return;
//        }
//        if (alterationEtat.equals(Status.PARALYZED) && Talent.LIMBER.equals(talent)) {
//            if (!simulation) {
//                Utils.println(Talent.LIMBER.getLibelle() + " empêche la paralysie de " + getLibelleColorized() + " !");
//            }
//            return;
//        }
//        if (alterationEtat.equals(Status.INFATUATED) && Talent.OBLIVIOUS.equals(talent)) {
//            if (!simulation) {
//                Utils.println(Talent.OBLIVIOUS.getLibelle() + " empêche " + getLibelleColorized() + " d'être charmé");
//            }
//            return;
//        }

        alterations.add(new AlterationInstance(alterationEtat, 999));

//        //DESTINY KNOT
//        if (alterationEtat.equals(Status.INFATUATED) && HeldItem.DESTINY_KNOT.equals(getHeldItem())) {
//            Pokemon otherPokemon = fight.getCurrentPokemonSecondTrainer().equals(this) ? fight.getCurrentPlayerKemon() : fight.getCurrentPokemonSecondTrainer();
//            if (!otherPokemon.getStatuses().contains(status)) {
//                if (!simulation) {
//                    Utils.println(HeldItem.DESTINY_KNOT.getLibelle() + " transmet l'effet " + status.getLibelle() + " à " + otherPokemon.getLibelleColorized());
//                }
//                otherPokemon.applyStatus(alterationEtat, duree, fight, simulation);
//            }
//        }
//
//        //SYNCHRONIZE
//        if ((alterationEtat.equals(Status.TOXIC) || alterationEtat.equals(Status.POISONED) || alterationEtat.equals(Status.PARALYZED) || alterationEtat.equals(Status.BURNT)) && Talent.SYNCHRONIZE.equals(talent)) {
//            Pokemon otherPokemon = fight.getCurrentPokemonSecondTrainer().equals(this) ? fight.getCurrentPlayerKemon() : fight.getCurrentPokemonSecondTrainer();
//            if (!otherPokemon.getStatuses().contains(alterationEtat)) {
//                if (!simulation) {
//                    Utils.println(Talent.SYNCHRONIZE.getLibelle() + " transmet l'effet " + status.getLibelle() + " à " + otherPokemon.getLibelleColorized());
//                }
//                otherPokemon.applyStatus(alterationEtat, duree, fight, simulation);
//            }
//        }
    }

}
