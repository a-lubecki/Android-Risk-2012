package fr.istic.androidrisk.moteur;

import android.util.Log;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.ArrayList;
import java.util.Date;

/**
 *	Classe représentant une partie de jeu
 */
public class Play implements Serializable {

    private static final long serialVersionUID = 3287620120150771336L;

    /**
     * Constantes de jeu
     */
    public static final int MAX_DES_ATTAQUANT = 3;
    public static final int MAX_DES_DEFENSEUR = 2;
    public final static int ARTILLERIE = 2;
    public final static int INFANTERIE = 0;
    public final static int CAVALERIE = 1;

    public static final Map<Integer, Integer> NB_UNITES_PAR_JOUEUR = new HashMap<Integer, Integer>() {

        private static final long serialVersionUID = 42;

        {
            put(new Integer(2), new Integer(100));
            put(new Integer(3), new Integer(87));
            put(new Integer(4), new Integer(75));
            put(new Integer(5), new Integer(63));
            put(new Integer(6), new Integer(50));
        }
    };

    public static final int PHASE_RENFORT = 0;
    public static final int PHASE_ATTAQUE = 1;
    public static final int PHASE_MOUVEMENT = 2;

    /**
     * Variables globales de la partie
     */
    private Map<Integer, Region> monde = new HashMap<Integer, Region>();
    private Map<String, Territory> territoires = new HashMap<String, Territory>();
    private List<Joueur> joueurs = new ArrayList<Joueur>();
    private String nom;
    private int index_joueur_courant;
    private int compteTour;
    private List<String> historique = new ArrayList<String>();
    private int phaseEnCours;
    private boolean aConquisTerritoire;

    public Play() {
    }

    public void setPhaseEnCours(int phaseEnCours) {
        this.phaseEnCours = phaseEnCours;
    }

    public static int getJetDeDes() {
        return new Random().nextInt(6) + 1;
    }

    public static int getNewCard() {
        return new Random().nextInt(3);
    }

    public int getCurrentPlayerIndex() {
        return index_joueur_courant;
    }

    public void setIndex_joueur_courant(int index_joueur_courant) {
        this.index_joueur_courant = index_joueur_courant;
    }

    public void setTerritoires(Map<String, Territory> territoires) {
        this.territoires = territoires;
    }

    /**
     * Getter du monde (ie : la liste des régions)
     * @return monde : la  liste des régions composants le monde.
     */
    public Map<Integer, Region> getMonde() {
        return monde;
    }

    /**
     * Setter du monde (ie : la liste des régions)
     * @param monde : une liste des régions composants le monde.
     */
    public void setMonde(Map<Integer, Region> monde) {
        this.monde = monde;
    }

    /**
     * Getter de la liste des territoires du monde
     * @return territoires : la List des ITerritoire du monde.
     */
    public Map<String, Territory> getTerritories() {
        return territoires;
    }

    /**
     * Getter de la liste des joueurs de la partie
     * @return joueurs : la liste des joueurs de la partie.
     */
    public List<Joueur> getPlayers() {
        return joueurs;
    }

    /**
     * Setter de la liste des joueurs de la partie
     * @param joueurs : une liste de joueurs
     */
    public void setJoueurs(List<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    /**
     * Getter du nom de la partie
     * @return nom : le nom de la partie
     */
    public String getNom() {
        return nom;
    }

    /**
     * Setter du nom de la partie
     * @param nom : le nom voulu
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Getter du numéro du tour
     * @return le numéro du tour.
     */
    public int getTurnCounter() {
        return compteTour;
    }

    /**
     * Setter du numéro du tour
     * @param compteTour : le tour où l'on veut placer la partie.
     */
    public void setCompteTour(int compteTour) {
        this.compteTour = compteTour;
    }

    /**
     * Méthode permettant de récupérer l'historique de la partie.
     * @return une liste de String représentant l'historique de la partie.
     */
    public List<String> getHistory() {
        return historique;
    }

    /**
     * Méthode permettant de set l'historique de la partie.
     * @param historique
     */
    public void setHistorique(List<String> historique) {
        this.historique = historique;
    }

    /**
     * Méthode permettant d'ajouter un élément à l'historique
     * @param string : String à ajouter à la liste.
     */
    public void addHistory(String string) {
        Date date = new Date();
        String sDate = "" + date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900) + " "
                + date.getHours() + ":" + date.getMinutes() + " - ";
        historique.add(sDate + string);
    }

    public boolean hasObtainedATerritory() {
        return aConquisTerritoire;
    }

    public void setHasObtainedATerritory(boolean aConquisTerritoire) {
        this.aConquisTerritoire = aConquisTerritoire;
    }

    /**
     * Méthode utilisée par le joueur pour signaler qu'il a fini son tour.
     * Déclence un changement du joueur actuel et changement du tour si jamais le joueur qui vient de jouer est le dernier de la liste
     */
    public void terminerTourJoueur() {
        index_joueur_courant = (index_joueur_courant + 1) % joueurs.size();
        if (index_joueur_courant == 0) {
            setCompteTour(getTurnCounter() + 1);
            addHistory("Tour n°" + getTurnCounter());
        }
        aConquisTerritoire = false;
        setRenforts(getCurrentPlayer());
        addHistory(("A " + getCurrentPlayer().getPseudo() + " de jouer."));
        addHistory(getCurrentPlayer().getReinforcements() + " renforts disponibles.");
    }

    public boolean isTourJoueur(String pseudoJoueur) {
        if (getCurrentPlayer().getPseudo().equals(pseudoJoueur)) {
            return true;
        }
        return false;
    }

    public Joueur getCurrentPlayer() {
        return joueurs.get(index_joueur_courant);
    }

    /**
     * Méthode donnant les renforts au joueur en fonction du nombre de cartes défaussées.
     * @param combinaison : les trois cartes données pour obtenir des renforts.
     * @return true si la combinaison donne bien des renforts, false sinon.
     */
    public boolean obtainsNonPeriodicReinforcements(int c0, int c1, int c2) {
        if (this.getCurrentPhase() == Play.PHASE_RENFORT) {
            if (c0 == c1 && c1 == c2) {
                if (c0 == 0) {
                    getCurrentPlayer().ajouterRenforts(3);
                } else if (c0 == 1) {
                    getCurrentPlayer().ajouterRenforts(5);
                } else {
                    getCurrentPlayer().ajouterRenforts(8);
                }
                getCurrentPlayer().retirerCarte(c0);
                getCurrentPlayer().retirerCarte(c1);
                getCurrentPlayer().retirerCarte(c2);
                return true;
            } else if (c0 != c1 && c1 != c2 && c2 != c0) {
                getCurrentPlayer().ajouterRenforts(10);
                getCurrentPlayer().retirerCarte(c0);
                getCurrentPlayer().retirerCarte(c1);
                getCurrentPlayer().retirerCarte(c2);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public void changePhase() {
        if (phaseEnCours == PHASE_MOUVEMENT) {
            terminerTourJoueur();
        }
        phaseEnCours = (phaseEnCours + 1) % 3;
    }

    /**
     * Méthode vérifiant si la partie est terminée.
     * @return booléen : true si la partie est terminée, false sinon.
     */
    public boolean finPartie() {
        Joueur gagnant = monde.values().iterator().next().isRegionControlee();
        if (gagnant != null) {
            for (Region region : monde.values()) {
                if (region.isRegionControlee() != gagnant) {
                    return false;
                }
            }
        }
        addHistory(("Victoire de " + gagnant.getPseudo() + " !!!"));
        return true;
    }

    public int getCurrentPhase() {
        return phaseEnCours;
    }

    public int getNbUnitesTotal(Joueur joueur) {
        int valret = 0;
        for (Territory territoire : territoires.values()) {
            if (territoire.getControler() == joueur) {
                valret += territoire.getNbStayingUnits();
            }
        }
        return valret;
    }

    public void setRenforts(Joueur joueur) {
        joueur.setRenforts(getTerritories(joueur).size() / 3);
    }

    public HashMap<String, Territory> getTerritories(Joueur joueur) {
        HashMap<String, Territory> t = new HashMap<String, Territory>();
        for (Territory territoire : territoires.values()) {
            if (territoire.getControler() == joueur) {
                t.put(territoire.getIdentifiant(), territoire);
            }
        }
        return t;
    }
    
}
