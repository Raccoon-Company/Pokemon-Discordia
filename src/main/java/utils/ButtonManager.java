package utils;

import executable.MyBot;
import game.Save;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collection;
import java.util.List;
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

    public boolean createPredicate(ButtonInteractionEvent e, Message message, Save save, List<LayoutComponent> components) {
        List<Button> lb = components.stream().map(LayoutComponent::getButtons).flatMap(Collection::stream).collect(Collectors.toList());
        return createPredicate(e, message, save.getUserId(), lb);
    }

    public boolean createPredicate(ButtonInteractionEvent e, Message message, Save save, LayoutComponent lc) {
        return createPredicate(e, message, save.getUserId(), lc.getButtons());
    }

    public boolean createPredicate(ButtonInteractionEvent e, Message message, long idUser, List<Button> lb) {
        if (e.getMessageIdLong() != message.getIdLong()) {
            return false;
        }
        if (e.getUser().getIdLong() != idUser) {
            return false;
        }
        //retourne false si l'id du bouton d'interaction ne correspond à aucun de ceux proposés à la base
        if (!lb.stream().map(ActionComponent::getId).collect(Collectors.toList()).contains(e.getComponentId())) {
            return false;
        }
        //is answer valid (id proposé)

        return !e.isAcknowledged();
    }

    public void timeout(MessageChannelUnion channel, User user) {
        bot.unlock(user);
        channel.sendMessage("Délai de réponse dépassé ! Relancez la commande /start pour continuer").queue();
    }


}
