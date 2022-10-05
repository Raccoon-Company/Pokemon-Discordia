package commands;

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

    volatile static CommandManager instance;

    public static final String PREFIX = PropertiesManager.getInstance().getProp("prefix");

    private final List<String> validCommands;

    /**
     * Constructeur.
     */
    CommandManager() {
        validCommands = Arrays.stream(Commands.values()).map(Commands::getTexte).collect(Collectors.toList());
    }

    /**
     * Retourne l'instance de CommandManager.
     */
    public static CommandManager getInstance() {
        if (instance == null) {
            synchronized (CommandManager.class) {
                if (instance == null) {
                    instance = new CommandManager();
                }
            }
        }
        return instance;
    }

    public void process(MessageReceivedEvent event) {
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
        Launcher.getInstance().start(message);
        break;
}
    }
}
