package game.model;

import game.Game;

public class SessionPC {
    private final Game game;

    public SessionPC(Game game) {

        this.game = game;
    }

    public void ouvrir() {
        game.getChannel().sendMessage("Choix des fonctionnalités blablabla").queue();//TODO
    }
}
