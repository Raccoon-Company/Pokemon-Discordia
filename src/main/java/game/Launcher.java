package game;

import net.dv8tion.jda.api.entities.Message;
import utils.FileManager;

import java.util.List;

public class Launcher {

    volatile static Launcher instance;

    /**
     * Constructeur.
     */
    Launcher() {
    }

    /**
     * Retourne l'instance de CommandManager.
     */
    public static Launcher getInstance() {
        if (instance == null) {
            synchronized (Launcher.class) {
                if (instance == null) {
                    instance = new Launcher();
                }
            }
        }
        return instance;
    }

    public void start(Message message) {
        String idUser = message.getAuthor().getId();
        //on lis les saves potentielles correspondantes à l'user
        //si au moins une, on propose le menu de sélection + possibilité de lancer une nouvelle (limite à ? saves)
        //sinon lancement auto d'une game
        List<Save> saves = FileManager.getSaves(idUser);

        message.getChannel().sendMessage(saves.size()+" saves ont été retrouvées !").complete();

        Save save = new Save(idUser);

        FileManager.writeSave(save);
        saves = FileManager.getSaves(idUser);

    }
}
