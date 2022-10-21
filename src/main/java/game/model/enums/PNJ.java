package game.model.enums;

import game.Game;
import game.model.Pokemon;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.util.Arrays;

public enum PNJ {
    SYSTEM(1, "System", "system.png", "\uD83E\uDD16"),
    RAOULT(2, "Prof. Didier Raoult", "raoult.jpg", "\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDD2C"),
    MOM(3, "Maman", "mom.jpg", "👩🏼"),
    ECOLIER(4, "Écolier Timothée", "kid.png", "\uD83E\uDDD2\uD83C\uDFFC"),
    INFIRMIERE(5, "Infirmière Joëlle", "infirmiere.jpg", "\uD83D\uDC69\uD83C\uDFFC\u200D⚕️"),
    VENDEUSE(6, "Vendeuse", "vendeuse.png", "👩🏼"),
    ;

    private final int id;
    //nom du pnj
    private final String nom;
    //url du fichier icone du pnj
    private final String iconPath;

    private final String emojiCode;

    PNJ(int id, String nom, String path, String emojiCode) {
        this.iconPath = path;
        this.nom = nom;
        this.emojiCode = emojiCode;
        this.id = id;
    }

    public static PNJ getPNJById(String componentId) {
        return Arrays.stream(values()).filter(s -> componentId.equals(String.valueOf(s.getId()))).findAny().orElse(null);
    }

    public String getEmojiCode() {
        return emojiCode;
    }

    public int getId() {
        return id;
    }


    public String getNom() {
        return nom;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void defaultTalk(Game game) {
        LayoutComponent lc = null;

        String message = null;

        //message d'introduction
        switch (this) {
            case SYSTEM:
                break;
            case RAOULT:
                break;
            case MOM:
                message = "Bonjour mon lapin ! Fais attention aux rattatas sur la route !";
                break;
            case ECOLIER:
                message = "Ca fait 2 heures que j'essaie d'attraper un roucool... Si seulement j'avais des pokéballs...";
                break;
            case INFIRMIERE:
                message = "Laissez moi m'occuper de vos pokémons...";
                break;
            case VENDEUSE:
                message = "On n'a rien à vendre pour l'instant, désolé le dev a la flemme...";
                break;
        }

        game.getChannel().sendMessage(
                game.getMessageManager().createMessageThumbnail(game.getSave(), this, message, lc)
        ).queue();

        //suite éventuelle pour certains PNJs
        switch (this) {
            case INFIRMIERE:
                game.getSave().getCampaign().getEquipe().forEach(Pokemon::completeHeal);
                message = "Et voilà, vos pokémons sont complètements soignés ! À bientôt !";
                game.getChannel().sendMessage(
                        game.getMessageManager().createMessageThumbnail(game.getSave(), this, message, lc)
                ).queue();
                break;
        }
    }
}
