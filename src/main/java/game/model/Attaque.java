package game.model;

import java.io.Serializable;

public class Attaque implements Serializable {

    private long idMoveAPI;

    private int bonusPp;
    private int ppLeft;

    public Attaque() {
    }

    public Attaque(long idMoveAPI, int bonusPp, int ppLeft) {
        this.idMoveAPI = idMoveAPI;
        this.bonusPp = bonusPp;
        this.ppLeft = ppLeft;
    }

    public Attaque(Move move){
        this.idMoveAPI = move.getId();
        this.bonusPp = 0;
        this.ppLeft = move.getPp();
    }

    public long getIdMoveAPI() {
        return idMoveAPI;
    }

    public void setIdMoveAPI(long idMoveAPI) {
        this.idMoveAPI = idMoveAPI;
    }

    public Move getMoveAPI(){
        return Client.getMoveById(idMoveAPI);
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
}
