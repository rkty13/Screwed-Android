package com.ohshootimscrewed.screwed;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by robertkim on 8/1/15.
 */
public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private TextView mTextView;
    private EditText mSearchInput;
    private Location mLastLocation;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        mTextView = (TextView) findViewById(R.id.title_screen);
        mSearchInput = (EditText) findViewById(R.id.search_field);

        final String base = "http://api.yelp.com/v2/search?";

        mSearchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    double[] coordinates = getLocation();

                    String term = "term=" + mSearchInput.getText().toString();
//                    String location = "location=" + "San+Francisco";
                    String location = "cll=" + coordinates[0] + "," + coordinates[1];
                    String uri = base + term + "&" + location;
                    String[] param = {uri};
                    RequestTask task = new RequestTask();

                    task.execute(param);

                    return true;
                }
                return false;
            }
        });

    }

    public double[] getLocation() {

        Log.d("asdf", "fda");
        double[] coordinates = null;
        if (mLastLocation != null) {
            Log.d("asdf", "asdf");
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Log.d("latitude", Double.toString(latitude));
            Log.d("longitude", Double.toString(longitude));

            coordinates = new double[]{latitude, longitude};
        }

        return coordinates;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public void onConnected(Bundle connectedHint) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class RequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... uri) {
            OAuthConsumer consumer = new DefaultOAuthConsumer("fbv9fQExe4oBdS_IIJ0iqw", "y_NDOXvlsHAy5HO8aiwpc_OclZE");
            consumer.setTokenWithSecret("VBtCfx1L2EwltnQNF4nFXqNyRcP6HmJI", "cJQS8ofJ5jsmFundFga88LuJjyo");
            String response = null;
            try {
                URL url = new URL(uri[0]);

                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                // sign the request
                consumer.sign(request);

                // send the request
                request.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                response = sb.toString();
                request.disconnect();

                return sb.toString();

            } catch (MalformedURLException m) {
                Log.i("URL EXception", m.toString());
            } catch (IOException i) {
                Log.i("URL EXception", i.toString());

            } catch (OAuthMessageSignerException o) {
                Log.i("URL EXception", o.toString());

            } catch (OAuthExpectationFailedException o) {
                Log.i("URL EXception", o.toString());
            } catch (OAuthCommunicationException o) {
                Log.i("URL EXception", o.toString());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject results = new JSONObject(result);
                JSONArray listOfPlaces = results.getJSONArray("businesses");
                for (int x = 0; x < 20; x++) {

                    String myurl = listOfPlaces.getJSONObject(x).getString("mobile_url");
                    Log.i(listOfPlaces.getJSONObject(x).getString("name"), myurl);
//                    Uri webpage = Uri.parse(myurl);
//                    Log.i(myurl,myurl);
                }
            } catch (JSONException j) {
                Log.i("JSONException", j.toString());

            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }
}