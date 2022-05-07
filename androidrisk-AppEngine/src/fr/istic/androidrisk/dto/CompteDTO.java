package fr.istic.androidrisk.dto;

import com.googlecode.objectify.annotation.Unindexed;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;

public class CompteDTO implements Serializable {

    private static final long serialVersionUID = -3297170743138530486L;

    @Id private String login;
    @Unindexed private String mdp;
    @Unindexed private boolean changed;
    @Unindexed private List<String> parties;

    public CompteDTO() {
    }

    public CompteDTO(String login, String mdp) {
        this.login = login;
        this.mdp = mdp;
        changed = false;
        parties = new ArrayList<String>();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public List<String> getParties() {
        if (parties==null) {
            parties = new ArrayList<String>();
        }
        return parties;
    }

    public void setParties(List<String> parties) {
        this.parties = parties;
    }
    
}
