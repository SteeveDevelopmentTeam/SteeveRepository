package com.steeve.steeveapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

/**
 * Created by Roman on 01/03/2016.
 */
public class ShoppingActivity extends Activity {
    public SharedPreferences sharedPreferences;
    private TextView shoppingTitleTV;
    private String LOG_TAG = "ShoppingActivity LOG";
    private StringBuilder builder = new StringBuilder();
    private String shoppingData;
    private ImageView mainAlertButton;
    private ImageView pStatusButton;
    private ImageView riStatusButton;
    private ImageView nStatusButton;
    private ImageView roStatusButton;
    private static boolean firstExecution = true;
    private static String userName;
    private static boolean need;
    private static ImageView personalStatus;
    private static Integer userID;
    private Float needAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_layout);
        setupListeners();
        shoppingTitleTV = (TextView) findViewById(R.id.shoppingTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Righteous-Regular.ttf");
        shoppingTitleTV.setTypeface(myTypeface);
        askForID();
        Log.v(LOG_TAG, "Local need: " + need);
    }

    private void askForID() {
        sharedPreferences = getSharedPreferences("userNamePreference", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        sharedPreferences = getSharedPreferences("userIdPreference", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1);
        if (userName == null && userID == -1) {
            need = false;
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ShoppingActivity.this);
            //builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Who are you?");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShoppingActivity.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Pando");
            arrayAdapter.add("Rimo");
            arrayAdapter.add("Neri");
            arrayAdapter.add("Roman");

            builderSingle.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userName = arrayAdapter.getItem(which);
                    SharedPreferences.Editor editor = getSharedPreferences("userNamePreference", MODE_PRIVATE).edit();
                    editor.putString("userName", userName);
                    editor.commit();
                    if (userName.equals("Pando")) {
                        personalStatus = pStatusButton;
                        userID = 0;
                    } else if (userName.equals("Rimo")) {
                        personalStatus = riStatusButton;
                        userID = 1;
                    } else if (userName.equals("Neri")) {
                        personalStatus = nStatusButton;
                        userID = 2;
                    } else {
                        personalStatus = roStatusButton;
                        userID = 3;
                    }
                    editor = getSharedPreferences("userIdPreference", MODE_PRIVATE).edit();
                    editor.putInt("userID", userID);
                    editor.commit();

                    new Connection().execute();
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(ShoppingActivity.this);
                    //builderInner.setMessage(userName);
                    builderInner.setTitle("Ciao, " + userName + "!");
                    builderInner.setPositiveButton("Fuck you", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderInner.show();
                }
            });
            builderSingle.show();
        } else {                         //if already registered skip askForID()
            new Connection().execute();
            setStatusButton();
        }
    }


    private void setStatusButton() {
        if (userName.equals("Pando")) {
            personalStatus = pStatusButton;
        } else if (userName.equals("Rimo")) {
            personalStatus = riStatusButton;
        } else if (userName.equals("Neri")) {
            personalStatus = nStatusButton;
        } else {
            personalStatus = roStatusButton;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupListeners() {
        mainAlertButton = (ImageView) findViewById(R.id.mainAlertButton);
        pStatusButton = (ImageView) findViewById(R.id.pStatusButton);
        riStatusButton = (ImageView) findViewById(R.id.riStatusButton);
        nStatusButton = (ImageView) findViewById(R.id.nStatusButton);
        roStatusButton = (ImageView) findViewById(R.id.roStatusButton);
        if (need) {
            mainAlertButton.setImageResource(R.drawable.green_alert_button);
        } else {
            mainAlertButton.setImageResource(R.drawable.alert_button);
        }
        mainAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (need) {
                    mainAlertButton.setImageResource(R.drawable.green_alert_button);
                    new UpdateConnection().execute();
                } else {
                    mainAlertButton.setImageResource(R.drawable.alert_button);

                    final RelativeLayout ratingLayout = (RelativeLayout) findViewById(R.id.ratingLayout);
                    ratingLayout.setVisibility(View.VISIBLE);
                    final RatingBar starRatingBar = (RatingBar) findViewById(R.id.needRatingBar);
                    starRatingBar.setRating(0);
                    starRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            if (fromUser) {
                                needAmount = rating;
                                ratingLayout.setVisibility(View.GONE);
                                new UpdateConnection().execute();
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateShoppingData() throws JSONException {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://steeve.altervista.org/VecchioSito/pages/php/update_shopping_data.php");
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("User", userName);
            if (need) {
                json.put("Need", false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAlertButton.setImageResource(R.drawable.alert_button);
                        personalStatus.setImageResource(R.drawable.green_alert_button);
                        need = false;
                    }
                });
            } else {
                json.put("Need", true);
                json.put("Amount", needAmount);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAlertButton.setImageResource(R.drawable.green_alert_button);
                        personalStatus.setImageResource(R.drawable.alert_button);
                        need = true;
                    }
                });
            }

            JSONArray postjson = new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json", json.toString());
            httppost.getParams().setParameter("jsonpost", postjson);

            // Execute HTTP Post Request
            Log.v(LOG_TAG, "Created JSON: " + json);
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


    public String getShoppingData() {
        builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://steeve.altervista.org/VecchioSito/pages/php/get_shopping_data.php");
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
        shoppingData = builder.toString();
        return builder.toString();
    }

    public class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            getShoppingData();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                populateShoppingData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                updateShoppingData();
                getShoppingData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                populateShoppingData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateShoppingData() throws JSONException {
        if (JSONDecoder.getUserShoppingData(0, shoppingData)[1].equals("0")) {
            pStatusButton.setImageResource(R.drawable.green_alert_button);
        } else {
            if (JSONDecoder.getUserShoppingData(0, shoppingData)[2].equals("1")) {
                pStatusButton.setImageResource(R.drawable.alert_1_star);
            } else if (JSONDecoder.getUserShoppingData(0, shoppingData)[2].equals("2")) {
                pStatusButton.setImageResource(R.drawable.alert_2_star);
            } else {
                pStatusButton.setImageResource(R.drawable.alert_3_star);
            }
        }

        if (JSONDecoder.getUserShoppingData(1, shoppingData)[1].equals("0")) {
            riStatusButton.setImageResource(R.drawable.green_alert_button);
        } else {
            if (JSONDecoder.getUserShoppingData(1, shoppingData)[2].equals("1")) {
                riStatusButton.setImageResource(R.drawable.alert_1_star);
            } else if (JSONDecoder.getUserShoppingData(1, shoppingData)[2].equals("2")) {
                riStatusButton.setImageResource(R.drawable.alert_2_star);
            } else {
                riStatusButton.setImageResource(R.drawable.alert_3_star);
            }
        }

        if (JSONDecoder.getUserShoppingData(2, shoppingData)[1].equals("0")) {
            nStatusButton.setImageResource(R.drawable.green_alert_button);
        } else {
            if (JSONDecoder.getUserShoppingData(2, shoppingData)[2].equals("1")) {
                nStatusButton.setImageResource(R.drawable.alert_1_star);
            } else if (JSONDecoder.getUserShoppingData(2, shoppingData)[2].equals("2")) {
                nStatusButton.setImageResource(R.drawable.alert_2_star);
            } else {
                nStatusButton.setImageResource(R.drawable.alert_3_star);
            }
        }

        if (JSONDecoder.getUserShoppingData(3, shoppingData)[1].equals("0")) {
            roStatusButton.setImageResource(R.drawable.green_alert_button);
        } else {
            if (JSONDecoder.getUserShoppingData(3, shoppingData)[2].equals("1")) {
                roStatusButton.setImageResource(R.drawable.alert_1_star);
            } else if (JSONDecoder.getUserShoppingData(3, shoppingData)[2].equals("2")) {
                roStatusButton.setImageResource(R.drawable.alert_2_star);
            } else {
                roStatusButton.setImageResource(R.drawable.alert_3_star);
            }
        }

        if (JSONDecoder.getUserShoppingData(userID, shoppingData)[1].equals("0")) { //Set colore mainAlertButton all'avvio
            mainAlertButton.setImageResource(R.drawable.alert_button);
            need = false;
            Log.v(LOG_TAG, "Colore mainAlertButton: ROSSO");
        } else {
            mainAlertButton.setImageResource(R.drawable.green_alert_button);
            need = true;
            Log.v(LOG_TAG, "Colore mainAlertButton: VERDE");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
