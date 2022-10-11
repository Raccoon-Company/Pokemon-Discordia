package game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import game.model.Campaign;
import utils.Utils;

import java.io.Serializable;
import java.util.Date;

public class Save implements Serializable {
    private long id;
    private long userId;

    private Date lastPlayed;

    private Campaign campaign;

    @JsonIgnore
    private long privilegedChannelId;

    public Save() {
    }

    public Save(long id, long user, Date lastPlayed, Campaign campaign) {
        this.id = id;
        this.userId = user;
        this.lastPlayed = lastPlayed;
        this.campaign = campaign;
    }

    public Save(long user) {
        this.lastPlayed = new Date();
        this.id = lastPlayed.getTime();
        this.userId = user;
        this.campaign = new Campaign();
    }

    public Campaign getCampaign() {
        return campaign;
    }

    @JsonIgnore
    public String getDescription() {
        String res = "";
        res += "Dernière session de jeu : " + Utils.formatDateDDMMYY(lastPlayed);
        return res;
    }


    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public long getPrivilegedChannelId() {
        return privilegedChannelId;
    }

    public void setPrivilegedChannelId(long privilegedChannelId) {
        this.privilegedChannelId = privilegedChannelId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public void gameMenu() {
    }
}
