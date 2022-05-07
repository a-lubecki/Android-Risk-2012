package fr.istic.androidrisk.ihm.play;

import fr.istic.androidrisk.MapActivityPlay;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class SeekBarNbUnits extends SeekBar {

	private int min;
	private int max;
	private MapActivityPlay mapActivityJeu;

	public SeekBarNbUnits(Context context) {
		super(context);
	}
	public SeekBarNbUnits(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SeekBarNbUnits(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void updateTextViewValeur(){
		if(isEnabled()){
			mapActivityJeu.getTextViewNbPlacedUnits().setText("" + getNbUnites());
		} else {
			mapActivityJeu.getTextViewNbPlacedUnits().setText("--");
		}

	}

	public void init(MapActivityPlay mapActivityJeu) {
		this.mapActivityJeu = mapActivityJeu;
		setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateTextViewValeur();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		updateTextViewValeur();
	}

	public void setMinMax(int min, int max){
		if(min < max){
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;
		}
		setMax(this.max - this.min);

	}

	public void setValeurDefaut(int valeur){
		setProgress(valeur);
		updateTextViewValeur();
	}

	public void activate(boolean enabled){
		setEnabled(enabled);
		setMinMax(min, max);
		if(!enabled){
			setValeurDefaut(0);
		} else {
			setValeurDefaut(max-min);
		}
		updateTextViewValeur();
	}

	public int getNbUnites(){
		return getProgress() + min;
	}
    
}
