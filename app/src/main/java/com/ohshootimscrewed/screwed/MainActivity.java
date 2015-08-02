package com.ohshootimscrewed.screwed;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import android.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;
import java.util.Locale;

/**
 * Created by robertkim on 8/1/15.
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView mTextView;
    private EditText mSearchInput;
    private Location mLastLocation;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (checkPlayServices()) {
//            buildGoogleApiClient();
        }
        doLocation();

        mTextView = (TextView) findViewById(R.id.title_screen);
        mSearchInput = (EditText) findViewById(R.id.search_field);

        final String base = "http://api.yelp.com/v2/search/?";

        mSearchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String term = "term=" + mSearchInput.getText().toString();
                    String location = "location=" + getCity(currentLocation);
                    String coordinates = "cll=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                    String uri = base + term + "&" + location + "&" + coordinates;
                    Log.i("ffsasdf", uri);
                    String[] param = {uri};
                    RequestTask task = new RequestTask();

                    task.execute(param);

                    return true;
                }
                return false;
            }
        });

    }

    public void doLocation() {
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                currentLocation = location;
                //doTimeCheck();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        long minTime = 0;
        float maxDistance = 0;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, maxDistance, locationListener);
    }

    public String getCity(Location l) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        String loc = null;
        try {
            List<Address> addresses = gcd.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            if (addresses.size() > 0) {
                loc = addresses.get(0).getLocality();
            }
            Log.i("City",loc);
        } catch (IOException e) {}

        loc.replace(" ", "+");

        return loc;
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
                for (int x = 0; x < listOfPlaces.length(); x++) {

                    JSONObject obj = listOfPlaces.getJSONObject(x);
                    String name = obj.getString("name");
                    JSONObject location = obj.getJSONObject("location");
                    JSONArray display_address = location.getJSONArray("display_address");
                    String address = display_address.getString(0) + " " + display_address.getString(2);

                    JSONObject coordinates = obj.getJSONObject("region").getJSONObject("center");

                    double latitude = Double.parseDouble(coordinates.getString("latitude"));
                    double longitude = Double.parseDouble(coordinates.getString("longitude"));
                    
                    Log.i("asdf", name + " " + address);
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

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}