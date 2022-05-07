package fr.istic.androidrisk.moteur;

import java.io.Serializable;

import javax.persistence.Id;

/**
 * Classe décrivant l'ensemble des données et des actions liées au joueur
 */
public class Joueur implements Serializable {

    private static final long serialVersionUID = -8446560533850928558L;

    @Id private Long id;
    private String pseudo;
    private int cartesArtillerie;
    private int cartesInfanterie;
    private int cartesCavalerie;
    private int renforts;

    public Joueur() {
    }

    /**
     * Constructeur du joueur, prend en paramètre un nom de joueur
     * @param nom
     */
    public Joueur(String pseudo) {
        this.pseudo = pseudo;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter du pseudo du joueur
     * @return le pseudo du joueur
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Setter du pseudo du joueur
     * @param pseudo : le pseudo du joueur
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getRenforts() {
        return renforts;
    }

    public void setRenforts(int renforts) {
        this.renforts = renforts;
    }

}
