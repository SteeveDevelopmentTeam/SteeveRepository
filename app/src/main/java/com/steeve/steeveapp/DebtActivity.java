package com.steeve.steeveapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
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
    public String dbDataSummary, debtPreference, creditPreference, temporaryDebtPreference, temporaryCreditPreference;
    private ProgressDialog progress;
    private Boolean isPopupShowing = false;
    private TextView debtActivityTitleTV;
    private SharedPreferences sharedPreferences;
    private Integer userID;
    private Float pandoCreditTotal;
    private Float pandoDebitTotal;
    private Float rimoCreditTotal;
    private Float rimoDebitTotal;
    private Float neriCreditTotal;
    private Float neriDebitTotal;
    private Float romanCreditTotal;
    private Float romanDebitTotal;
    private Float angieCreditTotal;
    private Float angieDebitTotal;
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

    private void setDebtPreferences() {
        debtPreference = sharedPreferences.getString("debt", null);
        creditPreference = sharedPreferences.getString("credit", null);
        if ( debtPreference == null || creditPreference == null) {
            switch (userID) {
                case 0:
                    debtPreference = pandoDebitTotal.toString();
                    creditPreference = pandoCreditTotal.toString();
                    break;
                case 1:
                    debtPreference = rimoDebitTotal.toString();
                    creditPreference = rimoCreditTotal.toString();
                    break;
                case 2:
                    debtPreference = neriDebitTotal.toString();
                    creditPreference = neriCreditTotal.toString();
                    break;
                case 3:
                    debtPreference = romanDebitTotal.toString();
                    creditPreference = romanCreditTotal.toString();
                    break;
                default:
                    debtPreference = angieDebitTotal.toString();
                    creditPreference = angieCreditTotal.toString();
                    break;
            }
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        new Connection().execute();
    }

    private void setupListeners() {
        ImageButton editDebtsButton = (ImageButton) findViewById(R.id.debtsEditButton);
        editDebtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingToEditDebtsActivity = true;
                Intent openEditIntent = new Intent(getApplicationContext(), EditDebtsActivity.class);
                startActivityForResult(openEditIntent, 2);
            }
        });
    }

    private void populatePandoDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDuser4 = (TextView) popupWindow.findViewById(R.id.sumTVDuser4);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);
        TextView sumTVDdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt4);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCuser4 = (TextView) popupWindow.findViewById(R.id.sumTVCuser4);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);
        TextView sumTVCdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt4);

        sumUserId.setText("Pando");

        Float[] pandoSummary = JSONDecoder.getUserSummary(userID, dbDataSummary);
        if (pandoSummary[1] != 0) {
            if (pandoSummary[1] > 0) {
                sumTVDuser1.setText("Rimo: ");
                sumTVDdebt1.setText(Float.toString(Math.abs(pandoSummary[1])));
            } else {
                sumTVCuser1.setText("Rimo: ");
                sumTVCdebt1.setText(Float.toString(Math.abs(pandoSummary[1])));
            }
        }

        if (pandoSummary[2] != 0) {
            if (pandoSummary[2] > 0) {
                sumTVDuser2.setText("Neri: ");
                sumTVDdebt2.setText(Float.toString(Math.abs(pandoSummary[2])));
            } else {
                sumTVCuser2.setText("Neri: ");
                sumTVCdebt2.setText(Float.toString(Math.abs(pandoSummary[2])));
            }
        }

        if (pandoSummary[3] != 0) {
            if (pandoSummary[3] > 0) {
                sumTVDuser3.setText("Roman: ");
                sumTVDdebt3.setText(Float.toString(Math.abs(pandoSummary[3])));
            } else {
                sumTVCuser3.setText("Roman: ");
                sumTVCdebt3.setText(Float.toString(Math.abs(pandoSummary[3])));
            }
        }

        if (pandoSummary[4] != 0) {
            if (pandoSummary[4] > 0) {
                sumTVDuser4.setText("Angie: ");
                sumTVDdebt4.setText(Float.toString(Math.abs(pandoSummary[4])));
            } else {
                sumTVCuser4.setText("Angie: ");
                sumTVCdebt4.setText(Float.toString(Math.abs(pandoSummary[4])));
            }
        }
    }


    private void populateRimoDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDuser4 = (TextView) popupWindow.findViewById(R.id.sumTVDuser4);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);
        TextView sumTVDdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt4);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCuser4 = (TextView) popupWindow.findViewById(R.id.sumTVCuser4);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);
        TextView sumTVCdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt4);

        sumUserId.setText("Rimo");
        Float[] rimoSummary = JSONDecoder.getUserSummary(userID, dbDataSummary);
        if (rimoSummary[0] != 0) {
            if (rimoSummary[0] > 0) {
                sumTVDuser1.setText("Pando: ");
                sumTVDdebt1.setText(Float.toString(Math.abs(rimoSummary[0])));
            } else {
                sumTVCuser1.setText("Pando: ");
                sumTVCdebt1.setText(Float.toString(Math.abs(rimoSummary[0])));
            }
        }

        if (rimoSummary[2] != 0) {
            if (rimoSummary[2] > 0) {
                sumTVDuser2.setText("Neri: ");
                sumTVDdebt2.setText(Float.toString(Math.abs(rimoSummary[2])));
            } else {
                sumTVCuser2.setText("Neri: ");
                sumTVCdebt2.setText(Float.toString(Math.abs(rimoSummary[2])));
            }
        }

        if (rimoSummary[3] != 0) {
            if (rimoSummary[3] > 0) {
                sumTVDuser3.setText("Roman: ");
                sumTVDdebt3.setText(Float.toString(Math.abs(rimoSummary[3])));
            } else {
                sumTVCuser3.setText("Roman: ");
                sumTVCdebt3.setText(Float.toString(Math.abs(rimoSummary[3])));
            }
        }

        if (rimoSummary[4] != 0) {
            if (rimoSummary[4] > 0) {
                sumTVDuser4.setText("Angie: ");
                sumTVDdebt4.setText(Float.toString(Math.abs(rimoSummary[4])));
            } else {
                sumTVCuser4.setText("Angie: ");
                sumTVCdebt4.setText(Float.toString(Math.abs(rimoSummary[4])));
            }
        }
    }


    private void populateNeriDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDuser4 = (TextView) popupWindow.findViewById(R.id.sumTVDuser4);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);
        TextView sumTVDdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt4);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCuser4 = (TextView) popupWindow.findViewById(R.id.sumTVCuser4);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);
        TextView sumTVCdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt4);

        sumUserId.setText("Neri");
        Float[] neriSummary = JSONDecoder.getUserSummary(userID, dbDataSummary);
        if (neriSummary[0] != 0) {
            if (neriSummary[0] > 0) {
                sumTVDuser1.setText("Pando: ");
                sumTVDdebt1.setText(Float.toString(Math.abs(neriSummary[0])));
            } else {
                sumTVCuser1.setText("Pando: ");
                sumTVCdebt1.setText(Float.toString(Math.abs(neriSummary[0])));
            }
        }

        if (neriSummary[1] != 0) {
            if (neriSummary[1] > 0) {
                sumTVDuser2.setText("Rimo: ");
                sumTVDdebt2.setText(Float.toString(Math.abs(neriSummary[1])));
            } else {
                sumTVCuser2.setText("Rimo: ");
                sumTVCdebt2.setText(Float.toString(Math.abs(neriSummary[1])));
            }
        }

        if (neriSummary[3] != 0) {
            if (neriSummary[3] > 0) {
                sumTVDuser3.setText("Roman: ");
                sumTVDdebt3.setText(Float.toString(Math.abs(neriSummary[3])));
            } else {
                sumTVCuser3.setText("Roman: ");
                sumTVCdebt3.setText(Float.toString(Math.abs(neriSummary[3])));
            }
        }

        if (neriSummary[4] != 0) {
            if (neriSummary[4] > 0) {
                sumTVDuser4.setText("Angie: ");
                sumTVDdebt4.setText(Float.toString(Math.abs(neriSummary[4])));
            } else {
                sumTVCuser4.setText("Angie: ");
                sumTVCdebt4.setText(Float.toString(Math.abs(neriSummary[4])));
            }
        }
    }


    private void populateRomanDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDuser4 = (TextView) popupWindow.findViewById(R.id.sumTVDuser4);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);
        TextView sumTVDdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt4);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCuser4 = (TextView) popupWindow.findViewById(R.id.sumTVCuser4);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);
        TextView sumTVCdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt4);

        sumUserId.setText("Roman");
        Float[] romanSummary = JSONDecoder.getUserSummary(userID, dbDataSummary);
        if (romanSummary[0] != 0) {
            if (romanSummary[0] > 0) {
                sumTVDuser1.setText("Pando: ");
                sumTVDdebt1.setText(Float.toString(Math.abs(romanSummary[0])));
            } else {
                sumTVCuser1.setText("Pando: ");
                sumTVCdebt1.setText(Float.toString(Math.abs(romanSummary[0])));
            }
        }

        if (romanSummary[1] != 0) {
            if (romanSummary[1] > 0) {
                sumTVDuser2.setText("Rimo: ");
                sumTVDdebt2.setText(Float.toString(Math.abs(romanSummary[1])));
            } else {
                sumTVCuser2.setText("Rimo: ");
                sumTVCdebt2.setText(Float.toString(Math.abs(romanSummary[1])));
            }
        }

        if (romanSummary[2] != 0) {
            if (romanSummary[2] > 0) {
                sumTVDuser3.setText("Neri: ");
                sumTVDdebt3.setText(Float.toString(Math.abs(romanSummary[2])));
            } else {
                sumTVCuser3.setText("Neri: ");
                sumTVCdebt3.setText(Float.toString(Math.abs(romanSummary[2])));
            }
        }

        if (romanSummary[4] != 0) {
            if (romanSummary[4] > 0) {
                sumTVDuser4.setText("Angie: ");
                sumTVDdebt4.setText(Float.toString(Math.abs(romanSummary[4])));
            } else {
                sumTVCuser4.setText("Angie: ");
                sumTVCdebt4.setText(Float.toString(Math.abs(romanSummary[4])));
            }
        }
    }


    private void populateAngieDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDuser4 = (TextView) popupWindow.findViewById(R.id.sumTVDuser4);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);
        TextView sumTVDdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt4);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCuser4 = (TextView) popupWindow.findViewById(R.id.sumTVCuser4);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);
        TextView sumTVCdebt4 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt4);

        sumUserId.setText("Angie");
        Float[] angieSummary = JSONDecoder.getUserSummary(userID, dbDataSummary);
        if (angieSummary[0] != 0) {
            if (angieSummary[0] > 0) {
                sumTVDuser1.setText("Pando: ");
                sumTVDdebt1.setText(Float.toString(Math.abs(angieSummary[0])));
            } else {
                sumTVCuser1.setText("Pando: ");
                sumTVCdebt1.setText(Float.toString(Math.abs(angieSummary[0])));
            }
        }

        if (angieSummary[1] != 0) {
            if (angieSummary[1] > 0) {
                sumTVDuser2.setText("Rimo: ");
                sumTVDdebt2.setText(Float.toString(Math.abs(angieSummary[1])));
            } else {
                sumTVCuser2.setText("Rimo: ");
                sumTVCdebt2.setText(Float.toString(Math.abs(angieSummary[1])));
            }
        }

        if (angieSummary[2] != 0) {
            if (angieSummary[2] > 0) {
                sumTVDuser3.setText("Neri: ");
                sumTVDdebt3.setText(Float.toString(Math.abs(angieSummary[2])));
            } else {
                sumTVCuser3.setText("Neri: ");
                sumTVCdebt3.setText(Float.toString(Math.abs(angieSummary[2])));
            }
        }

        if (angieSummary[3] != 0) {
            if (angieSummary[3] > 0) {
                sumTVDuser4.setText("Roman: ");
                sumTVDdebt4.setText(Float.toString(Math.abs(angieSummary[3])));
            } else {
                sumTVCuser4.setText("Roman: ");
                sumTVCdebt4.setText(Float.toString(Math.abs(angieSummary[3])));
            }
        }
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
                        setDebtPreferences();
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
        pandoCreditTotal = 0f;
        pandoDebitTotal =  0f;
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
            if (!temporaryCreditPreference.equals(pandoCreditTotal.toString()) || !temporaryDebtPreference.equals(pandoDebitTotal.toString())) {
                SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                editor.putString("debt", pandoDebitTotal.toString());
                editor.putString("credit", pandoCreditTotal.toString());
                editor.apply();
            }
        }
        
        

        //Carico dati Rimo
        Float [] rimoData = JSONDecoder.getUserSummary(1, dbDataSummary);
        rimoCreditTotal = 0f;
        rimoDebitTotal =  0f;
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
            if (!temporaryCreditPreference.equals(rimoCreditTotal)) {
                SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                editor.putString("debt", rimoDebitTotal.toString());
                editor.putString("credit", rimoCreditTotal.toString());
                editor.apply();
            }
        }


        //Carico dati Neri
        Float [] neriData = JSONDecoder.getUserSummary(2, dbDataSummary);
        neriCreditTotal = 0f;
        neriDebitTotal =  0f;
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
            if (!temporaryCreditPreference.equals(neriCreditTotal)) {
                SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                editor.putString("debt", neriDebitTotal.toString());
                editor.putString("credit", neriCreditTotal.toString());
                editor.apply();
            }
        }


        //Carico dati Roman
        Float [] romanData = JSONDecoder.getUserSummary(3, dbDataSummary);
        romanCreditTotal = 0f;
        romanDebitTotal =  0f;
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
            if (!temporaryCreditPreference.equals(romanCreditTotal)) {
                Log.v(LOG_TAG, "UPDATING Debt and Credit");
                SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                editor.putString("debt", romanDebitTotal.toString());
                editor.putString("credit", romanCreditTotal.toString());
                editor.apply();
            }
        }


        //Carico dati Angie
        Float [] angieData = JSONDecoder.getUserSummary(4, dbDataSummary);
        angieCreditTotal = 0f;
        angieDebitTotal =  0f;
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
            if (!temporaryCreditPreference.equals(angieCreditTotal)) {
                SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                editor.putString("debt", angieDebitTotal.toString());
                editor.putString("credit", angieCreditTotal.toString());
                editor.apply();
            }
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
