package fr.istic.androidrisk;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import fr.istic.androidrisk.client.MyRequestFactory;
import fr.istic.androidrisk.client.MyRequestFactory.MyRequest;
import fr.istic.androidrisk.client.Util;
import fr.istic.androidrisk.ihm.connexion.AccountManager;

public class BackgroundService extends Service {

    private Timer timer;
    private int id;
    private final int PERIOD = 30 * 1000;
    private TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        id = 0;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        timerTask = new TimerTask() {

            public void run() {
                SharedPreferences prefs = getSharedPreferences(AccountManager.NOM_FICHIER_PREFERENCES, Context.MODE_WORLD_READABLE);
                String login = prefs.getString("id", "");
                MyRequestFactory requestFactory = Util.getRequestFactory(BackgroundService.this, MyRequestFactory.class);
                final MyRequest request = requestFactory.myRequest();

                request.notification(login).fire(new Receiver<Boolean>() {

                    @Override
                    public void onFailure(ServerFailure error) {
                        // ne rien faire
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification notification = new Notification(R.drawable.ic_launcher, "Risk : A vous de jouer !", System.currentTimeMillis());
                            notification.defaults |= Notification.DEFAULT_VIBRATE;
                            notification.defaults |= Notification.DEFAULT_SOUND;
                            notification.defaults |= Notification.DEFAULT_LIGHTS;
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
                            Context context = getApplicationContext();
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ORMActivity.class), 0);
                            String titreNotification = "Android Risk";
                            String texteNotification = "A vous de jouer !";
                            notification.setLatestEventInfo(context, titreNotification, texteNotification, pendingIntent);
                            notificationManager.notify(id, notification);
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, PERIOD);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        this.timer.cancel();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
}