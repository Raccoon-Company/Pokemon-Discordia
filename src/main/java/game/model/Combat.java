package game.model;

import com.github.oscar0812.pokeapi.models.locations.PokemonEncounter;
import com.github.oscar0812.pokeapi.models.moves.Move;
import game.Game;
import game.model.enums.Meteo;
import game.model.enums.PNJ;
import game.model.enums.Type;
import game.model.enums.TypeCombat;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
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
import java.util.*;
import java.util.List;

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

    private Duelliste blanc;
    private Duelliste noir;

    //suivi de l'état

    private Terrain terrainBlanc;
    private Terrain terrainNoir;

    private Pokemon currentPokemonBlanc;
    private Pokemon currentPokemonNoir;

    private Meteo meteo;

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
        this.meteo = meteo;
        this.objetsAutorises = objetsAutorises;
        this.currentPokemonBlanc = blanc.getEquipe().get(0);
        this.currentPokemonNoir = noir.getEquipe().get(0);

        setUpBackground();
    }

    private void setUpBackground() {
        String bg = game.getSave().getCampaign().getCurrentZone().getCombatBackground();
        this.background = "temp/" + game.getImageManager().merge(PropertiesManager.getInstance().getImage(bg), PropertiesManager.getInstance().getImage("battle.ui"), 0, 0, LARGEUR, HAUTEUR);

    }

    public CombatResultat resolve() {
        CombatResultat combatResultat = new CombatResultat();
        try {
            String imageCombat = updateImageCombat();

            MessageCreateBuilder mcb = new MessageCreateBuilder();
            List<Button> buttons = new ArrayList<>();
            List<Button> buttons2 = new ArrayList<>();

            for (Attaque attaque : currentPokemonBlanc.getMoveset()) {
                Move move = attaque.getMoveAPI();
                Type type = Type.getById(move.getType().getId());
                buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(attaque.getIdMoveAPI()), APIUtils.getFrName(move.getNames()) + " " + attaque.getPpLeft() + "/" + (move.getPp()+attaque.getBonusPp()), Emoji.fromCustom(type.getEmoji(), type.getIdDiscordEmoji(), false)));
            }

            buttons2 = new ArrayList<>(Arrays.asList(
                    Button.of(ButtonStyle.SECONDARY, "ball", "Pokéball", Emoji.fromCustom("pokeball", 1032561600701399110L, false)),
                    Button.of(ButtonStyle.SECONDARY, "item", "Potion",  Emoji.fromFormatted("\uD83E\uDDF4")),
                    Button.of(ButtonStyle.SECONDARY, "escape", "Fuite", Emoji.fromFormatted("\uD83C\uDFC3\uD83C\uDFFC"))
            ));


            File combat = new File(getClass().getClassLoader().getResource("images/temp/").getPath()+imageCombat);
            mcb.addFiles(FileUpload.fromData(combat, combat.getName()));

            LayoutComponent lc = ActionRow.of(buttons);
            LayoutComponent lc2 = ActionRow.of(buttons2);
            mcb.addComponents(lc, lc2);
            mcb.addContent("Un " + noir.getNom() + " sauvage apparaît !");


            game.getChannel().sendMessage(mcb.build()).queue();
//            game.getChannel().sendMessage(game.getMessageManager().createMessageImage(game.getSave(),"Un " + noir.getNom() + " sauvage apparaît !" , lc, "temp/" + imageCombat)).queue();

        } catch (IOException ioe) {
            logger.error("Erreur update image", ioe);
        }

        return combatResultat;
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
        elementUIS.add(new ImageUI(-5, 50, ImageIO.read(new URL(currentPokemonBlanc.getBackSprite()))));
        TextUI nomPokemonBlanc = new TextUI(91, 90, currentPokemonBlanc.getSpecieName(), font, Color.BLACK);
        TextUI genrePokemonBlanc = new TextUI(91 + (int) (font.getStringBounds(nomPokemonBlanc.getText(), frc).getWidth()), 90, " " + currentPokemonBlanc.getGender().getEmoji(), fontGender, currentPokemonBlanc.getGender().getColor());
        elementUIS.add(nomPokemonBlanc);
        elementUIS.add(genrePokemonBlanc);
        int hpBarBlanc = (int) ((MAX_X_HP_BAR_BLANC - MIN_X_HP_BAR_BLANC) * ((double) currentPokemonBlanc.getCurrentHp() / currentPokemonBlanc.getMaxHp()));
        if (currentPokemonBlanc.getCurrentHp() > 0 && hpBarBlanc <= 0) {
            hpBarBlanc = 1;
        }
        RectangleUI hpBlanc = new RectangleUI(MIN_X_HP_BAR_BLANC, MIN_Y_HP_BAR_BLANC, Color.GREEN, hpBarBlanc, MAX_Y_HP_BAR_BLANC - MIN_Y_HP_BAR_BLANC);
        elementUIS.add(hpBlanc);
        TextUI hpTextBlanc = new TextUI(144, 109, currentPokemonBlanc.getCurrentHp() + "/" + currentPokemonBlanc.getMaxHp(), fontHp, Color.BLACK);
        elementUIS.add(hpTextBlanc);

        TextUI levelBlanc = new TextUI(166, 90, "Lv." + currentPokemonBlanc.getLevel(), font, Color.BLACK);
        elementUIS.add(levelBlanc);
        currentPokemonBlanc.setXp(48);
        int currentXpBar = (int) ((MAX_X_XP_BAR - MIN_X_XP_BAR) * ((double) currentPokemonBlanc.getXp() / 155));
        RectangleUI xpBar = new RectangleUI(MIN_X_XP_BAR, MIN_Y_XP_BAR, Color.CYAN, currentXpBar, MAX_Y_XP_BAR - MIN_Y_XP_BAR);
        elementUIS.add(xpBar);

        int pokeY = 110;
        for(Pokemon pokemon : blanc.getEquipe()){
            if(pokemon.getCurrentHp() <= 0){
                elementUIS.add(new ImageUI(3, pokeY, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("empty"))))));
            }else{
                elementUIS.add(new ImageUI(3, pokeY, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("pokeball"))))));
            }
        pokeY -=12;
        }

        if (currentPokemonBlanc.isShiny()) {
            elementUIS.add(new ImageUI(genrePokemonBlanc.getX() + (int) (font.getStringBounds(genrePokemonBlanc.getText(), frc).getWidth()) + 5, 82, ImageIO.read(new File(game.getFileManager().getFullPathToImage(PropertiesManager.getInstance().getImage("shiny"))))));
        }

        elementUIS.add(new ImageUI(100, 0, ImageIO.read(new URL(currentPokemonNoir.getFrontSprite()))));
        TextUI nomPokemonNoir = new TextUI(16, 19, currentPokemonNoir.getSpecieName(), font, Color.BLACK);
        TextUI genrePokemonNoir = new TextUI(16 + (int) (font.getStringBounds(nomPokemonNoir.getText(), frc).getWidth()), 19, " " + currentPokemonNoir.getGender().getEmoji(), fontGender, currentPokemonNoir.getGender().getColor());
        elementUIS.add(nomPokemonNoir);
        elementUIS.add(genrePokemonNoir);
        int hpBarNoir = (int) ((MAX_X_HP_BAR_NOIR - MIN_X_HP_BAR_NOIR) * ((double) currentPokemonNoir.getCurrentHp() / currentPokemonNoir.getMaxHp()));
        if (currentPokemonNoir.getCurrentHp() > 0 && hpBarNoir <= 0) {
            hpBarNoir = 1;
        }
        RectangleUI hpNoir = new RectangleUI(MIN_X_HP_BAR_NOIR, MIN_Y_HP_BAR_NOIR, Color.GREEN, hpBarNoir, MAX_Y_HP_BAR_NOIR - MIN_Y_HP_BAR_NOIR);
        elementUIS.add(hpNoir);

        TextUI levelNoir = new TextUI(80, 19, "Lv." + currentPokemonNoir.getLevel(), font, Color.BLACK);
        elementUIS.add(levelNoir);

        if (currentPokemonNoir.isShiny()) {
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
}
