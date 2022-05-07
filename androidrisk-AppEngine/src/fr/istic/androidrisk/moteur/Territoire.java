package fr.istic.androidrisk.moteur;

import com.googlecode.objectify.annotation.Serialized;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import fr.istic.mmm.orm.exception.*;
import javax.persistence.Id;

/**
 * Classe représentant un territoire et les unités stationnées dessus.
 */
public class Territoire implements Serializable {

    private static final long serialVersionUID = -9134724774181313360L;

    @Id private Long id;
    private List<String> tLimitrophes = new ArrayList<String>();
    private int nb_unites_stationnees;
    private String nom;
    private String identifiant;
    @Serialized private Joueur controleur;

    /*
     * Constructeurs
     */
    public Territoire() {
    }

    public Territoire(String nom, String id) {
        this.nom = nom;
        identifiant = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> gettLimitrophes() {
        return tLimitrophes;
    }

    public void settLimitrophes(List<String> tLimitrophes) {
        this.tLimitrophes = tLimitrophes;
    }

    public int getNb_unites_stationnees() {
        return nb_unites_stationnees;
    }

    public void setNb_unites_stationnees(int nb_unites_stationnees) {
        this.nb_unites_stationnees = nb_unites_stationnees;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public Joueur getControleur() {
        return controleur;
    }

    public void setControleur(Joueur controleur) {
        this.controleur = controleur;
    }

}
