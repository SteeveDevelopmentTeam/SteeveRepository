package com.steeve.steeveapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Roman on 20/03/2016.
 */
public class LetMeInActivity extends Activity {
    private ImageView doorIV;
    private SharedPreferences sharedPreferences;
    private String userName, retrievedTokenSetString;
    private int userID;
    private StringBuilder builder = new StringBuilder();
    private String LOG_TAG = "LetMeInActivity";
    private String messageTarget;
    private TextView letMeInTitleTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("userDataPreferences", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        userID = sharedPreferences.getInt("userID", 0);
        setContentView(R.layout.let_me_in_layout);
        letMeInTitleTV = (TextView) findViewById(R.id.letMeInTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "AllertaStencil-Regular.ttf");
        letMeInTitleTV.setTypeface(myTypeface);
        new getTokenSetConnection().execute();
        setupListeners();
    }

    private void setupListeners() {
        doorIV = (ImageView) findViewById(R.id.doorIV);
        doorIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorIV.setImageResource(R.drawable.open_door);
                new SendOpenAsyncMessage().execute();
            }
        });

        final ToggleButton pOpenButton = (ToggleButton) findViewById(R.id.pOpenButton);
        pOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pOpenButton.isChecked()) {
                    messageTarget = "Pando";
                }
            }
        });

        final ToggleButton riOpenButton = (ToggleButton) findViewById(R.id.riOpenButton);
        riOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (riOpenButton.isChecked()) {
                    messageTarget = "Rimo";
                }
            }
        });

        final ToggleButton nOpenButton = (ToggleButton) findViewById(R.id.nOpenButton);
        nOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nOpenButton.isChecked()) {
                    messageTarget = "Neri";
                }
            }
        });

        final ToggleButton roOpenButton = (ToggleButton) findViewById(R.id.roOpenButton);
        roOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (roOpenButton.isChecked()) {
                    messageTarget = "Roman";
                }
            }
        });
    }

    private class SendOpenAsyncMessage extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                sendOpenMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }


    private void sendOpenMessage() throws JSONException {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://steeve.altervista.org/VecchioSito/pages/php/send_open_message.php");
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            JSONArray jsonArray = new JSONArray(Arrays.asList(userName));
            try {
                json.put("to", JSONDecoder.getUserToken(messageTarget, retrievedTokenSetString));
            } catch (Exception e) { e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Select a target first", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            json.put("data", jsonArray);


            JSONArray postjson = new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json", json.toString());
            httppost.getParams().setParameter("jsonpost", postjson);

            // Execute HTTP Post Request
            Log.v("SendOpenMessage", "Created JSON: " + json);
            HttpResponse response = httpclient.execute(httppost);

            // for JSON:
            if (response != null) {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.v("Log1", line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    public class getTokenSetConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {

            try {
                getTokenSet();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    public String getTokenSet() throws JSONException {
        builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://steeve.altervista.org/VecchioSito/pages/php/get_token_set.php");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.v(LOG_TAG, "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("getShoppingData result", builder.toString());
        retrievedTokenSetString = builder.toString();
        return builder.toString();
    }
}
