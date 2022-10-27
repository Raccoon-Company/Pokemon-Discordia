package game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.oscar0812.pokeapi.models.moves.Move;
import com.github.oscar0812.pokeapi.utils.Client;

import java.io.Serializable;

public class Attaque implements Serializable {

    private int idMoveAPI;

    private int bonusPp;
    private int ppLeft;

    @JsonIgnore
    private Move move;

    public Attaque() {
    }

    public Attaque(int idMoveAPI, int bonusPp, int ppLeft) {
        this.idMoveAPI = idMoveAPI;
        this.bonusPp = bonusPp;
        this.ppLeft = ppLeft;
    }

    public Attaque(Move move){
        this.idMoveAPI = move.getId();
        this.bonusPp = 0;
        this.ppLeft = move.getPp();
    }

    public int getIdMoveAPI() {
        return idMoveAPI;
    }

    public void setIdMoveAPI(int idMoveAPI) {
        this.idMoveAPI = idMoveAPI;
    }

    @JsonIgnore
    public Move getMoveAPI(){
        if(this.move == null){
            this.move = Client.getMoveById(idMoveAPI);
        }
       return this.move;
    }

    public int getBonusPp() {
        return bonusPp;
    }

    public void setBonusPp(int bonusPp) {
        this.bonusPp = bonusPp;
    }

    public int getPpLeft() {
        return ppLeft;
    }

    public void setPpLeft(int ppLeft) {
        this.ppLeft = ppLeft;
    }

    public void realiser(Combat combat, ActionCombat actionCombat){
        Move move = actionCombat.getAttaque().getMoveAPI();
        //TODO utiliser l'attaque
    }
}
