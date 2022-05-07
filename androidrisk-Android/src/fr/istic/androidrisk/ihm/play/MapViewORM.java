package fr.istic.androidrisk.ihm.play;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import fr.istic.androidrisk.MapActivityPlay;
import fr.istic.androidrisk.ihm.connexion.AccountManager;
import fr.istic.androidrisk.ihm.connexion.GestionCompteException;
import fr.istic.androidrisk.ihm.connexion.PlaysManager;
import fr.istic.androidrisk.ihm.utils.DialogManager;
import fr.istic.androidrisk.moteur.Joueur;
import fr.istic.androidrisk.moteur.Play;
import fr.istic.androidrisk.moteur.Territory;
import java.io.IOException;
import java.util.*;

public class MapViewORM extends MapView implements SensorEventListener {

    private static final String TAG = "MapViewORM";
    public static boolean TEST_SANS_COULEURS_REGIONS_PARCEQUE_CA_RAME = true;
    public static final Map<String, MapLabel> labels = new HashMap<String, MapLabel>();

    private float valRollDefaut = 0;
    private float roll = 0;
    private MapActivityPlay mapActivityJeu;
    private List<Overlay> overlays;
    private String territoireSelectionne = "";
    private boolean senseursActivesManuellement = true;
    private boolean senseursActivesLogiquement = true;
    private boolean dragndrop = false;
    private SensorManager mSensorManager;

    static {
        labels.put("35", new MapLabel("35", Color.RED, new GeoPoint(48210190,
                -1575550)));
        labels.put("36", new MapLabel("36", Color.BLACK, new GeoPoint(46673944,
                1577059)));
        labels.put("33", new MapLabel("33", Color.BLACK, new GeoPoint(44664100,
                -462265)));
        labels.put("34", new MapLabel("34", Color.BLACK, new GeoPoint(43532536,
                3243467)));
        labels.put("39", new MapLabel("39", Color.BLACK, new GeoPoint(46955397,
                5632719)));
        labels.put("37", new MapLabel("37", Color.BLACK, new GeoPoint(47200826,
                716559)));
        labels.put("38", new MapLabel("38", Color.BLACK, new GeoPoint(45319527,
                5497549)));
        labels.put("43", new MapLabel("43", Color.BLACK, new GeoPoint(45169873,
                3841255)));
        labels.put("42", new MapLabel("42", Color.BLACK, new GeoPoint(45833609,
                4050098)));
        labels.put("41", new MapLabel("41", Color.BLACK, new GeoPoint(47810749,
                1142761)));
        labels.put("40", new MapLabel("40", Color.BLACK, new GeoPoint(44056981,
                -738713)));
        labels.put("22", new MapLabel("22", Color.BLACK, new GeoPoint(48349028,
                -2773519)));
        labels.put("23", new MapLabel("23", Color.BLACK, new GeoPoint(45912617,
                2083705)));
        labels.put("24", new MapLabel("24", Color.BLACK, new GeoPoint(44977504,
                782781)));
        labels.put("25", new MapLabel("25", Color.BLACK, new GeoPoint(47162686,
                6361124)));
        labels.put("26", new MapLabel("26", Color.BLACK, new GeoPoint(44659114,
                5177655)));
        labels.put("27", new MapLabel("27", Color.BLACK, new GeoPoint(49114980,
                932098)));
        labels.put("28", new MapLabel("28", Color.BLACK, new GeoPoint(48278791,
                1409812)));
        labels.put("29", new MapLabel("29", Color.BLACK, new GeoPoint(48368202,
                -3939913)));
        labels.put("30", new MapLabel("30", Color.BLACK, new GeoPoint(44028650,
                4247915)));
        labels.put("32", new MapLabel("32", Color.BLACK, new GeoPoint(43776801,
                374488)));
        labels.put("31", new MapLabel("31", Color.BLACK, new GeoPoint(43407032,
                1452215)));
        labels.put("19", new MapLabel("19", Color.BLACK, new GeoPoint(45130545,
                1792252)));
        labels.put("17", new MapLabel("17", Color.BLACK, new GeoPoint(45896675,
                -629719)));
        labels.put("18", new MapLabel("18", Color.BLACK, new GeoPoint(46933790,
                2593164)));
        labels.put("15", new MapLabel("15", Color.BLACK, new GeoPoint(45097029,
                2738690)));
        labels.put("16", new MapLabel("16", Color.BLACK, new GeoPoint(45833889,
                310416)));
        labels.put("13", new MapLabel("13", Color.BLACK, new GeoPoint(43598202,
                5042889)));
        labels.put("14", new MapLabel("14", Color.BLACK, new GeoPoint(49059899,
                -205062)));
        labels.put("11", new MapLabel("11", Color.BLACK, new GeoPoint(43054497,
                2530188)));
        labels.put("12", new MapLabel("12", Color.BLACK, new GeoPoint(44210541,
                2740872)));
        labels.put("21", new MapLabel("21", Color.BLACK, new GeoPoint(47474598,
                4762771)));
        labels.put("95", new MapLabel("95", Color.BLACK, new GeoPoint(49093039,
                2223838)));
        labels.put("08", new MapLabel("08", Color.BLACK, new GeoPoint(49562273,
                4624314)));
        labels.put("09", new MapLabel("09", Color.BLACK, new GeoPoint(42865717,
                1397393)));
        labels.put("91", new MapLabel("91", Color.BLACK, new GeoPoint(48483033,
                2231143)));
        labels.put("04", new MapLabel("04", Color.BLACK, new GeoPoint(43942483,
                6197095)));
        labels.put("90", new MapLabel("90", Color.BLACK, new GeoPoint(47713101,
                6887273)));
        labels.put("05", new MapLabel("05", Color.BLACK, new GeoPoint(44723623,
                6481770)));
        labels.put("06", new MapLabel("06", Color.BLACK, new GeoPoint(43964357,
                7248261)));
        labels.put("07", new MapLabel("07", Color.BLACK, new GeoPoint(44603793,
                4393512)));
        labels.put("01", new MapLabel("01", Color.BLACK, new GeoPoint(46069826,
                5314318)));
        labels.put("02", new MapLabel("02", Color.BLACK, new GeoPoint(49790590,
                3661698)));
        labels.put("03", new MapLabel("03", Color.BLACK, new GeoPoint(46367418,
                3141843)));
        labels.put("10", new MapLabel("10", Color.BLACK, new GeoPoint(48191437,
                4230432)));
        labels.put("88", new MapLabel("88", Color.BLACK, new GeoPoint(48063566,
                6572766)));
        labels.put("89", new MapLabel("89", Color.BLACK, new GeoPoint(47634785,
                3710050)));
        labels.put("79", new MapLabel("79", Color.BLACK, new GeoPoint(46578658,
                -308162)));
        labels.put("78", new MapLabel("78", Color.BLACK, new GeoPoint(48768933,
                1879307)));
        labels.put("77", new MapLabel("77", Color.BLACK, new GeoPoint(48656178,
                2991471)));
        labels.put("82", new MapLabel("82", Color.BLACK, new GeoPoint(44096829,
                1252293)));
        labels.put("83", new MapLabel("83", Color.BLACK, new GeoPoint(43442583,
                6271757)));
        labels.put("80", new MapLabel("80", Color.BLACK, new GeoPoint(50011802,
                2027920)));
        labels.put("81", new MapLabel("81", Color.BLACK, new GeoPoint(43709048,
                2190188)));
        labels.put("86", new MapLabel("86", Color.BLACK, new GeoPoint(46412482,
                521242)));
        labels.put("87", new MapLabel("87", Color.BLACK, new GeoPoint(46064345,
                1205463)));
        labels.put("84", new MapLabel("84", Color.BLACK, new GeoPoint(44015477,
                5149286)));
        labels.put("85", new MapLabel("85", Color.BLACK, new GeoPoint(46538134,
                -1180548)));
        labels.put("67", new MapLabel("67", Color.BLACK, new GeoPoint(48769073,
                7650167)));
        labels.put("66", new MapLabel("66", Color.BLACK, new GeoPoint(42558548,
                2533459)));
        labels.put("69", new MapLabel("69", Color.BLACK, new GeoPoint(45851663,
                4625906)));
        labels.put("68", new MapLabel("68", Color.BLACK, new GeoPoint(47824576,
                7265335)));
        labels.put("70", new MapLabel("70", Color.BLACK, new GeoPoint(47741035,
                6210570)));
        labels.put("71", new MapLabel("71", Color.BLACK, new GeoPoint(46656017,
                4543505)));
        labels.put("72", new MapLabel("72", Color.BLACK, new GeoPoint(47831343,
                199383)));
        labels.put("73", new MapLabel("73", Color.BLACK, new GeoPoint(45494985,
                6403394)));
        labels.put("74", new MapLabel("74", Color.BLACK, new GeoPoint(46045251,
                6423393)));
        labels.put("75", new MapLabel("75", Color.BLACK, new GeoPoint(48837020,
                2327291)));
        labels.put("76", new MapLabel("76", Color.BLACK, new GeoPoint(49643228,
                973758)));
        labels.put("59", new MapLabel("59", Color.BLACK, new GeoPoint(50185801,
                3639425)));
        labels.put("58", new MapLabel("58", Color.BLACK, new GeoPoint(47186201,
                3549861)));
        labels.put("57", new MapLabel("57", Color.BLACK, new GeoPoint(48985969,
                6560607)));
        labels.put("56", new MapLabel("56", Color.BLACK, new GeoPoint(47824145,
                -2737925)));
        labels.put("55", new MapLabel("55", Color.BLACK, new GeoPoint(49013519,
                5371111)));
        labels.put("64", new MapLabel("64", Color.BLACK, new GeoPoint(43170315,
                -797273)));
        labels.put("65", new MapLabel("65", Color.BLACK, new GeoPoint(42979174,
                175577)));
        labels.put("62", new MapLabel("62", Color.BLACK, new GeoPoint(50614065,
                1978779)));
        labels.put("63", new MapLabel("63", Color.BLACK, new GeoPoint(45601942,
                3182413)));
        labels.put("60", new MapLabel("60", Color.BLACK, new GeoPoint(49336997,
                2370394)));
        labels.put("61", new MapLabel("61", Color.BLACK, new GeoPoint(48657868,
                230040)));
        labels.put("49", new MapLabel("49", Color.BLACK, new GeoPoint(47267356,
                -555284)));
        labels.put("48", new MapLabel("48", Color.BLACK, new GeoPoint(44652803,
                3442952)));
        labels.put("45", new MapLabel("45", Color.BLACK, new GeoPoint(47842272,
                2363115)));
        labels.put("44", new MapLabel("44", Color.BLACK, new GeoPoint(47413738,
                -1679388)));
        labels.put("47", new MapLabel("47", Color.BLACK, new GeoPoint(44413269,
                475144)));
        labels.put("46", new MapLabel("46", Color.BLACK, new GeoPoint(44635713,
                1635415)));
        labels.put("51", new MapLabel("51", Color.BLACK, new GeoPoint(48825283,
                4209158)));
        labels.put("52", new MapLabel("52", Color.BLACK, new GeoPoint(47950369,
                5333221)));
        labels.put("53", new MapLabel("53", Color.BLACK, new GeoPoint(48270083,
                -613339)));
        labels.put("54", new MapLabel("54", Color.BLACK, new GeoPoint(48565529,
                6358930)));
        labels.put("50", new MapLabel("50", Color.BLACK, new GeoPoint(49356543,
                -1424661)));
    }

    public MapViewORM(Context context, String apiKey) {
        super(context, apiKey);
    }

    public MapViewORM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MapViewORM(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public void init(MapActivityPlay mapActivityJeu) {

        setSatellite(true);
        GeoPoint centerGeoPoint = new GeoPoint(47082892, 2396578);
        getController().animateTo(centerGeoPoint);
        getController().setZoom(6);

        this.mapActivityJeu = mapActivityJeu;

        mSensorManager = (SensorManager) mapActivityJeu.getSystemService(Activity.SENSOR_SERVICE);
        activateSensorsManually(true);

        clearSelectedTerritory();
        overlays = getOverlays();
        List<Joueur> joueurs = GameManager.getInstance().getCurrentPlay().getPlayers();
        for (int i = 0; i < joueurs.size(); i++) {
            Joueur j = joueurs.get(i);
            Map<String, Territory> territoires = GameManager.getInstance().getCurrentPlay().getTerritories(j);
            for (Territory t : territoires.values()) {
                MapLabel label = labels.get(t.getIdentifiant());
                label.setColor(MapActivityPlay.playersColor[i]);
                label.setText(t.getNbStayingUnits() + "");
                overlays.add(label);
            }
        }
        activateSensorsManually(false);
    }

    public void updateTerritoryDisplay(String territoire, String nbUnites,
            int couleur) {
        MapLabel label = labels.get(territoire);
        label.setColor(couleur);
        label.setText(nbUnites);
        invalidate();
    }

    public void updateTerritoryLabel(String territoire, String nbUnites) {
        MapLabel label = labels.get(territoire);
        label.setText(nbUnites);
        invalidate();
    }

    public void clearSelectedTerritory() {
        activerSenseursLogique(true);
        territoireSelectionne = "";
        initLabels();
        invalidate();
    }

    public void setSelectedTerritory(String territoireSelectionne,
            Play partie) {
        if (territoireSelectionne == null || territoireSelectionne.equals("")) {
            return;
        }

        clearSelectedTerritory();

        // si la région n'est pas la même on la change
        if (!this.territoireSelectionne.equals(territoireSelectionne)) {

            activerSenseursLogique(false);
            focusSurCentreTerritoire(territoireSelectionne);

        }
        mapActivityJeu.getSeekBarNbUnits().activate(true);
        this.territoireSelectionne = territoireSelectionne;
        labels.get(territoireSelectionne).setEtat(MapLabel.ETAT_SOURCE);
        initLabelsDeplacement(territoireSelectionne, partie);
    }

    public String getTerritoireSelectionne() {
        return territoireSelectionne;
    }

    public void focusSurCentreTerritoire(String territoire) {

        clearSelectedTerritory();

        mapActivityJeu.getSeekBarNbUnits().activate(false);

        MapLabel labTerr = labels.get(territoire);
        getController().animateTo(labTerr.getGeoPoint());
    }

    public void activateSensorsManually(boolean activer) {
        senseursActivesManuellement = activer;
        valRollDefaut = roll;
        mapActivityJeu.updateSensorsImage(activer);
        if (activer) {
            Sensor mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            mSensorManager.registerListener(this, mGyroscope,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            mSensorManager.unregisterListener(this);
        }
    }

    public void activerSenseursLogique(boolean activer) {
        senseursActivesLogiquement = activer;
    }

    public boolean areSensorsManuallyActivated() {
        return senseursActivesManuellement;
    }

    public boolean isSenseursActivesLogiquement() {
        return senseursActivesManuellement;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {

        Play partie = GameManager.getInstance().getCurrentPlay();
        HashMap<String, Territory> territoiresJoueurCourant = partie.getTerritories(partie.getCurrentPlayer());
        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            dragndrop = true;

        } else if (me.getAction() == MotionEvent.ACTION_UP) {

            if (!dragndrop) {
                int touchX = (int) me.getX();
                int touchY = (int) me.getY();
                GeoPoint geoPoint = getProjection().fromPixels(touchX, touchY);

                double latitude = geoPoint.getLatitudeE6() / 1000000.0;
                double longitude = geoPoint.getLongitudeE6() / 1000000.0;

                String newTerritoire = null;

                Geocoder gcd = new Geocoder(mapActivityJeu, Locale.getDefault());
                List<Address> addresses = new ArrayList<Address>();
                try {
                    addresses.addAll(gcd.getFromLocation(latitude, longitude, 1));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!addresses.isEmpty()) {
                    for (Address address : addresses) {
                        String postalCode = address.getPostalCode();
                        if (postalCode != null) {

                            newTerritoire = postalCode.substring(0, 2);
                            if (newTerritoire.equals("92")
                                    || newTerritoire.equals("93")
                                    || newTerritoire.equals("94")) {
                                newTerritoire = "75";
                            }
                            break;
                        }
                    }
                }

                if (newTerritoire == null) {
                    // Get instance of Vibrator from current Context
                    Vibrator v = (Vibrator) mapActivityJeu.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                    return super.onTouchEvent(me);
                }

                if (partie.getTerritories().get(newTerritoire) == null) {

                    // Get instance of Vibrator from current Context
                    Vibrator v = (Vibrator) mapActivityJeu.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                    return super.onTouchEvent(me);
                }

                String oldTerritoire = getTerritoireSelectionne();

                SeekBarNbUnits seekBar = mapActivityJeu.getSeekBarNbUnits();
                int nbUnitesSeekBar = mapActivityJeu.getSeekBarNbUnits().getNbUnites();
                String id = "";
                try {
                    id = AccountManager.getInstance().getId();
                } catch (GestionCompteException e) {
                    try {
                        DialogManager.showErrorDialog(mapActivityJeu,
                                e.getMessage(),
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        mapActivityJeu.finish();
                                    }
                                });
                    } catch (Exception e1) {
                        return super.onTouchEvent(me);
                    }
                }
                if (!partie.getCurrentPlayer().getPseudo().equals(id)) {
                    focusSurCentreTerritoire(newTerritoire);
                } else {
                    switch (partie.getCurrentPhase()) {
                        case Play.PHASE_RENFORT:
                            int nbRenfortsRestants = partie.getCurrentPlayer().getReinforcements();
                            if (nbRenfortsRestants <= 0) {
                                focusSurCentreTerritoire(newTerritoire);
                                break;
                            }

                            // si on a cliqué sur la même région
                            if (newTerritoire.equals(oldTerritoire)) {
                                GameManager.getInstance().placeReinforcements(
                                        mapActivityJeu, newTerritoire,
                                        nbUnitesSeekBar);
                                mapActivityJeu.getSeekBarNbUnits().activate(
                                        false);

                                clearSelectedTerritory();
                                if (partie.getCurrentPlayer().getReinforcements() <= 0) {
                                    mapActivityJeu.getButtonEndTurn().setEnabled(true);
                                    mapActivityJeu.getButtonEndTurn().performClick();
                                }
                                break;
                            }
                            // si on a cliqué sur une autre région qui nous
                            // appartient
                            if (territoiresJoueurCourant.containsKey(newTerritoire)) {
                                seekBar.activate(true);
                                seekBar.setMinMax(1, nbRenfortsRestants);
                                seekBar.setValeurDefaut(nbRenfortsRestants);
                                setSelectedTerritory(newTerritoire, partie);
                            } else {
                                focusSurCentreTerritoire(newTerritoire);
                            }
                            break;
                        case Play.PHASE_ATTAQUE:
                            // si on avait aucune région sélectionnée avant
                            if (oldTerritoire.equals("")) {
                                // si on a cliqué sur une autre région qui nous
                                // appartient
                                if (territoiresJoueurCourant.containsKey(newTerritoire)) {

                                    // s'il a plus d'une unité
                                    int nbUnites = territoiresJoueurCourant.get(
                                            newTerritoire).getNbStayingUnits();
                                    if (nbUnites > 1) {
                                        // il peut attaquer
                                        seekBar.activate(true);
                                        nbUnites = Math.min(3, nbUnites - 1);
                                        seekBar.setMinMax(1, nbUnites);
                                        seekBar.setValeurDefaut(nbUnites);
                                        setSelectedTerritory(newTerritoire,
                                                partie);
                                    } else {
                                        focusSurCentreTerritoire(newTerritoire);
                                    }
                                } else {
                                    focusSurCentreTerritoire(newTerritoire);
                                }
                            } else {
                                // si on a cliqué sur la même région
                                if (newTerritoire.equals(oldTerritoire)
                                        || // ou sur une région ennemie non
                                        // limitrophe
                                        (!territoiresJoueurCourant.containsKey(newTerritoire) && !partie.getTerritories().get(newTerritoire).isLimitrophe(oldTerritoire))) {
                                    focusSurCentreTerritoire(newTerritoire);
                                    break;
                                }

                                // si on a cliqué sur une autre région qui nous
                                // appartient
                                if (territoiresJoueurCourant.containsKey(newTerritoire)) {
                                    // s'il a plus d'une unité
                                    int nbUnites = territoiresJoueurCourant.get(
                                            newTerritoire).getNbStayingUnits();
                                    if (nbUnites > 1) {
                                        // il peut attaquer
                                        seekBar.activate(true);
                                        nbUnites = Math.min(3, nbUnites - 1);
                                        seekBar.setMinMax(1, nbUnites);
                                        seekBar.setValeurDefaut(nbUnites);
                                        setSelectedTerritory(newTerritoire,
                                                partie);
                                    } else {
                                        focusSurCentreTerritoire(newTerritoire);
                                    }
                                } else {
                                    mapActivityJeu.getSeekBarNbUnits().activate(false);

                                    clearSelectedTerritory();
                                    GameManager.getInstance().attackTerritory(
                                            mapActivityJeu, oldTerritoire,
                                            newTerritoire, nbUnitesSeekBar);
                                }
                                break;
                            }
                            break;
                        case Play.PHASE_MOUVEMENT:
                            // si on avait aucune région sélectionnée avant
                            if (oldTerritoire.equals("")) {
                                // si on a cliqué sur une autre région qui nous
                                // appartient

                                if (territoiresJoueurCourant.containsKey(newTerritoire)) {

                                    int nbUnites = territoiresJoueurCourant.get(
                                            newTerritoire).getNbStayingUnits();
                                    if (nbUnites > 1) {
                                        // il peut déplacer des unités
                                        seekBar.activate(true);
                                        nbUnites = Math.max(1, nbUnites - 1);
                                        seekBar.setMinMax(1, nbUnites);
                                        seekBar.setValeurDefaut(nbUnites);
                                        setSelectedTerritory(newTerritoire,
                                                partie);
                                    } else {
                                        focusSurCentreTerritoire(newTerritoire);
                                    }
                                } else {

                                    focusSurCentreTerritoire(newTerritoire);
                                }
                            } else {
                                // si on a cliqué sur la même région ou sur une
                                // région ennemie
                                if (newTerritoire.equals(oldTerritoire)
                                        || !territoiresJoueurCourant.containsKey(newTerritoire)) {
                                    focusSurCentreTerritoire(newTerritoire);
                                    break;
                                }
                                // si on a cliqué sur une autre région qui nous
                                // appartient
                                if (territoiresJoueurCourant.containsKey(newTerritoire)) {

                                    if (territoiresJoueurCourant.get(newTerritoire).isLimitrophe(oldTerritoire)) {
                                        mapActivityJeu.getSeekBarNbUnits().activate(false);

                                        clearSelectedTerritory();
                                        GameManager.getInstance().moveUnits(
                                                mapActivityJeu, oldTerritoire,
                                                newTerritoire, nbUnitesSeekBar);
                                        mapActivityJeu.getButtonEndTurn().setEnabled(true);
                                        mapActivityJeu.getButtonEndTurn().performClick();
                                    } else {
                                        int nbUnites = territoiresJoueurCourant.get(newTerritoire).getNbStayingUnits();
                                        if (nbUnites > 1) {
                                            // il peut déplacer des unités
                                            seekBar.activate(true);
                                            nbUnites = Math.min(1, nbUnites - 1);
                                            seekBar.setMinMax(1, nbUnites);
                                            seekBar.setValeurDefaut(nbUnites);
                                            setSelectedTerritory(newTerritoire,
                                                    partie);
                                        } else {
                                            focusSurCentreTerritoire(newTerritoire);
                                        }
                                    }

                                }
                                break;
                            }
                    }
                }
            }

            dragndrop = false;
        }
        return super.onTouchEvent(me);
    }

    public void initLabelsDeplacement(String source, final Play partie) {
        List<String> destinations = new ArrayList<String>(partie.getTerritories().get(source).gettLimitrophes());
        int phase = partie.getCurrentPhase();
        if (phase != Play.PHASE_RENFORT) {
            List<String> result = new ArrayList<String>(destinations);
            for (String dest : destinations) {
                boolean mmJoueur = partie.getTerritories().get(dest).getControler().getPseudo().equals(partie.getCurrentPlayer().getPseudo());
                if ((mmJoueur && phase == Play.PHASE_ATTAQUE)
                        || (!mmJoueur && phase == Play.PHASE_MOUVEMENT)) {
                    result.remove(dest);
                }
            }
            for (String dest : result) {
                labels.get(dest).setEtat(MapLabel.ETAT_DESTINATION);
            }
        }
    }

    public void initLabels() {
        for (MapLabel label : labels.values()) {
            label.setEtat(MapLabel.ETAT_AUCUN);
        }
    }

    public void onSensorChanged(SensorEvent se) {

        float pitch = se.values[1];
        roll = se.values[2];

        if (!senseursActivesLogiquement || !senseursActivesManuellement) {
            return;
        }
        float p = 0;
        float r = 0;
        if (pitch > 10 || pitch < -10) {
            p = pitch;
        }
        if (roll - valRollDefaut > 10 || roll - valRollDefaut < -10) {
            r = roll - valRollDefaut;
        }
        if (p != 0 || r != 0) {
            GeoPoint mapCenter = this.getMapCenter();
            GeoPoint newCenter = new GeoPoint(mapCenter.getLatitudeE6()
                    - (int) (r * 40000 / Math.pow(2, getZoomLevel() - 5)),
                    mapCenter.getLongitudeE6()
                    - (int) (p * 80000 / Math.pow(2, getZoomLevel() - 5)));

            this.getController().animateTo(newCenter);
        }

    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void draw(Canvas canvas) {
        if (getZoomLevel() < 6) {
            getController().setZoom(6);
        } else if (getZoomLevel() > 9) {
            getController().setZoom(9);
        }
        super.draw(canvas);
    }

}
