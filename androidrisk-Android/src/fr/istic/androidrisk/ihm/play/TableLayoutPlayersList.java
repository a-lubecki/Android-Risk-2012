package fr.istic.androidrisk.ihm.play;

import java.util.List;

import fr.istic.androidrisk.MapActivityPlay;
import fr.istic.androidrisk.R;
import fr.istic.androidrisk.moteur.Joueur;
import fr.istic.androidrisk.moteur.Play;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableLayoutPlayersList extends TableLayout {

	public TableLayoutPlayersList(Context context) {
		super(context);
		initialiser();
	}

	public TableLayoutPlayersList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialiser();
	}

	private void initialiser() {
	}

	public void addPlayers(List<Joueur> joueurs) {

		final int COLONNE_NOM_JOUEUR = 0;
		final int COLONNE_NB_UNITES = 1;
		final int COLONNE_NB_TERRITOIRES = 2;

		if(joueurs == null || joueurs.size() <= 0){
			return;
		}

		removeAllViews();
		Context context = getContext();
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TableRow tr = (TableRow) inflater.inflate(R.layout.item_table_liste_joueurs, null);

		TextView titre1 = (TextView) tr.getChildAt(COLONNE_NOM_JOUEUR);
		titre1.setText("Joueur");

		TextView titre2 = (TextView) tr.getChildAt(COLONNE_NB_UNITES);
		titre2.setText("U");

		TextView titre3 = (TextView) tr.getChildAt(COLONNE_NB_TERRITOIRES);
		titre3.setText("T");

		addView(tr);
		int taille = joueurs.size();
		for(int i=0;i<taille;i++){

			tr = (TableRow) inflater.inflate(R.layout.item_table_liste_joueurs, null);

			Joueur j = joueurs.get(i);

			String pseudo = j.getPseudo();
			if(pseudo==null || pseudo == ""){
				break;
			}
			pseudo = tronquerPseudo(pseudo);
			TextView nom = (TextView) tr.getChildAt(COLONNE_NOM_JOUEUR);
			if(i < MapActivityPlay.playersColor.length){
				nom.setTextColor(MapActivityPlay.playersColor[i]);
			}
			nom.setText(pseudo);

			TextView nbUnites = (TextView) tr.getChildAt(COLONNE_NB_UNITES);
			nbUnites.setText("" +GameManager.getInstance().getCurrentPlay().getNbUnitesTotal(j));

			TextView nbTerritoires = (TextView) tr.getChildAt(COLONNE_NB_TERRITOIRES);
			nbTerritoires.setText(""+GameManager.getInstance().getCurrentPlay().getTerritories(j).size());

			addView(tr);
		}
	}


	private static String tronquerPseudo(String pseudo) {
		final int NB_CARAC_MAX = 16;

		if(pseudo.length() > NB_CARAC_MAX){
			pseudo = pseudo.substring(0, NB_CARAC_MAX-3);
			pseudo += "...";
		}
		return pseudo;
	}

	public void ajouterJoueur(){
	}

}
