package game;

import executable.MyBot;
import game.model.PNJ;
import game.model.Structure;
import game.model.ZoneTypes;
import game.model.Zones;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Game {
    private MyBot bot;
    private Save save;
    private final DiscordManager discordManager;
    private final MessageManager messageManager;

    private final ImageManager imageManager;
    private final FileManager fileManager;
    private final ButtonManager buttonManager;

    private MessageChannelUnion channel;

    private User user;

    public Game(MyBot bot, Save save) {
        this.bot = bot;
        this.save = save;
        this.fileManager = new FileManager(bot);
        this.messageManager = new MessageManager(bot);
        this.discordManager = new DiscordManager(bot);
        this.buttonManager = new ButtonManager(bot);
        this.user = discordManager.getUserById(save.getUserId());
        this.channel = discordManager.getChannelById(save.getPrivilegedChannelId());
        this.imageManager = new ImageManager(bot);
    }

    public MyBot getBot() {
        return bot;
    }

    public void setBot(MyBot bot) {
        this.bot = bot;
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public MessageChannelUnion getChannel() {
        return channel;
    }

    public void setChannel(MessageChannelUnion channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void gameMenu() {
        Structure currentStructure = save.getCampaign().getCurrentStructure();
        Zones currentZone = save.getCampaign().getCurrentZone();
        /**
         * Actions (5 max):
         * -Se déplacer
         *      -Changer de zone
         *      -Batiments
         *          -Centre pokémon
         *          -boutique
         *          -Arene
         *          -Autres
         * -Hautes herbes (hors ville)
         * -Menu sac à dos (max 5)
         *      -Equipe
         *      -Inventaire
         *      -Options
         *      -Quitter
         *      -Retour
         * -Bataille de dresseurs
         * -PNJ
         */

        List<Button> buttons = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.SUCCESS, "move", "Se déplacer", Emoji.fromFormatted("\uD83E\uDDED")),
                Button.of(ButtonStyle.SUCCESS, "bag", "Sac à dos", Emoji.fromFormatted("\uD83C\uDF92"))
        ));

        if (currentStructure == null && currentZone.getTypeZone().equals(ZoneTypes.ROUTE)) {
            buttons.add(Button.of(ButtonStyle.SUCCESS, "grass", "Hautes herbes", Emoji.fromFormatted("\uD83C\uDF3F")));
        }
        if ((currentStructure != null && currentStructure.getPnjs().size() > 0) || (currentStructure == null && currentZone.getPnjs().size() > 0)) {
            buttons.add(Button.of(ButtonStyle.SUCCESS, "pnj", "Discuter", Emoji.fromFormatted("\uD83D\uDDE3")));
        }
//        Button.of(ButtonStyle.SUCCESS, "battle", "Combat de dresseur", Emoji.fromFormatted("\uD83D\uDCA5"));


        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(buttons);

        String background;
        String nom;
        int x;
        int y;

        if (currentStructure != null) {
            background = currentStructure.getBackground();
            y = currentStructure.getY();
            x = currentStructure.getX();
            nom = currentStructure.getNom();
        } else {
            background = currentZone.getBackground();
            y = currentZone.getY();
            x = currentZone.getX();
            nom = currentZone.name();
        }

        String combined = "temp/" + imageManager.merge(PropertiesManager.getInstance().getImage(background), getPlayerSprite(), x, y);

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(nom, lc, combined))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    if (e.getComponentId().equals("move")) {
                                        moveMenu();
                                    }else if(e.getComponentId().equals("pnj")){
                                        talkMenu();
                                    }
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    buttonManager.timeout(channel, user);
                                }
                        )
                );

    }

    private void talkMenu() {
        Structure currentStructure = save.getCampaign().getCurrentStructure();
        Zones currentZone = save.getCampaign().getCurrentZone();
        List<PNJ> pnjs = currentStructure == null ? currentZone.getPnjs() : currentStructure.getPnjs();
        List<Button> buttons = new ArrayList<>();
        for (PNJ pnj : pnjs) {
            if (pnj != null) {
                buttons.add(Button.of(ButtonStyle.SUCCESS, String.valueOf(pnj.getId()), pnj.getNom(), Emoji.fromFormatted(pnj.getEmojiCode())));
            }
        }

        LayoutComponent lc = ActionRow.of(buttons);
        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage("Avec qui ?", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    //structure sélectionnée
                                    PNJ.getPNJById(e.getComponentId()).defaultTalk(this);
                                    gameMenu();
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> buttonManager.timeout(channel, user)
                        )
                );
    }

    private void moveMenu() {
        Structure currentStructure = save.getCampaign().getCurrentStructure();
        Zones currentZone = save.getCampaign().getCurrentZone();
        List<Structure> structuresAccessibles = currentStructure == null ? currentZone.getListeBatimentsSpeciaux() : currentStructure.getStructuresAccessibles();
        List<Zones> zonesAccessibles = currentStructure == null ? currentZone.getListeZonesAccessibles() : Collections.singletonList(currentStructure.getZoneAccessible());
        List<Button> buttons = new ArrayList<>();
        for (Structure structureAccessible : structuresAccessibles) {
            if (structureAccessible != null) {
                buttons.add(Button.of(ButtonStyle.SUCCESS, "s" + structureAccessible.getId(), structureAccessible.getNom(), Emoji.fromFormatted("\uD83D\uDEAA")));
            }
        }
        for (Zones zoneAccessible : zonesAccessibles) {
            if (zoneAccessible != null) {
                buttons.add(Button.of(ButtonStyle.SUCCESS, "z" + zoneAccessible.getIdZone(), "nom zone", Emoji.fromFormatted(zoneAccessible.getTypeZone().getEmojiCode())));
            }
        }

        LayoutComponent lc = ActionRow.of(buttons);
        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage("Pour aller où ?", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    //structure sélectionnée
                                    if (e.getComponentId().startsWith("s")) {
                                        save.getCampaign().setCurrentStructure(Structure.getById(e.getComponentId().split("s")[1]));
                                        //zone sélectionnée
                                    } else {
                                        save.getCampaign().setCurrentZone(Zones.getById(e.getComponentId().split("s")[1]));
                                        save.getCampaign().setCurrentStructure(null);
                                    }
                                    gameMenu();
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    buttonManager.timeout(channel, user);
                                }
                        )
                );
    }

    private String getPlayerSprite() {
        if (save.getCampaign().isGender()) {
            return PropertiesManager.getInstance().getImage("boy");
        } else {
            return PropertiesManager.getInstance().getImage("girl");
        }
    }
}
