package com.steeve.steeveapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Roman on 01/03/2016.
 */
public class DebtActivity extends Activity{
    private String LOG_TAG = "DebtActivity LOG";
    public StringBuilder builder = new StringBuilder();
    public StringBuilder builder2 = new StringBuilder();
    public String dbDataSummary, temporaryDebtPreference, temporaryCreditPreference;
    private TextView debtActivityTitleTV;
    private SharedPreferences sharedPreferences;
    private Integer userID;
    private ProgressBar progressBarDebt;
    private boolean goingToEditDebtsActivity = false;
    private ListView debtListView;
    private DebtActivityAdapter debtListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debt_activity_layout);
        sharedPreferences = getSharedPreferences("userDataPreferences", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1);
        temporaryDebtPreference = sharedPreferences.getString("debt", null);
        temporaryCreditPreference = sharedPreferences.getString("credit", null);
        setupListeners();
        setupProgressBar();
        debtActivityTitleTV = (TextView) findViewById(R.id.debtActivityTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "AllertaStencil-Regular.ttf");
        debtActivityTitleTV.setTypeface(myTypeface);
        }

    private void setupDebtList(String dbDataSummary, String [] userDebts, String [] userCredits) {
        debtListView = (ListView) findViewById(R.id.debtListView);
        debtListAdapter = new DebtActivityAdapter(this.getApplicationContext(), userDebts, userCredits, dbDataSummary);
        debtListView.setAdapter(debtListAdapter);
        Log.v(LOG_TAG, "Adapter SET!");
    }

    private void setupProgressBar() {
        progressBarDebt = (ProgressBar) findViewById(R.id.progressBarDebt);
        progressBarDebt.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBarDebt, "progress", 0, 40);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }


    @Override
    protected void onResume () {
        super.onResume();
        new Connection().execute();
    }

    private void setupListeners() {
        TextView editDebtsButton = (TextView) findViewById(R.id.debtsEditButton);
        editDebtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingToEditDebtsActivity = true;
                Intent openEditIntent = new Intent(getApplicationContext(), EditDebtsActivity.class);
                startActivityForResult(openEditIntent, 2);
            }
        });
    }



    public String readDbDataSummary() {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://steeve.altervista.org/VecchioSito/pages/php/get_debt_summary.php");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                Log.v(LOG_TAG, "I came here in summary");
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder2.append(line);
                }
            } else {
                Log.v(LOG_TAG, "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, builder2.toString());
        dbDataSummary = builder2.toString();
        return builder.toString();
    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            readDbDataSummary();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBarDebt, "progress", 35, 100);
            animation.setDuration(500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarDebt.setVisibility(View.GONE);
                    try {
                        populateUserData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 700);
        }
    }

    private void populateUserData () throws  JSONException {
        Log.v(LOG_TAG, "POPULATING DEBT LIST");
        //Carico dati Pando
        Float [] pandoData = JSONDecoder.getUserSummary(0, dbDataSummary);
        Float pandoCreditTotal = 0f;
        Float pandoDebitTotal = 0f;
        if (pandoData[1] <= 0 ) { 
            pandoCreditTotal = pandoCreditTotal + pandoData[1];
        } else {
            pandoDebitTotal = pandoDebitTotal + pandoData[1];
        }
        if (pandoData[2] <= 0 ) {
            pandoCreditTotal = pandoCreditTotal + pandoData[2];
        } else {
            pandoDebitTotal = pandoDebitTotal + pandoData[2];
        }
        if (pandoData[3] <= 0 ) {
            pandoCreditTotal = pandoCreditTotal + pandoData[3];
        } else {
            pandoDebitTotal = pandoDebitTotal + pandoData[3];
        }
        if (pandoData[4] <= 0 ) {
            pandoCreditTotal = pandoCreditTotal + pandoData[4];
        } else {
            pandoDebitTotal = pandoDebitTotal + pandoData[4];
        }
        if (userID == 0) {
            SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
            editor.putString("debt", pandoDebitTotal.toString());
            editor.putString("credit", pandoCreditTotal.toString());
            editor.apply();
        }
        
        

        //Carico dati Rimo
        Float [] rimoData = JSONDecoder.getUserSummary(1, dbDataSummary);
        Float rimoCreditTotal = 0f;
        Float rimoDebitTotal = 0f;
        if (rimoData[0] <= 0 ) {
            rimoCreditTotal = rimoCreditTotal + rimoData[0];
        } else {
            rimoDebitTotal = rimoDebitTotal + rimoData[0];
        }
        if (rimoData[2] <= 0 ) {
            rimoCreditTotal = rimoCreditTotal + rimoData[2];
        } else {
            rimoDebitTotal = rimoDebitTotal + rimoData[2];
        }
        if (rimoData[3] <= 0 ) {
            rimoCreditTotal = rimoCreditTotal + rimoData[3];
        } else {
            rimoDebitTotal = rimoDebitTotal + rimoData[3];
        }
        if (rimoData[4] <= 0 ) {
            rimoCreditTotal = rimoCreditTotal + rimoData[4];
        } else {
            rimoDebitTotal = rimoDebitTotal + rimoData[4];
        }
        if (userID == 1) {
            SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
            editor.putString("debt", rimoDebitTotal.toString());
            editor.putString("credit", rimoCreditTotal.toString());
            editor.apply();
        }


        //Carico dati Neri
        Float [] neriData = JSONDecoder.getUserSummary(2, dbDataSummary);
        Float neriCreditTotal = 0f;
        Float neriDebitTotal = 0f;
        if (neriData[0] <= 0 ) {
            neriCreditTotal = neriCreditTotal + neriData[0];
        } else {
            neriDebitTotal = neriDebitTotal + neriData[0];
        }
        if (neriData[1] <= 0 ) {
            neriCreditTotal = neriCreditTotal + neriData[1];
        } else {
            neriDebitTotal = neriDebitTotal + neriData[1];
        }
        if (neriData[3] <= 0 ) {
            neriCreditTotal = neriCreditTotal + neriData[3];
        } else {
            neriDebitTotal = neriDebitTotal + neriData[3];
        }
        if (neriData[4] <= 0 ) {
            neriCreditTotal = neriCreditTotal + neriData[4];
        } else {
            neriDebitTotal = neriDebitTotal + neriData[4];
        }

        if (userID == 2) {
            SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
            editor.putString("debt", neriDebitTotal.toString());
            editor.putString("credit", neriCreditTotal.toString());
            editor.apply();
        }


        //Carico dati Roman
        Float [] romanData = JSONDecoder.getUserSummary(3, dbDataSummary);
        Float romanCreditTotal = 0f;
        Float romanDebitTotal = 0f;
        if (romanData[0] <= 0 ) {
            romanCreditTotal = romanCreditTotal + romanData[0];
        } else {
            romanDebitTotal = romanDebitTotal + romanData[0];
        }
        if (romanData[1] <= 0 ) {
            romanCreditTotal = romanCreditTotal + romanData[1];
        } else {
            romanDebitTotal = romanDebitTotal + romanData[1];
        }
        if (romanData[2] <= 0 ) {
            romanCreditTotal = romanCreditTotal + romanData[2];
        } else {
            romanDebitTotal = romanDebitTotal + romanData[2];
        }
        if (romanData[4] <= 0 ) {
            romanCreditTotal = romanCreditTotal + romanData[4];
        } else {
            romanDebitTotal = romanDebitTotal + romanData[4];
        }
        if (userID == 3) {
            SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
            editor.putString("debt", romanDebitTotal.toString());
            editor.putString("credit", romanCreditTotal.toString());
            editor.apply();
        }


        //Carico dati Angie
        Float [] angieData = JSONDecoder.getUserSummary(4, dbDataSummary);
        Float angieCreditTotal = 0f;
        Float angieDebitTotal = 0f;
        if (angieData[0] <= 0 ) {
            angieCreditTotal = angieCreditTotal + angieData[0];
        } else {
            angieDebitTotal = angieDebitTotal + angieData[0];
        }
        if (angieData[1] <= 0 ) {
            angieCreditTotal = angieCreditTotal + angieData[1];
        } else {
            angieDebitTotal = angieDebitTotal + angieData[1];
        }
        if (angieData[2] <= 0 ) {
            angieCreditTotal = angieCreditTotal + angieData[2];
        } else {
            angieDebitTotal = angieDebitTotal + angieData[2];
        }
        if (angieData[3] <= 0 ) {
            angieCreditTotal = angieCreditTotal + angieData[3];
        } else {
            angieDebitTotal = angieDebitTotal + angieData[3];
        }
        if (userID == 4) {
            SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
            editor.putString("debt", angieDebitTotal.toString());
            editor.putString("credit", angieCreditTotal.toString());
            editor.apply();
        }

        String [] adapterDebts = new String [] {Float.toString(pandoDebitTotal), Float.toString(rimoDebitTotal), Float.toString(neriDebitTotal), Float.toString(romanDebitTotal), Float.toString(angieDebitTotal)};
        String [] adapterCredits = new String [] {Float.toString(pandoCreditTotal), Float.toString(rimoCreditTotal), Float.toString(neriCreditTotal), Float.toString(romanCreditTotal), Float.toString(angieCreditTotal)};
        setupDebtList(dbDataSummary, adapterDebts, adapterCredits);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 2) {
            Log.v(LOG_TAG, "Entered onActivityResult, REFRESHING");
            Intent refreshIntent = getIntent();
            refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(getIntent());
        }
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "DESTROYING");
        if (!goingToEditDebtsActivity) {
            Log.v(LOG_TAG, "SETTING RESULT");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        super.onDestroy();
    }
}
