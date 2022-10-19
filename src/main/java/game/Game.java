package game;

import executable.MyBot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import utils.*;

import java.util.Arrays;
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

    public void gameMenu() {
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



        //déclaration des boutons choix du genre
        LayoutComponent lc = ActionRow.of(Arrays.asList(
                Button.of(ButtonStyle.SUCCESS, "move", "Se déplacer", Emoji.fromFormatted("\uD83E\uDDED")),
                Button.of(ButtonStyle.SUCCESS, "pnj", "Discuter", Emoji.fromFormatted("\uD83D\uDDE3")),

//                Button.of(ButtonStyle.SUCCESS, "grass", "Hautes herbes", Emoji.fromFormatted("\uD83C\uDF3F")),

                Button.of(ButtonStyle.SUCCESS, "bag", "Sac à dos", Emoji.fromFormatted("\uD83C\uDF92"))
//                Button.of(ButtonStyle.SUCCESS, "battle", "Combat de dresseur", Emoji.fromFormatted("\uD83D\uDCA5")),
        ));

        String background;
        int x;
        int y;
        if(save.getCampaign().getCurrentStructure() != null){
            background = save.getCampaign().getCurrentStructure().getBackground();
            y = save.getCampaign().getCurrentStructure().getY();
            x = save.getCampaign().getCurrentStructure().getX();
        }else{
            background = save.getCampaign().getCurrentZone().getBackground();
            y = save.getCampaign().getCurrentZone().getY();
            x = save.getCampaign().getCurrentZone().getX();
        }

        String combined = "temp/"+ imageManager.merge( PropertiesManager.getInstance().getImage(background), getPlayerSprite(),x,y);

        channel.sendMessage(messageManager.createMessageImage("Que faire ?", lc, combined))
                .queue((message) ->
                        bot.getEventWaiter().waitForEvent(
                                ButtonInteractionEvent.class,
                                //vérif basique de correspondance entre message/interaction
                                e -> buttonManager.createPredicate(e, message, save, lc),
                                //action quand interaction détectée

                                e -> {
                                    bot.unlock(user);
                                    e.deferEdit().queue();
                                    channel.sendMessage(e.getComponentId() + " sélectionné").queue();
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
        if(save.getCampaign().isGender()){
            return PropertiesManager.getInstance().getImage("boy");
        }else{
            return PropertiesManager.getInstance().getImage("girl");
        }
    }
}
