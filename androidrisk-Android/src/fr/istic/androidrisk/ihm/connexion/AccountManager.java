package fr.istic.androidrisk.ihm.connexion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fr.istic.androidrisk.ORMActivity;
import fr.istic.androidrisk.client.MyRequestFactory;
import fr.istic.androidrisk.client.Util;
import fr.istic.androidrisk.client.MyRequestFactory.MyRequest;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import fr.istic.androidrisk.ihm.utils.GestionMessagesErreurs;
import fr.istic.androidrisk.ihm.utils.GestionMessagesErreurs.TypeChamp;

import java.util.ArrayList;
import java.util.List;

public final class AccountManager {

    public static final int NB_CAR_MIN_ID = 4;
    public static final int NB_CAR_MIN_PASS = 6;
    public static final String NOM_FICHIER_PREFERENCES = "compte_orm.xml";
    public static final boolean TEST = true;

    private static volatile AccountManager instance = null;

    public final static AccountManager getInstance() {
        if (AccountManager.instance == null) {
            synchronized (AccountManager.class) {
                if (AccountManager.instance == null) {
                    AccountManager.instance = new AccountManager();
                }
            }
        }
        return AccountManager.instance;
    }

    private String id;
    private String pass;
    private boolean isLogged;

    private AccountManager() {
        id = "";
        pass = "";
        isLogged = false;
    }

    public String getId() throws GestionCompteException {
        if (!isLogged) {
            throw new GestionCompteException(
                    GestionMessagesErreurs.comptePasAuthentifie());
        }
        return id;
    }

    public String getPass() throws GestionCompteException {
        if (!isLogged) {
            throw new GestionCompteException(
                    GestionMessagesErreurs.comptePasAuthentifie());
        }
        return pass;
    }

    public void testFichierCompteValideSurTel(final ORMActivity ormActivity) {
        isLogged = false;

        if (TEST) {
            ormActivity.afficherVueGestionCompte();
            return;
        }

        try {
            String i = getIdFichier(ormActivity);
            String p = getPassFichier(ormActivity);

            if (i.equals("") || p.equals("")) {
                ormActivity.afficherVueGestionCompte();
            } else {
                testSiConnexionValide(i, p, ormActivity);
            }

        } catch (GestionCompteException e) {
            DialogManager.showActivityEndError(ormActivity, e.getMessage());
            return;
        }
    }

    private void enregistrerDonneesDansFichier(Activity activity, String id,
            String pass) throws GestionCompteException {

        SharedPreferences prefs = activity.getSharedPreferences(
                NOM_FICHIER_PREFERENCES, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString("id", id);
        prefsEditor.putString("pass", pass);
        prefsEditor.commit();
    }

    public String getIdFichier(Activity activity) throws GestionCompteException {
        SharedPreferences prefs = activity.getSharedPreferences(
                NOM_FICHIER_PREFERENCES, Context.MODE_WORLD_READABLE);
        return prefs.getString("id", "");
    }

    private String getPassFichier(Activity activity)
            throws GestionCompteException {
        SharedPreferences prefs = activity.getSharedPreferences(
                NOM_FICHIER_PREFERENCES, Context.MODE_WORLD_READABLE);
        return prefs.getString("pass", "");
    }

    private void testSiConnexionValide(final String id, final String pass,
            final ORMActivity ormActivity) {

        new AsyncTask<Void, Void, Void>() {

            private boolean isCompleteSuccess = false;
            private ProgressDialog progressDialog;
            private String dialogMessage = "";

            protected void onPreExecute() {
                try {
                    progressDialog = DialogManager.showProgressDialog(
                            ormActivity, "Authentification...");
                } catch (Exception e) {
                }
            }

            protected void onPostExecute(Void arg0) {

                progressDialog.dismiss();
                if (isCompleteSuccess) {
                    AccountManager.this.id = id;
                    AccountManager.this.pass = pass;
                    isLogged = true;
                    ormActivity.afficherVuesGestionParties();
                    return;
                } else if (!dialogMessage.equals("")) {
                    try {
                        DialogManager.showErrorDialog(ormActivity,
                                dialogMessage);
                    } catch (Exception e) {
                    }
                }
                ormActivity.afficherVueGestionCompte();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(
                        ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.connection(id, pass).fire(new Receiver<Integer>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(Integer result) {
                        isCompleteSuccess = (result == 0);
                    }
                });

                return null;
            }
        }.execute();
    }

    public void enregistrerCompte(final String id, final String pass,
            final ORMActivity ormActivity) throws GestionCompteException,
            Exception {
        isLogged = false;
        try {
            verifierDonnees(id, pass);
        } catch (GestionCompteException ex) {
            DialogManager.showErrorDialog(ormActivity, ex.getMessage());
            return;
        }

        new AsyncTask<Void, Void, String>() {

            private boolean isCompleteSuccess = false;
            private String dialogMessage = "";
            private ProgressDialog progressDialog;

            protected void onPreExecute() {
                ormActivity.activerInterfaceGestionCompte(false);
                try {
                    progressDialog = DialogManager.afficherProgressDialog(
                            ormActivity, "Connexion...",
                            new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    cancel(true);// cancel de la AsyncTask
                                    ormActivity.activerInterfaceGestionCompte(true);
                                }
                            });
                } catch (Exception e) {
                }
            }

            protected void onPostExecute(String message) {
                progressDialog.dismiss();

                if (isCompleteSuccess) {
                    try {
                        enregistrerDonneesDansFichier(ormActivity, id, pass);
                    } catch (GestionCompteException e1) {
                        DialogManager.showActivityEndError(ormActivity,
                                e1.getMessage());
                        return;
                    }
                    AccountManager.this.id = id;
                    AccountManager.this.pass = pass;
                    isLogged = true;

                    ormActivity.afficherVuesGestionParties();

                } else {
                    try {
                        DialogManager.showErrorDialog(ormActivity, message);
                    } catch (Exception e) {
                    }
                    ormActivity.activerInterfaceGestionCompte(true);
                }
            }

            @Override
            protected String doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(
                        ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.connection(id, pass).fire(new Receiver<Integer>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(Integer result) {// return 0 si ok, 1
                        // si le compte
                        // n'existe pas, 2
                        // si le mdp est
                        // incorrect
                        switch (result) {
                            case 0:
                                isCompleteSuccess = true;
                                break;
                            case 1:
                                dialogMessage = GestionMessagesErreurs.compteIdNonTrouve(id);
                                break;
                            case 2:
                                dialogMessage = GestionMessagesErreurs.comptePassErrone();
                                break;
                        }
                    }
                });

                return dialogMessage;
            }
        }.execute();
    }

    public void creerCompte(final String id, final String pass,
            final ORMActivity ormActivity) throws GestionCompteException,
            Exception {
        isLogged = false;
        try {
            verifierDonnees(id, pass);
        } catch (GestionCompteException ex) {
            DialogManager.showErrorDialog(ormActivity, ex.getMessage());
            return;
        }

        new AsyncTask<Void, Void, String>() {

            private boolean isCompleteSuccess = false;
            private String dialogMessage = "";
            private ProgressDialog progressDialog;

            protected void onPreExecute() {
                ormActivity.activerInterfaceGestionCompte(false);
                try {
                    progressDialog = DialogManager.afficherProgressDialog(
                            ormActivity, "Connexion...",
                            new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    cancel(true);// cancel de la AsyncTask
                                    ormActivity.activerInterfaceGestionCompte(true);
                                }
                            });
                } catch (Exception e) {
                }
            }

            protected void onPostExecute(String message) {
                progressDialog.dismiss();

                if (isCompleteSuccess) {

                    try {
                        enregistrerDonneesDansFichier(ormActivity, id, pass);
                    } catch (GestionCompteException e1) {
                        DialogManager.showActivityEndError(ormActivity,
                                e1.getMessage());
                        return;
                    }

                    AccountManager.this.id = id;
                    AccountManager.this.pass = pass;
                    isLogged = true;

                    try {
                        DialogManager.showOKDialog(ormActivity,
                                "Création effectuée", message,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        ormActivity.afficherVuesGestionParties();
                                    }
                                });
                    } catch (Exception e) {
                    }

                } else {
                    try {
                        DialogManager.showErrorDialog(ormActivity, message);
                    } catch (Exception e) {
                    }
                    ormActivity.activerInterfaceGestionCompte(true);
                }
            }

            @Override
            protected String doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(
                        ormActivity, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.creerCompte(id, pass).fire(new Receiver<Boolean>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            isCompleteSuccess = true;
                            dialogMessage = "Le compte " + id
                                    + " a été synchronisé.";
                        } else {
                            dialogMessage = GestionMessagesErreurs.compteIdDejaExistant(id);
                        }
                    }
                });

                return dialogMessage;
            }
        }.execute();

    }

    private void verifierDonnees(String id, String pass)
            throws GestionCompteException {
        if (id == null || id.equals("")) {
            throw new GestionCompteException(
                    GestionMessagesErreurs.champVide(TypeChamp.LOGIN));
        } else if (id.length() < NB_CAR_MIN_ID) {
            throw new GestionCompteException(
                    GestionMessagesErreurs.champPasAssezDeCaracteres(
                    TypeChamp.LOGIN, NB_CAR_MIN_ID));
        } else if (pass == null || pass.equals("")) {
            throw new GestionCompteException(
                    GestionMessagesErreurs.champVide(TypeChamp.PASS));
        } else if (pass.length() < NB_CAR_MIN_PASS) {
            throw new GestionCompteException(
                    GestionMessagesErreurs.champPasAssezDeCaracteres(
                    TypeChamp.PASS, NB_CAR_MIN_PASS));
        }
    }
}
