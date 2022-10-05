package listeners;

import commands.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.PropertiesManager;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {


    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        /*si le message ne remplit les conditions pour être une commande :
        * -l'expéditeur est un bot
        * -ne commence pas par le bon préfixe
        * -trop courte
        * on ne prend pas en compte
        */
        if(event.getAuthor().isBot() || !event.getMessage().getContentDisplay().startsWith(CommandManager.PREFIX) || event.getMessage().getContentDisplay().length() < 2){
            return;
        }
        System.out.println("Message reçu : "+event.getAuthor().getName());
        CommandManager.getInstance().process(event);
    }
}
