package fr.istic.androidrisk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.istic.androidrisk.dto.InfosPartieDTO;
import fr.istic.androidrisk.ihm.connexion.DialogCreerNouvellePartie;
import fr.istic.androidrisk.ihm.connexion.AccountManager;
import fr.istic.androidrisk.ihm.connexion.GestionCompteException;
import fr.istic.androidrisk.ihm.connexion.PlaysManager;
import fr.istic.androidrisk.ihm.connexion.GestionPartiesException;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ORMActivity extends Activity {

    public static final String TAG = "ORMActivity";
    private static final int COULEUR_FOND = Color.TRANSPARENT;
    private static final int COULEUR_SELECTION = Color.GRAY;
    protected static final int COULEUR_CHECKED = Color.CYAN;
    public static final int CODE_ACTIVITY = 666;

    private boolean isRejoindrePartie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        afficherSplashScreen();

        AccountManager.getInstance().testFichierCompteValideSurTel(this);
        Intent startServiceIntent = new Intent(this, BackgroundService.class);
        startService(startServiceIntent);
    }

    public void afficherSplashScreen() {
        setContentView(R.layout.splashscreen);
    }

    public void afficherVueGestionCompte() {
        setContentView(R.layout.gestion_compte);

        final ORMActivity ormActivity = this;
        ((Button) findViewById(R.id.buttonCreerCompte)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                try {
                    AccountManager.getInstance().creerCompte(getUser(),
                            getPass(), ormActivity);
                } catch (GestionCompteException e) {
                    try {
                        DialogManager.showErrorDialog(ormActivity,
                                e.getMessage());
                    } catch (Exception e1) {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            }
        });
        ((Button) findViewById(R.id.buttonSauthentifier)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                try {
                    AccountManager.getInstance().enregistrerCompte(
                            getUser(), getPass(), ormActivity);
                } catch (GestionCompteException e) {
                    try {
                        DialogManager.showErrorDialog(ormActivity,
                                e.getMessage());
                    } catch (Exception e1) {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            }
        });
    }

    public void afficherVuesGestionParties() {
        PlaysManager.getInstance().testSiAuMoinsUnePartieCommencee(this);
    }

    public void afficherVueGestionPartiesCommencees() {
        // nomPartieSelectionnee = "";

        setContentView(R.layout.gestion_parties_commencees);
        try {
            ((TextView) findViewById(R.id.TextViewBienvenue)).setText("Bienvenue " + AccountManager.getInstance().getId());
        } catch (GestionCompteException ex) {
            afficherVueGestionCompte();
            try {
                DialogManager.showErrorDialog(this, ex.getMessage());
            } catch (Exception e) {
                return;
            }
            return;
        }

        actualiserButtonNbPartiesEnAttenteClient();

        ((Button) findViewById(R.id.buttonPartiesEnAttente)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                afficherVueGestionPartiesEnAttente();
            }
        });
        ((Button) findViewById(R.id.buttonDeclarerForfait)).setOnClickListener(new OnClickListener() {

            @SuppressWarnings("unchecked")
            public void onClick(View v) {

                try {
                    AlertDialog dialog = DialogManager.showOkCancelDialog(
                            ORMActivity.this,
                            "Voulez-vous vraiment déclarer forfait pour cette partie ?",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {

                                    try {

                                        ListView lv = getListViewMesPartiesCommencees();
                                        HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(lv.getCheckedItemPosition());
                                        PlaysManager.getInstance().declarerForfaitPartieCommencee(
                                                ORMActivity.this,
                                                map.get("itemNomPartie"));
                                    } catch (GestionCompteException e) {
                                        DialogManager.showActivityEndError(
                                                ORMActivity.this,
                                                e.getMessage());
                                        return;
                                    } catch (GestionPartiesException ex) {
                                        try {
                                            DialogManager.showErrorDialog(
                                                    ORMActivity.this,
                                                    ex.getMessage());
                                        } catch (Exception e) {
                                            return;
                                        }
                                        return;

                                    }

                                }
                            });
                } catch (Exception e1) {
                    DialogManager.showActivityEndError(
                            ORMActivity.this, e1.getMessage());
                    return;
                }

            }
        });
        ((Button) findViewById(R.id.buttonPoursuivrePartie)).setOnClickListener(new OnClickListener() {

            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                try {
                    ListView lv = getListViewMesPartiesCommencees();
                    HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(lv.getCheckedItemPosition());
                    PlaysManager.getInstance().poursuivrePartieCommencee(
                            ORMActivity.this,
                            map.get("itemNomPartie"));
                } catch (GestionPartiesException ex) {
                    try {
                        DialogManager.showErrorDialog(
                                ORMActivity.this, ex.getMessage());
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
            }
        });

        PlaysManager.getInstance().actualiserMesPartiesCommencees(this);

        final ListView partiesCommencees = getListViewMesPartiesCommencees();
        partiesCommencees.setItemsCanFocus(true);
        partiesCommencees.requestFocus();

        partiesCommencees.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v,
                    int position, long id) {

                partiesCommencees.setItemChecked(position, true);

                activerInterfacePartiesCommencees();
            }
        });

        activerInterfacePartiesCommencees();
    }

    public void afficherVueGestionPartiesEnAttente() {
        // nomPartieSelectionnee = "";

        setContentView(R.layout.gestion_parties_en_attente);

        ((ImageView) findViewById(R.id.imageViewRefreshParties)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String id = "";
            	try {
					id = AccountManager.getInstance().getId();
				} catch (GestionCompteException e) {
					DialogManager.showActivityEndError(ORMActivity.this, e.getMessage());
				}
                PlaysManager.getInstance().obtenirMesParties(ORMActivity.this, id, true);
            }
        });
        ((Button) findViewById(R.id.buttonCreerPartie)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                new DialogCreerNouvellePartie(ORMActivity.this).show();
            }
        });
        ((Button) findViewById(R.id.buttonRejoindreQuitterPartie)).setOnClickListener(new OnClickListener() {

            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                try {
                    if (isRejoindrePartie) {
                        ListView lv = getListViewPartiesServeurEnAttente();
                        HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(lv.getCheckedItemPosition());
                        PlaysManager.getInstance().rejoindrePartieEnAttente(
                                ORMActivity.this,
                                map.get("itemNomPartie"));
                    } else {
                        ListView lv = getListViewMesPartiesEnAttente();
                        HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(lv.getCheckedItemPosition());
                        PlaysManager.getInstance().quitterPartieEnAttente(
                                ORMActivity.this,
                                map.get("itemNomPartie"));
                    }
                } catch (GestionCompteException ex) {
                    DialogManager.showActivityEndError(
                            ORMActivity.this, ex.getMessage());
                } catch (GestionPartiesException ex) {
                    try {
                        DialogManager.showErrorDialog(
                                ORMActivity.this, ex.getMessage());
                    } catch (Exception e) {
                        return;
                    }
                    return;
                }
            }
        });

        actualiserButtonNbPartiesCommencees();

        final ListView partiesServeur = getListViewPartiesServeurEnAttente();
        try {
            PlaysManager.getInstance().actualiserPartiesServeurEnAttente(this);
        } catch (GestionCompteException e) {
            DialogManager.showActivityEndError(this, e.getMessage());
        }

        final ListView partiesClient = getListViewMesPartiesEnAttente();
        PlaysManager.getInstance().actualiserMesPartiesEnAttente(this);

        partiesServeur.setItemsCanFocus(true);
        partiesClient.setItemsCanFocus(true);

        partiesServeur.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v,
                    int position, long id) {
                partiesClient.setBackgroundColor(COULEUR_FOND);
                partiesClient.setCacheColorHint(COULEUR_FOND);

                partiesServeur.setBackgroundColor(COULEUR_SELECTION);
                partiesServeur.setCacheColorHint(COULEUR_SELECTION);

                partiesServeur.setItemChecked(position, true);

                setRejoindrePartie(true);
                activerInterfacePartiesEnAttente();
            }
        });

        partiesClient.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v,
                    int position, long id) {
                partiesServeur.setBackgroundColor(COULEUR_FOND);
                partiesServeur.setCacheColorHint(COULEUR_FOND);

                partiesClient.setBackgroundColor(COULEUR_SELECTION);
                partiesClient.setCacheColorHint(COULEUR_SELECTION);

                partiesClient.setItemChecked(position, true);

                setRejoindrePartie(false);
                activerInterfacePartiesEnAttente();

            }
        });

        ((Button) findViewById(R.id.buttonMesPartiesEnCours)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                afficherVueGestionPartiesCommencees();
            }
        });

        setRejoindrePartie(false);
        activerInterfacePartiesEnAttente();

    }

    public ListView getListViewMesPartiesCommencees() {
        return (ListView) findViewById(R.id.listViewPartiesCommencees);
    }

    public ListView getListViewMesPartiesEnAttente() {
        return (ListView) findViewById(R.id.listViewMesPartiesEnAttente);
    }

    public ListView getListViewPartiesServeurEnAttente() {
        return (ListView) findViewById(R.id.listViewPartiesServeurEnAttente);
    }

    private void setRejoindrePartie(boolean rejoindreOuQuitter) {
        isRejoindrePartie = rejoindreOuQuitter;
        Button b = (Button) findViewById(R.id.buttonRejoindreQuitterPartie);
        if (isRejoindrePartie) {
            b.setText("Rejoindre");
        } else {
            b.setText("Quitter");
        }
    }

    public void remplirListViewCommencees(List<InfosPartieDTO> listParties) {

        ArrayList<HashMap<String, String>> elems = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map;
        for (InfosPartieDTO p : listParties) {
            map = new HashMap<String, String>();
            map.put("itemNomPartie", p.getNom());
            String votreTour = "";
            String idJoueur = "";
            try {
                idJoueur = AccountManager.getInstance().getId();
                if (p.getJoueurCourant().equals(idJoueur)) {
                    votreTour = "[votre tour]";
                }
            } catch (GestionCompteException e) {
            }

            map.put("itemVotreTour", votreTour);
            map.put("itemNbJoueurs", "Tour " + p.getNumTour()
                    + " - " + p.getNbJoueurs() + " joueurs");
            elems.add(map);

        }

        ListView lv = getListViewMesPartiesCommencees();
        lv.setAdapter(new SimpleAdapter(lv.getContext(), elems,
                R.layout.item_list_partie_commencees, new String[]{
                    "itemNomPartie", "itemVotreTour", "itemNbJoueurs"},
                new int[]{R.id.itemNomPartie, R.id.itemVotreTour,
                    R.id.itemNbJoueurs}));

    }

    public void remplirListViewEnAttente(ListView lv,
            List<InfosPartieDTO> listParties) {

        ArrayList<HashMap<String, String>> elems = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map;
        for (InfosPartieDTO p : listParties) {
            map = new HashMap<String, String>();
            map.put("itemNomPartie", p.getNom());
            map.put("itemNbJoueurs", p.getJoueurs().size() + "/" + p.getNbJoueurs() + "j");
            elems.add(map);

        }

        lv.setAdapter(new SimpleAdapter(lv.getContext(), elems,
                R.layout.item_list_partie_en_attente,
                // android.R.layout.simple_list_item_checked,
                new String[]{"itemNomPartie", "itemNbJoueurs"}, new int[]{
                    R.id.itemNomPartie, R.id.itemNbJoueurs}));

    }

    public String getUser() {
        EditText text = ((EditText) findViewById(R.id.editTextUser));
        if (text == null) {
            return "";
        }
        return text.getText().toString();
    }

    public String getPass() {
        EditText text = ((EditText) findViewById(R.id.editTextPass));
        if (text == null) {
            return "";
        }
        String pwd = "";
        if (text.getText().toString().length() >= AccountManager.NB_CAR_MIN_PASS) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                byte[] input = digest.digest(text.getText().toString().getBytes("UTF8"));
                pwd = new String(input, "UTF8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ORMActivity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ORMActivity.class.getName()).log(Level.SEVERE,
                        ex.getMessage(), ex);
            }
        } else {
            pwd = text.getText().toString();
        }
        return pwd;
    }

    public String getNomNouvellePartie() {
        EditText text = ((EditText) findViewById(R.id.editTextNomPartie));
        if (text == null) {
            return "";
        }
        return text.getText().toString();
    }

    public void activerInterfaceGestionCompte(boolean activer) {
        ((Button) findViewById(R.id.buttonCreerCompte)).setEnabled(activer);
        ((Button) findViewById(R.id.buttonSauthentifier)).setEnabled(activer);
        ((EditText) findViewById(R.id.editTextUser)).setEnabled(activer);
        ((EditText) findViewById(R.id.editTextPass)).setEnabled(activer);
    }

    public void activerInterfacePartiesCommencees() {
        boolean activer = !PlaysManager.getInstance().getMesPartiesCommencees().isEmpty();

        ((Button) findViewById(R.id.buttonDeclarerForfait)).setEnabled(activer);
        ((Button) findViewById(R.id.buttonPoursuivrePartie)).setEnabled(activer);
    }

    public void activerInterfacePartiesEnAttente() {

        boolean aucunePartieServeur = PlaysManager.getInstance().getPartiesServeurEnAttente().isEmpty();
        boolean aucunePartieClient = PlaysManager.getInstance().getMesPartiesEnAttente().isEmpty();
        ListView partiesServeur = getListViewPartiesServeurEnAttente();
        ListView partiesClient = getListViewMesPartiesEnAttente();
        if (aucunePartieServeur) {
            partiesServeur.setBackgroundColor(COULEUR_FOND);
            if (!aucunePartieClient) {
                partiesClient.setBackgroundColor(COULEUR_SELECTION);
                setRejoindrePartie(false);
            }
        }
        if (aucunePartieClient) {
            partiesClient.setBackgroundColor(COULEUR_FOND);
            if (!aucunePartieServeur) {
                partiesServeur.setBackgroundColor(COULEUR_SELECTION);
                setRejoindrePartie(true);
            }
        }

        boolean actButtonRejoindre = isRejoindrePartie && !aucunePartieServeur
                || !isRejoindrePartie && !aucunePartieClient;
        boolean actButtonCommencees = !PlaysManager.getInstance().getMesPartiesCommencees().isEmpty();

        ((Button) findViewById(R.id.buttonRejoindreQuitterPartie)).setEnabled(actButtonRejoindre);
        ((Button) findViewById(R.id.buttonMesPartiesEnCours)).setEnabled(actButtonCommencees);
    }

    public void setTextNbPartiesEnAttenteServeur(boolean enChargement) {
        String stringNb = "";
        if (enChargement) {
            stringNb = "chargement...";
        } else {
            stringNb = ""
                    + PlaysManager.getInstance().getPartiesServeurEnAttente().size();
        }
        ((TextView) findViewById(R.id.textViewPartiesServeur)).setText("Parties existantes (" + stringNb + ")");
    }

    public void actualiserTextNbPartiesEnAttenteClient() {
        int nbParties = PlaysManager.getInstance().getMesPartiesEnAttente().size();
        int total = PlaysManager.NOMBRE_PARTIES_MAX
                - PlaysManager.getInstance().getMesPartiesCommencees().size();
        ((TextView) findViewById(R.id.textViewPartiesSelectionnees)).setText("Parties selectionnées (" + nbParties + "/" + total
                + ")");
    }

    public void actualiserTextNbPartiesCommencees() {
        int nbParties = PlaysManager.getInstance().getMesPartiesCommencees().size();
        ((TextView) findViewById(R.id.textViewMesPartiesEnCours)).setText("Mes parties en cours (" + nbParties + ")");
    }

    public void actualiserButtonNbPartiesCommencees() {
        int nbParties = PlaysManager.getInstance().getMesPartiesCommencees().size();
        ((Button) findViewById(R.id.buttonMesPartiesEnCours)).setText("Parties en cours (" + nbParties + ")");
    }

    public void actualiserButtonNbPartiesEnAttenteClient() {
        int nbParties = PlaysManager.getInstance().getMesPartiesEnAttente().size();
        ((Button) findViewById(R.id.buttonPartiesEnAttente)).setText("Parties en attente (" + nbParties + ")");
    }

    public void lancerActivityJeu() {

        // on passe au jeu
        Intent intent = new Intent(this, MapActivityPlay.class);
        startActivityForResult(intent, MapActivityPlay.CODE_ACTIVITY);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        String id = "";
        try {
            id = AccountManager.getInstance().getId();
        } catch (GestionCompteException ex) {
            DialogManager.showActivityEndError(this, ex.getMessage());
        }

        PlaysManager.getInstance().obtenirMesParties(this, id, true);
    }
}
