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
    private Meteo meteo;

    private TypeCombatResultat typeCombatResultat;

    private String background;

    //règles
    private TypeCombat typeCombat;

    private boolean objetsAutorises;

    //compteurs
    private int tentativesDeFuite = 0;
    private int piecesEparpillees = 0;
    private int compteurToursMeteo = 999;

    public Combat(Game game, Duelliste blanc, Duelliste noir, TypeCombat typeCombat, Meteo meteo, boolean objetsAutorises) {
        this.game = game;
        this.typeCombat = typeCombat;
        this.blanc = blanc;
        this.noir = noir;
        this.turnCount = 0;
        this.meteo = meteo;
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

        //todo effets d'entree sur le champ de bataille

        roundPhase1();
    }

    public void roundPhase1() {
        String imageCombat;
        try {
            imageCombat = updateImageCombat();
        } catch (IOException ioe) {
            logger.error("Erreur update image", ioe);
            throw new IllegalStateException("Erreur mise à jour de l'image");
        }

        String text = "";

        if (turnCount == 1 && noir.getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE)) {
            text += "Un " + noir.getNom() + " sauvage apparaît !";
        } else if (turnCount == 1 && noir.getTypeDuelliste().equals(TypeDuelliste.PNJ)) {
            text += noir.getNom() + " veut se battre !";
        } else {
            text += "Tour " + turnCount;
        }

        //on créé le message à partir de la nouvelle image et des infos combat
        MessageCreateBuilder mcb = getMcb(imageCombat, text);
        //envoi du message
        //game.getChannel().sendMessage(game.getMessageManager().createMessageImage(game.getSave(),"Un " + noir.getNom() + " sauvage apparaît !" , lc, "temp/" + imageCombat)).queue();
        game.getChannel().sendMessage(mcb.build()).queue((message) ->
                game.getBot().getEventWaiter().waitForEvent(
                        ButtonInteractionEvent.class,
                        //vérif basique de correspondance entre message/interaction
                        e -> game.getButtonManager().createPredicate(e, message, game.getSave(), mcb.getComponents()),
                        //action quand interaction détectée

                        e -> {
                            game.getBot().unlock(game.getUser());
                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                            if (e.getComponentId().equals("change")) {
                                //changer de pokes
                                roundPhase2();
                            } else if (e.getComponentId().equals("ball")) {
                                //pokeball
                                pokeballMenu();
                            } else if (e.getComponentId().equals("item")) {
                                //bag//
                                roundPhase2();
                            } else if (e.getComponentId().equals("escape")) {
                                fuite();
                            } else {
                                //choix de l'attaque
                                blanc.getPokemonActif().getLastUsedMoves().add(Client.getMoveById(Integer.parseInt(e.getComponentId())));
                                roundPhase2();
                            }
                        },
                        1,
                        TimeUnit.MINUTES,
                        () -> {
                            game.getButtonManager().timeout(game.getChannel(), game.getUser());
                        }
                ));
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
        if (!fuiteAutorisee(blanc.getPokemonActif(), noir, false)) {
            roundPhase1();
        }

        //up le nm de tentatives de fuite
        tentativesDeFuite++;
        //on passe le tour du pokemon, utilisé a fuir
        blanc.getPokemonActif().getLastUsedMoves().add(null);

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
                Button.of(ButtonStyle.PRIMARY, "pokeball", "Pokéball", Emoji.fromCustom("pokeball", 1032561600701399110L, false)),
                Button.of(ButtonStyle.PRIMARY, "superball", "Superball", Emoji.fromCustom("superball", 1034421567570063400L, false)),
                Button.of(ButtonStyle.PRIMARY, "hyperball", "HyperBall", Emoji.fromCustom("hyperball", 1034421564210429993L, false)),
                Button.of(ButtonStyle.PRIMARY, "masterball", "Masterball", Emoji.fromCustom("masterball", 1034421566244659280L, false)),
                Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19"))
        ));

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

//      TODO  campaign.updateInventory(this, -1);
        if (!noir.getTypeDuelliste().equals(TypeDuelliste.POKEMON_SAUVAGE)) {
            //erreur
            roundPhase1();
        }
        //KEBALL
        //throw keball
        double hpMax = noir.getPokemonActif().getMaxHp();
        double hpCur = noir.getPokemonActif().getCurrentHp();
        double rate = noir.getPokemonActif().getPokemonAPI().getSpecies().getCaptureRate();
//        double bonusStatus = 1 + (noir.getPokemonActif().getStatuses().size() * 0.5); //TODO ratio pour les status
        double a = ((((3 * hpMax) - (3 * hpCur)) * rate * pokeball.getEfficacite()) / (3 * hpMax)); //* bonusStatus;
        if (Utils.randomTest(a) || pokeball.equals(Pokeball.MASTERBALL)) {
            //réussite de la capture
            noir.getPokemonActif().setFriendship(Pokemon.BASE_FRIENDSHIP_VALUE);
            //si pas déjà attrapé
            if (game.getSave().getCampaign().getPokedex().get(noir.getPokemonActif().getIdSpecie()) != 2) {
                game.getSave().getCampaign().getPokedex().replace(noir.getPokemonActif().getIdSpecie(), 2);
            }
        }else{
            //TODO capture ratée
        }
    }

    public void roundPhase2() {
        //TODO resolution des attaques choisies
        blanc.getPokemonActif().setCurrentHp(blanc.getPokemonActif().getCurrentHp() - 4);
        noir.getPokemonActif().setCurrentHp(noir.getPokemonActif().getCurrentHp() - 8);

        //TODO effets de fin de tour

        //vérif combat terminé
        if (blanc.getPokemonActif().getCurrentHp() >= 0 && noir.getPokemonActif().getCurrentHp() >= 0) {
            roundPhase1();

            //TODO combat terminé
        } else {
            game.apresCombat(this);
        }
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

    private String updateImageCombat() throws IOException {
        /**
         * merge le background
         * les sprites des 2 à 4 pokémons
         * les barres de points de vie et d'xp
         * les pokéballs représentant l'équipe
         * les noms, genres, shiny stars, et niveaux de chaque
         */

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

        return game.getImageManager().composeImageCombat(backImage, elementUIS, LARGEUR, HAUTEUR);
    }

    public void changerMeteo(Meteo meteo) {
        changerMeteo(meteo, Utils.getRandom().nextInt(4) + 2);
    }

    public void changerMeteo(Meteo meteo, int compteurToursMeteo) {
        this.meteo = meteo;
        this.compteurToursMeteo = compteurToursMeteo;
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

    public Meteo getMeteo() {
        return meteo;
    }

    public void setMeteo(Meteo meteo) {
        this.meteo = meteo;
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

    public int getCompteurToursMeteo() {
        return compteurToursMeteo;
    }

    public void setCompteurToursMeteo(int compteurToursMeteo) {
        this.compteurToursMeteo = compteurToursMeteo;
    }
}
