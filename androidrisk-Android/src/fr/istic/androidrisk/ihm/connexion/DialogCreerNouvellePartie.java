package fr.istic.androidrisk.ihm.connexion;

import fr.istic.androidrisk.ORMActivity;
import fr.istic.androidrisk.R;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class DialogCreerNouvellePartie extends Dialog {
    
	public DialogCreerNouvellePartie(final ORMActivity ormActivity) {
		super(ormActivity);

		setContentView(R.layout.dialog_nouvelle_partie);
		setTitle("Créer une nouvelle partie");
		setCancelable(true);

		final EditText nomPartie = (EditText) findViewById(R.id.editTextNomPartie);

		final SeekBar bar = (SeekBar) findViewById(R.id.seekBarNombreJoueurs);
		((TextView) findViewById(R.id.textViewNbJoueursNouvellePartie)).setText("" + (bar.getProgress() + 2));
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				((TextView) findViewById(R.id.textViewNbJoueursNouvellePartie))
						.setText("" + (progress + 2));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		Button buttonValider = (Button) findViewById(R.id.buttonValiderCreerPartie);
		buttonValider.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					PlaysManager.getInstance().creerNouvellePartie(ormActivity, nomPartie.getText().toString(), bar.getProgress() + 2);
					dismiss();
				} catch (GestionCompteException ex) {
					DialogManager.showActivityEndError(ormActivity, ex.getMessage());
				} catch (GestionPartiesException e) {
					try {
						DialogManager.showErrorDialog(ormActivity, e.getMessage());
					} catch (Exception e1) {
						return;
					}
					return;
				}

			}
		});

	}
}
