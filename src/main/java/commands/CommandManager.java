package commands;

import executable.MyBot;
import game.Launcher;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.PropertiesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Après réception d'une commande textuelle, redirige vers la méthode appropriée
 */
public class CommandManager {


    public static final String PREFIX = PropertiesManager.getInstance().getProp("prefix");

    private final List<String> validCommands;
    private final MyBot bot;

    /**
     * Constructeur.
     * @param bot
     */
    public CommandManager(MyBot bot) {
        this.bot = bot;
        validCommands = Arrays.stream(Commands.values()).map(Commands::getTexte).collect(Collectors.toList());
    }

    public void process(MessageReceivedEvent event, MyBot bot) {
        Message message = event.getMessage();
        //on décompose le message pour extraire la commande et les arguments
        String fullCommand = message.getContentDisplay().substring(1);
        List<String> args = new ArrayList<>(Arrays.asList(fullCommand.split(" ")));
        String command = args.remove(0);
        //si la commande est inconnue, retour + message warn
        if (!validCommands.contains(command)) {
            message.getChannel().sendMessage("La commande !" + command + " n'existe pas. Utiliser " + PREFIX + "help pour une liste de commandes valides !").complete();
            return;
        }
        //vérif du nb d'arguments
        Commands discordiaCommand = Commands.getByTexte(command);
        if (discordiaCommand.getMinArgs() > args.size()) {
            message.getChannel().sendMessage("La commande !" + command + " attend au moins " + discordiaCommand.getMinArgs() + " arguments. Utiliser " + PREFIX + "help pour plus de détails !").complete();
            return;
        }

switch (discordiaCommand){
    case HELP:

        break;
    case START:
        Launcher launcher = new Launcher(bot);
        launcher.start(message);
        break;
}
    }
}
