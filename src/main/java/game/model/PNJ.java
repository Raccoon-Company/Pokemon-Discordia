package game.model;

import game.Game;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.util.Arrays;

public enum PNJ {
    SYSTEM(1,"System", "system.png","\uD83E\uDD16"),
    RAOULT(2,"Prof. Didier Raoult", "raoult.jpg","\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDD2C"),
    MOM(3,"Maman", "mom.jpg", "👩🏼"),
    ECOLIER(4, "Écolier Timothée", "kid.png","\uD83E\uDDD2\uD83C\uDFFC"),
    INFIRMIERE(5,"Infirmière Joëlle" ,"infirmiere.jpg" ,"\uD83D\uDC69\uD83C\uDFFC\u200D⚕️" );

    private int id;
    //nom du pnj
    private String nom;
    //url du fichier icone du pnj
    private String iconPath;

    private String emojiCode;

    PNJ(int id,String nom, String path, String emojiCode) {
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

    public void setEmojiCode(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void defaultTalk(Game game) {
        LayoutComponent lc = null;

        String message = null;

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
                message = "Je vais soigner vos pokémons (quand ca sera codé)";
break;
        }

        game.getChannel().sendMessage(
                game.getMessageManager().createMessageThumbnail(game.getSave(),this, message, lc)
        ).queue();

    }
}
