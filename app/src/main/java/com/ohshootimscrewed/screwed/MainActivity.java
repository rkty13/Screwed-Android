package com.ohshootimscrewed.screwed;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

/**
 * Created by robertkim on 8/1/15.
 */
public class MainActivity extends FragmentActivity {

    private TextView mTextView;
    private EditText mSearchInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.title_screen);
        mSearchInput = (EditText) findViewById(R.id.search_field);

        final String base = "http://api.yelp.com/v2/search?";

        mSearchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String term = "term=" + mSearchInput.getText().toString();
                    String location = "location=" + "San+Francisco";

                    String uri = base + term + "&" + location;
                    String[] param = { uri };
                    RequestTask task = new RequestTask();

                    task.execute(param);

                    return true;
                }
                return false;
            }
        });

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
                    sb.append(line+"\n");
                }
                br.close();
                response = sb.toString();
                request.disconnect();

                return sb.toString();

            } catch(MalformedURLException m) {
                Log.i("URL EXception",m.toString());
            } catch(IOException i) {
                Log.i("URL EXception",i.toString());

            } catch(OAuthMessageSignerException o) {
                Log.i("URL EXception",o.toString());

            } catch(OAuthExpectationFailedException o) {
                Log.i("URL EXception",o.toString());
            } catch(OAuthCommunicationException o) {
                Log.i("URL EXception",o.toString());
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
}