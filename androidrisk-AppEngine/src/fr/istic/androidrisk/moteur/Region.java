package fr.istic.androidrisk.moteur;

import com.googlecode.objectify.annotation.Serialized;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Id;

/**
 * Classe implémentant le concept de région géographique
 */
public class Region implements Serializable {

    private static final long serialVersionUID = 2431271323566500723L;

    @Id private Long id1;
    @Serialized private Map<String, Territoire> territoires = new HashMap<String, Territoire>();
    private String nom;
    private int id;

    public Region() {
    }

    public Region(String nom, int id) {
        this.nom = nom;
        this.id = id;
    }

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public Map<String, Territoire> getTerritoires() {
        return territoires;
    }

    public void setTerritoires(Map<String, Territoire> territoires) {
        this.territoires = territoires;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
