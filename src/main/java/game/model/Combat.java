package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.oscar0812.pokeapi.models.moves.Move;
import com.github.oscar0812.pokeapi.models.moves.MoveTarget;
import com.github.oscar0812.pokeapi.utils.Client;
import game.Game;
import game.model.enums.*;
import game.model.moveEffets.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.apache.commons.lang.SerializationUtils;
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
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Combat implements Serializable {

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

    private final static int DOUBLE_MIN_X_HP_BAR_BLANC = 144;
    private final static int DOUBLE_MAX_X_HP_BAR_BLANC = 190;
    private final static int DOUBLE_MIN_Y_HP_BAR_BLANC = 95;
    private final static int DOUBLE_MAX_Y_HP_BAR_BLANC = 98;
    private final static int DOUBLE_MIN_X_HP_BAR_NOIR = 52;
    private final static int DOUBLE_MAX_X_HP_BAR_NOIR = 98;
    private final static int DOUBLE_MIN_Y_HP_BAR_NOIR = 16;
    private final static int DOUBLE_MAX_Y_HP_BAR_NOIR = 19;

    private final static int DOUBLE_MIN_X_HP_BAR_BLANC_BIS = 144;
    private final static int DOUBLE_MAX_X_HP_BAR_BLANC_BIS = 190;
    private final static int DOUBLE_MIN_Y_HP_BAR_BLANC_BIS = 115;
    private final static int DOUBLE_MAX_Y_HP_BAR_BLANC_BIS = 118;
    private final static int DOUBLE_MIN_X_HP_BAR_NOIR_BIS = 52;
    private final static int DOUBLE_MAX_X_HP_BAR_NOIR_BIS = 98;
    private final static int DOUBLE_MIN_Y_HP_BAR_NOIR_BIS = 36;
    private final static int DOUBLE_MAX_Y_HP_BAR_NOIR_BIS = 39;

    private final static List<Integer> listeAttaquesTouchantVol = Arrays.asList(16, 87, 239, 18, 327, 542, 479, 614);
    private final static List<Integer> listeAttaquesTouchantTunnel = Arrays.asList(89, 90, 222);


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

    private int methodeRencontre;

    private HashMap<Attaque, Integer> attaquesEntravees;

    public Combat(Game game, Duelliste blanc, Duelliste noir, TypeCombat typeCombat, boolean objetsAutorises, int methodeRencontre) {
        this.game = game;
        this.typeCombat = typeCombat;
        this.blanc = blanc;
        this.terrainBlanc = new Terrain();
        this.terrainNoir = new Terrain();
        this.noir = noir;
        this.methodeRencontre = methodeRencontre;
        this.turnCount = 0;
        this.objetsAutorises = objetsAutorises;
        this.attaquesEntravees = new HashMap<>();
        //set up meteo et background
        StatutsTerrain statutFromMeteo = StatutsTerrain.getStatutFromMeteo(game.getSave().getCampaign().getCurrentMeteo());
        if (statutFromMeteo != null) {
            terrainBlanc.ajoutStatut(statutFromMeteo, 999);
            terrainNoir.ajoutStatut(statutFromMeteo, 999);
        }
        setUpBackground();
    }

    private void setUpBackground() {
        String bg = game.getSave().getCampaign().getCurrentZone().getCombatBackground();
        if (game.getSave().getCampaign().getCurrentStructure() != null) {
            bg = game.getSave().getCampaign().getCurrentStructure().getCombatBackground();
        }

        if (typeCombat.equals(TypeCombat.DOUBLE)) {
            this.background = "temp/" + game.getImageManager().merge(PropertiesManager.getInstance().getImage(bg), PropertiesManager.getInstance().getImage(terrainBlanc.getMeteo().getFiltre()), false, PropertiesManager.getInstance().getImage("battle-double.ui"), 0, 0, LARGEUR, HAUTEUR);
        } else {
            this.background = "temp/" + game.getImageManager().merge(PropertiesManager.getInstance().getImage(bg), PropertiesManager.getInstance().getImage(terrainBlanc.getMeteo().getFiltre()), false, PropertiesManager.getInstance().getImage("battle.ui"), 0, 0, LARGEUR, HAUTEUR);
        }
    }

    public void resolve() {
        this.typeCombatResultat = TypeCombatResultat.EN_COURS;
        this.turnCount = 1;

        blanc.getEquipe().forEach(p -> {
            p.setItemAutorise(true);
            p.soinLegerCombat();
            p.setActionsCombat(new HashMap<>());
        });
        noir.getEquipe().forEach(p -> {
            p.setItemAutorise(true);
            p.soinLegerCombat();
            p.setActionsCombat(new HashMap<>());
        });

        game.getSave().getCampaign().getPokedex().saw(noir.getPokemonActif().getIdSpecie());

        if (typeCombat.equals(TypeCombat.DOUBLE)) {
            game.getSave().getCampaign().getPokedex().saw(noir.getPokemonActifBis().getIdSpecie());
        }

        for (Pokemon pokemon : tousLesPokemonsEnJeu()) {
            effetsEntree(pokemon, getDuellisteAllie(pokemon).getId() == blanc.getId() ? terrainBlanc : terrainNoir, true);
        }

        roundPhase0();
    }

    private List<Pokemon> tousLesPokemonsEnJeu() {
        List<Pokemon> tous = new ArrayList<>(blanc.getPokemonsActifsEnVie());
        tous.addAll(noir.getPokemonsActifsEnVie());
        return tous;
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

        if (!blanc.getPokemonChoixCourant(turnCount).getActionsCombat().containsKey(turnCount)) {

            text += "\nQue dois-faire " + blanc.getPokemonChoixCourant(turnCount).getNomPresentation() + " ?";

            //on créé le message à partir de la nouvelle image et des infos combat
            MessageCreateBuilder mcb = getMcb(blanc.getPokemonChoixCourant(turnCount), imageCombat, text);
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
                                        game.getChannel().sendMessage(choixCourant.getNomPresentation() + " ne peut pas être échangé !").queue();
                                        roundPhase1();
                                    } else {
                                        //changer de pokes
                                        selectionPokemon(blanc.getPokemonChoixCourant(turnCount), false, "phase2");
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
        } else {
            //si action déjà décidée (action en 2 tours ou autre, comme vol ou colère par exemple) on passseeee
            game.getChannel().sendMessage(game.getMessageManager().createMessageImage(game.getSave(), text, null, imageCombat)).queue();
            roundPhase2();
        }
    }

    private void selectionCibles(String idMove, Pokemon lanceur) {
        //choix cible(s)
        Move move = Client.getMoveById(Integer.parseInt(idMove));
        MoveTarget target = move.getTarget();
        TypeCibleCombat typeCibleCombat = TypeCibleCombat.getById(target.getId());
        Attaque attaque = lanceur.getMoveset().stream().filter(m -> String.valueOf(m.getIdMoveAPI()).equals(idMove)).findAny().orElse(null);

        switch (typeCibleCombat) {
            case SPECIFIC_MOVE:
            case ALL_OTHER_POKEMON:
            case ALL_OPPONENTS:
            case ENTIRE_FIELD:
            case USER_AND_ALLIES:
            case ALL_POKEMON:
            case ALL_ALLIES:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                roundPhase2();
                break;
            case SELECTED_POKEMON_ME_FIRST:
                roundPhase2();
                break;
            case ALLY:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    //échec
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
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
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, terrainBlanc));
                roundPhase2();
                break;
            case USER_OR_ALLY:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                    roundPhase2();
                } else {
                    List<Pokemon> cibles = new ArrayList<>();
                    cibles.add(blanc.getPokemonActif());
                    cibles.add(blanc.getPokemonActifBis());
                    selectionCibleManuelle(cibles, lanceur, attaque, typeCibleCombat);
                }
                break;
            case OPPONENTS_FIELD:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, terrainNoir));
                roundPhase2();
                break;
            case USER:
                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                roundPhase2();
                break;
            case RANDOM_OPPONENT:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, noir.getPokemonActif()));
                } else {
                    if (Utils.getRandom().nextBoolean() || !noir.getPokemonActif().estEnVie()) {
                        lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, noir.getPokemonActifBis()));
                    } else {
                        lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, noir.getPokemonActif()));
                    }
                }
                roundPhase2();
                break;
            case SELECTED_POKEMON:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, noir.getPokemonActif()));
                    roundPhase2();
                } else {
                    List<Pokemon> cibles = new ArrayList<>();
                    cibles.add(noir.getPokemonActifBis());
                    cibles.add(noir.getPokemonActif());
                    cibles.add(blanc.getPokemonActif());
                    cibles.add(blanc.getPokemonActifBis());
                    cibles.remove(lanceur);
                    selectionCibleManuelle(cibles, lanceur, attaque, typeCibleCombat);
                }

                break;
        }
    }

    private void selectionCibleManuelle(List<Pokemon> cibles, Pokemon lanceur, Attaque attaque, TypeCibleCombat typeCibleCombat) {
        cibles.removeIf(p -> p.getCurrentHp() <= 0);
        if (cibles.size() == 0) {
            game.getChannel().sendMessage("Aucune cible valide !").queue();
            roundPhase1();
        }
        //retour
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        List<Button> buttons = new ArrayList<>();
        List<Button> buttons2 = new ArrayList<>();

        for (Pokemon pokemon : cibles) {
            if (buttons.size() >= 5) {
                buttons2.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokemon.getId()), pokemon.getNomCompletPresentation(), Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
            } else {
                buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokemon.getId()), pokemon.getNomCompletPresentation(), Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
            }
        }
        buttons2.add(Button.of(ButtonStyle.PRIMARY, "back", "Annuler", Emoji.fromFormatted("\uD83D\uDD19")));
        LayoutComponent lc = ActionRow.of(buttons);
        LayoutComponent lc2 = ActionRow.of(buttons2);
        List<Button> allButtons = new ArrayList<>(lc.getButtons());
        allButtons.addAll(lc2.getButtons());
        mcb.addComponents(lc, lc2);
        mcb.addContent("Choisir une cible");
        game.getBot().lock(game.getUser());
        game.getChannel().sendMessage(game.getMessageManager().createMessageData(mcb)).queue(message -> game.getBot().getEventWaiter().waitForEvent( // Setup Wait action once message was send
                        ButtonInteractionEvent.class,
                        e -> game.getButtonManager().createPredicate(e, message, game.getSave().getUserId(), allButtons),
                        //action quand réponse détectée
                        e -> {
                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                            game.getBot().unlock(game.getUser());
                            if (e.getComponentId().equals("back")) {
                                roundPhase1();
                            } else {
                                lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, cibles.stream().filter(c -> String.valueOf(c.getId()).equals(e.getComponentId())).findAny().orElse(null)));
                                roundPhase2();
                            }
                        },
                        1, TimeUnit.MINUTES,
                        () -> game.getButtonManager().timeout(game.getChannel(), game.getUser())
                )
        );
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
     * @param force  si le chgt est forcé par la mort du poke actif, ou bien par choix
     * @param retour
     */
    public void selectionPokemon(Pokemon aRemplacer, boolean force, String retour) {
        List<Pokemon> dispos = new ArrayList<>(blanc.getEquipe());
        dispos.remove(aRemplacer);
        dispos.removeIf(p -> p.getCurrentHp() <= 0);
        if (dispos.size() == 0) {
            game.getChannel().sendMessage("Vous n'avez pas d'autres pokémons en état de se battre").queue();
            roundPhase1();
            return;
        }

        //equipe sauf actif
        //retour
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        List<Button> buttons = new ArrayList<>();
        List<Button> buttons2 = new ArrayList<>();

        for (Pokemon pokemon : game.getSave().getCampaign().getEquipe()) {
            if (buttons.size() >= 5) {
                buttons2.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokemon.getId()), pokemon.getNomCompletPresentation(), Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
            } else {
                buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokemon.getId()), pokemon.getNomCompletPresentation(), Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
            }
        }
        buttons2.add(Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19")));
        LayoutComponent lc = ActionRow.of(buttons);
        LayoutComponent lc2 = ActionRow.of(buttons2);
        List<Button> allButtons = new ArrayList<>(lc.getButtons());
        allButtons.addAll(lc2.getButtons());
        mcb.addComponents(lc, lc2);
        mcb.addContent("Équipe");
        game.getBot().lock(game.getUser());
        game.getChannel().sendMessage(game.getMessageManager().createMessageData(mcb)).queue(message -> game.getBot().getEventWaiter().waitForEvent( // Setup Wait action once message was send
                        ButtonInteractionEvent.class,
                        e -> game.getButtonManager().createPredicate(e, message, game.getSave().getUserId(), allButtons),
                        //action quand réponse détectée
                        e -> {
                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                            game.getBot().unlock(game.getUser());
                            if (e.getComponentId().equals("back")) {
                                roundPhase1();
                            } else {
                                //switch active pokemno
                                Pokemon incoming = blanc.getEquipe().stream().filter(a -> String.valueOf(a.getId()).equals(e.getComponentId())).findAny().orElseThrow(IllegalStateException::new);
                                changerPokemonActifAction(aRemplacer, incoming, retour);
                            }
                        },
                        1, TimeUnit.MINUTES,
                        () -> game.getButtonManager().timeout(game.getChannel(), game.getUser())
                )
        );

    }

    public void fail() {
        game.getChannel().sendMessage("Mais cela échoue !").queue();
    }

    private void changerPokemonActifAction(Pokemon sortant, Pokemon entrant, String retour) {
        changerPokemonActif(blanc, sortant, entrant);
        if (retour.equals("phase2")) {
            roundPhase2();
        } else if (retour.equals("phase3")) {
            roundPhase3();
        } else {
            throw new IllegalStateException("Retour invalide sélection pokémon : " + retour);
        }

    }

    public void changerPokemonActif(Duelliste duelliste, Pokemon sortant, Pokemon entrant) {
        //changement de kemon
        sortant.soinLegerCombat();
        sortant.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.SWITCH_OUT));

        //        if (Talent.NATURAL_CURE.equals(currentPokemonFirstTrainer.getTalent())) {
//            currentPokemonFirstTrainer.getStatuses().clear();
//        }

        if (sortant.equals(duelliste.getPokemonActif())) {
            duelliste.setPokemonActif(entrant);
            duelliste.getPokemonActif().getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.SWITCH_IN));

        } else {
            duelliste.setPokemonActifBis(entrant);
            duelliste.getPokemonActifBis().getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.SWITCH_IN));
        }

        try {
            updateImageCombat();
        } catch (IOException ioe) {
            logger.error("Erreur update image", ioe);
            throw new IllegalStateException("Erreur mise à jour de l'image");
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
                game.getChannel().sendMessage(fuyard.getNomPresentation() + " est piégé et ne peut pas s'enfuir !").queue();
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
            return;
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
            typeCombatResultat = TypeCombatResultat.FUITE_JOUEUR;
            game.apresCombat(this);
        } else {
            //échec de la fuite
            game.getChannel().sendMessage("Vous ne parvenez pas à vous enfuir !").queue();
            roundPhase2();
        }
    }

    private void pokeballMenu() {
        List<Button> buttons = new ArrayList<>();

        List<Pokeball> pokeballs = game.getSave().getCampaign().getInventaire().getAllPokeballsTypes();

        for (Pokeball pokeball : pokeballs) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokeball.getIdItemApi()), Item.getById(pokeball.getIdItemApi()).getLibelle(), pokeball.getEmoji()));
        }

//        int nbGroupes = pokeballs.size() / 5;

        buttons.add(Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19")));

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
                                    if (e.getComponentId().equals("back")) {
                                        roundPhase1();
                                    } else {
                                        launchPokeball(Pokeball.getById(e.getComponentId()));
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
        if (!noir.getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE) || noir.getPokemonActif().hasStatut(AlterationEtat.SEMI_INVULNERABLE)) {
            //erreur
            fail();
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

        StringBuilder pokeEmotes = new StringBuilder();
        for (int i = 0; i < secoussesReussies; i++) {
            pokeEmotes.append(Emoji.fromCustom("pokeball", 1032561600701399110L, false).getFormatted());
        }
        game.getChannel().sendMessage(pokeEmotes).queue();

        if (secoussesReussies >= 4) {
            //réussite de la capture
            noir.getPokemonActif().setFriendship(Pokemon.BASE_FRIENDSHIP_VALUE);
            game.getSave().getCampaign().getPokedex().captured(noir.getPokemonActif().getIdSpecie());

            if (game.getSave().getCampaign().getEquipe().size() < 6) {
                game.getSave().getCampaign().getEquipe().add(noir.getPokemonActif());
            } else {
                game.getSave().getCampaign().getReserve().add(noir.getPokemonActif());
            }

            this.typeCombatResultat = TypeCombatResultat.CAPTURE;
            game.apresCombat(this);
        } else {
            game.getChannel().sendMessage("Oh non ! Le " + noir.getPokemonActif().getNomPresentation() + " s'est échappé de la pokéball !").queue();
            roundPhase2();
        }
    }

    private double modifiedCatchRate(Pokeball pokeball) {
        Pokemon cible = noir.getPokemonActif();
        double hpMax = cible.getMaxHp();
        double hpCur = cible.getCurrentHp();
        double rate = cible.getPokemonAPI().getSpecies().getCaptureRate();
        double effPokeball = 1;

        switch (pokeball) {
            case NETBALL:
                if (cible.hasType(Type.WATER) || cible.hasType(Type.BUG)) {
                    effPokeball = 3;
                }
                break;
            case DIVEBALL:
                if (methodeRencontre > 1 && methodeRencontre < 6) {
                    effPokeball = 3.5;
                }
                break;
            case NESTBALL:
                if (cible.getLevel() > 30) {
                    effPokeball = 1;
                } else {
                    effPokeball = (double) (40 - cible.getLevel()) / 10;
                }
                break;
            case REPEATBALL:
                if (game.getSave().getCampaign().getPokedex().hasCaptured(cible.getIdSpecie())) {
                    effPokeball = 3;
                }
                break;
            case TIMERBALL:
                effPokeball = turnCount * 0.1 > 4 ? 4 : turnCount * 0.1;
                break;
            case DUSKBALL:
                //TODO 3.5 at night and caves
                break;
            case QUICKBALL:
                if (turnCount == 1) {
                    effPokeball = 4;
                }
                break;
            default:
                effPokeball = pokeball.getEfficacite();
        }

        double bonusStatus = 1;
        if (cible.hasStatut(AlterationEtat.SOMMEIL) || cible.hasStatut(AlterationEtat.GEL)) {
            bonusStatus = 2.5;
        } else if (cible.hasAnyNonVolatileStatus()) {
            bonusStatus = 1.5;
        }

        return (((3 * hpMax - 2 * hpCur) * rate * effPokeball) / 3 * hpMax) * bonusStatus;
    }

    public void roundPhase2() {
        //on vérifie que tous les pokémons actifs du joueur ont choisis une action, sinon on retourne à la phase 1 (double up)
        if (typeCombat.equals(TypeCombat.SIMPLE)) {
            if (blanc.getPokemonActif().getActionsCombat().get(turnCount) == null) {
                roundPhase1();
            }
        } else if (typeCombat.equals(TypeCombat.DOUBLE)) {
            if (blanc.getPokemonActifBis().getActionsCombat().get(turnCount) == null || blanc.getPokemonActif().getActionsCombat().get(turnCount) == null) {
                roundPhase1();
            }
        }

        //choix auto des actions IA
        if (noir.getPokemonActif() != null && noir.getPokemonActif().estEnVie()) {
            choixAttaqueAuto(noir.getPokemonActif());
        }
        if (noir.getPokemonActifBis() != null && noir.getPokemonActifBis().estEnVie()) {
            choixAttaqueAuto(noir.getPokemonActifBis());
        }

        //attaque si sauvage
        //mais aussi eventuellement utiliser une potion ou changer de pokemon selon l'IA en face

        ordreDAction().forEach(p -> effectuerAction(p, false));

        effetsDeFinDeTour();
        turnCount++;

        roundPhase3();
    }

    /**
     * Cette phase consiste en le remplacement des pokémons actifs KO du joueur
     */
    private void roundPhase3() {
        if (!blanc.aPerdu()) {
            if (!blanc.getPokemonActif().estEnVie()) {
                game.getChannel().sendMessage("Remplacez votre pokémon actif").queue();
                selectionPokemon(blanc.getPokemonActif(), true, "phase3");
            } else if (typeCombat.equals(TypeCombat.DOUBLE) && !blanc.getPokemonActifBis().estEnVie()) {
                game.getChannel().sendMessage("Remplacez votre second pokémon actif").queue();
                selectionPokemon(blanc.getPokemonActifBis(), true, "phase3");
            } else {
                roundPhase4();
            }
        } else {
            roundPhase4();
        }
    }

    /**
     * Cette dernière phase consiste à vérifier si le combat est terminé et à agir en conséquence
     */
    private void roundPhase4() {
        remplacementPokemonAuto();

        //vérif combat terminé
        if (!blanc.aPerdu() && !noir.aPerdu()) {
            if (typeCombatResultat.equals(TypeCombatResultat.EN_COURS)) {
                //tour suivant
                roundPhase0();
            } else {
                game.apresCombat(this);
            }
        } else {
            if (blanc.getPokemonsActifsEnVie().isEmpty()) {
                typeCombatResultat = TypeCombatResultat.DEFAITE;
            } else {
                typeCombatResultat = TypeCombatResultat.VICTOIRE;
            }
            game.apresCombat(this);
        }
    }

    private void remplacementPokemonAuto() {
        if (noir.getPokemonActif() != null && !noir.getPokemonActif().estEnVie()) {
            noir.setPokemonActif(choixAutoBestPokemon(noir.getPokemonActif()));
        }

        if (typeCombat.equals(TypeCombat.DOUBLE) && noir.getPokemonActifBis() != null && !noir.getPokemonActifBis().estEnVie()) {
            noir.setPokemonActifBis(choixAutoBestPokemon(noir.getPokemonActifBis()));
        }
    }

    private Pokemon choixAutoBestPokemon(Pokemon sortant) {
        sortant.soinLegerApresCombat();
        game.getChannel().sendMessage(noir.getPokemonActif().getNomPresentation() + " est K.O ! Vos pokémons gagnent de l'expérience !").queue();
        blanc.getPokemonsEnVie().forEach(v -> v.gainXp(calculerXp(v, sortant, blanc.getPokemonsActifsEnVie().contains(v)), true, game));

        List<Pokemon> disponibles = noir.getEquipe().stream().filter(p -> p.estEnVie() && !noir.getPokemonsActifsEnVie().contains(p)).collect(Collectors.toList());
        if (disponibles.isEmpty()) {
            return null;
        } else if (disponibles.size() == 1) {
            game.getChannel().sendMessage(noir.getNom() + " envoie " + disponibles.get(0).getNomPresentation() + " !").queue();
            return disponibles.get(0);
        } else {
            Pokemon choisi;
            if (noir.getNiveauIA().equals(NiveauIA.PRO) || noir.getNiveauIA().equals(NiveauIA.AVANCE)) {
                //choix réfléchi
                HashMap<Pokemon, Integer> map = new HashMap<>();
                disponibles.forEach(m -> {
                    map.put(m, advantageCoeff(m, blanc) - dangerCoeff(m, blanc));
                });
                choisi = (Collections.max(map.entrySet(), Comparator.comparingDouble(Map.Entry::getValue))).getKey();
                //random pour les ai basic et dumb
            } else {
                choisi = disponibles.get(Utils.getRandom().nextInt(disponibles.size()));
            }
            game.getChannel().sendMessage(noir.getNom() + " envoie " + choisi.getNomPresentation() + " !").queue();
            return choisi;
        }
    }

    private int dangerCoeff(Pokemon pokemon, Duelliste opponent) {
        List<Type> typesAdversaires = opponent.getPokemonsActifsEnVie().stream().map(Pokemon::getTypes).flatMap(Collection::stream).collect(Collectors.toList());
        return typesAdversaires.stream().map(t -> t.pourcentageDegatsAttaque(pokemon.getPokemonAPI().getTypes())).max(Integer::compare).orElse(100);
    }

    private int advantageCoeff(Pokemon pokemon, Duelliste opponent) {
        int advRatio = 0;
        List<Type> typeAv = pokemon.getMoveset().stream().filter(m -> m.getMoveAPI().getDamageClass().getId() != 1).map(m -> Type.getById(m.getMoveAPI().getType().getId())).collect(Collectors.toList());
        if (typeAv.isEmpty()) {
            typeAv.add(Type.NORMAL);
        }
        for (Type type : typeAv) {
            advRatio = Math.max(type.pourcentageDegatsAttaque(pokemon.getPokemonAPI().getTypes()), advRatio);
        }
        return advRatio;
    }

    private int calculerXp(Pokemon v, Pokemon sortant, boolean aParticipe) {
        int s = aParticipe ? 1 : 2;
        int level = sortant.getLevel();
        int xpValue = sortant.getPokemonAPI().getBaseExperience();
        double tradeMultiplier = v.isaEteEchange() ? 1.5 : 1;
        double luckyEgg = 1; //TODO 1.5 si tient un lucky egg
        double amitie = v.getFriendship() >= 220 ? 1.2 : 1;
        return (int) ((xpValue * level / 5.0)
                * (1.0 / s)
                * (Math.pow((double) (2 * level + 10) / (level + v.getLevel() + 10), 2.5) + 1)
                * tradeMultiplier
                * luckyEgg
                * Game.XP_MULTIPLIER_CUSTOM
                * amitie);
    }


    @JsonIgnore
    public Combat getCopy() {
        return (Combat) SerializationUtils.clone(this);
    }

    private void choixAttaqueAuto(Pokemon pokemon) {
        List<Pokemon> ciblesPotentielles = new ArrayList<>(blanc.getPokemonsActifsEnVie());

        List<Attaque> availables = pokemon.getMoveset().stream().filter(m -> m.getPpLeft() > 0 && !attaquesEntravees.containsKey(m)).collect(Collectors.toList());
        if (pokemon.hasStatut(AlterationEtat.PROVOCATION)) {
            availables = availables.stream().filter(a -> a.getMoveAPI().getDamageClass().getId() != 1).collect(Collectors.toList());
        }
        if (availables.isEmpty()) {
            availables.add(new Attaque(Client.getMoveByName("struggle")));
        }

        if (noir.getNiveauIA() == NiveauIA.RANDOM) {
            Pokemon cible = ciblesPotentielles.get(Utils.getRandom().nextInt(ciblesPotentielles.size()));
            pokemon.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, availables.get(Utils.getRandom().nextInt(availables.size())), TypeCibleCombat.RANDOM_OPPONENT, noir.getPokemonActif(), cible));
            return;
        }

        //dernier recours est utilisé intelligemment
        if (availables.size() >= 2) {
            availables.removeIf(a -> a.getIdMoveAPI() == 387);
        }

        HashMap<ActionCombat, Double> map = new HashMap<>();
        final double scoreFight = evaluer(noir) - evaluer(blanc);
        availables.forEach(m -> {
            Pokemon pokemonLauncherCopy = pokemon.getCopy();
            TypeCibleCombat typeCibleCombat = TypeCibleCombat.getById(m.getMoveAPI().getTarget().getId());
            List<ActionCombat> listeActions = new ArrayList<>();
            selectionCiblesIA(m, pokemonLauncherCopy, listeActions);

            for (ActionCombat action : listeActions) {
                Combat copie = getCopy();
                copie.effectuerAction(pokemonLauncherCopy, true);
                double foeEvaluation = copie.evaluer(copie.getBlanc());
                double newScoreFight = copie.evaluer(copie.getNoir()) - foeEvaluation;

                if (foeEvaluation <= 0) {
                    newScoreFight = newScoreFight * 3;
                }
                map.put(action, (newScoreFight - scoreFight));
            }
        });
        ActionCombat result = (Collections.max(map.entrySet(), Comparator.comparingDouble(Map.Entry::getValue))).getKey();
        //les IA elite et master se voient offrir la possibilité d'utiliser 1 ou 2 guérisons, quand le combat l'autorise
        if (objetsAutorises && noir.getTypeDuelliste().equals(TypeDuelliste.PNJ) && noir.getPotionsRestantes() > 0) {
            //TODO calculer valeur guerison

        }
        pokemon.getActionsCombat().put(turnCount, result);
    }

    private void selectionCiblesIA(Attaque attaque, Pokemon lanceur, List<ActionCombat> listeActions) {
        Duelliste blancCopie = blanc.getCopy();
        Duelliste noirCopie = noir.getCopy();
        Terrain terrainBlancCopie = terrainBlanc.getCopy();
        Terrain terrainNoirCopie = terrainNoir.getCopy();

        //choix cible(s)
        Move move = Client.getMoveById(attaque.getIdMoveAPI());
        MoveTarget target = move.getTarget();
        TypeCibleCombat typeCibleCombat = TypeCibleCombat.getById(target.getId());

        switch (typeCibleCombat) {
            case SPECIFIC_MOVE:
            case ALL_OTHER_POKEMON:
            case ALL_OPPONENTS:
            case ENTIRE_FIELD:
            case USER_AND_ALLIES:
            case ALL_POKEMON:
            case ALL_ALLIES:
                listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                break;
            case ALLY:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    //échec
                    lanceur.getActionsCombat().put(turnCount, new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                } else {
                    if (lanceur.equals(noirCopie.getPokemonActif())) {
                        listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, noirCopie.getPokemonActifBis()));
                    } else {
                        listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, noirCopie.getPokemonActif()));
                    }
                }
                roundPhase2();
                break;
            case USERS_FIELD:
                ActionCombat actionCombatUF = new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur);
                actionCombatUF.setTerrainCible(terrainNoirCopie);
                listeActions.add(actionCombatUF);
                break;
            case USER_OR_ALLY:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, lanceur));
                } else {
                    List<Pokemon> cibles = new ArrayList<>();
                    cibles.add(blancCopie.getPokemonActif());
                    cibles.add(blancCopie.getPokemonActifBis());
                    for (Pokemon cible : cibles) {
                        listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, cible));
                    }
                }
                break;
            case OPPONENTS_FIELD:
                ActionCombat actionCombatOF = new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur);
                actionCombatOF.setTerrainCible(terrainBlancCopie);
                listeActions.add(actionCombatOF);
                break;
            case USER:
                listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur));
                break;
            case RANDOM_OPPONENT:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, blancCopie.getPokemonActif()));
                } else {
                    if (Utils.getRandom().nextBoolean() || !noirCopie.getPokemonActif().estEnVie()) {
                        listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, blancCopie.getPokemonActifBis()));
                    } else {
                        listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, blancCopie.getPokemonActif()));
                    }
                }
                break;
            case SELECTED_POKEMON_ME_FIRST:
            case SELECTED_POKEMON:
                if (typeCombat.equals(TypeCombat.SIMPLE)) {
                    listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, blancCopie.getPokemonActif()));
                } else {
                    List<Pokemon> cibles = new ArrayList<>();
                    cibles.add(noirCopie.getPokemonActifBis());
                    cibles.add(noirCopie.getPokemonActif());
                    cibles.add(blancCopie.getPokemonActif());
                    cibles.add(blancCopie.getPokemonActifBis());
                    cibles.remove(lanceur);
                    for (Pokemon cible : cibles) {
                        listeActions.add(new ActionCombat(TypeActionCombat.ATTAQUE, attaque, typeCibleCombat, lanceur, cible));
                    }
                }
                break;
        }
    }

    public void effectuerAction(Pokemon lanceur, boolean simulation) {
        lanceur.setaDejaAttaque(true);

        ActionCombat actionCombat = lanceur.getActionsCombat().get(turnCount);

        //si le combat n'est plus en cours, on n'effectue pas d'action supplémentaire
        if (!typeCombatResultat.equals(TypeCombatResultat.EN_COURS)) {
            return;
        }

        //on n'effectue des actions qu'en mode ATTAQUE ici
        if (!actionCombat.getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
            return;
        }

        Move attaqueSelectionnee = actionCombat.getAttaque().getMoveAPI();

        //pas d'attaque si le lanceur meurt entretemps
        if (lanceur.getCurrentHp() <= 0) {
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.APEURE)) {
            game.getChannel().sendMessage(lanceur.getNomPresentation() + " est effrayé et ne peut pas attaquer !").queue();
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.CHARGING_TURN)) {
            String text = lanceur.getNomPresentation();
            if (attaqueSelectionnee.getId() == 143) {//sky attack
                text += " est entouré d'une lumière intense !";
            } else if (attaqueSelectionnee.getId() == 76 || attaqueSelectionnee.getId() == 669) {
                text += " absorbe la lumière !";
            } else if (attaqueSelectionnee.getId() == 13) {
                text += " déclenche un cyclone !";
            } else {
                text += " prépare son attaque !";
            }
            game.getChannel().sendMessage(text).queue();
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.RECHARGE)) {
            game.getChannel().sendMessage(lanceur.getNomPresentation() + " est fatigué et doit se recharger !").queue();
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.GEL)) {
            game.getChannel().sendMessage(lanceur.getNomPresentation() + " est congelé et ne peut pas bouger !").queue();
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.SOMMEIL)) { //&& !(selectedMove.equals(Moves.RONFLEMENT) || selectedMove.equals(Moves.BLABLA_DODO))
            game.getChannel().sendMessage(lanceur.getNomPresentation() + " est endormi et ne peut pas bouger !").queue();
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
            game.getChannel().sendMessage(lanceur.getNomPresentation() + " est paralysé et ne peut pas bouger !").queue();
            return;
        }
//
        if (lanceur.hasStatut(AlterationEtat.CHARME) && Utils.getRandom().nextBoolean()) {
            game.getChannel().sendMessage(lanceur.getNomPresentation() + " est amoureux et refuse d'attaquer !").queue();
            return;
        }

        if (lanceur.hasStatut(AlterationEtat.CONFUSION)) {
            if (Utils.randomTest(33)) {
                game.getChannel().sendMessage(lanceur.getNomPresentation() + " se blesse dans sa confusion !").queue();
                //TODO Confusion damage is calculated as if it were a typeless physical move with a power of 40; it cannot score a critical hit, and does not receive STAB
                lanceur.blesser(40, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
                return;
            }
        }

        game.getChannel().sendMessage(lanceur.getNomPresentation() + " utilise " + APIUtils.getFrName(attaqueSelectionnee.getNames()) + " !").queue();

        switch (attaqueSelectionnee.getMeta().getCategory().getName()) {
            case "damage":
                MoveDamage.utiliser(this, actionCombat, simulation);
                break;
            case "ailment":
                MoveAilment.utiliser(this, actionCombat, simulation);
                break;
            case "net-good-stats":
                MoveNetGoodStats.utiliser(this, actionCombat, simulation);
                break;
            case "heal":
                MoveHeal.utiliser(this, actionCombat, simulation);
                break;
            case "damage+ailment":
                MoveDamageAilment.utiliser(this, actionCombat, simulation);
                break;
            case "swagger":
                MoveSwagger.utiliser(this, actionCombat, simulation);
                break;
            case "damage+lower":
                MoveDamageLower.utiliser(this, actionCombat, simulation);
                break;
            case "damage+raise":
                MoveDamageRaise.utiliser(this, actionCombat, simulation);
                break;
            case "damage+heal":
                MoveDamageHeal.utiliser(this, actionCombat, simulation);
                break;
            case "ohko":
                MoveOHKO.utiliser(this, actionCombat, simulation);
                break;
            case "whole-field-effect":
                MoveWholeFieldEffect.utiliser(this, actionCombat, simulation);
                break;
            case "field-effect":
                MoveFieldEffect.utiliser(this, actionCombat, simulation);
                break;
            case "force-switch":
                MoveForceSwitch.utiliser(this, actionCombat, simulation);
                break;
            case "unique":
                MoveUnique.utiliser(this, actionCombat, simulation);
                break;
            default:
                logger.error("Catégorie d'attaque inconnue : " + attaqueSelectionnee.getMeta().getCategory().getName());
                throw new IllegalStateException("Catégorie d'attaque inconnue");
        }
    }

    public boolean tenirCompteDeLaMeteo() {
        boolean tenirCompteDeLaMeteo = true;
        // TODO on ne prend pas en compte les effets de la météo si un kemon avec CLOUD NINE est présent et AIR LOCK aussi
//        if (Talent.CLOUD_NINE.equals(currentPokemonSecondTrainer.getTalent()) || Talent.CLOUD_NINE.equals(currentPokemonFirstTrainer.getTalent())) {
//            return false;
//        }
        return tenirCompteDeLaMeteo;
    }

    public void effetsEntree(Pokemon entree, Terrain terrain, boolean affichage) {
        //TODO impl. talents et items
        //AIR BALLON
//            if (HeldItem.AIR_BALLOON.equals(entered.getHeldItem()) && !simulation) {
//                Utils.println(entered.getLibelleColorizedAndLvl() + " flotte grâce à son Ballon.");
//            }
//            //RAZOR CLAW monte le taux de crit
//            if (HeldItem.RAZOR_CLAW.equals(entered.getHeldItem()) || HeldItem.SCOPE_LENS.equals(entered.getHeldItem())) {
//                entered.updateStage(1, simulation, Stats.CRIT);
//            }
//            //WIDE LENS monte l'accuracy'
//            if (HeldItem.WIDE_LENS.equals(entered.getHeldItem())) {
//                entered.updateStage(1, simulation, Stats.ACCURACY);
//            }
//
//            //TRACE copie le talent de l'adversaire
//            if (Talent.TRACE.equals(entered.getTalent())) {
//                if (!simulation) {
//                    Utils.println(entered.getLibelleColorized() + " copie " + foe.getTalent().getLibelle() + " !");
//                }
//                entered.setTalent(foe.getTalent());
//            }
//            //DOWNLOAD change les stats par rapport à la def majo de l'adversaire
//            if (Talent.DOWNLOAD.equals(entered.getTalent())) {
//                if (!simulation) {
//                    Utils.println(entered.getLibelleColorized() + " ajuste ses stats par rapport à son adversaire !");
//                }
//                if (foe.getCurrentDefSpe() > foe.getCurrentDefPhy()) {
//                    entered.updateStage(1, false, Stats.ATK_PHY);
//                } else {
//                    entered.updateStage(1, false, Stats.ATK_SPE);
//                }
//            }
//            //INTIMIDATE
//            if (Talent.INTIMIDATE.equals(entered.getTalent())) {
//                if (!simulation) {
//                    Utils.println(Talent.INTIMIDATE.getLibelle() + " de " + entered.getLibelleColorized() + "  baisse l'attaque de " + foe.getLibelleColorized());
//                }
//                foe.updateStage(-1, false, Stats.ATK_PHY);
//            }
//            //FOREWARN
//            if (Talent.FOREWARN.equals(entered.getTalent())) {
//                List<Moves> mostPowerfulMove = foe.getMoveset()
//                        .stream().map(Attack::getMove)
//                        .sorted(Comparator.comparing(Moves::getPower)
//                                .reversed()
//                        )
//                        .collect(Collectors.toList());
//                if (!mostPowerfulMove.isEmpty() && !simulation) {
//                    Utils.println(entered.getLibelleColorized() + " avertit que " + foe.getLibelleColorized() + " connaît l'attaque " + mostPowerfulMove.get(0) + " !");
//                }
//            }

        //FIELD effects
        if (terrain.hasStatut(StatutsTerrain.SPIKES) && entree.isGrounded(terrain)) {
            long nb = terrain.getAlterations().get(StatutsTerrain.SPIKES);
            int dmg;
            switch ((int) nb) {
                case 1:
                    dmg = entree.getMaxHp() / 8;
                    break;
                case 2:
                    dmg = entree.getMaxHp() / 6;
                    break;
                case 3:
                    dmg = entree.getMaxHp() / 4;
                    break;
                default:
                    throw new IllegalStateException(nb + " : Nb occurence picots invalide");
            }
            if (!affichage) {
                game.getChannel().sendMessage(entree.getNomPresentation() + " est blessé par les picots au sol !").queue();
            }
            entree.blesser(dmg, new SourceDegats(TypeSourceDegats.STATUT_TERRAIN));
        }

        if (terrain.hasStatut(StatutsTerrain.TOXIC_SPIKES) && entree.isGrounded(terrain)) {
            long nb = terrain.getAlterations().get(StatutsTerrain.TOXIC_SPIKES);
            if (nb > 1) {
                entree.applyStatus(AlterationEtat.POISON_GRAVE, new SourceDegats(TypeSourceDegats.STATUT_TERRAIN), 1, affichage, game);
            } else {
                entree.applyStatus(AlterationEtat.POISON, new SourceDegats(TypeSourceDegats.STATUT_TERRAIN), 1, affichage, game);
            }
            if (!affichage) {
                game.getChannel().sendMessage(entree.getNomPresentation() + " est empoisonné par les picots au sol !").queue();
            }
        }

        if (terrain.hasStatut(StatutsTerrain.STEALTH_ROCK)) {
            int rtio = Type.ROCK.pourcentageDegatsAttaque(entree.getPokemonAPI().getTypes());
            double multiplier = rtio * 12.5 / 100;
            int damage = (int) (multiplier * entree.getMaxHp());
            entree.blesser(damage, new SourceDegats(TypeSourceDegats.STATUT_TERRAIN));
            if (!affichage) {
                game.getChannel().sendMessage(entree.getNomPresentation() + " est blessé par les pierres !").queue();
            }
        }
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

            if (k.getActionsCombat().get(turnCount).getTypeActionCombat().equals(TypeActionCombat.ATTAQUE)) {
                if (k.getActionsCombat().get(turnCount) != null) {
                    v += k.getActionsCombat().get(turnCount).getAttaque().getMoveAPI().getPriority() * 1000;
                }
            }

//        } else if (HeldItem.QUICK_CLAW.equals( blanc.getPokemonActif().getHeldItem()) && Utils.randomTest(20)) {
//            return true;
//        } else if (HeldItem.QUICK_CLAW.equals(noir.getPokemonActif().getHeldItem()) && Utils.randomTest(20)) {
//            return false;

            scoresActionPokemons.put(k, v);
        });

        // Create an ArrayList and insert all hashmap key-value pairs.
        List<Map.Entry<Pokemon, Integer>> sortedList = new ArrayList<>(scoresActionPokemons.entrySet());

        // Sort the Arraylist using a custom comparator.
        sortedList.sort(Comparator.comparingInt(Map.Entry::getValue));

        return sortedList.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public Terrain getTerrainAllie(Pokemon pokemon) {
        return getDuellisteAllie(pokemon).equals(noir) ? terrainNoir : terrainBlanc;
    }

    private void effetsDeFinDeTour() {
        //TODO effets des items (restes, etc)
        for (Pokemon pokemon : tousLesPokemonsEnJeu()) {
            Terrain terrain = getTerrainAllie(pokemon);
            if (pokemon.hasStatut(AlterationEtat.RACINES)) {
                game.getChannel().sendMessage(pokemon.getNomPresentation() + AlterationEtat.RACINES.getAfflictionText()).queue();
                int heal = pokemon.getMaxHp() / 16;
//            if (HeldItem.BIG_ROOT.equals(currentPokemonFirstTrainer.getHeldItem())) {
//                heal = (int) (heal * 1.3);
//            }
                pokemon.soigner(heal, game);
            }

            if (pokemon.hasStatut(AlterationEtat.ANNEAU_HYDRO)) {
                game.getChannel().sendMessage(pokemon.getNomPresentation() + AlterationEtat.ANNEAU_HYDRO.getAfflictionText()).queue();
                int heal = pokemon.getMaxHp() / 16;
//            if (HeldItem.BIG_ROOT.equals(currentPokemonFirstTrainer.getHeldItem())) {
//                heal = (int) (heal * 1.3);
//            }
                pokemon.soigner(heal, game);
            }

            if (pokemon.hasStatut(AlterationEtat.VAMPIGRAINE)) {

                game.getChannel().sendMessage("Les points de vie de " + pokemon.getNomPresentation() + " sont drainés !").queue();
                int drain = pokemon.getMaxHp() / 8;
                pokemon.blesser(drain, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
//            if (HeldItem.BIG_ROOT.equals(currentPokemonFirstTrainer.getHeldItem())) {
//                drain = (int) (drain * 1.3);
//            }
                pokemon.getAlterations().stream().filter(a -> a.getAlterationEtat().equals(AlterationEtat.VAMPIGRAINE) && a.getSourceAlteration().getTypeSourceDegats().equals(TypeSourceDegats.POKEMON) && a.getSourceAlteration().getPokemonSource() != null).findAny().ifPresent(s -> {
                    s.getSourceAlteration().getPokemonSource().soigner(drain, game);
                });
            }

            if (pokemon.hasStatut(AlterationEtat.LIEN)) {
                AlterationInstance ai = pokemon.getAlterationInstance(AlterationEtat.LIEN);
                if (ai.getSourceAlteration().getPokemonSource() != null) {
                    game.getChannel().sendMessage(pokemon.getNomPresentation() + " subit des dégâts de l'étreinte de " + ai.getSourceAlteration().getPokemonSource().getNomPresentation() + " !").queue();
                }
                //if holding binding band 1/6
                pokemon.blesser(pokemon.getMaxHp() / 8, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
            }

            if (pokemon.hasStatut(AlterationEtat.BRULURE)) {
                game.getChannel().sendMessage(pokemon.getNomPresentation() + AlterationEtat.BRULURE.getAfflictionText()).queue();
                pokemon.blesser(pokemon.getMaxHp() / 8, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
            }

            if (pokemon.hasStatut(AlterationEtat.MALEDICTION)) {
                game.getChannel().sendMessage(pokemon.getNomPresentation() + AlterationEtat.MALEDICTION.getAfflictionText()).queue();
                pokemon.blesser(pokemon.getMaxHp() / 4, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
            }

            if (pokemon.hasStatut(AlterationEtat.POISON) || pokemon.hasStatut(AlterationEtat.POISON_GRAVE)) {
                game.getChannel().sendMessage(pokemon.getNomPresentation() + AlterationEtat.POISON.getAfflictionText()).queue();
                if (pokemon.hasStatut(AlterationEtat.POISON_GRAVE)) {
                    AlterationInstance ai = pokemon.getAlterationInstance(AlterationEtat.POISON_GRAVE);
                    pokemon.blesser((pokemon.getMaxHp() / 16) * ai.getToursRestants(), new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
                    ai.setToursRestants(ai.getToursRestants() + 1);
                } else {
                    pokemon.blesser(pokemon.getMaxHp() / 8, new SourceDegats(TypeSourceDegats.ALTERATION_ETAT));
                }
            }

            if (pokemon.hasStatut(AlterationEtat.REQUIEM)) {
                AlterationInstance requiem = pokemon.getAlterationInstance(AlterationEtat.REQUIEM);
                game.getChannel().sendMessage(pokemon.getNomPresentation() + " tombera K.O. dans " + requiem.getToursRestants() + " tours !").queue();
            }

            if (tenirCompteDeLaMeteo()) {
                //SAND STORM
                if (terrain.hasStatut(StatutsTerrain.SANDSTORM)) {
                    //si pokemon onn immunisé par type ou sand veil, lose 1/16 hp
                    if (!pokemon.hasType(Type.GROUND)
                            && !pokemon.hasType(Type.STEEL)
                            && !pokemon.hasType(Type.ROCK)
//                        && Talent.SAND_VEIL.equals(currentPokemonSecondTrainer.getTalent())
                    ) {
                        game.getChannel().sendMessage(pokemon.getNomPresentation() + " est blessé par la tempête de sable !").queue();
                        pokemon.blesser(pokemon.getMaxHp() / 16, new SourceDegats(TypeSourceDegats.STATUT_TERRAIN));
                    }
                } else if (terrain.hasStatut(StatutsTerrain.HAIL)) {
                    //si kemon non immunisé par type ou snow cloak, lose 1/16 hp
                    if (!pokemon.hasType(Type.ICE)
//                        && Talent.SNOW_CLOAK.equals(currentPokemonSecondTrainer.getTalent())
                    ) {
                        game.getChannel().sendMessage(pokemon.getNomPresentation() + " est blessé par la grêle !").queue();
                        pokemon.blesser(pokemon.getMaxHp() / 16, new SourceDegats(TypeSourceDegats.STATUT_TERRAIN));
                    }
                }
            }

            if (pokemon.hasStatut(AlterationEtat.THRASHING)) {
                ActionCombat ac = pokemon.getActionsCombat().get(turnCount);
                pokemon.getActionsCombat().put(turnCount + 1, new ActionCombat(ac));
            }

            pokemon.setDernierMontantDePVsPerdus(0);
            pokemon.setaDejaAttaque(false);

            pokemon.decrementerAlterations(game);
        }

        decrementerEntraves();

        for (Pokemon pokemonActif : getPokemonsActifs()) {
            if(pokemonActif.getPatienceTours()>0){
                pokemonActif.setPatienceTours(pokemonActif.getPatienceTours()-1);
            }
        }

        //decrementation des tours restants des altérations terrain
        terrainBlanc.finDeTourMajStatus();
        terrainNoir.finDeTourMajStatus();
    }

    @NotNull
    private MessageCreateBuilder getMcb(Pokemon courant, String imageCombat, String text) {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        List<Button> buttons = new ArrayList<>();
        List<Button> buttons2 = new ArrayList<>();

        for (Attaque attaque : courant.getMoveset()) {
            Move move = attaque.getMoveAPI();
            Type type = Type.getById(move.getType().getId());
            Button button = Button.of(ButtonStyle.PRIMARY, String.valueOf(attaque.getIdMoveAPI()), APIUtils.getFrName(move.getNames()) + " " + attaque.getPpLeft() + "/" + (move.getPp() + attaque.getBonusPp()), Emoji.fromCustom(type.getEmoji(), type.getIdDiscordEmoji(), false));
            //désactivation du bouton si attaque entravée
            if (attaquesEntravees.containsKey(attaque)) {
                button = button.asDisabled();
            }
            buttons.add(button);
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

    public void updateImageCombat() throws IOException {
        game.getChannel().sendTyping().queue();
        List<ElementUI> elementUIS = new ArrayList<>();
        Font font = new Font("Arial", Font.PLAIN, 10);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, -0.1);
        font = font.deriveFont(attributes);
        Font fontGender = new Font("Arial", Font.PLAIN, 9);
        Font fontHp = new Font("Arial", Font.PLAIN, 11);

        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);

        if (typeCombat.equals(TypeCombat.SIMPLE)) {

            //pokemon allié
            if (blanc.getPokemonActif() != null && blanc.getPokemonActif().estEnVie() && !typeCombatResultat.equals(TypeCombatResultat.FUITE_JOUEUR) && !blanc.getPokemonActif().hasStatut(AlterationEtat.SEMI_INVULNERABLE)) {
                elementUIS.add(new ImageUI(-5, 50, ImageIO.read(new URL(blanc.getPokemonActif().getBackSprite()))));
            }
            TextUI nomPokemonBlanc = new TextUI(91, 90, blanc.getPokemonActif().getNomPresentation(), font, Color.BLACK);
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

            int pokeY2 = 2;
            for (Pokemon pokemon : noir.getEquipe()) {
                if (pokemon.getCurrentHp() <= 0) {
                    elementUIS.add(new ImageUI(190, pokeY2, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("empty"))))));
                } else {
                    elementUIS.add(new ImageUI(190, pokeY2, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("pokeball"))))));
                }
                pokeY2 += 12;
            }

            if (blanc.getPokemonActif().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonBlanc.getX() + (int) (font.getStringBounds(genrePokemonBlanc.getText(), frc).getWidth()) + 5, 82, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }
            if (noir.getPokemonActif() != null && noir.getPokemonActif().estEnVie() && !noir.getPokemonActif().hasStatut(AlterationEtat.SEMI_INVULNERABLE) && !typeCombatResultat.equals(TypeCombatResultat.FUITE_ADVERSAIRE) && !typeCombatResultat.equals(TypeCombatResultat.CAPTURE)) {
                elementUIS.add(new ImageUI(100, 0, ImageIO.read(new URL(noir.getPokemonActif().getFrontSprite()))));
            }
            TextUI nomPokemonNoir = new TextUI(16, 19, noir.getPokemonActif().getNomPresentation(), font, Color.BLACK);
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
            //pokemon allié
            if (blanc.getPokemonActif() != null && blanc.getPokemonActif().estEnVie()) {
                elementUIS.add(new ImageUI(-20, 50, ImageIO.read(new URL(blanc.getPokemonActif().getBackSprite()))));
            }
            TextUI nomPokemonBlanc = new TextUI(95, 90, blanc.getPokemonActif().getNomPresentation(), font, Color.BLACK);
            TextUI genrePokemonBlanc = new TextUI(95 + (int) (font.getStringBounds(nomPokemonBlanc.getText(), frc).getWidth()), 90, " " + blanc.getPokemonActif().getGender().getEmoji(), fontGender, blanc.getPokemonActif().getGender().getColor());
            elementUIS.add(nomPokemonBlanc);
            elementUIS.add(genrePokemonBlanc);
            int hpBarBlanc = (int) ((DOUBLE_MAX_X_HP_BAR_BLANC - DOUBLE_MIN_X_HP_BAR_BLANC) * ((double) blanc.getPokemonActif().getCurrentHp() / blanc.getPokemonActif().getMaxHp()));
            if (blanc.getPokemonActif().getCurrentHp() > 0 && hpBarBlanc <= 0) {
                hpBarBlanc = 1;
            }
            RectangleUI hpBlanc = new RectangleUI(DOUBLE_MIN_X_HP_BAR_BLANC, DOUBLE_MIN_Y_HP_BAR_BLANC, Color.GREEN, hpBarBlanc, DOUBLE_MAX_Y_HP_BAR_BLANC - DOUBLE_MIN_Y_HP_BAR_BLANC);
            elementUIS.add(hpBlanc);

            TextUI levelBlanc = new TextUI(168, 90, "Lv." + blanc.getPokemonActif().getLevel(), font, Color.BLACK);
            elementUIS.add(levelBlanc);

            int pokeY = 110;
            for (Pokemon pokemon : blanc.getEquipe()) {
                if (pokemon.getCurrentHp() <= 0) {
                    elementUIS.add(new ImageUI(3, pokeY, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("empty"))))));
                } else {
                    elementUIS.add(new ImageUI(3, pokeY, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("pokeball"))))));
                }
                pokeY -= 12;
            }

            int pokeY2 = 2;
            for (Pokemon pokemon : noir.getEquipe()) {
                if (pokemon.getCurrentHp() <= 0) {
                    elementUIS.add(new ImageUI(190, pokeY2, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("empty"))))));
                } else {
                    elementUIS.add(new ImageUI(190, pokeY2, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("pokeball"))))));
                }
                pokeY2 += 12;
            }

            if (blanc.getPokemonActif().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonBlanc.getX() + (int) (font.getStringBounds(genrePokemonBlanc.getText(), frc).getWidth()) + 5, 82, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }

            //blanc bis
            if (blanc.getPokemonActifBis() != null && blanc.getPokemonActifBis().estEnVie()) {
                elementUIS.add(new ImageUI(10, 50, ImageIO.read(new URL(blanc.getPokemonActifBis().getBackSprite()))));
            }
            TextUI nomPokemonBlancBis = new TextUI(95, 110, blanc.getPokemonActifBis().getNomPresentation(), font, Color.BLACK);
            TextUI genrePokemonBlancBis = new TextUI(95 + (int) (font.getStringBounds(nomPokemonBlancBis.getText(), frc).getWidth()), 110, " " + blanc.getPokemonActifBis().getGender().getEmoji(), fontGender, blanc.getPokemonActifBis().getGender().getColor());
            elementUIS.add(nomPokemonBlancBis);
            elementUIS.add(genrePokemonBlancBis);
            int hpBarBlancBis = (int) ((DOUBLE_MAX_X_HP_BAR_BLANC_BIS - DOUBLE_MIN_X_HP_BAR_BLANC_BIS) * ((double) blanc.getPokemonActifBis().getCurrentHp() / blanc.getPokemonActifBis().getMaxHp()));
            if (blanc.getPokemonActifBis().getCurrentHp() > 0 && hpBarBlancBis <= 0) {
                hpBarBlancBis = 1;
            }
            RectangleUI hpBlancBis = new RectangleUI(DOUBLE_MIN_X_HP_BAR_BLANC_BIS, DOUBLE_MIN_Y_HP_BAR_BLANC_BIS, Color.GREEN, hpBarBlancBis, DOUBLE_MAX_Y_HP_BAR_BLANC_BIS - DOUBLE_MIN_Y_HP_BAR_BLANC_BIS);
            elementUIS.add(hpBlancBis);

            TextUI levelBlancBis = new TextUI(168, 110, "Lv." + blanc.getPokemonActifBis().getLevel(), font, Color.BLACK);
            elementUIS.add(levelBlancBis);

            if (blanc.getPokemonActifBis().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonBlancBis.getX() + (int) (font.getStringBounds(genrePokemonBlancBis.getText(), frc).getWidth()) + 5, 82, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }

            //noir actif
            if (noir.getPokemonActif() != null && noir.getPokemonActif().estEnVie()) {
                elementUIS.add(new ImageUI(90, 0, ImageIO.read(new URL(noir.getPokemonActif().getFrontSprite()))));
            }
            TextUI nomPokemonNoir = new TextUI(3, 11, noir.getPokemonActif().getNomPresentation(), font, Color.BLACK);
            TextUI genrePokemonNoir = new TextUI(3 + (int) (font.getStringBounds(nomPokemonNoir.getText(), frc).getWidth()), 11, " " + noir.getPokemonActif().getGender().getEmoji(), fontGender, noir.getPokemonActif().getGender().getColor());
            elementUIS.add(nomPokemonNoir);
            elementUIS.add(genrePokemonNoir);
            int hpBarNoir = (int) ((DOUBLE_MAX_X_HP_BAR_NOIR - DOUBLE_MIN_X_HP_BAR_NOIR) * ((double) noir.getPokemonActif().getCurrentHp() / noir.getPokemonActif().getMaxHp()));
            if (noir.getPokemonActif().getCurrentHp() > 0 && hpBarNoir <= 0) {
                hpBarNoir = 1;
            }
            RectangleUI hpNoir = new RectangleUI(DOUBLE_MIN_X_HP_BAR_NOIR, DOUBLE_MIN_Y_HP_BAR_NOIR, Color.GREEN, hpBarNoir, DOUBLE_MAX_Y_HP_BAR_NOIR - DOUBLE_MIN_Y_HP_BAR_NOIR);
            elementUIS.add(hpNoir);

            TextUI levelNoir = new TextUI(76, 11, "Lv." + noir.getPokemonActif().getLevel(), font, Color.BLACK);
            elementUIS.add(levelNoir);

            if (noir.getPokemonActif().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonNoir.getX() + (int) (font.getStringBounds(genrePokemonNoir.getText(), frc).getWidth()) + 2, 1, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }

            //noir actif bis
            if (noir.getPokemonActifBis() != null && noir.getPokemonActifBis().estEnVie()) {
                elementUIS.add(new ImageUI(115, 0, ImageIO.read(new URL(noir.getPokemonActifBis().getFrontSprite()))));
            }
            TextUI nomPokemonNoirBis = new TextUI(3, 31, noir.getPokemonActifBis().getNomPresentation(), font, Color.BLACK);
            TextUI genrePokemonNoirBis = new TextUI(3 + (int) (font.getStringBounds(nomPokemonNoirBis.getText(), frc).getWidth()), 31, " " + noir.getPokemonActifBis().getGender().getEmoji(), fontGender, noir.getPokemonActifBis().getGender().getColor());
            elementUIS.add(nomPokemonNoirBis);
            elementUIS.add(genrePokemonNoirBis);
            int hpBarNoirBis = (int) ((DOUBLE_MAX_X_HP_BAR_NOIR_BIS - DOUBLE_MIN_X_HP_BAR_NOIR_BIS) * ((double) noir.getPokemonActifBis().getCurrentHp() / noir.getPokemonActifBis().getMaxHp())); // ?
            if (noir.getPokemonActifBis().getCurrentHp() > 0 && hpBarNoirBis <= 0) {
                hpBarNoirBis = 1;
            }
            RectangleUI hpNoirBis = new RectangleUI(DOUBLE_MIN_X_HP_BAR_NOIR_BIS, DOUBLE_MIN_Y_HP_BAR_NOIR_BIS, Color.GREEN, hpBarNoirBis, DOUBLE_MAX_Y_HP_BAR_NOIR_BIS - DOUBLE_MIN_Y_HP_BAR_NOIR_BIS);
            elementUIS.add(hpNoirBis);

            TextUI levelNoirBis = new TextUI(76, 31, "Lv." + noir.getPokemonActifBis().getLevel(), font, Color.BLACK);
            elementUIS.add(levelNoirBis);

            if (noir.getPokemonActifBis().isShiny()) {
                elementUIS.add(new ImageUI(genrePokemonNoirBis.getX() + (int) (font.getStringBounds(genrePokemonNoirBis.getText(), frc).getWidth()) + 2, 21, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
            }

            BufferedImage backImage = ImageIO.read(new File(game.getFileManager().getFullPathToImage(background)));

            imageCombat = game.getImageManager().composeImageCombat(backImage, elementUIS, LARGEUR, HAUTEUR);
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

    public Duelliste getDuellisteAllie(Pokemon pokemon) {
        if (blanc.estALui(pokemon)) {
            return blanc;
        } else if (noir.estALui(pokemon)) {
            return noir;
        } else {
            logger.error("Le pokémon " + pokemon.getNomCompletPresentation() + " id " + pokemon.getId() + " n'a pas de propriétaire reconnu...");
            throw new IllegalStateException("Le pokémon " + pokemon.getNomCompletPresentation() + " id " + pokemon.getId() + " n'a pas de propriétaire reconnu...");
        }
    }

    public Duelliste getDuellisteAdverse(Pokemon pokemon) {
        if (blanc.estALui(pokemon)) {
            return noir;
        } else if (noir.estALui(pokemon)) {
            return blanc;
        } else {
            logger.error("Le pokémon " + pokemon.getNomCompletPresentation() + " id " + pokemon.getId() + " n'a pas de propriétaire reconnu...");
            throw new IllegalStateException("Le pokémon " + pokemon.getNomCompletPresentation() + " id " + pokemon.getId() + " n'a pas de propriétaire reconnu...");
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

    public int getMethodeRencontre() {
        return methodeRencontre;
    }

    public void setMethodeRencontre(int methodeRencontre) {
        this.methodeRencontre = methodeRencontre;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public TypeCombatResultat getTypeCombatResultat() {
        return typeCombatResultat;
    }

    public void setTypeCombatResultat(TypeCombatResultat typeCombatResultat) {
        this.typeCombatResultat = typeCombatResultat;
    }

    public String getImageCombat() {
        return imageCombat;
    }

    public void setImageCombat(String imageCombat) {
        this.imageCombat = imageCombat;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public HashMap<Attaque, Integer> getAttaquesEntravees() {
        return attaquesEntravees;
    }

    public void setAttaquesEntravees(HashMap<Attaque, Integer> attaquesEntravees) {
        this.attaquesEntravees = attaquesEntravees;
    }

    @JsonIgnore
    public boolean isAttaqueEntravee(Attaque attaque) {
        return attaquesEntravees.containsKey(attaque);
    }

    public void decrementerEntraves() {
        attaquesEntravees.entrySet().forEach(a -> {
            a.setValue(a.getValue() - 1);
        });
        attaquesEntravees.entrySet().removeIf(e -> e.getValue() <= 0);
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

    public boolean verificationsCibleIndividuelle(ActionCombat actionCombat, Pokemon cible, boolean ignorerPrecision, boolean ignorerImmunite) {
        if (cible == null) {
            return false;
        }
        //pas d'attaque si la cible est dead
        if (cible.getCurrentHp() <= 0) {
            return false;
        }
        //pas d'attaque si la cible se protège
        if (cible.hasStatut(AlterationEtat.PROTECTION)) {
            game.getChannel().sendMessage(actionCombat.getPokemonCible().getNomPresentation() + " se protège !").queue();
            return false;
        }

        //pas d'attaque si cible semi-invulnérable sauf exceptions...
        if (cible.hasStatut(AlterationEtat.SEMI_INVULNERABLE) && !actionCombat.getLanceur().hasStatut(AlterationEtat.VISEE)) {
            if (!(cible.getActionsCombat().get(turnCount - 1).getAttaque().getIdMoveAPI() == 19 && listeAttaquesTouchantVol.contains(actionCombat.getAttaque().getIdMoveAPI()))) {
                //TODO no guard empe^che les esquives
                return false;
            }

            if (!(cible.getActionsCombat().get(turnCount - 1).getAttaque().getIdMoveAPI() == 91 && listeAttaquesTouchantTunnel.contains(actionCombat.getAttaque().getIdMoveAPI()))) {
                //TODO no guard empe^che les esquives
                return false;
            }
        }

        if (!ignorerImmunite && Type.getById(actionCombat.getAttaque().getMoveAPI().getType().getId()).pourcentageDegatsAttaque(cible.getPokemonAPI().getTypes()) == 0) {
            game.getChannel().sendMessage("Cela n'affecte pas " + actionCombat.getPokemonCible().getNomPresentation() + " !").queue();
            return false;
        }

        //en simulation, la précision est comptabilisée en ratio de dégats, alors qu'en vrai, c'est tout ou rien
        //aussi, on ne prend en compte la précision que si l'attaque a une précision != null, sinon c'est qu'elle est inratable
        int movePrecision = actionCombat.getAttaque().getMoveAPI().getAccuracy();
        return false;//temp
        //TODO précision
//        if (movePrecision != null) {
//            //précision modifiée de THUNDER, BLIZZARD et HURRICANE en fonction de la météo
//            if (computeWeatherEffects()) {
//                if (selectedMove.equals(Moves.THUNDER) || selectedMove.equals(Moves.HURRICANE)) {
//                    if (weather.equals(Weather.HARSH_SUNLIGHT)) {
//                        movePrecision = 50;
//                    } else if (weather.equals(Weather.RAIN)) {
//                        movePrecision = 999;
//                    }
//                } else if (selectedMove.equals(Moves.BLIZZARD)) {
//                    if (weather.equals(Weather.HAIL)) {
//                        movePrecision = 999;
//                    }
//                }
//            }
//
//            if (HeldItem.LAX_INCENSE.equals(targetPokemon.getHeldItem())) {
//                movePrecision -= 10;
//            }
//
//            return testPrecision(launcherPokemon, targetPokemon, selectedMove, simulation);
//        }else{
//            return true;
//        }
    }

    public double evaluer(Duelliste duelliste) {
        return duelliste.getPokemonsActifsEnVie().stream().map(this::evaluer).reduce(0.0, Double::sum);
    }

    public double evaluer(Pokemon pokemon) {
        double critMultiplier = 1 + (Pokemon.BASE_CRIT_MODIFIER * (1.0 / pokemon.getDenominateurCritChance()));
        double atkScore = pokemon.getMaxAtkPhy() > pokemon.getMaxAtkSpe() ? (pokemon.getCurrentAtkPhy() * critMultiplier * 1.5) + (pokemon.getCurrentAtkSpe() * critMultiplier * 0.5) : (pokemon.getCurrentAtkSpe() * critMultiplier * 1.5) + (pokemon.getCurrentAtkPhy() * critMultiplier * 0.5);
        double note = ((pokemon.getCurrentHp() / 2.25) * (atkScore + pokemon.getCurrentSpeed() + pokemon.getCurrentDefPhy() + pokemon.getCurrentDefSpe() + pokemon.getEvasivenessStage() + pokemon.getAccuracyStage()));
        double ratio = pokemon.getAlterations().stream().distinct().map(a -> {
            int val = Math.min(Math.max(1, a.getToursRestants()), 3);
            return a.getAlterationEtat().getRatio() * val;
        }).reduce(1f, (subtotal, element) -> subtotal * element);
        ;
        return note * ratio;
    }

    public List<Pokemon> getPokemonsActifs() {
        List<Pokemon> pokemons = new ArrayList<>();
        pokemons.addAll(blanc.getPokemonsActifsEnVie());
        pokemons.addAll(noir.getPokemonsActifsEnVie());
        return pokemons;
    }
}
