package game;

import commands.Commands;
import executable.MyBot;
import game.model.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import utils.*;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        fileManager.writeSave(save);
        Structure currentStructure = save.getCampaign().getCurrentStructure();
        Zones currentZone = save.getCampaign().getCurrentZone();

        List<Button> buttons = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "move", "Se déplacer", Emoji.fromFormatted("\uD83E\uDDED")),
                Button.of(ButtonStyle.PRIMARY, "bag", "Sac à dos", Emoji.fromFormatted("\uD83C\uDF92"))
        ));

        if (currentStructure == null && currentZone.getTypeZone().equals(ZoneTypes.ROUTE)) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "grass", "Hautes herbes", Emoji.fromFormatted("\uD83C\uDF3F")));
        }
        if ((currentStructure != null && currentStructure.getPnjs().size() > 0) || (currentStructure == null && currentZone.getPnjs().size() > 0)) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "pnj", "Discuter", Emoji.fromFormatted("\uD83D\uDDE3")));
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
            nom = currentZone.getNom();
        }

        String combined = "temp/" + imageManager.merge(PropertiesManager.getInstance().getImage(background), getPlayerSprite(), x, y);

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(save, nom, lc, combined))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    if (e.getComponentId().equals("move")) {
                                        moveMenu();
                                    } else if (e.getComponentId().equals("pnj")) {
                                        talkMenu();
                                    } else if (e.getComponentId().equals("bag")) {
                                        bagMenu();
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
                buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pnj.getId()), pnj.getNom(), Emoji.fromFormatted(pnj.getEmojiCode())));
            }
        }

        LayoutComponent lc = ActionRow.of(buttons);
        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(save, "Avec qui ?", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
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

    private void pokedex() {
        long a = save.getCampaign().getPokedex().entrySet().stream().filter(e -> e.getValue() == 1).count();
        long b = save.getCampaign().getPokedex().entrySet().stream().filter(e -> e.getValue() == 2).count();

        channel.sendMessage("Espèces de pokémons observées : " + (a + b) + "\nEspèces de pokémons capturées : " + b).queue();
        bagMenu();
    }

    private void pokemons() {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        List<Button> buttons = new ArrayList<>();
        List<Button> buttons2 = new ArrayList<>();

        for (Pokemon pokemon : save.getCampaign().getEquipe()) {
            if(buttons.size()>=5){
                buttons2.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokemon.getId()), pokemon.getSpecieName(), Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
            }else{
                buttons.add(Button.of(ButtonStyle.PRIMARY, String.valueOf(pokemon.getId()), pokemon.getSpecieName(), Emoji.fromCustom("pokeball", 1032561600701399110L, false)));
            }
        }
        buttons2.add(Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19")));
        LayoutComponent lc = ActionRow.of(buttons);
        LayoutComponent lc2 = ActionRow.of(buttons2);
        mcb.addComponents(lc,lc2);
        mcb.addContent("Inspection de l'équipe");
        bot.lock(user);
        channel.sendMessage(messageManager.createMessageData(mcb)).queue(message -> bot.getEventWaiter().waitForEvent( // Setup Wait action once message was send
                        ButtonInteractionEvent.class,
                        e -> buttonManager.createPredicate(e, message, save.getUserId(), lc),
                        //action quand réponse détectée
                        e -> {
                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                            bot.unlock(user);
                            if(e.getComponentId().equals("back")){
                                gameMenu();
                            }else{
                                menuPokemon(save.getCampaign().getTeamPokemonById(Long.parseLong(e.getComponentId())));
                            }
                        },
                        1, TimeUnit.MINUTES,
                        () -> buttonManager.timeout(channel, user)
                )
        );

    }

    private void menuPokemon(Pokemon pokemon) {
        if(pokemon == null){
            pokemons();
            return;
        }
        String text = pokemon.getDescriptionDetaillee();
        MessageCreateBuilder mcb= new MessageCreateBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(save.getColorRGB()))
                .setDescription(text);

            embedBuilder.setThumbnail(pokemon.getPokemonAPI().getSprites().getFrontDefault());

        mcb.addEmbeds(embedBuilder.build());

       channel.sendMessage(messageManager.createMessageData(mcb)).queue();
       pokemons();
    }

    private void settings() {
        List<Button> buttons = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "color", "Changer la couleur", Emoji.fromFormatted("\uD83C\uDFA8")),
                Button.of(ButtonStyle.PRIMARY, "delete", "Supprimer la sauvegarde", Emoji.fromFormatted("\uD83D\uDDD1")),
                Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19"))
        ));

        LayoutComponent lc = ActionRow.of(buttons);

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(save, "Options", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    switch (e.getComponentId()) {
                                        case "color":
                                            colorSettings();
                                            break;
                                        case "delete":
                                            deleteSettings();
                                            break;
                                        case "back":
                                        default:
                                            gameMenu();
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

    private void deleteSettings() {
        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "true", "Oui", Emoji.fromFormatted("✅")),
                Button.of(ButtonStyle.SECONDARY, "false", "Non", Emoji.fromFormatted("❌"))
        ));

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageThumbnail(save, null, "Êtes-vous sur de vouloir supprimer cette sauvegarde ? Toutes les données seront définitivement perdues.", lc))
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
                                        if (fileManager.deleteSave(getSave().getUserId(), save.getId())) {
                                            e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                            channel.sendMessage("Supprimée avec succès. Vous pouvez relancer une nouvelle partie avec " + PropertiesManager.getInstance().getProp("prefix") + Commands.START.getTexte()).queue();
                                        } else {
                                            e.editButton(Button.of(ButtonStyle.DANGER, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                            channel.sendMessage("Échec de la suppression.").queue();
                                            settings();
                                        }
                                    } else {
                                        settings();
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


    private void colorSettings() {
        List<Button> buttons = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "0x5865F2", "Bleu Discord", null),
                Button.of(ButtonStyle.PRIMARY, "0xFEE75C", "Jaune Citron", null),
                Button.of(ButtonStyle.PRIMARY, "0xEB459E", "Rose Fuschia", null),
                Button.of(ButtonStyle.PRIMARY, "custom", "Personnalisé", Emoji.fromFormatted("\uD83C\uDFA8")),
                Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19"))
        ));

        LayoutComponent lc = ActionRow.of(buttons);

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(save, "Choix de la couleur", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    if (e.getComponentId().equals("back")) {
                                        gameMenu();
                                    } else if (e.getComponentId().equals("custom")) {
                                        customColorSettings();
                                    } else {
                                        save.setColorRGB(Color.decode(e.getComponentId()).getRGB());
                                        settings();
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

    private void customColorSettings() {
        bot.lock(user);
        //demande d'entrée du prénom
        channel.sendMessage(messageManager.createMessageImage(save, "Entrez le code hex de la couleur (ex. : #FFCCEE, 0xFEE75C, etc)", null, null))
                .queue(message -> bot.getEventWaiter().waitForEvent( // Setup Wait action once message was send
                                MessageReceivedEvent.class,
                                e -> messageManager.createPredicate(e, save),
                                //action quand réponse détectée
                                e -> {
                                    bot.unlock(user);
                                    try {
                                        Color color = Color.decode(e.getMessage().getContentRaw());
                                        save.setColorRGB(color.getRGB());
                                        settings();
                                    } catch (NumberFormatException nfe) {
                                        channel.sendMessage("Code couleur invalide.").queue();
                                        colorSettings();
                                    }
                                },
                                1, TimeUnit.MINUTES,
                                () -> messageManager.timeout(channel, user)
                        )
                );
    }

    private void bagMenu() {
        List<Button> buttons = new ArrayList<>(Arrays.asList(
                Button.of(ButtonStyle.PRIMARY, "pokedex", "Pokedex", Emoji.fromFormatted("\uD83D\uDCC7")),
                Button.of(ButtonStyle.PRIMARY, "pokemon", "Pokémon", Emoji.fromCustom("pokeball", 1032561600701399110L, false)),
                Button.of(ButtonStyle.PRIMARY, "sac", "Objets", Emoji.fromFormatted("\uD83C\uDF92")),
                Button.of(ButtonStyle.PRIMARY, "options", "Options", Emoji.fromFormatted("⚙")),
                Button.of(ButtonStyle.PRIMARY, "back", "Retour", Emoji.fromFormatted("\uD83D\uDD19"))
        ));

        LayoutComponent lc = ActionRow.of(buttons);

        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(save, "Sac à dos", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    switch (e.getComponentId()) {
                                        case "pokedex":
                                            pokedex();
                                            break;
                                        case "pokemon":
                                            pokemons();
                                            break;
                                        case "sac":
                                            break;
                                        case "options":
                                            settings();
                                            break;
                                        default:
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

    private void moveMenu() {
        Structure currentStructure = save.getCampaign().getCurrentStructure();
        Zones currentZone = save.getCampaign().getCurrentZone();
        List<Structure> structuresAccessibles = currentStructure == null ? currentZone.getListeIdStructures().stream().map(Structure::getById).collect(Collectors.toList()) : currentStructure.getStructuresAccessibles();
        List<Zones> zonesAccessibles = currentStructure == null ? currentZone.getListeZonesAccessibles() : Collections.singletonList(save.getCampaign().getCurrentZone());
        List<Button> buttons = new ArrayList<>();
        for (Structure structureAccessible : structuresAccessibles) {
            if (structureAccessible != null) {
                buttons.add(Button.of(ButtonStyle.PRIMARY, "s" + structureAccessible.getId(), structureAccessible.getNom(), Emoji.fromFormatted("\uD83D\uDEAA")));
            }
        }
        for (Zones zoneAccessible : zonesAccessibles) {
            if (zoneAccessible != null) {
                buttons.add(Button.of(ButtonStyle.PRIMARY, "z" + zoneAccessible.getIdZone(), zoneAccessible.getNom(), Emoji.fromFormatted(zoneAccessible.getTypeZone().getEmojiCode())));
            }
        }

        LayoutComponent lc = ActionRow.of(buttons);
        bot.lock(user);
        channel.sendMessage(messageManager.createMessageImage(save, "Pour aller où ?", lc, null))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.editButton(Button.of(ButtonStyle.SUCCESS, Objects.requireNonNull(e.getButton().getId()), e.getButton().getLabel(), e.getButton().getEmoji())).queue();
                                    //structure sélectionnée
                                    if (e.getComponentId().startsWith("s")) {
                                        save.getCampaign().setCurrentStructure(Structure.getById(e.getComponentId().split("s")[1]));
                                        //zone sélectionnée
                                    } else {
                                        save.getCampaign().setCurrentZone(Zones.getById(e.getComponentId().split("z")[1]));
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
