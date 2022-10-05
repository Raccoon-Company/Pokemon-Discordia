package game;

import game.model.PNJ;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;
import utils.FileManager;
import utils.MessageManager;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.Arrays;
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
        List<Save> saves = FileManager.getInstance().getSaves(idUser);

        Save save = new Save(idUser);
save.setPrivilegedChannel(message.getChannel());
            startGame(save);




//        if(saves.isEmpty()){
//            Save save = new Save(idUser);
//
//            startGame(save);
//
//            FileManager.getInstance().writeSave(save);
//        }else{
//            message.getChannel().sendMessage(saves.size()+" saves ont été retrouvées !").complete();
//
//        }
    }

    private void startGame(Save save) {
        Message sent = MessageManager.getInstance()
                .sendMessageEmbedThumbnail(
                        save.getPrivilegedChannel(),
                        "Bonjour et bienvenue dans le fabuleux monde des pokémons !\nEs-tu un garçon ou une fille ?",
                        PNJ.RAOULT.getIconPath(), PNJ.RAOULT.getNom(),
                        0x9900FF,
                        Arrays.asList(Button.primary("f", "Fille ♀️"), Button.primary("g", "Garçon ♂️"))
                );
    }
}
