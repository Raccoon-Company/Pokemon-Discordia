package game.model;

import com.github.oscar0812.pokeapi.models.moves.Move;
import com.github.oscar0812.pokeapi.utils.Client;
import game.Game;
import game.model.enums.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.APIUtils;
import utils.PropertiesManager;
import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Combat {

    private final Logger logger = LoggerFactory.getLogger(Combat.class);

    private final static int HAUTEUR = 120;
    private final static int LARGEUR = 200;


    private final static int MIN_X_XP_BAR = 96;
    private final static int MAX_X_XP_BAR = 186;
    private final static int MIN_Y_XP_BAR = 112;
    private final static int MAX_Y_XP_BAR = 114;

    private final static int MIN_X_HP_BAR_BLANC = 140;
    private final static int MAX_X_HP_BAR_BLANC = 186;
    private final static int MIN_Y_HP_BAR_BLANC = 95;
    private final static int MAX_Y_HP_BAR_BLANC = 98;

    private final static int MIN_X_HP_BAR_NOIR = 62;
    private final static int MAX_X_HP_BAR_NOIR = 104;
    private final static int MIN_Y_HP_BAR_NOIR = 24;
    private final static int MAX_Y_HP_BAR_NOIR = 27;

    private Game game;

    private int turnCount;

    private Duelliste blanc;
    private Duelliste noir;

    //suivi de l'état

    private Terrain terrainBlanc;
    private Terrain terrainNoir;

    private TypeCombatResultat typeCombatResultat;

    private String background;

    //règles
    private TypeCombat typeCombat;

    private boolean objetsAutorises;

    private String imageCombat;

    //compteurs
    private int tentativesDeFuite = 0;
    private int piecesEparpillees = 0;

    public Combat(Game game, Duelliste blanc, Duelliste noir, TypeCombat typeCombat, boolean objetsAutorises) {
        this.game = game;
        this.typeCombat = typeCombat;
        this.blanc = blanc;
        this.noir = noir;
        this.turnCount = 0;
        this.objetsAutorises = objetsAutorises;

        setUpBackground();
    }

    private void setUpBackground() {
        String bg = game.getSave().getCampaign().getCurrentZone().getCombatBackground();
        this.background = "temp/" + game.getImageManager().merge(PropertiesManager.getInstance().getImage(bg), PropertiesManager.getInstance().getImage("battle.ui"), 0, 0, LARGEUR, HAUTEUR);

    }

    public void resolve() {
        this.typeCombatResultat = TypeCombatResultat.EN_COURS;
        this.turnCount = 1;
        blanc.getEquipe().forEach(p -> {
            p.setItemAutorise(true);
            p.setLastUsedMoves(new ArrayList<>());
        });
        noir.getEquipe().forEach(p -> {
            p.setItemAutorise(true);
            p.setLastUsedMoves(new ArrayList<>());
        });

        game.getSave().getCampaign().getPokedex().saw(noir.getPokemonActif().getIdSpecie());

        if (typeCombat.equals(TypeCombat.DOUBLE)) {
            game.getSave().getCampaign().getPokedex().saw(noir.getPokemonActifBis().getIdSpecie());
        }

        //todo effets d'entree sur le champ de bataille

        roundPhase1();
    }

    /**
     * création de l'image
     */
    public void roundPhase0() {
        try {
            updateImageCombat();
        } catch (IOException ioe) {
            logger.error("Erreur update image", ioe);
            throw new IllegalStateException("Erreur mise à jour de l'image");
        }
        roundPhase1();
    }


    /**
     * création du message et des boutons pour le choix de l'action
     */
    public void roundPhase1() {
        String text = "";

        if (turnCount == 1 && noir.getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE)) {
            text += "Un " + noir.getNom() + " sauvage apparaît !";
        } else if (turnCount == 1 && noir.getTypeDuelliste().equals(TypeDuelliste.PNJ)) {
            text += noir.getNom() + " veut se battre !";
        } else {
            text += "Tour " + turnCount;
        }

        text += "\nQue dois-faire " + blanc.getPokemonChoixCourant(turnCount).getSpecieName() + " ?";

        //on créé le message à partir de la nouvelle image et des infos combat
        MessageCreateBuilder mcb = getMcb(imageCombat, text);
        //envoi du message
        game.getChannel().sendMessage(mcb.build()).queue((message) ->
                game.getBot().getEventWaiter().waitForEvent(
                        ButtonInteractionEvent.class,
                        //vérif basique de correspondance entre message/interaction
                        e -> game.getButtonManager().createPredicate(e, message, game.getSave(), mcb.getComponents()),
                        //action quand interaction détectée

                        e -> {
                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                            game.getBot().unlock(game.getUser());
                            Pokemon choixCourant = blanc.getPokemonChoixCourant(turnCount);
                            if (e.getComponentId().equals("change")) {
                                if (choixCourant.hasStatut(AlterationEtat.NO_ESCAPE) || choixCourant.hasStatut(AlterationEtat.LIEN)) {
                                    game.getChannel().sendMessage(choixCourant.getSpecieName() + " ne peut pas être échangé !").queue();
                                    roundPhase1();
                                } else {
                                    //changer de pokes
                                    selectionPokemon(false);
                                }
                            } else if (e.getComponentId().equals("ball")) {
                                //pokeball
                                if (!noir.getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE)) {
                                    game.getChannel().sendMessage("Vous ne pouvez pas voler le pokémon d'un autre dresseur ! (mais bien essayé)").queue();
                                    roundPhase1();
                                } else if (!game.getSave().getCampaign().getInventaire().hasPokeballs()) {
                                    game.getChannel().sendMessage("Vous ne possédez aucune pokéball...").queue();
                                    roundPhase1();
                                } else {
                                    pokeballMenu();
                                }
                            } else if (e.getComponentId().equals("item")) {
                                //bag//
                                menuSac();
                            } else if (e.getComponentId().equals("escape")) {
                                fuite();
                            } else {
                                //choix de la ou des cibles de l'attaque si nécessaire
                                selectionCibles(e.getComponentId(), choixCourant);
                            }
                        },
                        1,
                        TimeUnit.MINUTES,
                        () -> {
                            game.getButtonManager().timeout(game.getChannel(), game.getUser());
                        }
                ));
    }

    private void selectionCibles(int idMove, Pokemon lanceur) {
        //choix cible(s)
        Move move = Client.getMoveById(idMove);
        MoveTarget target = move.getTarget();
        TypeCibleCombat typeCibleCombat = TypeCibleCombat.getById(target.getId());
        Attaque attaque = lanceur.getMoveset().stream().filter(m -> m.getIdMoveAPI() == idMove).findAny().orElse(null);

        switch (typeCibleCombat) {
            case SPECIFIC_MOVE:
            case ALL_OTHER_POKEMON:
            case ALL_OPPONENTS:
            case ENTIRE_FIELD:
            case USER_AND_ALLIES:
            case ALL_POKEMON:
            case ALL_ALLIES:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat));
                roundPhase2();
                break;
            case SELECTED_POKEMON_ME_FIRST:
                roundPhase2();
                break;
            case ALLY:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    //échec
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat));
                } else {
                    if (lanceur.equals(blanc.getPokemonActif())) {
                        lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, blanc.getPokemonActifBis()));
                    } else {
                        lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, blanc.getPokemonActif()));
                    }
                }
                roundPhase2();
                break;
            case USERS_FIELD:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, terrainBlanc));
                roundPhase2();
                break;
            case USER_OR_ALLY:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat,lanceur));
                    roundPhase2();
                }else{
                    //TODO selection lanceur ou allié
                }
                break;
            case OPPONENTS_FIELD:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, terrainNoir));
                roundPhase2();
                break;
            case USER:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                roundPhase2();
                break;
            case RANDOM_OPPONENT:
                if(typeCombat.equals(TypeCombat.SIMPLE) || Utils.getRandom().nextBoolean()){
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, noir.getPokemonActif()));
                }else{
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, noir.getPokemonActifBis()));
                }
                roundPhase2();
                break;
            case SELECTED_POKEMON:
                if(typeCombat.equals(TypeCombat.SIMPLE)){
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, noir.getPokemonActif()));
                }else{
                    //TODO selection adversaire ou allié
                }
                break;
        }
    }

    private void menuSac() {
        //TODO choix de l'item à utiliser ou retour
        if (true) {//retour
            roundPhase1();
        } else {
            blanc.getPokemonChoixCourant(turnCount).getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.OBJET));
        }
    }

    /**
     * @param force si le chgt est forcé par la mort du poke actif, ou bien par choix
     */
    private void selectionPokemon(boolean force) {
        Pokemon choixCourant = blanc.getPokemonChoixCourant(turnCount);
        //TODO affichage des choix possibles
        //equipe sauf actif
        //retour

        if (true) { //si retour
            roundPhase1();
        } else {
            //changement de kemon

            //TODO choix du nouveau pokemon
            choixCourant.soinLegerCombat();
            choixCourant.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.SWITCH_OUT));

            //        if (Talent.NATURAL_CURE.equals(currentPokemonFirstTrainer.getTalent())) {
//            currentPokemonFirstTrainer.getStatuses().clear();
//        }
            Pokemon selected = null;

            if (choixCourant.equals(blanc.getPokemonActif())) {
                blanc.setPokemonActif(selected);
                blanc.getPokemonActif().getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.SWITCH_IN));
            } else {
                blanc.setPokemonActifBis(selected);
                blanc.getPokemonActifBis().getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.SWITCH_IN));

            }

            try {
                updateImageCombat();
            } catch (IOException ioe) {
                logger.error("Erreur update image", ioe);
                throw new IllegalStateException("Erreur mise à jour de l'image");
            }
            roundPhase2();
        }
    }

    public boolean fuiteAutorisee(Pokemon fuyard, Duelliste adversaire, boolean simulation) {
        if (adversaire.getTypeDuelliste().equals(TypeDuelliste.PNJ)) {
            if (!simulation) {
                game.getChannel().sendMessage("Impossible de fuir une bataille de dresseurs !").queue();
            }
            return false;
        }
//        if (HeldItem.SHED_SHELL.equals(fuyard.getHeldItem()) || HeldItem.SMOKE_BALL.equals(fuyard.getHeldItem())) {
//            return true;
//        }
        if (fuyard.hasStatut(AlterationEtat.RACINES)) {
            return false;
        }
//        if (Talent.RUN_AWAY.equals(flee.getTalent())) {
//            return true;
//        }

        if (fuyard.hasStatut(AlterationEtat.LIEN) || fuyard.hasStatut(AlterationEtat.NO_ESCAPE)) {
            if (!simulation) {
                game.getChannel().sendMessage(fuyard.getSpecieName() + " est piégé et ne peut pas s'enfuir !").queue();
            }
            return false;
        }

//        if (fight.getFightType().equals(FightType.PLAYER_VS_AI) || fight.getFightType().equals(FightType.PLAYER_VS_PLAYER)) {
//            if (fight.getOwner(flee).equals(fight.getPlayer()) && fight.getField1().getStatusList().contains(FieldStatus.SPIDER_WEB) || fight.getOwner(flee).equals(fight.getFoe()) && fight.getField2().getStatusList().contains(FieldStatus.SPIDER_WEB)) {
//                if (!simulation) {
//                    Utils.println("Le terrain recouvert de toile d'araignées vous empêche de fuir !");
//                }
//                return false;
//            }
//        } else {
//            if (fight.getOwner(flee).equals(fight.getTrainer()) && fight.getField1().getStatusList().contains(FieldStatus.SPIDER_WEB) || fight.getOwner(flee).equals(fight.getFoe()) && fight.getField2().getStatusList().contains(FieldStatus.SPIDER_WEB)) {
//                if (!simulation) {
//                    Utils.println("Le terrain recouvert de toile d'araignées vous empêche de fuir !");
//                }
//                return false;
//            }
//        }
//
//        if (Talent.ARENA_TRAP.equals(other.getTalent())) {
//            if (!simulation) {
//                Utils.println("ARENA TRAP de " + other.getLibelleColorized() + " empêche " + flee.getLibelleColorized() + " de s'enfuir !");
//            }
//            return false;
//        }
//        if (Talent.MAGNET_PULL.equals(other.getTalent()) && (flee.getSpecies().getType2().equals(ChartMonType.STEEL) || flee.getSpecies().getType1().equals(ChartMonType.STEEL))) {
//            if (!simulation) {
//                Utils.println("MAGNET PULL de " + other.getLibelleColorized() + " empêche " + flee.getLibelleColorized() + " de s'enfuir !");
//            }
//            return false;
//        }
        return true;
    }

    private void fuite() {
        //on vérifie avant tout que le joueur puisse bien fuir
        //sinon retour à l'étape 1
        if (!fuiteAutorisee(blanc.getPokemonChoixCourant(turnCount), noir, false)) {
            roundPhase1();
        }

        //up le nm de tentatives de fuite
        tentativesDeFuite++;
        //on passe le tour du pokemon, utilisé a fuir
        blanc.getPokemonActif().getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.FUITE));

        //si le temps est HARSH_SUNLIGHT, les kemons avec le talent chlorophyll doublent leur vitesse
        double speedPlayer = blanc.getPokemonActif().getCurrentSpeed();
        double speedWild = noir.getPokemonActif().getCurrentSpeed();

//        if (computeWeatherEffects()) {
//            if (weather.equals(Weather.HARSH_SUNLIGHT)) {
//                if (Talent.CHLOROPHYLL.equals(currentPokemonFirstTrainer.getTalent())) {
//                    speedPlayer = speedPlayer * 2;
//                }
//                if (Talent.CHLOROPHYLL.equals(currentPokemonSecondTrainer.getTalent())) {
//                    speedWild = speedWild * 2;
//                }
//            } else if (weather.equals(Weather.RAIN)) {
//                if (Talent.SWIFT_SWIM.equals(currentPokemonFirstTrainer.getTalent())) {
//                    speedPlayer = speedPlayer * 2;
//                }
//                if (Talent.SWIFT_SWIM.equals(currentPokemonSecondTrainer.getTalent())) {
//                    speedWild = speedWild * 2;
//                }
//            }
//        }

        double proba = (((speedPlayer * 128) / speedWild) + (30 * tentativesDeFuite)) % 256;

        if (speedPlayer > speedWild || Utils.randomTest(proba)) {
            //fuite réussie
            game.getChannel().sendMessage("Vous fuyez le combat !").queue();
            typeCombatResultat = TypeCombatResultat.FUITE_JOUEUR;
            game.apresCombat(this);
        } else {
            //échec de la fuite
            game.getChannel().sendMessage("Vous ne parvenez pas à vous enfuir !").queue();
            roundPhase2();
        }
    }

    private void pokeballMenu() {
        List<Button> buttons = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19"))
        ));

        if (game.getSave().getCampaign().getInventaire().has(Item.POKEBALL)) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "pokeball", "Pokéball", Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
        }
        if (game.getSave().getCampaign().getInventaire().has(Item.SUPERBALL)) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "superball", "Superball", Emoji.fromCustom("superball", 1034421567570063400L, false)));
        }
        if (game.getSave().getCampaign().getInventaire().has(Item.HYPERBALL)) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "hyperball", "HyperBall", Emoji.fromCustom("hyperball", 1034421564210429993L, false)));
        }
        if (game.getSave().getCampaign().getInventaire().has(Item.MASTERBALL)) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "masterball", "Masterball", Emoji.fromCustom("masterball", 1034421566244659280L, false)));
        }

        LayoutComponent lc = ActionRow.of(buttons);

        game.getBot().lock(game.getUser());
        game.getChannel().sendMessage(game.getMessageManager().createMessageImage(game.getSave(), "Attraper le pokémon", lc, null))
                .queue((message) ->
                        game.getBot().getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> game.getButtonManager().createPredicate(e, message, game.getSave(), lc),
                                //action quand interaction détectée

                                e -> {
                                    game.getBot().unlock(game.getUser());
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    switch (e.getComponentId()) {
                                        case "pokeball":
                                            launchPokeball(Pokeball.POKEBALL);
                                            roundPhase2();
                                            break;
                                        case "superball":
                                            launchPokeball(Pokeball.GREATBALL);
                                            roundPhase2();
                                            break;
                                        case "hyperball":
                                            launchPokeball(Pokeball.ULTRABALL);
                                            roundPhase2();
                                            break;
                                        case "masterball":
                                            launchPokeball(Pokeball.MASTERBALL);
                                            roundPhase2();
                                            break;
                                        case "back":
                                            roundPhase1();
                                            break;
                                        default:
                                            roundPhase1();
                                    }
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    game.getButtonManager().timeout(game.getChannel(), game.getUser());
                                }
                        )
                );
    }

    private void launchPokeball(Pokeball pokeball) {
        if (!noir.getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE)) {
            //erreur
            roundPhase1();
        }

        blanc.getPokemonActif().getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.OBJET));

        game.getSave().getCampaign().getInventaire().retraitItem(Item.getById(pokeball.getIdItemApi()), 1);

        //calcul des probas sous forme de secousses de pokeballs
        double a = modifiedCatchRate(pokeball);
        double b = 1048560 / (Math.sqrt(Math.sqrt(16711680 / a)));
        int secoussesReussies = 0;
        for (int i = 0; i < 4; i++) {
            if (Utils.getRandomNumber(0, 65535) < b) {
                secoussesReussies++;
            }
        }

        //TODO afficher les secousses

        if (secoussesReussies >= 4) {
            //réussite de la capture
            noir.getPokemonActif().setFriendship(Pokemon.BASE_FRIENDSHIP_VALUE);
            game.getSave().getCampaign().getPokedex().captured(noir.getPokemonActif().getIdSpecie());
            this.typeCombatResultat = TypeCombatResultat.CAPTURE;
            game.apresCombat(this);
        } else {
            //TODO capture ratée
            roundPhase2();
        }
    }

    private double modifiedCatchRate(Pokeball pokeball) {
        double hpMax = noir.getPokemonActif().getMaxHp();
        double hpCur = noir.getPokemonActif().getCurrentHp();
        double rate = noir.getPokemonActif().getPokemonAPI().getSpecies().getCaptureRate();
        double bonusStatus = 1;
        if (noir.getPokemonActif().hasStatut(AlterationEtat.SOMMEIL) || noir.getPokemonActif().hasStatut(AlterationEtat.GEL)) {
            bonusStatus = 2.5;
        } else if (noir.getPokemonActif().hasAnyNonVolatileStatus()) {
            bonusStatus = 1.5;
        }

        return (((3 * hpMax - 2 * hpCur) * rate * pokeball.getEfficacite()) / 3 * hpMax) * bonusStatus;
    }

    public void roundPhase2() {
        //on vérifie que tous les pokémons actifs du joueur ont choisis une action, sinon on retourne à la phase 1 (double up)
        if (!blanc.getPokemonActifBis().isaDejaChoisi() || !blanc.getPokemonActif().isaDejaChoisi()) {
            roundPhase1();
        }

        //TODO le pokémon adverse choisit ce qu'il fait
        //attaque si sauvage
        //mais aussi eventuellement utiliser une potion ou changer de pokemon selon l'IA en face

        //TODO resolution des attaques choisies
        ordreDAction().forEach(this::effectuerAction);

        blanc.getPokemonActif().blesser(4, new SourceDegats(TypeSourceDegats.POKEMON, noir.getPokemonActif()));
        blanc.getPokemonActif().blesser(10, new SourceDegats(TypeSourceDegats.POKEMON, blanc.getPokemonActif()));

        effetsDeFinDeTour();

        //vérif combat terminé
        if (blanc.getPokemonActif().getCurrentHp() >= 0 && noir.getPokemonActif().getCurrentHp() >= 0) {
            //tour suivant
            roundPhase0();

            //TODO combat terminé
        } else {
            game.apresCombat(this);
        }
    }

    private void effectuerAction(Pokemon lanceur) {
        lanceur.setaDejaAttaque(true);

        ActionCombat actionCombat = lanceur.getActionsCombat().get(turnCount);

        //on n'effectue des actions qu'en mode ATTAQUE ici
        if(!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)){
            return;
        }

        Move attaqueSelectionnee = actionCombat.getAttaque().getMoveAPI();

        //pas d'attaque si le lanceur meurt entretemps
        if (lanceur.getCurrentHp() <= 0) {
            return;
        }

        //pas d'attaque si la cible est dead
        if(actionCombat.getPokemonCible() != null && actionCombat.getPokemonCible().getCurrentHp() <= 0){
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.APEURE)) {
//TODO poke apeure notif
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.CHARGING_TURN)) {
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.RECHARGE)) {
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.GEL)) {
            return;
        }
        if (lanceur.hasStatut(AlterationEtat.SOMMEIL)) { //&& !(selectedMove.equals(Moves.RONFLEMENT) || selectedMove.equals(Moves.BLABLA_DODO))
            return;
        }

        //désobéissance uniquement si player
        if (blanc.estALui(lanceur)) {
            //TODO désobéissance
        }
//
//        if (!simulation && !cancel) {
//            launcherPokemon.getMoveset().stream().filter(a -> a.getMove().equals(selectedMove)).findAny().ifPresent(a -> {
//                a.setPpLeft(a.getPpLeft() - 1);
//                //PRESSURE
//                if (Talent.PRESSURE.equals(targetPokemon.getTalent())) {
//                    a.setPpLeft(a.getPpLeft() - 1);
//                }
//            });
//            Utils.println("---------------------------");
//            if (getOwner(launcherPokemon) instanceof Wild) {
//                Utils.println(getOwner(launcherPokemon).getName() + " utilise " + selectedMove.getLibelleColorized());
//            } else {
//                Utils.println(launcherPokemon.getLibelleColorized() + " de " + getOwner(launcherPokemon).getName() + " utilise " + selectedMove.getLibelleColorized());
//            }
//            Utils.sleep(SpeedText.LENGTHY_DELAY.getSpeedRatio(), true);
//        }
//
        if (lanceur.hasStatut(AlterationEtat.PARALYSIE) && Utils.getRandom().nextBoolean()) {
            //TODO notif paralysie
            return;
        }
//
        if (lanceur.hasStatut(AlterationEtat.CHARME) && Utils.getRandom().nextBoolean()) {
            //TODO notif charme
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.CONFUSION)) {
            if (Utils.randomTest(33)) {
                //TODO notif + perte hp
                return;
            }
        }

        if (actionCombat.getPokemonCible() != null && actionCombat.getPokemonCible().hasStatut(AlterationEtat.PROTECTION)) { //TODO && !attaqueSelectionnee.equals(Moves.RUSE)
           //TODO notif protect
            return;
        }
//
//        //en simulation, la précision est comptabilisée en ratio de dégats, alors qu'en vrai, c'est tout ou rien
//        //aussi, on ne prend en compte la précision que si le move de bse a une précision =< 100, sinon, on considère le move inratable
//        int movePrecision = selectedMove.getPrecision();
//
//        //précision modifiée de THUNDER, BLIZZARD et HURRICANE en fonction de la météo
//        if (computeWeatherEffects()) {
//            if (selectedMove.equals(Moves.THUNDER) || selectedMove.equals(Moves.HURRICANE)) {
//                if (weather.equals(Weather.HARSH_SUNLIGHT)) {
//                    movePrecision = 50;
//                } else if (weather.equals(Weather.RAIN)) {
//                    movePrecision = 999;
//                }
//            } else if (selectedMove.equals(Moves.BLIZZARD)) {
//                if (weather.equals(Weather.HAIL)) {
//                    movePrecision = 999;
//                }
//            }
//        }
//
//        if (HeldItem.LAX_INCENSE.equals(targetPokemon.getHeldItem())) {
//            movePrecision -= 10;
//        }
//
//        boolean hit = movePrecision > 100 || accuracyCheck(launcherPokemon, targetPokemon, selectedMove, simulation);
//
//
//        selectedMove.use(launcherPokemon, targetPokemon, simulation ? this.getCopy() : this, simulation);
    }

    public boolean tenirCompteDeLaMeteo() {
        boolean tenirCompteDeLaMeteo = true;
        // TODO on ne prend pas en compte les effets de la météo si un kemon avec CLOUD NINE est présent et AIR LOCK aussi
//        if (Talent.CLOUD_NINE.equals(currentPokemonSecondTrainer.getTalent()) || Talent.CLOUD_NINE.equals(currentPokemonFirstTrainer.getTalent())) {
//            return false;
//        }
        return tenirCompteDeLaMeteo;
    }

    /**
     * TODO vérifier
     *
     * @return liste des pokémons sur le terrain, triés par ordre d'action du premier à bouger au dernier
     */
    public List<Pokemon> ordreDAction() {

        HashMap<Pokemon, Integer> scoresActionPokemons = new HashMap<>();

        scoresActionPokemons.put(blanc.getPokemonActif(), blanc.getPokemonActif().getCurrentSpeed());
        scoresActionPokemons.put(noir.getPokemonActif(), noir.getPokemonActif().getCurrentSpeed());

        if (typeCombat.equals(TypeCombat.DOUBLE)) {
            scoresActionPokemons.put(blanc.getPokemonActifBis(), blanc.getPokemonActifBis().getCurrentSpeed());
            scoresActionPokemons.put(noir.getPokemonActifBis(), noir.getPokemonActifBis().getCurrentSpeed());
        }

        scoresActionPokemons.forEach((k, v) -> {
            //si le temps est HARSH_SUNLIGHT, les kemons avec le talent chlorophyll doublent leur vitesse
            if (tenirCompteDeLaMeteo()) {
                if (terrainBlanc.hasStatut(StatutsTerrain.SUNNY_DAY)) {
//                if (Talent.CHLOROPHYLL.equals(currentPokemonFirstTrainer.getTalent())) {
//                    v = v * 2;
//                }
                } else if (terrainBlanc.hasStatut(StatutsTerrain.RAIN_DANCE)) {
//                if (Talent.SWIFT_SWIM.equals(currentPokemonFirstTrainer.getTalent())) {
//                    v = v * 2;
//                }
                }
            }

            if (terrainBlanc.hasStatut(StatutsTerrain.TRICK_ROOM) || terrainNoir.hasStatut(StatutsTerrain.TRICK_ROOM)) {
                v = -v;
            }

            v += k.getLastMove().getPriorite() * 1000;


//        } else if (HeldItem.QUICK_CLAW.equals( blanc.getPokemonActif().getHeldItem()) && Utils.randomTest(20)) {
//            return true;
//        } else if (HeldItem.QUICK_CLAW.equals(noir.getPokemonActif().getHeldItem()) && Utils.randomTest(20)) {
//            return false;

        });

        // Create an ArrayList and insert all hashmap key-value pairs.
        List<Map.Entry<Pokemon, Integer>> sortedList = new ArrayList<>(scoresActionPokemons.entrySet());

        // Sort the Arraylist using a custom comparator.
        sortedList.sort(Comparator.comparingInt(Map.Entry::getValue));

        return sortedList.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private void effetsDeFinDeTour() {
        //TODO effets des items (restes, etc)
        //TODO effets des altérations (racines, poison, etc)
        //TODO effets de la météo si pris en compte (càd pas de cloud nine ou air lock)

        //decrementation des tours restants des altérations
        blanc.decrementerAlterations();
        noir.decrementerAlterations();

        //decrementation des tours restants des altérations terrain
        terrainBlanc.finDeTourMajStatus();
        terrainNoir.finDeTourMajStatus();

        blanc.getPokemonActif().setaPerduDeLaVieCeTour(false);
        noir.getPokemonActif().setaPerduDeLaVieCeTour(false);
        blanc.getPokemonActif().setaDejaAttaque(false);
        noir.getPokemonActif().setaDejaAttaque(false);
    }

    @NotNull
    private MessageCreateBuilder getMcb(String imageCombat, String text) {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        List<Button> buttons = new ArrayList<>();
        List<Button> buttons2 = new ArrayList<>();

        for (Attaque attaque : blanc.getPokemonActif().getMoveset()) {
            Move move = attaque.getMoveAPI();
            Type type = Type.getById(move.getType().getId());
            buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(attaque.getIdMoveAPI()), APIUtils.getFrName(move.getNames()) + " " + attaque.getPpLeft() + "/" + (move.getPp() + attaque.getBonusPp()), Emoji.fromCustom(type.getEmoji(), type.getIdDiscordEmoji(), false)));
        }

        buttons2 = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.SECONDARY, "change", "Pokémon", Emoji.fromFormatted("\uD83D\uDD03")),
                Button.of(ButtonStyle.SECONDARY, "ball", "Pokéball", Emoji.fromCustom("pokeball", 1032561600701399110L, false)),
                Button.of(ButtonStyle.SECONDARY, "item", "Potion", Emoji.fromFormatted("\uD83E\uDDF4")),
                Button.of(ButtonStyle.SECONDARY, "escape", "Fuite", Emoji.fromFormatted("\uD83C\uDFC3\uD83C\uDFFC"))
        ));


        File combat = new File(getClass().getClassLoader().getResource("images/temp/").getPath() + imageCombat);
        mcb.addFiles(FileUpload.fromData(combat, combat.getName()));

        LayoutComponent lc = ActionRow.of(buttons);
        LayoutComponent lc2 = ActionRow.of(buttons2);
        mcb.addComponents(lc, lc2);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(game.getSave().getColorRGB()))
                .setDescription(text)
                .setImage("attachment://" + combat.getName());


        mcb.addEmbeds(embedBuilder.build());
        return mcb;
    }

    private void updateImageCombat() throws IOException {

        /**
         * merge le background
         * les sprites des 2 à 4 pokémons
         * les barres de points de vie et d'xp
         * les pokéballs représentant l'équipe
         * les noms, genres, shiny stars, et niveaux de chaque
         */
        if (typeCombat.equals(TypeCombat.SIMPLE)) {
            List<ElementUI> elementUIS = new ArrayList<>();
            Font font = new Font("Arial", Font.PLAIN, 10);
            Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.TRACKING, -0.1);
            font = font.deriveFont(attributes);
            Font fontGender = new Font("Arial", Font.PLAIN, 9);
            Font fontHp = new Font("Arial", Font.PLAIN, 11);

            FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);

            //pokemon allié
            elementUIS.add(new ImageUI(-5, 50, ImageIO.read(new URL(blanc.getPokemonActif().getBackSprite()))));
            TextUI nomPokemonBlanc = new TextUI(91, 90, blanc.getPokemonActif().getSpecieName(), font, Color.BLACK);
            TextUI genrePokemonBlanc = new TextUI(91 + (int) (font.getStringBounds(nomPokemonBlanc.getText(), frc).getWidth()), 90, " " + blanc.getPokemonActif().getGender().getEmoji(), fontGender, blanc.getPokemonActif().getGender().getColor());
            elementUIS.add(nomPokemonBlanc);
            elementUIS.add(genrePokemonBlanc);
            int hpBarBlanc = (int) ((MAX_X_HP_BAR_BLANC - MIN_X_HP_BAR_BLANC) * ((double) blanc.getPokemonActif().getCurrentHp() / blanc.getPokemonActif().getMaxHp()));
            if (blanc.getPokemonActif().getCurrentHp() > 0 && hpBarBlanc <= 0) {
                hpBarBlanc = 1;
            }
            RectangleUI hpBlanc = new RectangleUI(MIN_X_HP_BAR_BLANC, MIN_Y_HP_BAR_BLANC, Color.GREEN, hpBarBlanc, MAX_Y_HP_BAR_BLANC - MIN_Y_HP_BAR_BLANC);
            elementUIS.add(hpBlanc);
            TextUI hpTextBlanc = new TextUI(144, 109, blanc.getPokemonActif().getCurrentHp() + "/" + blanc.getPokemonActif().getMaxHp(), fontHp, Color.BLACK);
            elementUIS.add(hpTextBlanc);

            TextUI levelBlanc = new TextUI(166, 90, "Lv." + blanc.getPokemonActif().getLevel(), font, Color.BLACK);
            elementUIS.add(levelBlanc);
            blanc.getPokemonActif().setXp(48);
            int currentXpBar = (int) ((MAX_X_XP_BAR - MIN_X_XP_BAR) * ((double) blanc.getPokemonActif().getXp() / 155));
            RectangleUI xpBar = new RectangleUI(MIN_X_XP_BAR, MIN_Y_XP_BAR, Color.CYAN, currentXpBar, MAX_Y_XP_BAR - MIN_Y_XP_BAR);
            elementUIS.add(xpBar);

            int pokeY = 110;
            for (Pokemon pokemon : blanc.getEquipe()) {
                if (pokemon.getCurrentHp() <= 0) {
                    elementUIS.add(new ImageUI(3, pokeY, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("empty"))))));
                } else {
                    elementUIS.add(new ImageUI(3, pokeY, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("pokeball"))))));
                }
                pokeY -= 12;
            }

            if (blanc.getPokemonActif().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonBlanc.getX() + (int) (font.getStringBounds(genrePokemonBlanc.getText(), frc).getWidth()) + 5, 82, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }

            elementUIS.add(new ImageUI(100, 0, ImageIO.read(new URL(noir.getPokemonActif().getFrontSprite()))));
            TextUI nomPokemonNoir = new TextUI(16, 19, noir.getPokemonActif().getSpecieName(), font, Color.BLACK);
            TextUI genrePokemonNoir = new TextUI(16 + (int) (font.getStringBounds(nomPokemonNoir.getText(), frc).getWidth()), 19, " " + noir.getPokemonActif().getGender().getEmoji(), fontGender, noir.getPokemonActif().getGender().getColor());
            elementUIS.add(nomPokemonNoir);
            elementUIS.add(genrePokemonNoir);
            int hpBarNoir = (int) ((MAX_X_HP_BAR_NOIR - MIN_X_HP_BAR_NOIR) * ((double) noir.getPokemonActif().getCurrentHp() / noir.getPokemonActif().getMaxHp()));
            if (noir.getPokemonActif().getCurrentHp() > 0 && hpBarNoir <= 0) {
                hpBarNoir = 1;
            }
            RectangleUI hpNoir = new RectangleUI(MIN_X_HP_BAR_NOIR, MIN_Y_HP_BAR_NOIR, Color.GREEN, hpBarNoir, MAX_Y_HP_BAR_NOIR - MIN_Y_HP_BAR_NOIR);
            elementUIS.add(hpNoir);

            TextUI levelNoir = new TextUI(80, 19, "Lv." + noir.getPokemonActif().getLevel(), font, Color.BLACK);
            elementUIS.add(levelNoir);

            if (noir.getPokemonActif().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonNoir.getX() + (int) (font.getStringBounds(genrePokemonNoir.getText(), frc).getWidth()) + 2, 11, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }

            BufferedImage backImage = ImageIO.read(new File(game.getFileManager().getFullPathToImage(background)));

            imageCombat = game.getImageManager().composeImageCombat(backImage, elementUIS, LARGEUR, HAUTEUR);
        } else if (typeCombat.equals(TypeCombat.DOUBLE)) {
            //TODO update image pour double up
        }
    }

    public void changerMeteo(Meteo meteo) {
        changerMeteo(meteo, 5);
    }

    public void changerMeteo(Meteo meteo, int compteurToursMeteo) {
        terrainBlanc.getAlterations().entrySet().removeIf(a -> a.getKey().isMeteo());
        terrainNoir.getAlterations().entrySet().removeIf(a -> a.getKey().isMeteo());

        StatutsTerrain statutsTerrain = StatutsTerrain.getStatutFromMeteo(meteo);
        if (statutsTerrain != null) {
            terrainBlanc.ajoutStatut(statutsTerrain, compteurToursMeteo);
        }
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Duelliste getBlanc() {
        return blanc;
    }

    public void setBlanc(Duelliste blanc) {
        this.blanc = blanc;
    }

    public Duelliste getNoir() {
        return noir;
    }

    public void setNoir(Duelliste noir) {
        this.noir = noir;
    }

    public Terrain getTerrainBlanc() {
        return terrainBlanc;
    }

    public void setTerrainBlanc(Terrain terrainBlanc) {
        this.terrainBlanc = terrainBlanc;
    }

    public Terrain getTerrainNoir() {
        return terrainNoir;
    }

    public void setTerrainNoir(Terrain terrainNoir) {
        this.terrainNoir = terrainNoir;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public TypeCombat getTypeCombat() {
        return typeCombat;
    }

    public void setTypeCombat(TypeCombat typeCombat) {
        this.typeCombat = typeCombat;
    }

    public boolean isObjetsAutorises() {
        return objetsAutorises;
    }

    public void setObjetsAutorises(boolean objetsAutorises) {
        this.objetsAutorises = objetsAutorises;
    }

    public int getTentativesDeFuite() {
        return tentativesDeFuite;
    }

    public void setTentativesDeFuite(int tentativesDeFuite) {
        this.tentativesDeFuite = tentativesDeFuite;
    }

    public int getPiecesEparpillees() {
        return piecesEparpillees;
    }

    public void setPiecesEparpillees(int piecesEparpillees) {
        this.piecesEparpillees = piecesEparpillees;
    }
}
