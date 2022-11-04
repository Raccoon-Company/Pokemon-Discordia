package game;

import executable.MyBot;
import game.model.Campaign;
import game.model.enums.PNJ;
import game.model.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
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
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import utils.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Launcher extends ListenerAdapter {

    private final MyBot bot;
    private final DiscordManager discordManager;
    private final MessageManager messageManager;
    private final FileManager fileManager;
    private final ButtonManager buttonManager;
    private List<String> associatedMessagesIds;
    private long channelId;
    private int choice = -1;

    /**
     * Constructeur.
     *
     * @param myBot
     */
    public Launcher(MyBot myBot) {
        this.bot = myBot;
        this.fileManager = new FileManager(bot);
        this.messageManager = new MessageManager(bot);
        this.discordManager = new DiscordManager(bot);
        this.buttonManager = new ButtonManager(bot);
        this.associatedMessagesIds = new ArrayList<>();
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
        channelId = message.getChannel().getIdLong();
        associatedMessagesIds.add(message.getId());
        long idUser = message.getAuthor().getIdLong();
        FileManager fileManager = new FileManager(bot);

        //on lis les saves potentielles correspondantes à l'user
        //si au moins une, on propose le menu de sélection + possibilité de lancer une nouvelle (limite à ? saves)
        //sinon lancement auto d'une game
        List<Save> saves = fileManager.getSaves(idUser);

        if (saves.isEmpty()) {
            Save save = new Save(message.getAuthor().getIdLong());
            save.setPrivilegedChannelId(message.getChannel().getIdLong());
            startGame(save);
        } else {
            Message sent = message.getChannel().sendMessage("Choix de la sauvegarde").complete();
            associatedMessagesIds.add(sent.getId());
            chooseSave(message.getChannel(), saves, idUser);
        }
    }

    private void chooseSave(MessageChannelUnion channel, List<Save> saves, long idUser) {
        User user = discordManager.getUserById(idUser);
        MessageCreateBuilder mcb = new MessageCreateBuilder();

        List<Button> buttons = new ArrayList<>();
        for (Save value : saves) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(value.getId()), value.getCampaign().getNom()));
        }
        if (saves.size() < 3) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "new", "Nouvelle partie", Emoji.fromFormatted("\uD83C\uDD95")));
        }
        buttons.add(Button.of(ButtonStyle.SECONDARY, "exit", "", Emoji.fromFormatted("❌")));

        LayoutComponent lc = ActionRow.of(buttons);

        String thumbnailURL = fileManager.getFullPathToIcon(PNJ.SYSTEM.getIconPath());
        File thumbnailFile = new File(thumbnailURL);
        mcb.addFiles(FileUpload.fromData(thumbnailFile, PNJ.SYSTEM.getIconPath()));

        for (Save save : saves) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(new Color(save.getColorRGB()))
                    .setAuthor(save.getCampaign().getNom(), null, "attachment://" + PNJ.SYSTEM.getIconPath())
                    .setDescription(save.getDescription());

            mcb.addEmbeds(embedBuilder.build());
        }
        mcb.addComponents(lc);
        bot.lock(user);
        channel.sendMessage(messageManager.createMessageData(mcb)).queue(message -> bot.getEventWaiter().waitForEvent( // Setup Wait action once message was send
                        ButtonInteractionEvent.class,
                        e -> buttonManager.createPredicate(e, message, idUser, lc.getButtons()),
                        //action quand réponse détectée
                        e -> {
                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                            bot.unlock(user);
                            associatedMessagesIds.add(message.getId());
                            if (e.getComponentId().equals("exit")) {
                                clearMessagesAssociated();
                            } else if (e.getComponentId().equals("new")) {
                                Save save = new Save(user.getIdLong());
                                save.setPrivilegedChannelId(message.getChannel().getIdLong());
                                startGame(save);
                            } else {
                                Save selected = saves.stream().filter(s -> e.getComponentId().equals(String.valueOf(s.getId()))).findAny().orElseThrow(IllegalStateException::new);
                                selected.setPrivilegedChannelId(channelId);
                                loadSave(selected);
                            }
                        },
                        1, TimeUnit.MINUTES,
                        () -> buttonManager.timeout(channel, user)
                )
        );
    }

    private void loadSave(Save save) {
        Game game = new Game(bot, save);
        game.gameMenu();
    }

    private void startGame(Save save) {
        MessageChannelUnion channel = discordManager.getChannelById(save.getPrivilegedChannelId());
        User user = discordManager.getUserById(save.getUserId());

//        profIntro(channel, user, save);
save.getCampaign().setGender(true);
save.getCampaign().setNom("placeholder");
save.getCampaign().setNomRival("ph2");
save.getCampaign().setIdStarter(1);
validationSave(save, channel);
    }


    private void profIntro(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "f", "Fille", Emoji.fromFormatted("♀️")),
                Button.of(ButtonStyle.PRIMARY, "m", "Garçon", Emoji.fromFormatted("♂️"))
        ));

        bot.lock(user);
        //message d'intro : choix genre
        channel.sendMessage(messageManager.createMessageThumbnail(save, PNJ.RAOULT, "Bonjour et bienvenue dans le fabuleux monde des pokémons !\nEs-tu un garçon ou une fille ?", lc))
                .queue(message ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée
                                e -> {
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
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
                                () -> buttonManager.timeout(channel, user)
                        )
                );
    }

    private void choixPrenom(MessageChannelUnion channel, User user, Save save) {
        String image = save.getCampaign().isGender() ? PropertiesManager.getInstance().getImage("lab-boy") : PropertiesManager.getInstance().getImage("lab-girl");

        bot.lock(user);
        //demande d'entrée du prénom
        channel.sendMessage(messageManager.createMessageThumbnailAndImage(save, PNJ.RAOULT, "Ah d'accord ! Et comment t'appelles-tu ?", null, image))
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
                                () -> messageManager.timeout(channel, user)
                        )
                );
    }


    private void confirmationNom(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(save, PNJ.RAOULT, save.getCampaign().getNom() + " donc, c'est bien ça ?", lc))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
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
        String image = save.getCampaign().isGender() ? PropertiesManager.getInstance().getImage("lab-boy-rival") : PropertiesManager.getInstance().getImage("lab-girl-rival");

        bot.lock(user);
        //demande d'entrée du prénom
        channel.sendMessage(messageManager.createMessageThumbnailAndImage(save, PNJ.RAOULT, "Très bien.\nEt lui là-bas, c'est ton ami non ? Il va commencer son aventure dans le monde des pokémons en même temps que toi.\nComment s'appelle t'il ?", null, image))
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
                                () -> messageManager.timeout(channel, user)
                        )
                );
    }

    private void confirmationNomRival(MessageChannelUnion channel, User user, Save save) {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(save, PNJ.RAOULT, save.getCampaign().getNomRival() + " donc, c'est bien ça ?", lc))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
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
                Button.of(ButtonStyle.PRIMARY, "1", "Bulbizarre", Emoji.fromFormatted("\uD83C\uDF3F")),
                Button.of(ButtonStyle.PRIMARY, "4", "Salamèche", Emoji.fromFormatted("\uD83D\uDD25")),
                Button.of(ButtonStyle.PRIMARY, "7", "Carapuce", Emoji.fromFormatted("\uD83D\uDCA7"))
        ));

        bot.lock(user);

        channel.sendMessage(messageManager.createMessageThumbnailAndImage(save, PNJ.RAOULT, "Bon, pas le temps de niaiser " + save.getCampaign().getNom() + ", prends ce pokédex, choisis le pokémon que tu veux et casse-toi !\nAh oui, on a pas de pikachus, c'est pour les victimes. Au pire t'en trouveras dans la forêt à côté.", lc, PropertiesManager.getInstance().getImage("starters")))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    confirmationStarter(channel, user, save, e.getComponentId());
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
                Button.of(ButtonStyle.PRIMARY, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        String content = "";
        int id;
        if (componentId.equals("1")) {
            id = 1;
            content = "Bulbizarre est un pokémon de type plante et poison. Tu es sûr de ton choix ?";
        } else if (componentId.equals("4")) {
            id = 4;
            content = "Salamèche est un pokémon de type feu. Tu es sûr de ton choix ?";
        } else {
            id = 7;
            content = "Carapuce est un pokémon de type eau. Tu es sûr de ton choix ?";

        }

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(save, PNJ.RAOULT, content, lc))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    if (e.getComponentId().equals("true")) {
                                        save.getCampaign().setIdStarter(id);
                                        validationSave(save,channel);
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

    private void validationSave(Save save, MessageChannelUnion channel) {
        channel.sendTyping().queue();

        Campaign campaign = new Campaign(save.getCampaign().getNom(), save.getCampaign().isGender(), save.getCampaign().getNomRival(), save.getCampaign().getIdStarter());
        save.setCampaign(campaign);
        Game game = new Game(bot, save);
        Pokemon starter = new Pokemon(save.getCampaign().getIdStarter(), 5, false, game);
        save.getCampaign().getEquipe().add(starter);
        starter.choixSurnom(game, "premiercombat");
    }

    private void clearMessagesAssociated() {
        discordManager.getChannelById(channelId).purgeMessagesById(associatedMessagesIds);
    }
}

