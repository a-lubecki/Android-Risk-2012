package fr.istic.androidrisk.dto;

import com.googlecode.objectify.annotation.Unindexed;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

public class InfosPartieDTO implements Serializable {

    private static final long serialVersionUID = -2585578304957445658L;
    @Id
    private String nom;
    @Unindexed
    private int nbJoueurs;
    @Unindexed
    private int numTour;
    @Unindexed
    private boolean commence;
    @Unindexed
    private List<String> joueurs;
    @Unindexed
    private String joueurCourant;

    public InfosPartieDTO() {
    }

    public InfosPartieDTO(String nom, int nbJoueurs, String compte) {
        this.nom = nom;
        this.nbJoueurs = nbJoueurs;
        this.joueurs = new ArrayList<String>();
        this.joueurs.add(compte);
        this.commence = false;
        this.numTour = 0;
        this.joueurCourant = joueurs.get(0);
    }

    public boolean addJoueur(String joueur) {
        getJoueurs().add(joueur);
        if (getJoueurs().size() == nbJoueurs) {
            commence = true;
            numTour = 1;
        }
        return commence;
    }

    public String getJoueurCourant() {
        return joueurCourant;
    }

    public void setJoueurCourant(String joueurCourant) {
        this.joueurCourant = joueurCourant;
    }

    public int getNumTour() {
        return numTour;
    }

    public void setNumTour(int numTour) {
        this.numTour = numTour;
    }

    public List<String> getJoueurs() {
        if (joueurs == null) {
            joueurs = new ArrayList<String>();
        }
        return joueurs;
    }

    public void setJoueurs(List<String> joueurs) {
        this.joueurs = joueurs;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNbJoueurs() {
        return nbJoueurs;
    }

    public void setNbJoueurs(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;
    }

    public void setCommence(boolean commence) {
        this.commence = commence;
    }

    public boolean isCommence() {
        return commence;
    }
    
}
