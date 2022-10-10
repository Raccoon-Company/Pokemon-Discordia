package listeners;

import commands.CommandManager;
import executable.MyBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.MessageManager;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {
    private final MyBot bot;
    private final CommandManager commandManager;
    private final MessageManager messageManager;

    public CommandListener(MyBot bot) {
        this.bot = bot;
        this.messageManager = new MessageManager(bot);
        this.commandManager = new CommandManager(bot);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        /*si le message ne remplit les conditions pour être une commande :
         * -l'expéditeur est un bot
         * -ne commence pas par le bon préfixe
         * -trop courte
         * on ne prend pas en compte
         */
        if (event.getAuthor().isBot() || !event.getMessage().getContentDisplay().startsWith(CommandManager.PREFIX) || event.getMessage().getContentDisplay().length() < 2) {
            return;
        }

        commandManager.process(event, bot);
    }
}
