package game.model;

import game.Game;

public class Boutique {

    private Game game;
    public Boutique(Game game) {
        this.game = game;
    }

    public void entrer(){
        //achat
            //choix categorie
                //choix rapides + selection manuelle par id
        //vendre
            //selection manuellepar id
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
