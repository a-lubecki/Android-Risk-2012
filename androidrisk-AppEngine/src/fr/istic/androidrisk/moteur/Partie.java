package fr.istic.androidrisk.moteur;

import com.googlecode.objectify.annotation.Serialized;
import fr.istic.androidrisk.dto.MondeDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.istic.androidrisk.server.MyService;
import java.util.Date;
import javax.persistence.Id;

/**
 * Classe représentant une partie de jeu
 */
public final class Partie implements Serializable {

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
    @Serialized private Map<Integer, Region> monde = new HashMap<Integer, Region>();
    @Serialized private Map<String, Territoire> territoires = new HashMap<String, Territoire>();
    @Serialized private List<Joueur> joueurs = new ArrayList<Joueur>();
    @Id private String nom;
    private int compteTour;
    private List<String> historique = new ArrayList<String>();
    private int phaseEnCours;
    private int index_joueur_courant;
    private boolean aConquisTerritoire;

    /**
     * Constructeur d'une partie d'ORM
     */
    @SuppressWarnings("unchecked")
    public Partie(String nompartie, ArrayList<Joueur> joueurs) {
        MondeDTO mondeDTO = MyService.getMonde();
        this.monde = mondeDTO.getRegions();
        this.territoires = mondeDTO.getTerritoires();
        this.joueurs = joueurs;
        nom = nompartie;
        compteTour = 1;
        phaseEnCours = PHASE_RENFORT;
        index_joueur_courant = 0;
        aConquisTerritoire=false;
        distribuerTerritoires();
        placerUnites();
        addHistorique("Début de la partie " + nompartie);
        addHistorique("Tour n°1");
        addHistorique("A " + getJoueurCourant().getPseudo() + " de jouer.");
        setRenforts(getJoueurCourant());
        addHistorique(getJoueurCourant().getRenforts() + " renforts disponibles.");
    }

    public Partie() {
    }

    public boolean isaConquisTerritoire() {
        return aConquisTerritoire;
    }

    public void setaConquisTerritoire(boolean aConquisTerritoire) {
        this.aConquisTerritoire = aConquisTerritoire;
    }

    public static int getJetDeDes() {
        return new Random().nextInt(6) + 1;
    }

    public void setPhaseEnCours(int phaseEnCours) {
        this.phaseEnCours = phaseEnCours;
    }

    public void setTerritoires(Map<String, Territoire> territoires) {
        this.territoires = territoires;
    }

    /**
     * Getter du monde (ie : la liste des régions)
     * @return monde : la  liste des régions composants le monde.
     */
    public Map<Integer, Region> getMonde() {
        return monde;
    }

    public int getIndex_joueur_courant() {
        return index_joueur_courant;
    }

    public void setIndex_joueur_courant(int index_joueur_courant) {
        this.index_joueur_courant = index_joueur_courant;
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
    public Map<String, Territoire> getTerritoires() {
        return territoires;
    }

    /**
     * Getter de la liste des joueurs de la partie
     * @return joueurs : la liste des joueurs de la partie.
     */
    public List<Joueur> getJoueurs() {
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
     * Get le joueur courant
     * @return le joueur courant (IJoueur)
     */
    public final Joueur getJoueurCourant() {
        return joueurs.get(index_joueur_courant);
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
    public int getCompteTour() {
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
    public List<String> getHistorique() {
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
    public void addHistorique(String string) {
        Date date = new Date();
        String sDate = "" + date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900) + " "
                + date.getHours() + ":" + date.getMinutes() + " - ";
        historique.add(sDate + string);
    }

    /**
     * Fonction distribuant les territoire entre tous les joueurs.
     * (Il y a une inégalité si le nombre de territoires ne divise pas exactement
     * le nombre de joueurs. C'est cruel mais c'est la vie.)
     */
    public final void distribuerTerritoires() {
        Random r = new Random();
        List<Territoire> list_ter = new ArrayList<Territoire>();
        list_ter.addAll(territoires.values());

        while (list_ter.size() > 0) {
            for (Joueur joueur : joueurs) {
                int isdgtr = r.nextInt(list_ter.size());
                list_ter.get(isdgtr).setControleur(joueur);
                list_ter.remove(isdgtr);
                if (list_ter.isEmpty()) {
                    break;
                }
            }
        }
    }

    /**
     * Prépare une partie d'ORM
     * @param i : le nombre de joueurs de la partie
     */
    public final void placerUnites() {
        //Prépare le nombre d'unités par joueur
        //Placement automatique d'une unité sur chaque territoire.
        for (Joueur joueur : joueurs) {
            Iterator<Territoire> it = getTerritoires(joueur).values().iterator();
            int nbUnites = NB_UNITES_PAR_JOUEUR.get(joueurs.size());
            while (it.hasNext()) {
                //le territoire courant
                Territoire tCourant = it.next();
                tCourant.setNb_unites_stationnees(2);
                nbUnites -= 2;
            }
            List<Territoire> ter = new ArrayList<Territoire>(getTerritoires(joueur).values());
            while (nbUnites > 0) {
                Territoire territoire = ter.get(new Random().nextInt(ter.size()));
                territoire.setNb_unites_stationnees(territoire.getNb_unites_stationnees() + 1);
                nbUnites--;
            }
        }
    }

    public int getPhaseEnCours() {
        return phaseEnCours;
    }

    public HashMap<String,Territoire> getTerritoires(Joueur joueur) {
        HashMap<String,Territoire> t = new HashMap<String, Territoire>();
        for (Territoire territoire : territoires.values()) {
            if (territoire.getControleur() == joueur) {
                t.put(territoire.getIdentifiant(), territoire);
            }
        }
        return t;
    }

    public void setRenforts(Joueur joueur) {
        joueur.setRenforts(getTerritoires(joueur).size()/3);
    }

}
