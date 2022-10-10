package game;

import executable.MyBot;
import game.model.PNJ;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import utils.ButtonManager;
import utils.DiscordManager;
import utils.FileManager;
import utils.MessageManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Launcher extends ListenerAdapter {

    private final MyBot bot;
    private final DiscordManager discordManager;
    private final MessageManager messageManager;
    private final ButtonManager buttonManager;
    private int choice = -1;

    /**
     * Constructeur.
     *
     * @param myBot
     */
    public Launcher(MyBot myBot) {
        this.bot = myBot;
        this.messageManager = new MessageManager(bot);
        this.discordManager = new DiscordManager(bot);
        this.buttonManager = new ButtonManager(bot);
    }

    public MyBot getBot() {
        return bot;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public void start(Message message) {

        long idUser = message.getAuthor().getIdLong();
        FileManager fileManager = new FileManager(bot);

        //on lis les saves potentielles correspondantes à l'user
        //si au moins une, on propose le menu de sélection + possibilité de lancer une nouvelle (limite à ? saves)
        //sinon lancement auto d'une game
        List<Save> saves = fileManager.getSaves(idUser);

        Save save = new Save(message.getAuthor().getIdLong());
        save.setPrivilegedChannelId(message.getChannel().getIdLong());
        startGame(save);


//        if(saves.isEmpty()){
//            Save save = new Save(idUser);
//
//            startGame(save);
//
//            FileManager.getInstance().writeSave(save);
//        }else{
//            message.getChannel().sendMessage(saves.size()+" saves ont été retrouvées !").complete();
//
//        }
    }

    private void startGame(Save save) {
        MessageChannelUnion channel = discordManager.getChannelById(save.getPrivilegedChannelId());
        User user = discordManager.getUserById(save.getUserId());

        profIntro(channel, user, save);

    }


    private void profIntro(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "f", "Fille", Emoji.fromFormatted("♀️")),
                Button.of(ButtonStyle.PRIMARY, "m", "Garçon", Emoji.fromFormatted("♂️"))
        ));

        bot.lock(user);
        //message d'intro : choix genre
        channel.sendMessage(messageManager.createMessageThumbnail(PNJ.RAOULT, "Bonjour et bienvenue dans le fabuleux monde des pokémons !\nEs-tu un garçon ou une fille ?", lc))
                .queue(message ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée
                                e -> {
                                    e.deferEdit().queue();
                                    bot.unlock(user);
                                    if (e.getComponentId().equals("f")) {
                                        save.getCampaign().setGender(false);
                                    } else {
                                        save.getCampaign().setGender(true);
                                    }
                                    choixPrenom(channel, user, save);
                                },
                                1,
                                TimeUnit.MINUTES,
                                buttonManager.timeout(channel, user)
                        )
                );
    }

    private void choixPrenom(MessageChannelUnion channel, User user, Save save) {
        String image = save.getCampaign().isGender() ? "raoult1.jpg" : "raoult0.jpg";

        bot.lock(user);
        //demande d'entrée du prénom
        channel.sendMessage(messageManager.createMessageThumbnailAndImage(PNJ.RAOULT, "Ah d'accord ! Et comment t'appelles-tu ?", null, image))
                .queue(message -> bot.getEventWaiter().waitForEvent( // Setup Wait action once message was send
                        MessageReceivedEvent.class,
                        e -> messageManager.createPredicate(e, save),
                        //action quand réponse détectée
                        e -> {
                            bot.unlock(user);
                            String reply = e.getMessage().getContentRaw();
                            save.getCampaign().setNom(reply);
                            confirmationNom(channel, user, save);
                        },
                        1, TimeUnit.MINUTES,
                        messageManager.timeout(channel, user)
                        )
                );
    }


    private void confirmationNom(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.SUCCESS, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(PNJ.RAOULT, save.getCampaign().getNom() + " donc, c'est bien ça ?", lc))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    if (e.getComponentId().equals("true")) {
                                        save.getCampaign().setNom(save.getCampaign().getNom());
                                        choixPrenomRival(channel, user, save);
                                    } else {
                                        choixPrenom(channel, user, save);
                                    }
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    setChoice(0);
                                    buttonManager.timeout(channel, user);
                                }

                        )
                );
    }

    private void choixPrenomRival(MessageChannelUnion channel, User user, Save save) {
        String image = save.getCampaign().isGender() ? "raoult1rival.jpg" : "raoult0rival.jpg";

        bot.lock(user);
        //demande d'entrée du prénom
        channel.sendMessage(messageManager.createMessageThumbnailAndImage(PNJ.RAOULT, "Très bien.\nEt lui là-bas, c'est ton ami non ? Il va commencer son aventure dans le monde des pokémons en même temps que toi.\nComment s'appelle t'il ?", null, image))
                .queue(message -> bot.getEventWaiter().waitForEvent( // Setup Wait action once message was send
                        MessageReceivedEvent.class,
                        e -> messageManager.createPredicate(e, save),
                        //action quand réponse détectée
                        e -> {
                            bot.unlock(user);
                            String reply = e.getMessage().getContentRaw();
                            save.getCampaign().setNomRival(reply);
                            confirmationNomRival(channel, user, save);
                        },
                        1, TimeUnit.MINUTES,
                        messageManager.timeout(channel, user)
                        )
                );
    }

    private void confirmationNomRival(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.SUCCESS, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(PNJ.RAOULT, save.getCampaign().getNomRival() + " donc, c'est bien ça ?", lc))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    if (e.getComponentId().equals("true")) {
                                        choixStarter(channel, user, save);
                                    } else {
                                        choixPrenomRival(channel, user, save);
                                    }
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    setChoice(0);
                                    buttonManager.timeout(channel, user);
                                }
                        )
                );
    }

    private void choixStarter(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.SUCCESS, "1", "Bulbizarre", Emoji.fromFormatted("\uD83C\uDF3F")),
                Button.of(ButtonStyle.SUCCESS, "4", "Salamèche", Emoji.fromFormatted("\uD83D\uDD25")),
                Button.of(ButtonStyle.SUCCESS, "7", "Carapuce", Emoji.fromFormatted("\uD83D\uDCA7"))
        ));

        bot.lock(user);

        channel.sendMessage(messageManager.createMessageThumbnailAndImage(PNJ.RAOULT, "Bon, pas le temps de niaiser " + save.getCampaign().getNom() + ", prends ce pokédex, choisis le pokémon que tu veux et casse-toi !\nAh oui, on a pas de pikachus, c'est pour les victimes. Au pire t'en trouveras dans la forêt à côté.", lc, "starters.png"))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    confirmationStarter(channel, user, save,e.getComponentId());
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    setChoice(0);
                                    buttonManager.timeout(channel, user);
                                }
                        )
                );
    }

    private void confirmationStarter(MessageChannelUnion channel, User user, Save save, String componentId) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.SUCCESS, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        String content = "";
        int id;
        if(componentId.equals("1")){
            id = 1;
            content = "Bulbizarre est un pokémon de type plante et poison. Tu es sûr de ton choix ?";
        }else if(componentId.equals("4")){
            id = 4;
            content = "Salamèche est un pokémon de type feu. Tu es sûr de ton choix ?";
        }else{
            id = 7;
            content = "Carapuce est un pokémon de type eau. Tu es sûr de ton choix ?";

        }

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(PNJ.RAOULT, content, lc))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    if (e.getComponentId().equals("true")) {
                                        save.getCampaign().setIdStarter(id);
                                        validationSave(channel,user,save);
                                    } else {
                                        choixStarter(channel, user, save);
                                    }
                                },
                                1,
                                TimeUnit.MINUTES,
                                () -> {
                                    setChoice(0);
                                    buttonManager.timeout(channel, user);
                                }
                        )
                );
    }

    private void validationSave(MessageChannelUnion channel, User user, Save save) {
        System.out.println(save.getCampaign().getNom());
    }
}

