package game.model;

import game.Game;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.util.Arrays;

public enum PNJ {
    SYSTEM(1,"System", "system.png","\uD83E\uDD16"),
    RAOULT(2,"Prof. Didier Raoult", "raoult.jpg","\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDD2C"),
    MOM(3,"Maman", "mom.jpg", "👩🏼");

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
        }

        game.getChannel().sendMessage(
                game.getMessageManager().createMessageThumbnail(this, message, lc)
        ).queue();

    }
}
