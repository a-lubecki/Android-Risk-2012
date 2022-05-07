package fr.istic.androidrisk.ihm.play;

import java.util.List;

import fr.istic.androidrisk.MapActivityPlay;
import fr.istic.androidrisk.R;
import fr.istic.androidrisk.moteur.Joueur;
import fr.istic.androidrisk.moteur.Play;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogShowHand extends Dialog {

    private Button buttonEchanger3Infanteries;
    private Button buttonEchanger3Cavaliers;
    private Button buttonEchanger3Canons;
    private Button buttonEchanger1deChaque;
    
    private int nbInfanterie;
    private int nbCavaliers;
    private int nbCanons;

    public DialogShowHand(final MapActivityPlay mapActivityJeu, final Joueur joueur, boolean phaseRenfort) {
        super(mapActivityJeu);

        setContentView(R.layout.dialog_afficher_main);

        setTitle("Votre main");

        setCancelable(true);

        nbInfanterie = joueur.getCartesInfanterie();
        nbCavaliers = joueur.getCartesCavalerie();
        nbCanons = joueur.getCartesArtillerie();


        buttonEchanger3Infanteries = (Button) findViewById(R.id.buttonEchanger3Infanteries);
        buttonEchanger3Cavaliers = (Button) findViewById(R.id.buttonEchanger3Cavaleries);
        buttonEchanger3Canons = (Button) findViewById(R.id.buttonEchanger3Canons);
        buttonEchanger1deChaque = (Button) findViewById(R.id.buttonEchanger1deChaque);

        update();

        boolean hasCartesPourEchange = nbInfanterie >= 3 || nbCavaliers >= 3 || nbCanons >= 3
                || (nbInfanterie >= 1 && nbCavaliers >= 1 && nbCanons >= 1);

        if (hasCartesPourEchange) {
            String text = "Vous pouvez échanger des cartes :";

            if (phaseRenfort) {
                buttonEchanger3Infanteries.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GameManager.getInstance().changeCards(mapActivityJeu, Play.INFANTERIE, Play.INFANTERIE, Play.INFANTERIE);
                        nbInfanterie -= 3;
                        update();
                    }
                });
                buttonEchanger3Cavaliers.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GameManager.getInstance().changeCards(mapActivityJeu, Play.CAVALERIE, Play.CAVALERIE, Play.CAVALERIE);
                        nbCavaliers -= 3;
                        update();
                    }
                });
                buttonEchanger3Canons.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GameManager.getInstance().changeCards(mapActivityJeu, Play.ARTILLERIE, Play.ARTILLERIE, Play.ARTILLERIE);
                        nbCanons -= 3;
                        update();
                    }
                });
                buttonEchanger1deChaque.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GameManager.getInstance().changeCards(mapActivityJeu, Play.INFANTERIE, Play.CAVALERIE, Play.ARTILLERIE);
                        nbInfanterie--;
                        nbCanons--;
                        nbCavaliers--;
                        update();
                    }
                });

            } else {
                text = "Vous pourrez échanger des cartes au prochain tour.";
                buttonEchanger3Infanteries.setEnabled(false);
                buttonEchanger3Cavaliers.setEnabled(false);
                buttonEchanger3Canons.setEnabled(false);
                buttonEchanger1deChaque.setEnabled(false);
            }
            ((TextView) findViewById(R.id.textViewPeutEchangerCartes)).setText(text);
        } else {
            //on masque l'échange de cartes
            ((LinearLayout) findViewById(R.id.linearLayoutEchangeCarte)).setVisibility(View.INVISIBLE);
        }

    }

    public final void update() {
        ((TextView) findViewById(R.id.textViewNbCartesInfanterie)).setText("X" + nbInfanterie);
        ((TextView) findViewById(R.id.textViewNbCartesCavalerie)).setText("X" + nbCavaliers);
        ((TextView) findViewById(R.id.textViewNbCartesCanons)).setText("X" + nbCanons);

        buttonEchanger3Infanteries.setEnabled(nbInfanterie >= 3);
        buttonEchanger3Cavaliers.setEnabled(nbCavaliers >= 3);
        buttonEchanger3Canons.setEnabled(nbCanons >= 3);
        buttonEchanger1deChaque.setEnabled(nbInfanterie >= 1 && nbCavaliers >= 1 && nbCanons >= 1);
    }
}
