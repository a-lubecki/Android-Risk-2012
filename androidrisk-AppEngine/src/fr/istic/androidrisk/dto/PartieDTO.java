package fr.istic.androidrisk.dto;

import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;
import fr.istic.androidrisk.moteur.Partie;
import java.io.Serializable;
import javax.persistence.Id;

public class PartieDTO implements Serializable {

    @Serialized @Unindexed private Partie partie;
    @Id private String nom;

    public PartieDTO() {
    }

    public PartieDTO(Partie partie, String nom) {
        this.partie = partie;
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Partie getPartie() {
        return partie;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }
    
}
