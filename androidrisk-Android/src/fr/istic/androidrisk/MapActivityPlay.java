package fr.istic.androidrisk;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.maps.MapActivity;

import fr.istic.androidrisk.ihm.connexion.AccountManager;
import fr.istic.androidrisk.ihm.connexion.GestionCompteException;
import fr.istic.androidrisk.ihm.play.*;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import fr.istic.androidrisk.moteur.Joueur;
import fr.istic.androidrisk.moteur.Play;

/**
 * This Activity allow the player to see the map of the Risk (the map is the Google Map of France).
 * Territories number are the same of France territories (ex : 35 Ille et Vilaine).
 *
 * The player must have chosen before, in the main menu, a play. If the play has the exact
 * number of players, the play starts and this activity is shown.
 *
 * The player can activate sensors to move map with gravity. This feature is blocked when he
 * clicks on a sensor button or when he isn't navigating (ex : when a dialog is open)
 */
public class MapActivityPlay extends MapActivity {

    public static final String TAG = "MapActivityJeu";

    public static final int CODE_ACTIVITY = 42;

    public static final int[] playersColor = {
        Color.parseColor("#FFFF50"),//yellow
        Color.parseColor("#50FFFF"),//cyan
        Color.parseColor("#FF50FF"),//magenta
        Color.parseColor("#50FF50"),//green
        Color.parseColor("#FF5050"),//red
        Color.parseColor("#5050FF")//blue
    };

    /**
     * Gets the french name of the unit type in parameter
     * @param unitType
     * @return
     */
    public static String getCardName(final Integer unitType) {
        if (unitType == Play.ARTILLERIE) {
            return "Artillerie";
        } else if (unitType == Play.CAVALERIE) {
            return "Cavalerie";
        } else if (unitType == Play.INFANTERIE) {
            return "Infanterie";
        }
        return "";
    }

    /**
     * Gets the image in resources, corresponding the unit type in parameter
     * @param unitType
     * @return
     */
    public static int getCardImage(final Integer unitType) {
        if (unitType == Play.ARTILLERIE) {
            return R.drawable.carte_canon;
        } else if (unitType == Play.CAVALERIE) {
            return R.drawable.carte_cavalier;
        } else if (unitType == Play.INFANTERIE) {
            return R.drawable.carte_fantassin;
        }
        //returns a default image
        return R.drawable.carte_fantassin;
    }

    /**
     * Shows a splash screen and ask the server asynchronously, the chosen play of the player.
     * When the server has send the play, the method showMainView() from this Activity is called.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSplashScreen();
        GameManager.getInstance().getChosenPlay(this);
    }

    /**
     * Shows an image while the play hasn't been initialized
     */
    public void showSplashScreen() {
        setContentView(R.layout.splashscreen);
    }

    /**
     * Shows the play view with the map, the players, the phases...
     * This method is called after the Play assignment. The view elements and the map
     * are initialized with the Play
     */
    public void showMainView() {
        setContentView(R.layout.jeu);

        getSeekBarNbUnits().init(this);
        getSeekBarNbUnits().activate(false);

        getMapView().init(this);

        //init sensors activation button :
        getImageViewSensorsActivation().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMapView().activateSensorsManually(!getMapView().areSensorsManuallyActivated());
            }
        });

        Play play = GameManager.getInstance().getCurrentPlay();
        getTextViewTurnNumber().setText("Tour " + play.getTurnCounter());

        updatePlayView();

    }

    /**
     * Updates all elements of the view with the Play of the GameManager
     */
    public void updatePlayView() {

        Play play = GameManager.getInstance().getCurrentPlay();

        getEditTextHistory().setText("");
        getEditTextHistory().addText(play.getHistory());

        getTableLayoutPlayersList().addPlayers(play.getPlayers());

        TextView phaseName = getTextViewPhaseName();
        phaseName.setTextColor(playersColor[GameManager.getInstance().getCurrentPlay().getCurrentPlayerIndex()]);

        Button buttonEndPhase = getButtonEndTurn();
        buttonEndPhase.setText("Terminer");
        buttonEndPhase.setEnabled(true);

        try {
        	//gets the id of the player in the AccountManager
            final String id = AccountManager.getInstance().getId();

            //initializes the button click to show player hand
            ((ImageView) findViewById(R.id.imageViewMain)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    showHandView(false, id);
                }
            });


            if (!play.getCurrentPlayer().getPseudo().equals(id)) {
            	//this is not the player turn

                phaseName.setText("Attendez votre tour");
                buttonEndPhase.setEnabled(false);
            } else {
            	//this is the player turn

            	//updates the view for each different phase
                switch (play.getCurrentPhase()) {

                    case Play.PHASE_RENFORT:
                        phaseName.setText("Phase de Renfort");
                        updateButtonEndPhase();

                        //initializes the button click to show player hand with possibility to add reinforcements
                        ((ImageView) findViewById(R.id.imageViewMain)).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                showHandView(true, id);
                            }
                        });

                        break;

                    case Play.PHASE_ATTAQUE:
                        phaseName.setText("Phase d'Attaque");
                        buttonEndPhase.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                GameManager.getInstance().changePhase(MapActivityPlay.this);
                            }
                        });
                        break;

                    case Play.PHASE_MOUVEMENT:
                        phaseName.setText("Phase de mouvement");
                        buttonEndPhase.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                GameManager.getInstance().changePhase(MapActivityPlay.this);
                            }
                        });
                        break;
                }
            }

        } catch (GestionCompteException e) {

        	//Opens a dialog if an error occurs
            try {
                DialogManager.showErrorDialog(this, e.getMessage(), new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
            } catch (Exception e1) {
                return;
            }
        }
    }

    /**
     * Shows end text if there is player has no more reinforcements, else show the number
     * of available reinforcements
     */
    private void updateButtonEndPhase() {

        int reinforcements = GameManager.getInstance().getCurrentPlay().getCurrentPlayer().getReinforcements();

        Button buttonEndTurn = getButtonEndTurn();
        if (reinforcements <= 0) {
            buttonEndTurn.setEnabled(true);
            buttonEndTurn.setText("Terminer");
            buttonEndTurn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameManager.getInstance().changePhase(MapActivityPlay.this);
                }
            });
        } else {
            buttonEndTurn.setEnabled(false);
            buttonEndTurn.setText("Renforts : " + reinforcements);
        }
    }

    /**
     * Shows a dialog with the player cards. If the current phase is the reinforcement phase,
     * a button will be shown in the dialog to add some reinforcements.
     * @param isReinforcementPhase
     * @param pseudo The pseudo of the player
     */
    public void showHandView(boolean isReinforcementPhase, String pseudo) {
        Play play = GameManager.getInstance().getCurrentPlay();
        int nbPlayers = play.getPlayers().size();

        for (int i = 0; i < nbPlayers; i++) {
            Joueur j = play.getPlayers().get(i);
            if (j.getPseudo().equals(pseudo)) {
                new DialogShowHand(this, j, isReinforcementPhase).show();
                break;
            }
        }
    }

    public TextView getTextViewNbPlacedUnits() {
        return (TextView) findViewById(R.id.textViewNbUnitesPlacees);
    }

    public TextView getTextViewPhaseName() {
        return (TextView) findViewById(R.id.textViewNomPhase);
    }

    public TextView getTextViewTurnNumber() {
        return (TextView) findViewById(R.id.textViewNumeroTour);
    }

    public Button getButtonEndTurn() {
        return (Button) findViewById(R.id.buttonTerminerPhase);
    }

    public ImageView getImageViewSensorsActivation() {
        return (ImageView) findViewById(R.id.imageViewActiverSenseurs);
    }

    public SeekBarNbUnits getSeekBarNbUnits() {
        return (SeekBarNbUnits) findViewById(R.id.seekBarNombreUnites);
    }

    public EditTextHistory getEditTextHistory() {
        return (EditTextHistory) findViewById(R.id.editTextHistorique);
    }

    public MapViewORM getMapView() {
        return (MapViewORM) findViewById(R.id.mapView);
    }

    private TableLayoutPlayersList getTableLayoutPlayersList() {
        return (TableLayoutPlayersList) findViewById(R.id.tableLayoutListeJoueurs);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /**
     * Changes image of sensors button to notify the player of the sensors state
     * @param activate
     */
    public void updateSensorsImage(boolean activate) {
        if (activate) {
            ((ImageView) findViewById(R.id.imageViewActiverSenseurs)).setImageResource(R.drawable.senseurs_actives);
        } else {
            ((ImageView) findViewById(R.id.imageViewActiverSenseurs)).setImageResource(R.drawable.senseurs_desactives);
        }
    }

    /**
     * Sends the play to the server to save it when the activity is paused or stopped.
     * Disables sensors.
     */
    @Override
    protected void onPause() {
        try {
            GameManager.getInstance().sendPlayToServerWithoutLoading(this, false);
            getMapView().activateSensorsManually(false);
        } catch (Exception ex) {
        }
        super.onPause();
    }

}
