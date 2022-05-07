package fr.istic.androidrisk;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fr.istic.androidrisk.client.MyRequestFactory;
import fr.istic.androidrisk.client.Util;
import fr.istic.androidrisk.client.MyRequestFactory.MyRequest;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import fr.istic.androidrisk.dto.MondeDTO;
import fr.istic.androidrisk.parsers.RegTerParser;

/**
 * Main activity - requests "Hello, World" messages from the server and provides
 * a menu item to invoke the accounts activity.
 */
public class AndroidRiskActivity extends Activity {

    /**
     * Tag for logging.
     */
    private static final String TAG = "AndroidriskActivity";
    /**
     * The current context.
     */
    private Context mContext = this;

    /**
     * Begins the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        final TextView resultTextView = (TextView) findViewById(R.id.result);
        final Button testButton = (Button) findViewById(R.id.test);
        testButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                testButton.setEnabled(false);
                resultTextView.setText("loading...");

                // Use an AsyncTask to avoid blocking the UI thread
                new AsyncTask<Void, Void, String>() {

                    private String message;

                    @Override
                    protected String doInBackground(Void... arg0) {
                        MyRequestFactory requestFactory = Util.getRequestFactory(mContext,
                                MyRequestFactory.class);
                        final MyRequest request = requestFactory.myRequest();
                        Log.i(TAG, "Sending request to server");
                        XStream xStream = new XStream(new DomDriver());
                        MondeDTO monde = RegTerParser.parse(AndroidRiskActivity.this);
                        String s = xStream.toXML(monde);
                        request.saveMonde(s).fire(
                                new Receiver<Void>() {

                                    @Override
                                    public void onFailure(
                                            ServerFailure error) {
                                        message = "Failure: "
                                                + error.getMessage();
                                    }

                                    @Override
                                    public void onSuccess(
                                            Void result) {
                                        message = "result:\n";
                                    }
                                });

                        return message;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        resultTextView.setText(result);
                        testButton.setEnabled(true);
                    }
                }.execute();
            }
        });
    }
}
