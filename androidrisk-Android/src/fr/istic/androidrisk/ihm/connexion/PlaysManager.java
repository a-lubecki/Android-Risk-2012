package fr.istic.androidrisk.ihm.connexion;

import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fr.istic.androidrisk.ORMActivity;
import fr.istic.androidrisk.client.MyRequestFactory;
import fr.istic.androidrisk.client.Util;
import fr.istic.androidrisk.client.MyRequestFactory.MyRequest;
import fr.istic.androidrisk.dto.InfosPartieDTO;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import fr.istic.androidrisk.ihm.utils.GestionMessagesErreurs;
import fr.istic.androidrisk.ihm.utils.GestionMessagesErreurs.TypeChamp;
import fr.istic.androidrisk.ihm.utils.GestionMessagesErreurs.TypeListe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;

public final class PlaysManager {

    public static final int NOMBRE_JOUEURS_ORM_MAX = 6;
    private static final int NB_CAR_MIN_NOM_PARTIE = 4;
    public static final int NOMBRE_PARTIES_MAX = 10;

    private static volatile PlaysManager instance = null;
    
    public final static PlaysManager getInstance() {
        if (PlaysManager.instance == null) {
            synchronized (PlaysManager.class) {
                if (PlaysManager.instance == null) {
                    PlaysManager.instance = new PlaysManager();
                }
            }
        }
        return PlaysManager.instance;
    }

    private String nomPartieChoisie;
    private List<InfosPartieDTO> mesPartiesCommencees;
    private List<InfosPartieDTO> mesPartiesEnAttente;
    private List<InfosPartieDTO> partiesServeurEnAttente;

    private PlaysManager() {
        nomPartieChoisie = "";
        mesPartiesCommencees = new ArrayList<InfosPartieDTO>();
        mesPartiesEnAttente = new ArrayList<InfosPartieDTO>();
        partiesServeurEnAttente = new ArrayList<InfosPartieDTO>();
    }

    public List<InfosPartieDTO> getMesPartiesCommencees() {
        return mesPartiesCommencees;
    }

    public List<InfosPartieDTO> getMesPartiesEnAttente() {
        return mesPartiesEnAttente;
    }

    public List<InfosPartieDTO> getPartiesServeurEnAttente() {
        return partiesServeurEnAttente;
    }

    private String getId() throws GestionCompteException {
        return AccountManager.getInstance().getId();
    }

    private String getPass() throws GestionCompteException {
        return AccountManager.getInstance().getPass();
    }

    public void testSiAuMoinsUnePartieCommencee(final ORMActivity ormActivity) {

        ormActivity.afficherSplashScreen();
        try {
            obtenirMesParties(ormActivity, getId(), false);

        } catch (GestionCompteException e) {
            DialogManager.showActivityEndError(ormActivity, e.getMessage());
            return;
        }
    }

    public void obtenirMesParties(final ORMActivity ormActivity, final String id, final boolean cancelable) {

        new AsyncTask<Void, String, List<InfosPartieDTO>>() {

            private ProgressDialog progressDialog;
            private String dialogMessage = "";

            protected void onPreExecute() {
                try {
                	if(!cancelable){
                		progressDialog = DialogManager.showProgressDialog(ormActivity, "Récupération des parties utilisateur...");
                	} else {
                		progressDialog = DialogManager.afficherProgressDialog(ormActivity, "Récupération des parties utilisateur...",
                				new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										cancel(true);
									}
								});
                    }

                } catch (Exception e) {
                    return;
                }
            }

            protected void onPostExecute(List<InfosPartieDTO> parties) {
                progressDialog.dismiss();
                if (!dialogMessage.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity, dialogMessage);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }


                if (!mesPartiesCommencees.isEmpty()) {
                    mesPartiesCommencees.clear();
                }
                if (!mesPartiesEnAttente.isEmpty()) {
                    mesPartiesEnAttente.clear();
                }

                for (InfosPartieDTO partie : parties) {

                    if (partie.isCommence()) {
                        mesPartiesCommencees.add(partie);
                    } else {
                        mesPartiesEnAttente.add(partie);
                    }
                }

                if (mesPartiesCommencees.isEmpty()) {
                    ormActivity.afficherVueGestionPartiesEnAttente();
                } else {
                    ormActivity.afficherVueGestionPartiesCommencees();
                }
            }

            @Override
            protected List<InfosPartieDTO> doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();
                final List<InfosPartieDTO> parties = new ArrayList<InfosPartieDTO>();

                request.listePartiesCompte(id).fire(new Receiver<String>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        XStream xstream = new XStream(new DomDriver());
                        parties.addAll((List<InfosPartieDTO>) xstream.fromXML(arg0));
                    }
                });

                return parties;
            }

        }.execute();
    }

    public void creerNouvellePartie(final ORMActivity ormActivity, final String nomPartie, final int nbJoueursMax) throws GestionPartiesException, GestionCompteException {
        verifierDonnees(nomPartie);

        if (mesPartiesEnAttente.size() + mesPartiesCommencees.size() >= NOMBRE_PARTIES_MAX) {
            throw new GestionPartiesException(GestionMessagesErreurs.partieTropDeParticipation(NOMBRE_PARTIES_MAX));
        }

        if (nomPartie.length() < NB_CAR_MIN_NOM_PARTIE) {
            throw new GestionPartiesException(GestionMessagesErreurs.champPasAssezDeCaracteres(TypeChamp.NOUVELLE_PARTIE, NB_CAR_MIN_NOM_PARTIE));
        }
        if (nbJoueursMax <= 1) {
            throw new GestionPartiesException(GestionMessagesErreurs.partiePasAssezDeJoueursSouhaites(2));
        }
        if (nbJoueursMax > NOMBRE_JOUEURS_ORM_MAX) {
            throw new GestionPartiesException(GestionMessagesErreurs.partieTropDeJoueursSouhaites(NOMBRE_JOUEURS_ORM_MAX));
        }

        final String id = getId();
        final String pass = getPass();

        new AsyncTask<Void, String, Void>() {

            private ProgressDialog progressDialog;
            private String errorCompte = "";
            private String errorPartie = "";

            protected void onPreExecute() {
                try {
                    progressDialog = DialogManager.showProgressDialog(ormActivity, "Création en cours...");
                } catch (Exception e) {
                    return;
                }
            }

            protected void onPostExecute(Void v) {
                progressDialog.dismiss();
                if (!errorCompte.equals("")) {
                    DialogManager.showActivityEndError(ormActivity, errorCompte);
                    return;
                }
                if (!errorPartie.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity, errorPartie);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
                mesPartiesEnAttente.add(new InfosPartieDTO(nomPartie, nbJoueursMax, id));
                actualiserMesPartiesEnAttente(ormActivity);
                ormActivity.activerInterfacePartiesEnAttente();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.creerPartie(id, nomPartie, nbJoueursMax).fire(new Receiver<Integer>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        errorCompte = error.getMessage();
                    }

                    // return 0 si ok, 1 si le compte n'existe pas, 2 si le mdp est incorrect,
                    // 3 si le joueur à déjà atteint son nombre max de parties,
                    // 4 si la partie existe deja
                    @Override
                    public void onSuccess(Integer res) {
                        switch (res) {
                            case 1:
                                errorPartie = GestionMessagesErreurs.partieTropDeParticipation(NOMBRE_PARTIES_MAX);
                                break;
                            case 2:
                                errorPartie = GestionMessagesErreurs.partieDejaExistante(nomPartie);
                                break;
                        }
                    }
                });

                return null;
            }

        }.execute();
    }

    public void rejoindrePartieEnAttente(final ORMActivity ormActivity, final String nomPartie) throws GestionPartiesException, GestionCompteException {
        verifierDonnees(nomPartie);
        if (!listeContains(partiesServeurEnAttente, nomPartie)) {
            throw new GestionPartiesException(GestionMessagesErreurs.partiePasDansListe(TypeListe.PARTIES_SERVEUR_EN_ATTENTE, nomPartie));
        }

        final String id = getId();
        final String pass = getPass();

        new AsyncTask<Void, String, Void>() {

            private ProgressDialog progressDialog;
            private String errorCompte = "";
            private String errorPartie = "";

            protected void onPreExecute() {
                try {
                    progressDialog = DialogManager.showProgressDialog(ormActivity, "Sauvegarde...");
                } catch (Exception e) {
                    return;
                }
            }

            protected void onPostExecute(Void v) {
                progressDialog.dismiss();
                if (!errorCompte.equals("")) {
                    DialogManager.showActivityEndError(ormActivity, errorCompte);
                    return;
                }
                if (!errorPartie.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity, errorPartie);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
                InfosPartieDTO partie = listeRemove(partiesServeurEnAttente, nomPartie);
                if (partie.addJoueur(id)) {
                    mesPartiesCommencees.add(partie);
                    ormActivity.actualiserTextNbPartiesEnAttenteClient();
                    ormActivity.actualiserButtonNbPartiesCommencees();
                } else {
                    mesPartiesEnAttente.add(partie);
                    actualiserMesPartiesEnAttente(ormActivity);
                }

                actualiserPartiesServeurEnAttenteSansChargement(ormActivity);
                ormActivity.activerInterfacePartiesEnAttente();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.rejoindrePartie(id, nomPartie).fire(new Receiver<Integer>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        errorCompte = error.getMessage();
                    }

                    // return 0 si ok, 1 si le compte n'existe pas, 2 si le mdp est incorrect,
                    // 3 si le compte à déjà atteind son nombre maximal de parties
                    // 4 si l'utilisateur est deja present dans la partie,
                    // 5 si la partie est deja commence
                    @Override
                    public void onSuccess(Integer res) {
                        switch (res) {
                            case 1:
                                errorPartie = GestionMessagesErreurs.partieTropDeParticipation(NOMBRE_PARTIES_MAX);
                                break;
                            case 2:
                                errorPartie = GestionMessagesErreurs.partieUtilisateurDejaParticipant();
                                break;
                            case 3:
                                errorPartie = GestionMessagesErreurs.partieDejaCommencee(nomPartie);
                                break;
                        }
                    }
                });

                return null;
            }

        }.execute();
    }

    public void quitterPartieEnAttente(final ORMActivity ormActivity, final String nomPartie) throws GestionPartiesException, GestionCompteException {
        verifierDonnees(nomPartie);
        if (!listeContains(mesPartiesEnAttente, nomPartie)) {
            throw new GestionPartiesException(GestionMessagesErreurs.partiePasDansListe(TypeListe.MES_PARTIES_EN_ATTENTE, nomPartie));
        }

        final String id = getId();
        final String pass = getPass();

        new AsyncTask<Void, String, Void>() {

            private ProgressDialog progressDialog;
            private String errorCompte = "";
            private String errorPartie = "";

            protected void onPreExecute() {
                try {
                    progressDialog = DialogManager.showProgressDialog(ormActivity, "Sauvegarde...");
                } catch (Exception e) {
                    return;
                }
            }

            protected void onPostExecute(Void v) {
                progressDialog.dismiss();
                if (!errorCompte.equals("")) {
                    DialogManager.showActivityEndError(ormActivity, errorCompte);
                    return;
                }
                if (!errorPartie.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity, errorPartie);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
                InfosPartieDTO partie = listeRemove(mesPartiesEnAttente, nomPartie);
                if (partie.getJoueurs().size() > 1) {
                    partiesServeurEnAttente.add(partie);
                    actualiserPartiesServeurEnAttenteSansChargement(ormActivity);
                }

                actualiserMesPartiesEnAttente(ormActivity);

                ormActivity.activerInterfacePartiesEnAttente();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.abandonnerPartie(id, nomPartie).fire(new Receiver<Integer>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        errorCompte = error.getMessage();
                    }

                    // return 0 si ok, 1 si le compte n'existe pas, 2 si le mdp est incorrect,
                    // 3 si la partie n'existe pas, 4 si le joueur n'existe pas dans la partie
                    @Override
                    public void onSuccess(Integer res) {
                        switch (res) {
                            case 1:
                                errorPartie = GestionMessagesErreurs.partieNonExistante(nomPartie);
                                break;
                            case 2:
                                errorPartie = GestionMessagesErreurs.partiePasDansListe(TypeListe.MES_PARTIES_EN_ATTENTE, nomPartie);
                                break;
                        }
                    }
                });

                return null;
            }
        }.execute();


    }

    public void declarerForfaitPartieCommencee(final ORMActivity ormActivity, final String nomPartie) throws GestionPartiesException, GestionCompteException {
        verifierDonnees(nomPartie);
        if (!listeContains(mesPartiesCommencees, nomPartie)) {
            throw new GestionPartiesException(GestionMessagesErreurs.partiePasDansListe(TypeListe.MES_PARTIES_COMMENCEES, nomPartie));
        }

        final String id = getId();
        final String pass = getPass();

        new AsyncTask<Void, String, Void>() {

            private ProgressDialog progressDialog;
            private String errorCompte = "";
            private String errorPartie = "";

            protected void onPreExecute() {

                try {
                    progressDialog = DialogManager.showProgressDialog(ormActivity, "Sauvegarde...");
                } catch (Exception e) {
                    return;
                }
            }

            protected void onPostExecute(Void v) {
                progressDialog.dismiss();
                if (!errorCompte.equals("")) {
                    DialogManager.showActivityEndError(ormActivity, errorCompte);
                    return;
                }
                if (!errorPartie.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity, errorPartie);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
                listeRemove(mesPartiesCommencees, nomPartie);

                if (mesPartiesCommencees.size() == 0) {
                    ormActivity.afficherVueGestionPartiesEnAttente();
                } else {
                    actualiserMesPartiesCommencees(ormActivity);
                    ormActivity.activerInterfacePartiesCommencees();
                }

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.abandonnerPartie(id, nomPartie).fire(new Receiver<Integer>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        errorCompte = error.getMessage();
                    }

                    // return 0 si ok, 1 si le compte n'existe pas, 2 si le mdp est incorrect,
                    // 3 si la partie n'existe pas, 4 si le joueur n'existe pas dans la partie
                    @Override
                    public void onSuccess(Integer res) {
                        switch (res) {
                            case 1:
                                errorPartie = GestionMessagesErreurs.partieNonExistante(nomPartie);
                                break;
                            case 2:
                                errorPartie = GestionMessagesErreurs.partiePasDansListe(TypeListe.MES_PARTIES_COMMENCEES, nomPartie);
                                break;
                        }
                    }
                });

                return null;
            }

        }.execute();
    }

    public void poursuivrePartieCommencee(ORMActivity ormActivity, String nomPartie) throws GestionPartiesException {
        verifierDonnees(nomPartie);
        if (!listeContains(mesPartiesCommencees, nomPartie)) {
            throw new GestionPartiesException(GestionMessagesErreurs.partiePasDansListe(TypeListe.MES_PARTIES_COMMENCEES, nomPartie));
        }
        this.nomPartieChoisie = nomPartie;

        ormActivity.lancerActivityJeu();

    }

    public String getChosenPlayName() {
        return nomPartieChoisie;
    }

    private static void verifierDonnees(String nomPartie) throws GestionPartiesException {
        if (nomPartie == null || nomPartie.equals("")) {
            throw new GestionPartiesException(GestionMessagesErreurs.champVide(TypeChamp.PARTIE_EXISTANTE));
        }
    }

    private static boolean listeContains(List<InfosPartieDTO> listeParties, String nomPartie) {
        for (InfosPartieDTO p : listeParties) {
            if (p.getNom().equals(nomPartie)) {
                return true;
            }
        }
        return false;
    }

    private static InfosPartieDTO listeRemove(List<InfosPartieDTO> listeParties, String nomPartie) {
        for (int i = 0; i < listeParties.size(); i++) {
            if (listeParties.get(i).getNom().equals(nomPartie)) {
                return listeParties.remove(i);
            }
        }
        return null;
    }

    public void actualiserMesPartiesCommencees(ORMActivity ormActivity) {

        ormActivity.remplirListViewCommencees(mesPartiesCommencees);

        ListView lv = ormActivity.getListViewMesPartiesCommencees();
        lv.setItemChecked(0, true);
        lv.setSelectionFromTop(0, 0);

        ormActivity.actualiserTextNbPartiesCommencees();
    }

    public void actualiserMesPartiesEnAttente(ORMActivity ormActivity) {
        ListView lv = ormActivity.getListViewMesPartiesEnAttente();

        int posSelected = lv.getCheckedItemPosition();
        int taille1 = lv.getCount();

        ormActivity.remplirListViewEnAttente(lv, mesPartiesEnAttente);

        int taille2 = lv.getCount();

        if (taille2 > taille1 || posSelected == AdapterView.INVALID_POSITION || taille2 < taille1 && posSelected >= taille2) {//ajouté un élément
            posSelected = taille2 - 1;
        }
        lv.setItemChecked(posSelected, true);
        lv.setSelectionFromTop(posSelected, 0);// scroll auto

        ormActivity.actualiserTextNbPartiesEnAttenteClient();
    }

    public void actualiserPartiesServeurEnAttenteSansChargement(final ORMActivity ormActivity) {
        ListView lv = ormActivity.getListViewPartiesServeurEnAttente();

        int posSelected = lv.getCheckedItemPosition();
        int taille1 = lv.getCount();

        ormActivity.remplirListViewEnAttente(lv, partiesServeurEnAttente);

        int taille2 = lv.getCount();

        if (taille2 > taille1 || posSelected == AdapterView.INVALID_POSITION || taille2 < taille1 && posSelected >= taille2) {//ajouté un élément
            posSelected = taille2 - 1;
        }
        lv.setItemChecked(posSelected, true);
        lv.setSelectionFromTop(posSelected, 0);// scroll auto

        ormActivity.setTextNbPartiesEnAttenteServeur(false);
    }

    public void actualiserPartiesServeurEnAttente(final ORMActivity ormActivity) throws GestionCompteException {

        final String id = getId();

        ormActivity.setTextNbPartiesEnAttenteServeur(true);

        new AsyncTask<Void, String, List<InfosPartieDTO>>() {

            private String dialogMessage = "";

            protected void onPreExecute() {
            }

            protected void onPostExecute(List<InfosPartieDTO> parties) {
                if (!dialogMessage.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity, dialogMessage);
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }

                partiesServeurEnAttente.clear();

                partiesServeurEnAttente.addAll(parties);

                ListView lv = ormActivity.getListViewPartiesServeurEnAttente();
                if (lv == null) {// si on a changé de vue entretemp : pour pas que ça buggue
                    return;
                }

                ormActivity.remplirListViewEnAttente(lv, partiesServeurEnAttente);

                lv.setItemChecked(0, true);
                lv.setSelectionFromTop(0, 0);

                ormActivity.setTextNbPartiesEnAttenteServeur(false);
                ormActivity.activerInterfacePartiesEnAttente();

            }

            @Override
            protected List<InfosPartieDTO> doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();
                final List<InfosPartieDTO> parties = new ArrayList<InfosPartieDTO>();

                request.listePartiesNonCommencees(id).fire(new Receiver<String>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        XStream xstream = new XStream(new DomDriver());
                        List<InfosPartieDTO> tmp = (List<InfosPartieDTO>) xstream.fromXML(arg0);
                        parties.addAll(tmp);
                    }
                });

                return parties;
            }

        }.execute();
    }
}
