package utils;

import executable.MyBot;
import game.Save;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import utils.DiscordManager;

import java.util.stream.Collectors;

public class ButtonManager {

    private MyBot bot;
    private DiscordManager discordManager;

    /**
     * Constructeur.
     */
    public ButtonManager(MyBot bot) {
        this.bot = bot;
        this.discordManager = new DiscordManager(bot);
    }

    public boolean createPredicate(ButtonInteractionEvent e, Message message, Save save, LayoutComponent lc) {
        if (e.getMessageIdLong() != message.getIdLong()) {
            return false;
        }
        if (e.getUser().getIdLong() != save.getUserId()) {
            return false;
        }
        //retourne false si l'id du bouton d'interaction ne correspond à aucun de ceux proposés à la base
        if (!lc.getButtons().stream().map(ActionComponent::getId).collect(Collectors.toList()).contains(e.getComponentId())) {
            return false;
        }
        //is answer valid (id proposé)

        return !e.isAcknowledged();
    }

    public Runnable timeout(MessageChannelUnion channel) {
        return () -> channel.sendMessage("Délai de réponse dépassé ! ").queue();
    }
}
