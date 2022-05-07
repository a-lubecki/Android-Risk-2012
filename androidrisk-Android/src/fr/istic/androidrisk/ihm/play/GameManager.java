package fr.istic.androidrisk.ihm.play;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fr.istic.androidrisk.MapActivityPlay;
import fr.istic.androidrisk.client.MyRequestFactory;
import fr.istic.androidrisk.client.Util;
import fr.istic.androidrisk.client.MyRequestFactory.MyRequest;
import fr.istic.androidrisk.exception.AlreadyControledAttackedTerritoryException;
import fr.istic.androidrisk.exception.NotAuthorizedMoveException;
import fr.istic.androidrisk.exception.NotEnoughUnitsException;
import fr.istic.androidrisk.exception.NonBoundaryAttackedTerritoryException;
import fr.istic.androidrisk.ihm.connexion.AccountManager;
import fr.istic.androidrisk.ihm.connexion.GestionCompteException;
import fr.istic.androidrisk.ihm.connexion.PlaysManager;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import fr.istic.androidrisk.moteur.Play;
import fr.istic.androidrisk.moteur.Territory;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Game Manager provides methods to bind the model (class Play) and
 * the send share data with the server.
 * An instance of the class is accessible by all Activities
 * with a Singleton (static method getInstance()).
 */
public final class GameManager {

	/**
	 * A static instance of this class (Pattern Singleton)
	 */
    private static volatile GameManager instance = null;

    /**
     * Instantiate the static instance if there is no instance yet
     * @return the static instance of this class
     */
    public final static GameManager getInstance() {
        if (GameManager.instance == null) {
            synchronized (GameManager.class) {
                if (GameManager.instance == null) {
                    GameManager.instance = new GameManager();
                }
            }
        }
        return GameManager.instance;
    }

    /**
     * The model containing players, territories, phases...
     */
    private Play currentPlay;

    private GameManager() {
    }

	/**
	 * Ask the server the content of the play, chosen in the menus.
	 * @param mapActivityPlay
	 */
    public void getChosenPlay(final MapActivityPlay mapActivityPlay) {

    	/**
    	 * The name of the play, chosen by the player in the menus before,
    	 * and saved on the server. The asking is done with
    	 * an AsyncTask, so no waiting time is required
    	 */
        final String playname = PlaysManager.getInstance().getChosenPlayName();

    	//execute a new AsyncTask
        new AsyncTask<Void, String, Void>() {

        	/**
        	 * The save of a progress dialog to see the progression
        	 * to close it at the end of the operation
        	 */
            private ProgressDialog progressDialog;

            /**
             * A message to inform the player if the operation has been done correctly.
             */
            private String dialogMessage = "";

            /**
             * Before asking, show a new dialog
             */
            protected void onPreExecute() {
                try {
                    progressDialog = DialogManager.showProgressDialog(
                            mapActivityPlay, "Chargement...");
                } catch (Exception e) {
                    return;
                }
            }

            /**
             * Close the progress dialog and show an error dialog if the operation failed.
             * Else, show the main view of the play activity (map, players, phases...).
             */
            protected void onPostExecute(Void v) {
                progressDialog.dismiss();

                if (!dialogMessage.equals("")) {
                    try {
                        DialogManager.showActivityEndError(mapActivityPlay, dialogMessage);
                    } catch (Exception e) {
                        mapActivityPlay.finish();
                    }
                    return;
                }

                mapActivityPlay.showMainView();
            }

            /**
             * Get the serialized play. Add an error message if the operation failed.
             * @param arg0 The serialized Play
             */
            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(
                        mapActivityPlay, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                //Ask the server (fire() method) :
                request.getPlay(playname).fire(new Receiver<String>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        if (arg0 == null || arg0 == "") {
                            dialogMessage = "La partie n'existe pas.";
                        }

                        //Assign the serialized play to the current play
                        XStream xstream = new XStream(new DomDriver());
                        currentPlay = (Play) xstream.fromXML(arg0);
                    }
                });

                return null;
            }
        }.execute();
    }

    /**
     * Serialize the play and send it to the server. The sending is done with
     * an AsyncTask, so no waiting time is required
     * @param mapActivityPlay
     * @param endTurn
     */
    public void sendPlayToServerWithoutLoading(
            final MapActivityPlay mapActivityPlay, final boolean endTurn) {

    	//execute a new AsyncTask
        new AsyncTask<Void, Void, Void>() {

        	/**
        	 * A message to show is an error occurs
        	 */
            private String dialogMessage = "";

            protected void onPreExecute() {
            }

            /**
             * Show a returned error or update the play view
             */
            protected void onPostExecute(Void v) {
                if (!dialogMessage.equals("")) {
                    try {
                        DialogManager.showActivityEndError(mapActivityPlay, dialogMessage);
                    } catch (Exception e) {
                        mapActivityPlay.finish();
                    }
                    return;
                }

                mapActivityPlay.updatePlayView();
            }

            /**
             * Serialize the play and send it to the server. The server will save it
             * and return an error if the operation failed.
             */
            @Override
            protected Void doInBackground(Void... arg0) {
                MyRequestFactory requestFactory = Util.getRequestFactory(
                        mapActivityPlay, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                //Serialization :
                XStream xstream = new XStream(new DomDriver());
                String xmlSerializedPlay = xstream.toXML(currentPlay);

                //Send it (fire() method) :
                request.setPlay(xmlSerializedPlay, endTurn).fire(new Receiver<Void>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        dialogMessage = error.getMessage();
                    }

                    @Override
                    public void onSuccess(Void v) {
                    }
                });

                return null;
            }
        }.execute();
    }

    public void clearPlay() {
        currentPlay = null;
    }

    public Play getCurrentPlay() {
        return currentPlay;
    }

    /**
     * A play action : the player can add reinforcements on the map.
     * @param mapActivityPlay
     * @param territory The territory (must be controlled by the player)
     * @param nbUnits
     */
    public void placeReinforcements(MapActivityPlay mapActivityPlay, String territory, int nbUnits) {
        currentPlay.getTerritories().get(territory).addUnits(nbUnits);
        currentPlay.addHistory(nbUnits + " renforts ajoutés sur " + currentPlay.getTerritories().get(territory).getName() + ".");
        currentPlay.getCurrentPlayer().removeReinforcements(nbUnits);

        //doesn't wait the server :
        mapActivityPlay.updatePlayView();

        mapActivityPlay.getMapView().updateTerritoryLabel(
                territory, "" + currentPlay.getTerritories().get(territory).getNbStayingUnits());

    }

	/**
	 * A play action : the player can attack a boundary territory of an opponent
	 * @param mapActivityPlay
	 * @param atkTerritoryName The atk territory (must be controlled by the player)
	 * @param defTerritoryName The def territory (must be controlled by an opponent and near the atk territory)
	 * @param nbUnits The number of units from the atk territory which will attack
	 */
    public void attackTerritory(MapActivityPlay mapActivityPlay, String atkTerritoryName, String defTerritoryName, int nbUnits) {
        try {
        	//attack territory in play and append it to the history
            currentPlay.addHistory(currentPlay.getTerritories().get(atkTerritoryName).
                    attackTerritory(currentPlay.getTerritories().get(defTerritoryName), nbUnits));
        } catch (AlreadyControledAttackedTerritoryException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotEnoughUnitsException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonBoundaryAttackedTerritoryException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        //update the labels on the map
        mapActivityPlay.getMapView().updateTerritoryLabel(
                atkTerritoryName, "" + currentPlay.getTerritories().get(atkTerritoryName).getNbStayingUnits());


        // if player has won the attack : the earned territory is selected
        if (currentPlay.getTerritories().get(defTerritoryName).getControler()
                == currentPlay.getCurrentPlayer()) {


            Territory atkTerritory = currentPlay.getTerritories().get(atkTerritoryName);
            Territory capturedTerritory = currentPlay.getTerritories().get(defTerritoryName);

            mapActivityPlay.getMapView().updateTerritoryDisplay(
                    defTerritoryName, "" + capturedTerritory.getNbStayingUnits(),
                    MapActivityPlay.playersColor[currentPlay.getCurrentPlayerIndex()]);

            mapActivityPlay.getMapView().setSelectedTerritory(defTerritoryName, currentPlay);

            //add a new card to the player hand if he has obtained a new territory
            int earnedCard = -1;
            if (!currentPlay.hasObtainedATerritory()) {
                earnedCard = Play.getNewCard();
                currentPlay.getCurrentPlayer().addCard(earnedCard);
                for (int i = 0; i < 20; i++) {
                    earnedCard = Play.getNewCard();
                    currentPlay.getCurrentPlayer().addCard(earnedCard);
                }
                currentPlay.setHasObtainedATerritory(true);
            }

            //show a resume of the capture
            new DialogCapturedTerritory(mapActivityPlay, atkTerritory, capturedTerritory, earnedCard).show();

        } else {
        	//else : player has losed the attack

            mapActivityPlay.getMapView().updateTerritoryLabel(
                    defTerritoryName, "" + currentPlay.getTerritories().get(defTerritoryName).getNbStayingUnits());

            //if there is more than one unit on play : territory is selected again
            if (currentPlay.getTerritories(currentPlay.getCurrentPlayer()).
                    get(atkTerritoryName).getNbStayingUnits() > 1) {
                mapActivityPlay.getMapView().setSelectedTerritory(atkTerritoryName, currentPlay);
            }

        }

        mapActivityPlay.updatePlayView();
    }

    /**
     * A play action : the player can move units from a controlled territory to another,
     * territories can't have less than one unit
     * @param mapActivityPlay
     * @param oldTerritory The starting point
     * @param newTerritory The destination
     * @param nbUnits The number of unit to move : can't be less than one
     */
    public void moveUnits(MapActivityPlay mapActivityPlay, String oldTerritory, String newTerritory, int nbUnits) {

        try {
            currentPlay.addHistory(nbUnits + " unité" + (nbUnits > 1 ? "s" : "") + " déplacée"
                    + (nbUnits > 1 ? "s" : "") + " de " + currentPlay.getTerritories().get(oldTerritory).getName()
                    + " vers " + currentPlay.getTerritories().get(newTerritory).getName() + ".");

            //move units in the current play
            currentPlay.getTerritories().get(oldTerritory).moveUnits(currentPlay.getTerritories().get(newTerritory), nbUnits);

            mapActivityPlay.getMapView().updateTerritoryLabel(
                    oldTerritory, "" + currentPlay.getTerritories().get(oldTerritory).getNbStayingUnits());

            mapActivityPlay.getMapView().updateTerritoryLabel(
                    newTerritory, "" + currentPlay.getTerritories().get(newTerritory).getNbStayingUnits());

            //to avoid a second click of the player after the end of his turn
            mapActivityPlay.updatePlayView();
        } catch (NotAuthorizedMoveException e) {
            try {
                DialogManager.showErrorDialog(mapActivityPlay, e.getMessage());
            } catch (Exception e1) {
                return;
            }
            return;
        }

    }

    /**
     * A play action : end the current phase, if it's the last phase,
     * the next player turn begins
     * @param mapActivityPlay
     */
    public void changePhase(final MapActivityPlay mapActivityPlay) {

    	//change the phase in the current play
        currentPlay.changePhase();

        try {
            AccountManager.getInstance().getId();
        } catch (GestionCompteException e) {
            return;
        }

        mapActivityPlay.updatePlayView();
        mapActivityPlay.getMapView().clearSelectedTerritory();
        mapActivityPlay.getSeekBarNbUnits().activate(false);

        if (currentPlay.getCurrentPhase() == Play.PHASE_RENFORT) {
            sendPlayToServerWithoutLoading(mapActivityPlay, true);
        }

    }

    /**
     * A play action : the player can give cards to get new units on play
     * @param mapActivityPlay
     * @param nbCardsType1 The number of cards for type 1 : ARTILLERIE
     * @param nbCardsType2 The number of cards for type 2 : INFANTERIE
     * @param nbCardsType3 The number of cards for type 3 : CAVALERIE
     */
    public void changeCards(MapActivityPlay mapActivityPlay, Integer nbCardsType1, Integer nbCardsType2, Integer nbCardsType3) {

    	//change the cards in the current play
    	currentPlay.obtainsNonPeriodicReinforcements(nbCardsType1, nbCardsType2, nbCardsType3);
        mapActivityPlay.updatePlayView();
    }

}
