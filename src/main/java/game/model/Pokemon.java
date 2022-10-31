package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.oscar0812.pokeapi.models.evolution.ChainLink;
import com.github.oscar0812.pokeapi.models.evolution.EvolutionDetail;
import com.github.oscar0812.pokeapi.models.games.VersionGroup;
import com.github.oscar0812.pokeapi.models.moves.Move;
import com.github.oscar0812.pokeapi.models.pokemon.*;
import com.github.oscar0812.pokeapi.utils.Client;
import game.Game;
import game.model.enums.Gender;
import game.model.enums.Nature;
import game.model.enums.Type;
import game.model.enums.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang.StringUtils;
import utils.APIUtils;
import utils.Utils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Pokemon implements Serializable {
    private static final double BASE_CRIT_MODIFIER = 1.5;
    private static final double CRIT_MODIFIER_SNIPER = 2.25;

    private final static int MAX_EV_PER_STAT = 255;
    private final static int MAX_EV_TOTAL = 510;
    private static final int MAX_FRIENDSHIP_VALUE = 255;
    public final static int BASE_FRIENDSHIP_VALUE = 70;

    private long id;

    private Gender gender;

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

    private Nature nature;

    //valeurs de combat
    private int currentHp;
    @JsonIgnore
    private int critChanceStage = 0;
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
    private boolean aPerduDeLaVieCeTour;
    @JsonIgnore
    private boolean aDejaAttaque;
    @JsonIgnore
    private HashMap<Integer, ActionCombat> actionsCombat;

    @JsonIgnore
    private boolean itemAutorise;

    @JsonIgnore
    private List<PokemonMove> allMovesAPI;

    @JsonIgnore
    private com.github.oscar0812.pokeapi.models.pokemon.Pokemon pokemonAPI;

    @JsonIgnore
    private PokemonSpecies pokemonSpeciesAPI;

    public Pokemon(int idSpecie, int level, boolean canEvolve, Game game) {

        this.idSpecie = idSpecie;
        this.nature = Nature.random();
        this.id = Long.parseLong(idSpecie + "" + new Date().getTime());
        this.level = 0;
        this.xp = 0;
        this.shiny = Utils.getRandom().nextInt(1) == 0;
        this.aPerduDeLaVieCeTour = false;
        this.aDejaAttaque = false;
        this.friendship = 0;

        this.gender = Gender.getById(getPokemonSpeciesAPI().getGenderRate() == -1 ? 2 : Utils.getRandom().nextInt(8) > getPokemonSpeciesAPI().getGenderRate() ? 1 : 0);
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
        this.moveset = new ArrayList<>();

        levelXTimes(game, level, false, false, false);

        moveSetAuto();
        //on reset le bonheur apres les levels up, sinon ca fausse car rapportent du bonheur
        this.friendship = getPokemonSpeciesAPI().getBaseHappiness();

        //talent random si disponible
//        this.talent = species.getTalents().isEmpty() ? null : species.getTalents().get(Utils.getRandom().nextInt(species.getTalents().size()));
        this.currentAtkPhy = getMaxAtkPhy();
        this.currentDefPhy = getMaxDefPhy();
        this.currentAtkSpe = getMaxAtkSpe();
        this.currentDefSpe = getMaxDefSpe();
        this.currentSpeed = getMaxSpeed();
        this.currentHp = getMaxHp();
        this.type1 = Type.getById(getPokemonAPI().getTypes().get(0).getType().getId());
        if (getPokemonAPI().getTypes().size() > 1) {
            this.type2 = Type.getById(getPokemonAPI().getTypes().get(1).getType().getId());
        }

    }

    //default constructors
    public Pokemon() {
    }

    public Pokemon(long id, Gender gender, int idSpecie, int idAbility, boolean shiny, int idItemTenu, String surnom, int level, int xp, int friendship, List<AlterationInstance> alterations, List<Attaque> moveset, Type type1, Type type2, Nature nature, int currentHp, int critChanceStage, int hpIV, int hpEV, int currentAtkSpe, int atkSpeIV, int atkSpeEV, double atkSpeStage, int currentAtkPhy, int atkPhyIV, int atkPhyEV, double atkPhyStage, int currentDefSpe, int defSpeIV, int defSpeEV, double defSpeStage, int currentDefPhy, int defPhyIV, int defPhyEV, double defPhyStage, int currentSpeed, int speedIV, int speedEV, double speedStage, double accuracyStage, double evasivenessStage, boolean aPerduDeLaVieCeTour, boolean aDejaAttaque, HashMap<Integer, ActionCombat> actionsCombat, boolean itemAutorise, List<PokemonMove> allMovesAPI, com.github.oscar0812.pokeapi.models.pokemon.Pokemon pokemonAPI, PokemonSpecies pokemonSpeciesAPI) {
        this.id = id;
        this.gender = gender;
        this.idSpecie = idSpecie;
        this.idAbility = idAbility;
        this.shiny = shiny;
        this.idItemTenu = idItemTenu;
        this.surnom = surnom;
        this.level = level;
        this.xp = xp;
        this.friendship = friendship;
        this.alterations = alterations;
        this.moveset = moveset;
        this.type1 = type1;
        this.type2 = type2;
        this.nature = nature;
        this.currentHp = currentHp;
        this.critChanceStage = critChanceStage;
        this.hpIV = hpIV;
        this.hpEV = hpEV;
        this.currentAtkSpe = currentAtkSpe;
        this.atkSpeIV = atkSpeIV;
        this.atkSpeEV = atkSpeEV;
        this.atkSpeStage = atkSpeStage;
        this.currentAtkPhy = currentAtkPhy;
        this.atkPhyIV = atkPhyIV;
        this.atkPhyEV = atkPhyEV;
        this.atkPhyStage = atkPhyStage;
        this.currentDefSpe = currentDefSpe;
        this.defSpeIV = defSpeIV;
        this.defSpeEV = defSpeEV;
        this.defSpeStage = defSpeStage;
        this.currentDefPhy = currentDefPhy;
        this.defPhyIV = defPhyIV;
        this.defPhyEV = defPhyEV;
        this.defPhyStage = defPhyStage;
        this.currentSpeed = currentSpeed;
        this.speedIV = speedIV;
        this.speedEV = speedEV;
        this.speedStage = speedStage;
        this.accuracyStage = accuracyStage;
        this.evasivenessStage = evasivenessStage;
        this.aPerduDeLaVieCeTour = aPerduDeLaVieCeTour;
        this.aDejaAttaque = aDejaAttaque;
        this.actionsCombat = actionsCombat;
        this.itemAutorise = itemAutorise;
        this.allMovesAPI = allMovesAPI;
        this.pokemonAPI = pokemonAPI;
        this.pokemonSpeciesAPI = pokemonSpeciesAPI;
    }

    public PokemonSpecies getPokemonSpeciesAPI() {
        if (pokemonSpeciesAPI == null) {
            pokemonSpeciesAPI = Client.getPokemonSpeciesById(idSpecie);
        }
        return pokemonSpeciesAPI;
    }

    public com.github.oscar0812.pokeapi.models.pokemon.Pokemon getPokemonAPI() {
        if (pokemonAPI == null) {
            pokemonAPI = Client.getPokemonById(idSpecie);
        }
        return pokemonAPI;
    }

    @JsonIgnore
    public PokemonSpecies getEvolution(boolean isRaining, DeclencheurEvo declencheurEvo, int idItem, Zones zone, Game game, int idTraded) {
        List<ChainLink> evoPossibles = getPokemonSpeciesAPI().getEvolutionChain().getChain().getEvolvesTo();

        PokemonSpecies ps = null;

        boucle:
        for (ChainLink evoPossible : evoPossibles) {
            for (EvolutionDetail evolutionDetail : evoPossible.getEvolutionDetails()) {
                if (isEvoPossible(evolutionDetail, isRaining, declencheurEvo, idItem, game, idTraded)) {
                    ps = evoPossible.getSpecies();
                    break boucle;
                }
            }
        }
        return ps;
    }

    private boolean isEvoPossible(EvolutionDetail e, boolean isRaining, DeclencheurEvo declencheurEvo, int idItem, Game game, int idTraded) {
        if (e.getTrigger().getId() != declencheurEvo.getIdTrigger()) {
            return false;
        }
        if (e.getGender() != 0 && e.getGender() != gender.getIdGender()) {
            return false;
        }
        if (e.getNeedsOverworldRain() && !isRaining) {
            return false;
        }
        //TODO debugger ici
        if (e.getKnownMove() != null && moveset.stream().anyMatch(m -> m.getIdMoveAPI() == e.getKnownMove().getId())) {
            return false;
        }
        if (e.getKnownMoveType() != null && moveset.stream().anyMatch(m -> m.getMoveAPI().getType().getId() == e.getKnownMoveType().getId())) {
            return false;
        }
        if (e.getItem() != null && e.getItem().getId() != idItem) {
            return false;
        }
        if (e.getHeldItem() != null && getIdItemTenu() != e.getHeldItem().getId()) {
            return false;
        }
        if (e.getLocation() != null && e.getLocation().getId() != game.getSave().getCampaign().getCurrentZone().getIdZone()) {
            return false;
        }
        if ((e.getMinAffection() > 0 && friendship < e.getMinHappiness())) {
            return false;
        }
        if ((e.getMinHappiness() > 0 && friendship < e.getMinHappiness())) {
            return false;
        }
        //beauté pas prévue pour être implémentée atm
        if (e.getMinBeauty() > 0) {
            return false;
        }
        if (e.getPartyType() != null && game.getSave().getCampaign().getEquipe().stream().noneMatch(p -> p.hasType(Type.getById(e.getPartyType().getId())))) {
            return false;
        }
        if (e.getPartySpecies() != null && game.getSave().getCampaign().getEquipe().stream().noneMatch(p -> p.getIdSpecie() == e.getPartySpecies().getId())) {
            return false;
        }
        if (e.getMinLevel() > 0 && level < e.getMinLevel()) {
            return false;
        }
        if (idSpecie == 236 && e.getRelativePhysicalStats() != Integer.compare(currentAtkPhy, currentDefPhy)) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        boolean isNight = calendar.get(Calendar.HOUR_OF_DAY) >= 22 || calendar.get(Calendar.HOUR_OF_DAY) < 6;

        if (e.getTimeOfDay() != null) {
            if (e.getTimeOfDay().equals("night") && !isNight) {
                return false;
            } else if (e.getTimeOfDay().equals("day") && isNight) {
                return false;
            }
        }
        if (e.getTradeSpecies() != null && e.getTradeSpecies().getId() != idTraded) {
            return false;
        }
        return true;
    }


    public void levelXTimes(Game game, int times, boolean allowEvolution, boolean updateMoves, boolean affichage) {
        for (int i = 0; i < times; i++) {
            levelUp(game, false, allowEvolution, updateMoves, affichage);
        }
    }

    private void levelUp(Game game, boolean choixManuel, boolean evolution, boolean updateMoves, boolean affichage) {
        //on ne peut pas aller au dessus du lv100
        if (level >= 100) {
            return;
        }
        level++;
        friendship += FriendshipGains.getGainsFromAction(FriendshipGains.LEVEL_UP, friendship);

        xp = 0;
        if (game.getChannel() != null && affichage) {
            game.getChannel().sendMessage(getNomPresentation() + " passe au niveau " + level + " !").queue();
        }

        if (evolution) { //&& !HeldItem.EVERSTONE.equals(heldItem))
            PokemonSpecies species = game.getSave().getCampaign().getEquipe().get(0).getEvolution(false, DeclencheurEvo.LEVEL_UP, 1, Zones.BOURG_PALETTE, game, 0);
            if (species != null) {
                evolveTo(species, game);
            }
        }

        if (updateMoves) {
            if (choixManuel) {
//            movesetManuel();
            } else {
                moveSetAuto();
            }
        }
    }

    private void evolveTo(PokemonSpecies species, Game game) {
        int oldMax = getMaxHp();
        setIdSpecie(species.getId());
        pokemonAPI = null;
        pokemonSpeciesAPI = null;
        soinLegerApresCombat();
        int newMax = getMaxHp();
        if (newMax - oldMax > 0) {
            soigner(newMax - oldMax, game);
        }

        //TODO mettre à jour le talent
    }

    public void moveSetAuto() {
        //lister tous les moves apprenables par niveau accessibles par ce pokemon
        List<PokemonMove> pokemonMoves = getAllMovesAPI();
        HashMap<Move, Integer> availables = new HashMap<>();

        for (PokemonMove pokemonMove : pokemonMoves) {
            pokemonMove.getVersionGroupDetails().stream().map(PokemonMoveVersion::getVersionGroup).filter(p -> !APIUtils.FAUSSES_VERSIONS.contains(p.getName())).max(Comparator.comparing(VersionGroup::getOrder)).ifPresent(v -> {
                pokemonMove.getVersionGroupDetails().stream().filter(p -> p.getVersionGroup().equals(v)).findAny().ifPresent(d -> {
                    //vérifie que le move s'apprenne bien en level up
                    if (d.getMoveLearnMethod().getName().equals("level-up") && d.getLevelLearnedAt() <= level) {
                        availables.put(pokemonMove.getMove(), d.getLevelLearnedAt());
                    }
                });
            });
        }
        //TODO eventuellement filtrer certains moves en doublons dans es nivaux éléeves hyperbeam etc

        //ne garder que les moves pas déjà appris
        List<Integer> alreadyLearned = getMoveset().stream().map(Attaque::getIdMoveAPI).collect(Collectors.toList());
        List<Move> learnables = availables.keySet()
                .stream().filter(t -> alreadyLearned.stream().noneMatch(a -> a == t.getId()))
                .collect(Collectors.toList());

        learnables = learnables.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Comparator<Move> comparator = Comparator.comparing(availables::get);

        learnables.sort(comparator.reversed());

        this.moveset.clear();

        //on apprend chaque move
        while (!learnables.isEmpty() && moveset.size() < 4) {
            Move selected = learnables.remove(0);
            moveset.add(new Attaque(selected));
        }
    }

    public void gainXp(int amount, boolean manual, Game game) {
        int xpNeededToLvlUp = getXpNeededToLevelUp();
        if (amount > xpNeededToLvlUp) {
            levelUp(game, manual, true, true, true);
            changeLevel(level);
            amount -= xpNeededToLvlUp;
            gainXp(amount, manual, game);
        } else {
            xp += amount;
            game.getChannel().sendMessage(xp + "/" + (xpNeededToLvlUp + xp) + " (" + ((xp * 100) / (xpNeededToLvlUp + xp)) + "% du niveau " + (level + 1) + ")").queue();
        }
    }

    @JsonIgnore
    private int getXpNeededToLevelUp() {
        return getPokemonAPI().getSpecies().getGrowthRate().getLevels().stream().map(GrowthRateExperienceLevel::getLevel).filter(sLevel -> sLevel == level + 1).findAny().orElse(0);
    }

    public void changeLevel(int newLevel) {
        int oldMaxHP = getMaxHp();
        this.level = newLevel;
        //maj des stats en conséquence
        this.currentAtkPhy = getMaxAtkPhy();
        this.currentDefPhy = getMaxDefPhy();
        this.currentAtkSpe = getMaxAtkSpe();
        this.currentDefSpe = getMaxDefSpe();
        this.currentSpeed = getMaxSpeed();
        this.currentHp = getMaxHp() * currentHp / oldMaxHP;
    }

    @JsonIgnore
    public String getBackSprite() {
        if (isShiny()) {
            if (gender.equals(Gender.FEMALE)) {
                String shinyFemale = getPokemonAPI().getSprites().getBackShinyFemale();
                return shinyFemale != null ? shinyFemale : getPokemonAPI().getSprites().getBackShiny();
            } else {
                return getPokemonAPI().getSprites().getBackShiny();
            }
        } else {
            if (gender.equals(Gender.FEMALE)) {
                String female = getPokemonAPI().getSprites().getBackFemale();
                return female != null ? female : getPokemonAPI().getSprites().getBackDefault();
            } else {
                return getPokemonAPI().getSprites().getBackDefault();
            }
        }
    }

    @JsonIgnore
    public String getFrontSprite() {
        if (isShiny()) {
            if (gender.equals(Gender.FEMALE)) {
                String shinyFemale = getPokemonAPI().getSprites().getFrontShinyFemale();
                return shinyFemale != null ? shinyFemale : getPokemonAPI().getSprites().getFrontShiny();
            } else {
                return getPokemonAPI().getSprites().getFrontShiny();
            }
        } else {
            if (gender.equals(Gender.FEMALE)) {
                String female = getPokemonAPI().getSprites().getFrontFemale();
                return female != null ? female : getPokemonAPI().getSprites().getFrontDefault();
            } else {
                return getPokemonAPI().getSprites().getFrontDefault();
            }
        }
    }

    @JsonIgnore
    public String getSpecieName() {
        return APIUtils.getFrName(getPokemonSpeciesAPI().getNames());
    }

    @JsonIgnore
    public int getTotalEV() {
        return getHpEV() + getSpeedEV() + getAtkPhyEV() + getAtkSpeEV() + getDefPhyEV() + getDefSpeEV();
    }

    @JsonIgnore
    public int getMaxHp() {
        int baseHp = getPokemonAPI().getStats().stream().filter(s -> s.getStat().getId() == Stats.HP.getId()).map(PokemonStat::getBaseStat).findAny().orElse(1);
        return (((2 * baseHp + hpIV + (hpEV / 4)) * level) / 100) + level + 10;
    }

    @JsonIgnore
    public int getMaxAtkSpe() {
        int baseAtkSpe = getPokemonAPI().getStats().stream().filter(s -> s.getStat().getId() == Stats.SPECIAL_ATTACK.getId()).map(PokemonStat::getBaseStat).findAny().orElse(1);

        return ((((2 * baseAtkSpe + atkSpeIV + (atkSpeEV / 4)) * level) / 100) + 5) * nature.getAtkSpe() / 100;
    }

    @JsonIgnore
    public int getMaxAtkPhy() {
        int baseAtkPhy = getPokemonAPI().getStats().stream().filter(s -> s.getStat().getId() == Stats.ATTACK.getId()).map(PokemonStat::getBaseStat).findAny().orElse(1);
        return ((((2 * baseAtkPhy + atkPhyIV + (atkPhyEV / 4)) * level) / 100) + 5) * nature.getAtkPhy() / 100;
    }

    @JsonIgnore
    public int getMaxDefSpe() {
        int baseDefSpe = getPokemonAPI().getStats().stream().filter(s -> s.getStat().getId() == Stats.SPECIAL_DEFENSE.getId()).map(PokemonStat::getBaseStat).findAny().orElse(1);
        return ((((2 * baseDefSpe + defSpeIV + (defSpeEV / 4)) * level) / 50) + 5) * nature.getDefSpe() / 100;
    }

    @JsonIgnore
    public int getMaxDefPhy() {
        int baseDefPhy = getPokemonAPI().getStats().stream().filter(s -> s.getStat().getId() == Stats.DEFENSE.getId()).map(PokemonStat::getBaseStat).findAny().orElse(1);
        return ((((2 * baseDefPhy + defPhyIV + (defPhyEV / 4)) * level) / 100) + 5) * nature.getDefPhy() / 100;
    }

    @JsonIgnore
    public int getMaxSpeed() {
        int baseSpeed = getPokemonAPI().getStats().stream().filter(s -> s.getStat().getId() == Stats.SPEED.getId()).map(PokemonStat::getBaseStat).findAny().orElse(1);
        return ((((2 * baseSpeed + speedIV + (speedEV / 4)) * level) / 100) + 5) * nature.getSpeed() / 100;
    }

    public boolean hasType(Type type) {
        if (type == null) {
            return false;
        }
        return type.equals(this.type1) || type.equals(this.type2);
    }

    @JsonIgnore
    public boolean isMaxHappiness() {
        return MAX_FRIENDSHIP_VALUE == friendship;
    }

    @JsonIgnore
    public boolean isGrounded(Terrain terrain) {
        if (terrain.hasStatut(StatutsTerrain.GRAVITY)) { //HeldItem.IRON_BALL.equals(getHeldItem()) || or
            return true;
        }
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getIdSpecie() {
        return idSpecie;
    }

    public void setIdSpecie(int idSpecie) {
        this.idSpecie = idSpecie;
    }

    public List<Attaque> getMoveset() {
        return moveset;
    }

    public void setMoveset(List<Attaque> moveset) {
        this.moveset = moveset;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature(Nature nature) {
        this.nature = nature;
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

    public int getCritChanceStage() {
        return critChanceStage;
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

    public boolean isItemAutorise() {
        return itemAutorise;
    }

    public void setItemAutorise(boolean itemAutorise) {
        this.itemAutorise = itemAutorise;
    }

    public boolean isaPerduDeLaVieCeTour() {
        return aPerduDeLaVieCeTour;
    }

    public void setaPerduDeLaVieCeTour(boolean aPerduDeLaVieCeTour) {
        this.aPerduDeLaVieCeTour = aPerduDeLaVieCeTour;
    }

    public boolean isaDejaAttaque() {
        return aDejaAttaque;
    }

    public void setaDejaAttaque(boolean aDejaAttaque) {
        this.aDejaAttaque = aDejaAttaque;
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

    @JsonIgnore
    public String getNomPresentation() {
        return StringUtils.isEmpty(surnom) ? getSpecieName() : surnom;
    }

    @JsonIgnore
    public String getNomCompletPresentation() {
        return StringUtils.isEmpty(surnom) ? getSpecieName() : surnom + " (" + getSpecieName() + ")";
    }


    /**
     * garde en mémoire les moves pour éviter d'avoir à les requêter à chaque fois
     *
     * @return
     */
    @JsonIgnore
    public List<PokemonMove> getAllMovesAPI() {
        if (allMovesAPI == null || allMovesAPI.isEmpty()) {
            allMovesAPI = getPokemonAPI().getMoves();
        }
        return allMovesAPI;
    }

    public HashMap<Integer, ActionCombat> getActionsCombat() {
        return actionsCombat;
    }

    public void setActionsCombat(HashMap<Integer, ActionCombat> actionsCombat) {
        this.actionsCombat = actionsCombat;
    }

    @JsonIgnore
    public String getDescriptionDetaillee() {
        StringBuilder res = new StringBuilder();
        if (StringUtils.isNotEmpty(surnom)) {
            res.append(surnom).append(" (").append(getSpecieName()).append(")");
        } else {
            res.append(getSpecieName());
        }

        res.append(" Lv.").append(level).append(" ").append(xp).append("xp\n");
        res.append(currentHp).append("/").append(getMaxHp());
        res.append("  |");
        for (PokemonType type : getPokemonAPI().getTypes()) {
            res.append(APIUtils.getFrName(type.getType().getNames())).append("|");
        }
        res.append("\n");
        res.append("HP :        ").append(getMaxHp()).append("\n");
        res.append("Atk. :      ").append(getMaxAtkPhy()).append("\n");
        res.append("Atk. spé. : ").append(getMaxAtkSpe()).append("\n");
        res.append("Def. :      ").append(getMaxDefPhy()).append("\n");
        res.append("Def. spé. : ").append(getMaxDefSpe()).append("\n");
        res.append("Vitesse :   ").append(getMaxSpeed()).append("\n");
        res.append("\n").append(nature.getLibelle());
        return res.toString();
    }

    public void soinComplet() {
        this.currentHp = getMaxHp();
        this.alterations.clear();
        soinLegerApresCombat();
    }

    public void soinLegerCombat() {
        this.alterations.removeIf(a -> !a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE));
        this.alterations.stream().filter(a -> a.getAlterationEtat().equals(AlterationEtat.POISON_GRAVE)).findAny().ifPresent(p -> {
            p.setToursRestants(1);
        });
        this.currentAtkPhy = getMaxAtkPhy();
        this.currentDefPhy = getMaxDefPhy();
        this.currentAtkSpe = getMaxAtkSpe();
        this.currentDefSpe = getMaxDefSpe();
        this.currentSpeed = getMaxSpeed();
        this.currentHp = getMaxHp();
    }

    public void soinLegerApresCombat() {
        this.alterations.removeIf(a -> !a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE));
        if (hasStatut(AlterationEtat.POISON_GRAVE)) {
            enleveStatut(AlterationEtat.POISON_GRAVE);
            applyStatus(AlterationEtat.POISON, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT), 1, false);
        }
        this.currentAtkPhy = getMaxAtkPhy();
        this.currentDefPhy = getMaxDefPhy();
        this.currentAtkSpe = getMaxAtkSpe();
        this.currentDefSpe = getMaxDefSpe();
        this.currentSpeed = getMaxSpeed();
    }

    public int blesser(int valeur, SourceDegats sourceDegats) {
        //TODO magic_guard, sturdy, fauxchage, focus sash, focus band, requiem, air balloon

        int degatsFinaux = Math.min(currentHp, valeur);
        if (degatsFinaux > 0) {
            currentHp -= degatsFinaux;
            aPerduDeLaVieCeTour = true;
        }

        //TODO notif degats

        return degatsFinaux;
    }

    public int soigner(int valeur, Game game) {
        //on ne peut pas heal les morts !
        if (!estEnVie()) {
            return 0;
        }

        if (hasStatut(AlterationEtat.ANTISOIN)) {
            game.getChannel().sendMessage(getNomPresentation() + " ne peut pas guérir !").queue();
            return 0;
        }

        //calcul du heal effectif en fonction des pvs max
        int soinFinal = currentHp + valeur > getMaxHp() ? getMaxHp() - currentHp : valeur;

        currentHp += soinFinal;
        //inutile, mais par précaution
        if (currentHp > getMaxHp()) {
            currentHp = getMaxHp();
        }

        return soinFinal;
    }

    public void enleveStatut(AlterationEtat alteration) {
        alterations.removeIf(a -> a.getAlterationEtat().equals(alteration));
    }

    public void enleveAlterationsPerimees(Game game) {
        alterations.removeIf(a -> {
            if (a.getToursRestants() <= 0) {
                //les altérations produisant des effets à la dissipation (compte à rebours) requiem etc

                if (a.getAlterationEtat().equals(AlterationEtat.REQUIEM)) {
                    game.getChannel().sendMessage(getNomPresentation() + " tombe K.O. !").queue();
                    setCurrentHp(0);
                }
                if (a.getAlterationEtat().equals(AlterationEtat.SOMNOLENCE)) {
                    game.getChannel().sendMessage(getNomPresentation() + " s'endort...").queue();
                    applyStatus(AlterationEtat.SOMMEIL, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT), Utils.getRandomNumber(1, 3), false);
                }

                return true;
            }
            return false;
        });
    }

    public void decrementerAlterations(Game game) {
        getAlterations().stream().filter(a -> a.getAlterationEtat().equals(AlterationEtat.GEL) || a.getAlterationEtat().equals(AlterationEtat.SOMMEIL) || !a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE)).forEach(v -> {
            v.setToursRestants(v.getToursRestants() - 1);
        });
        enleveAlterationsPerimees(game);
    }

    public boolean hasStatut(AlterationEtat alterationEtat) {
        return alterations.stream().anyMatch(a -> a.getAlterationEtat().equals(alterationEtat));
    }

    public AlterationInstance getAlterationInstance(AlterationEtat alterationEtat) {
        return alterations.stream().filter(a -> a.getAlterationEtat().equals(alterationEtat)).findAny().orElse(null);

    }

    public boolean hasAnyNonVolatileStatus() {
        return alterations.stream().anyMatch(a -> a.getAlterationEtat().getTypeAlteration().equals(TypeAlteration.NON_VOLATILE));
    }

    public void applyStatus(AlterationEtat alterationEtat, SourceDegats source, int duree, boolean simulation) {

        //altération déjà infligée
        if (hasStatut(alterationEtat)) {
            return;
        }

        //un seul statut non-volatile à la fois
        if (alterationEtat.getTypeAlteration().equals(TypeAlteration.NON_VOLATILE) && hasAnyNonVolatileStatus()) {
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

        alterations.add(new AlterationInstance(alterationEtat, source, duree));

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

    /**
     * @param game
     * @param origine code déterminant où revenir après
     */
    public void choixSurnom(Game game, String origine) {
        game.getBot().lock(game.getUser());
        //demande d'entrée du prénom
        game.getChannel().sendMessage(game.getMessageManager().createMessageThumbnail(game.getSave(), PNJ.SYSTEM, "Choix du surnom pour le " + getSpecieName() + " (10 lettres max conseillé, à vos risques et périls sinon). Mettez un espace pour passer.", null))
                .queue(message -> game.getBot().getEventWaiter().waitForEvent( // Setup Wait action once message was send
                                MessageReceivedEvent.class,
                                e -> game.getMessageManager().createPredicate(e, game.getSave()),
                                //action quand réponse détectée
                                e -> {
                                    game.getBot().unlock(game.getUser());
                                    String choix = e.getMessage().getContentRaw();
                                    if (choix.length() > 32) {
                                        game.getChannel().sendMessage("Trop long ! ("+choix.length()+")").queue();
                                        choixSurnom(game, origine);
                                    } else {
                                        this.surnom = StringUtils.isEmpty(choix) ? null : choix;
                                        //en fonction d'où on vient, renvoie au bon endroit
                                        switch (origine) {
                                            case "mainmenu":
                                                game.gameMenu();
                                                break;
                                            case "premiercombat":
                                                game.combatDresseur();
                                                break;
                                            default:
                                                game.gameMenu();
                                        }
                                    }
                                },
                                1, TimeUnit.MINUTES,
                                () -> game.getMessageManager().timeout(game.getChannel(), game.getUser())
                        )
                );
    }

    public boolean estEnVie() {
        return currentHp > 0;
    }

    public int calculerDegatsAttaque(ActionCombat actionCombat, Combat combat) {

        //crit
        int critRate = getCritChanceStage() + critChanceStage;
        int denominateur;
        if (critRate == 1) {
            denominateur = 8;
        } else if (critRate == 2) {
            denominateur = 2;
        } else if (critRate == 0) {
            denominateur = 24;
        } else {
            denominateur = 1;
        }
        boolean crit = Utils.getRandomNumber(1, denominateur) == 1;

        Move move = actionCombat.getAttaque().getMoveAPI();
        Pokemon lanceur = actionCombat.getLanceur();
        int attaque = (move.getDamageClass().getId() == 2 ? lanceur.getCurrentAtkPhy() : lanceur.getCurrentAtkSpe());
        if (crit) {
            attaque = Math.max((move.getDamageClass().getId() == 2 ? lanceur.getCurrentAtkPhy() : lanceur.getCurrentAtkSpe()), (move.getDamageClass().getId() == 2 ? lanceur.getMaxAtkPhy() : lanceur.getMaxAtkSpe()));
        }
        int def = (move.getDamageClass().getId() == 2 ? getCurrentDefPhy() : getCurrentDefSpe());
        if (crit) {
            def = Math.min((move.getDamageClass().getId() == 2 ? getCurrentDefPhy() : getCurrentDefSpe()), (move.getDamageClass().getId() == 2 ? getCurrentDefPhy() : getCurrentDefSpe()));
        }
        double pointsDeViePerdusPart1 = (Math.floor(Math.floor(lanceur.getLevel() * 0.4) + 2) * attaque * move.getPower());
        double pointsDeViePerdusPart2 = def * 50;
        double pointsDeViePerdusPart3 = Math.floor(pointsDeViePerdusPart1 / pointsDeViePerdusPart2) + 2;

        //ratio type
        if (move.getType() != null) {
            pointsDeViePerdusPart3 = Math.floor(pointsDeViePerdusPart3 * Type.getById(move.getType().getId()).pourcentageDegatsAttaque(getPokemonAPI().getTypes()) / 100);
        }

        //STAB
        if (getPokemonAPI().getTypes().stream().map(PokemonType::getType).collect(Collectors.toList()).contains(move.getType())) {
            pointsDeViePerdusPart3 = Math.floor(pointsDeViePerdusPart3 * 1.5);
        }

        if (crit) {
            //TODO talent sniper
            combat.getGame().getChannel().sendMessage("Coup critique !").queue();
            pointsDeViePerdusPart3 = pointsDeViePerdusPart3 * BASE_CRIT_MODIFIER;
        }

        //todo talent
        //todo climat
        //todo objets tenus

        int random = Utils.getRandomNumber(85, 100);

        pointsDeViePerdusPart3 = Math.floor(pointsDeViePerdusPart3 * random / 100);

        return (int) pointsDeViePerdusPart3;
    }
}
