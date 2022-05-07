package fr.istic.androidrisk.ihm.play;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import fr.istic.androidrisk.MapActivityPlay;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class EditTextHistory extends EditText {

	private static final int NB_LIGNES_MAX = 50;
	
	public EditTextHistory(Context context) {
		super(context);
		initialiser();
	}
	public EditTextHistory(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialiser();
	}
	public EditTextHistory(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialiser();
	}

	private void initialiser(){
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DialogAfficherHistorique((MapActivityPlay)getContext()).show();
			}
		});
	}

	public void ajouterTexte(String newHisto){
		if(newHisto == null || newHisto == ""){
			return;
		}
		
		ArrayList<String> l = new ArrayList<String>();
		l.add(newHisto);
		addText(l);
	}

	public void addText(List<String> newHisto){
		if(newHisto == null || newHisto.size() <= 0){
			return;
		}
		
		int nbLignes = 0;
        StringReader sr = new StringReader(getText().toString());
        LineNumberReader lnr = new LineNumberReader(sr);
        try { 
            while (lnr.readLine() != null){}
            nbLignes = lnr.getLineNumber();
            lnr.close();
        } catch (IOException e) {
        	nbLignes = getLineCount();
        } finally {
            sr.close();
        }

		int nbLignesRestantes = NB_LIGNES_MAX - (newHisto.size()+nbLignes);		
		
		int taille = newHisto.size();
		for(int i=0;i < taille; i++){
			append(newHisto.get(i));
			if(i >= taille-1){ 
				break;
			}
			append("\n");
		}
				
		if(nbLignesRestantes < 0){
			String[] all = getText().toString().split("\n");
			setText("");
			for(int i=-nbLignesRestantes;i<all.length;i++){
				append(all[i]);
				if(i >= all.length-1){ 
					break;
				}
				append("\n");
			}
		}

		setSelection(getText().toString().length());
	}

}
