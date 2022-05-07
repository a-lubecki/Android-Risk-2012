package fr.istic.androidrisk.dto;

import fr.istic.androidrisk.moteur.Play;
import java.io.Serializable;

public class PartieDTO implements Serializable {

    private Play partie;
    private String nom;

    public PartieDTO() {
    }

    public PartieDTO(Play partie, String nom) {
        this.partie = partie;
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Play getPartie() {
        return partie;
    }

    public void setPartie(Play partie) {
        this.partie = partie;
    }
    
}
