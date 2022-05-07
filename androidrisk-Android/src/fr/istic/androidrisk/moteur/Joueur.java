package fr.istic.androidrisk.moteur;

import java.io.Serializable;

/**
 * Classe décrivant l'ensemble des données et des actions liées au joueur
 */
public class Joueur implements Serializable {

    private static final long serialVersionUID = -8446560533850928558L;

    private Long id;
    private String pseudo;
    private int cartesArtillerie;
    private int cartesInfanterie;
    private int cartesCavalerie;
    private int renforts;

    public Joueur() {
    }

    public Joueur(String pseudo) {
        this.pseudo = pseudo;
    }

    public Long getId() {
        return id;
    }

    public void addCard(int carte) {
        switch (carte) {
            case Play.ARTILLERIE:
                cartesArtillerie++;
                break;
            case Play.CAVALERIE:
                cartesCavalerie++;
                break;
            case Play.INFANTERIE:
                cartesInfanterie++;
                break;
        }
    }

    public void retirerCarte(int carte) {
         switch (carte) {
            case Play.ARTILLERIE:
                cartesArtillerie--;
                break;
            case Play.CAVALERIE:
                cartesCavalerie--;
                break;
            case Play.INFANTERIE:
                cartesInfanterie--;
                break;
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRenforts(int renforts) {
        this.renforts = renforts;
    }

    public int getReinforcements() {
        return renforts;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getCartesArtillerie() {
        return cartesArtillerie;
    }

    public void setCartesArtillerie(int cartesArtillerie) {
        this.cartesArtillerie = cartesArtillerie;
    }

    public int getCartesCavalerie() {
        return cartesCavalerie;
    }

    public void setCartesCavalerie(int cartesCavalerie) {
        this.cartesCavalerie = cartesCavalerie;
    }

    public int getCartesInfanterie() {
        return cartesInfanterie;
    }

    public void setCartesInfanterie(int cartesInfanterie) {
        this.cartesInfanterie = cartesInfanterie;
    }

    public void ajouterRenforts(int nb) {
        renforts += nb;
    }

    public void removeReinforcements(int nb) {
        renforts -= nb;
    }

}
