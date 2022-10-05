package game;

import java.io.Serializable;
import java.util.Date;

public class Save implements Serializable {
    private long id;
    private String idUser;
    private Date lastPlayed;

    public Save() {
    }

    public Save(long id, String idUser, Date lastPlayed) {
        this.id = id;
        this.idUser = idUser;
        this.lastPlayed = lastPlayed;
    }

    public Save(String idUser) {
        this.lastPlayed = new Date();
        this.id = lastPlayed.getTime();
        this.idUser = idUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
