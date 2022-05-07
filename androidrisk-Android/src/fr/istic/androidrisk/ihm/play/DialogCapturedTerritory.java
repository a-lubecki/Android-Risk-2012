package fr.istic.androidrisk.ihm.play;

import fr.istic.androidrisk.MapActivityPlay;
import fr.istic.androidrisk.R;
import fr.istic.androidrisk.moteur.Play;
import fr.istic.androidrisk.moteur.Territory;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class DialogCapturedTerritory extends Dialog {

    public DialogCapturedTerritory(final MapActivityPlay mapActivityJeu, final Territory territoireAttaquant, final Territory territoireCapture, int carteGagnee) {
        super(mapActivityJeu);

        setContentView(R.layout.dialog_resultat_attaque);

        setTitle("Territoire capturé");
        setCancelable(false);

        if (carteGagnee ==-1) {
            LinearLayout layout = ((LinearLayout) findViewById(R.id.linearLayoutCarteGagnee));
            layout.setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.textViewNomCarteGagnee)).setText(MapActivityPlay.getCardName(carteGagnee));
            ((ImageView) findViewById(R.id.imageViewCarteGagnee)).setImageResource(MapActivityPlay.getCardImage(carteGagnee));
        }


        final SeekBar bar = (SeekBar) findViewById(R.id.seekBarNbUnitesApresAttaque);
        final int min = 1;
        final int max = territoireAttaquant.getNbStayingUnits() + territoireCapture.getNbStayingUnits() - 1;

        bar.setMax(max - min);
        bar.setProgress(bar.getMax());

        ((TextView) findViewById(R.id.textViewNbUnitesSource)).setText("" + (min + bar.getProgress()));
        ((TextView) findViewById(R.id.textViewNbUnitesDest)).setText("" + (max - bar.getProgress()));

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.textViewNbUnitesSource)).setText("" + (min + bar.getProgress()));
                ((TextView) findViewById(R.id.textViewNbUnitesDest)).setText("" + (max - bar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button buttonValider = (Button) findViewById(R.id.buttonDeplacerUnitesApresAttaque);
        buttonValider.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Play partie = GameManager.getInstance().getCurrentPlay();
                int nbUnitesSrc = Integer.parseInt(((TextView) findViewById(R.id.textViewNbUnitesSource)).getText().toString());               
                int nbUnitesDest = Integer.parseInt(((TextView) findViewById(R.id.textViewNbUnitesDest)).getText().toString());         
                partie.getTerritories().get(territoireAttaquant.getIdentifiant()).setNb_unites_stationnees(
                        nbUnitesSrc);
                partie.getTerritories().get(territoireCapture.getIdentifiant()).setNb_unites_stationnees(
                        nbUnitesDest);             
                mapActivityJeu.getMapView().updateTerritoryLabel(
                        territoireAttaquant.getIdentifiant(), "" + nbUnitesSrc);
                mapActivityJeu.getMapView().updateTerritoryLabel(
                        territoireCapture.getIdentifiant(), "" + nbUnitesDest);             
                dismiss();
            }
        });

    }
}
