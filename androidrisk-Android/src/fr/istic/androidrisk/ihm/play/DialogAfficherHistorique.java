package fr.istic.androidrisk.ihm.play;

import fr.istic.androidrisk.MapActivityPlay;
import fr.istic.androidrisk.ORMActivity;
import fr.istic.androidrisk.R;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class DialogAfficherHistorique extends Dialog {

	public DialogAfficherHistorique(final MapActivityPlay mapActivityJeu) {
		super(mapActivityJeu);

		setContentView(R.layout.dialog_afficher_historique);

		setTitle("Historique");
		setCancelable(true);

		TextView tv = (TextView) findViewById(R.id.textViewHistoriqueDialog);
		tv.setText(mapActivityJeu.getEditTextHistory().getText().toString());
	}
    
}
