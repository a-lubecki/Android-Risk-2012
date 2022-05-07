package fr.istic.androidrisk.moteur;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *	Classe implémentant le concept de région géographique
 */
public class Region implements Serializable {

    private static final long serialVersionUID = 2431271323566500723L;

    private Long id1;
    private Map<String, Territory> territoires = new HashMap<String, Territory>();
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

    public Map<String, Territory> getTerritoires() {
        return territoires;
    }

    public void setTerritoires(Map<String, Territory> territoires) {
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

    /**
     * Fonctione retournant le joueur controlant la région
     * @return null si la région n'est pas contrôlée, le joueur si la région est contrôlée.
     */
    public Joueur isRegionControlee() {
        boolean controlee = true;
        Joueur j = territoires.values().iterator().next().getControler();

        for (Territory territoire : territoires.values()) {
            if (!(territoire.getControler().equals(j))) {
                controlee = false;
                break;
            }
        }
        if (controlee) {
            return j;
        } else {
            return null;
        }
    }

}
