package fr.istic.androidrisk.moteur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import fr.istic.androidrisk.exception.*;

/**
 * Classe représentant un territoire et les unités stationnées dessus.
 */
public class Territory implements Serializable {

    private static final long serialVersionUID = -9134724774181313360L;

    private Long id;
    private List<String> tLimitrophes = new ArrayList<String>();
    private int nb_unites_stationnees;
    private String nom;
    //Identifiant du département ie son numéro
    private String identifiant;
    private Joueur controleur;

    /*
     * Constructeurs
     */
    public Territory() {
    }

    public Territory(String nom, String id) {
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

    public int getNbStayingUnits() {
        return nb_unites_stationnees;
    }

    public void setNb_unites_stationnees(int nb_unites_stationnees) {
        this.nb_unites_stationnees = nb_unites_stationnees;
    }

    public String getName() {
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

    public Joueur getControler() {
        return controleur;
    }

    public void setControleur(Joueur controleur) {
        this.controleur = controleur;
    }

    /**
     * Fonction permettant l'ajour de Territoire limitrophes à un territoire donné.
     */
    public void addTerritoireLimitrophe(String t) {
        tLimitrophes.add(t);
    }

    /**
     * Fonction permettant de placer les unités sur le territoire.
     */
    public void addUnits(int nb) {
        nb_unites_stationnees += nb;
    }

    public void retirerUnites(int nb) {
        nb_unites_stationnees -= nb;
    }

    /**
     * Méthode résolvant les combats entre territoires.
     * @param t : le territoire attaqué.
     * @param nbDesJ1 : le nombre de dés lancés par le joueur attaquant.
     * @param nbDesJ2 : le nombre de dés lancés par le joueur défenseur.
     * @return : Un tableau contenant le nombre d'unité perdues par les deux joueurs.
     */
    private int[] resoudreCombat(Territory t, int nbDesJ1, int nbDesJ2) {
        int[] jetsJ1 = new int[nbDesJ1];
        int[] jetsJ2 = new int[nbDesJ2];
        int[] pertes = new int[2];

        for (int i = 0; i < nbDesJ1; i++) {
            jetsJ1[i] = Play.getJetDeDes();
        }
        for (int i = 0; i < nbDesJ2; i++) {
            jetsJ2[i] = Play.getJetDeDes();
        }

        Arrays.sort(jetsJ1);
        jetsJ1 = inverser(jetsJ1);
        Arrays.sort(jetsJ2);
        jetsJ2 = inverser(jetsJ2);

        for (int j = 0; j < (Math.min(nbDesJ2, nbDesJ1)); j++) {
            if (jetsJ1[j] != jetsJ2[j] && Math.max(jetsJ1[j], jetsJ2[j]) == jetsJ1[j]) {
                pertes[1]++;
            } else {
                pertes[0]++;
            }
        }

        System.out.println("Résultat du combat :\nPertes de J1 : " + pertes[0] + "\nPertes de J2 : " + pertes[1]);
        return pertes;
    }

    private int[] inverser(int[] tab) {
        int[] res = new int[tab.length];

        for (int i = 0; i < tab.length; i++) {
            res[i] = tab[tab.length - i - 1];
        }

        return res;
    }

    /**
     * Méthode permettant d'attaquer un territoire depuis le territoire courant.
     * @throws Exception : Exception levée en cas d'utilisation non-autorisée de cette fonction.
     */
    public String attackTerritory(Territory t, int nbDesAttaquant) throws AlreadyControledAttackedTerritoryException, NotEnoughUnitsException, NonBoundaryAttackedTerritoryException {
        int nbDesJ1 = nbDesAttaquant;
        if (nbDesAttaquant <= 0) {
            nbDesAttaquant = 1;
        } else if (nbDesAttaquant > 3) {
            nbDesAttaquant = 3;
        }

        String retour = "";

        int nbDesJ2;
        if (t.getNbStayingUnits() > 1) {
            nbDesJ2 = 2;
        } else {
            nbDesJ2 = 1;
        }

        if (t.getControler().equals(controleur)) {
            System.err.println("Erreur, tentative de capture d'un territoire que vous contrôlez déjà !");
            throw (new AlreadyControledAttackedTerritoryException());
        }

        if (nb_unites_stationnees <= 1) {
            System.err.println("Erreur, tentative d'attaque depuis un territoire ne contenant pas assez d'unités.");
            throw (new NotEnoughUnitsException());
        }

        if (!(tLimitrophes.contains(t.getIdentifiant()))) {
            System.err.println("Erreur, tentative de capture d'un territoire non-limitrophes !");
            throw (new NonBoundaryAttackedTerritoryException());
        }

        if (nbDesJ1 > nb_unites_stationnees) {
            System.err.println("Erreur, vous ne pouvez avoir plus de dés que le nombre d'unités attaquantes.");
            nbDesJ1 = nb_unites_stationnees - 1;
        }
        if (nbDesJ2 > t.getNbStayingUnits()) {
            System.err.println("Erreur, vous ne pouvez avoir plus de dés que le nombre d'unités attaquantes.");
            nbDesJ2 = t.getNbStayingUnits() - 1;
        }

        System.out.println("Début du combat...");
        retour += getName() + " (" + getControler().getPseudo() + ") attaque " + t.getName()
                + " (" + t.getControler().getPseudo() + ").\n";
        int[] res = resoudreCombat(t, nbDesJ1, nbDesJ2);

        t.retirerUnites(res[1]);
        this.retirerUnites(res[0]);

        if (t.getNbStayingUnits() <= 0) {
            retirerUnites(nbDesJ1);
            retour += capturerTerritoire(t, nbDesJ1 - res[0]);
        } else {
            System.out.println("Tentative de " + controleur.getPseudo() + " d'attaquer " + t.getName() + " depuis " + getName() + " avec " + nbDesJ1 + " unités.\nPertes de " + getControler().getPseudo() + " : " + res[0] + "\nPertes de " + t.getControler().getPseudo() + " : " + res[1]);
        }
        if (res[0] > 0) {
            retour += getControler().getPseudo() + " a perdu " + res[0] + " unité" + (res[0] > 1 ? "s" : "") + ".";
        }
         if (res[1] > 0) {
            retour += t.getControler().getPseudo() + " a perdu " + res[1] + " unité" + (res[1] > 1 ? "s" : "") + ".";
        }

        return retour;
    }

    /**
     * Fonction permettant la capture d'un territoire en cas de victoire du joueur contrôleur dans un combat.
     * @param nbDesJ1 : Nombre d'unités attaquantes à placer sur le territoire conquis.
     */
    private String capturerTerritoire(Territory t, int nbDesJ1) {
        t.setControleur(this.getControler());
        t.addUnits(nbDesJ1);
        System.out.println("Territoire : " + t.getName() + " capturé par " + controleur.getPseudo() + " depuis " + getName() + " avec " + nbDesJ1 + " unités !");
        return ( t.getName() + " a été capturé par " + controleur.getPseudo()+".\n");
    }

    /**
     * Fonction permettant de déplacer des unités d'un territoire contrôlé vers un autre territoire contrôlé.
     * @throws NotAuthorizedMoveException : En cas de déplacement non-autorisé.
     */
    public void moveUnits(Territory t, int nb) throws NotAuthorizedMoveException {
        if (t.getControler().equals(controleur) || this.nb_unites_stationnees - nb >= 1) {
            this.retirerUnites(nb);
            t.addUnits(nb);
        } else {
            throw (new NotAuthorizedMoveException());
        }
    }

    public boolean isLimitrophe(String idTerritoire) {
        return tLimitrophes.contains(idTerritoire);
    }

}
