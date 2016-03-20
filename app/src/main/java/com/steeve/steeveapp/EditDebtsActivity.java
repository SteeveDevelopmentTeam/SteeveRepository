package com.steeve.steeveapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Roman on 02/03/2016.
 */
public class EditDebtsActivity  extends Activity{
    private ToggleButton tbP1;
    private ToggleButton tbRi1;
    private ToggleButton tbRo1;
    private ToggleButton tbN1;
    private ToggleButton tbP2;
    private ToggleButton tbRi2;
    private ToggleButton tbRo2;
    private ToggleButton tbN2;
    private ImageButton editDebtsTickButton;
    private EditText debtNum;
    private ToggleButton firstSelectedButton;
    private ToggleButton secondSelectedButton;
    private String LOG_TAG = "EditDebtsActivity LOG";
    private HttpClient httpClient;
    private Switch DCswitch;
    public StringBuilder builder = new StringBuilder();
    private String transactionData;
    private ListView transactionLV;
    private String [] usersArray;
    private String [] receiversArray;
    private String[] debtAmountsArray;
    private String [] timeStampsArray;
    public static Context context;
    public DebtListAdapter transactionAdapter;
    private TextView editDebtsTitleTV;
    private ProgressBar progressBarEditDebt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.edit_debts_layout);
        setToggleButtonListeners();
        editDebtsTitleTV = (TextView) findViewById(R.id.editDebtsTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Righteous-Regular.ttf");
        editDebtsTitleTV.setTypeface(myTypeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProgressBar();
        new AsyncTransactionLoad().execute();
    }

    private void setupProgressBar() {
        progressBarEditDebt = (ProgressBar) findViewById(R.id.progressBarEditDebts);
        progressBarEditDebt.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBarEditDebt, "progress", 0, 35);
        animation.setDuration(1000); // 3.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void setToggleButtonListeners() {
        tbP1  = (ToggleButton) findViewById(R.id.toggleButtonP1);
        tbRi1 = (ToggleButton) findViewById(R.id.toggleButtonRi1);
        tbRo1 = (ToggleButton) findViewById(R.id.toggleButtonRo1);
        tbN1  = (ToggleButton) findViewById(R.id.toggleButtonN1);
        tbP2  = (ToggleButton) findViewById(R.id.toggleButtonP2);
        tbRi2 = (ToggleButton) findViewById(R.id.toggleButtonRi2);
        tbRo2 = (ToggleButton) findViewById(R.id.toggleButtonRo2);
        tbN2  = (ToggleButton) findViewById(R.id.toggleButtonN2);
        editDebtsTickButton = (ImageButton) findViewById(R.id.editDebtsTickButton);
        debtNum = (EditText) findViewById(R.id.moneyNumEditText);
        firstSelectedButton = null;
        secondSelectedButton = null;
        DCswitch = (Switch) findViewById(R.id.CDswitch);

        tbP1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbP2.setVisibility(View.INVISIBLE);
                    firstSelectedButton = tbP1;
                } else { tbP2.setVisibility(View.VISIBLE);
                }

            }
        });

        tbRi1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbRi2.setVisibility(View.INVISIBLE);
                    firstSelectedButton = tbRi1;
                } else { tbRi2.setVisibility(View.VISIBLE);
                }

            }
        });

        tbN1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbN2.setVisibility(View.INVISIBLE);
                    firstSelectedButton = tbN1;
                } else { tbN2.setVisibility(View.VISIBLE);
                }

            }
        });


        tbRo1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbRo2.setVisibility(View.INVISIBLE);
                    firstSelectedButton = tbRo1;
                } else { tbRo2.setVisibility(View.VISIBLE);
                }

            }
        });


        editDebtsTickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (debtNum.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Inserisci cifra", Toast.LENGTH_SHORT).show();
                } else {
                    if (tbP2.isChecked()) {
                        secondSelectedButton = tbP2;
                    } else if (tbRi2.isChecked()) {
                        secondSelectedButton = tbRi2;
                    } else if (tbRo2.isChecked()) {
                        secondSelectedButton = tbRo2;
                    } else if (tbN2.isChecked()) {
                        secondSelectedButton = tbN2;
                    }
                }
                if (DCswitch.isChecked()) {
                    debtNum.setText(Float.toString(Float.parseFloat(debtNum.getText().toString()) * -1));
                } //Controllo se si registra un debito o un credito
                new AsyncConnection().execute();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                addNewTransaction(firstSelectedButton.getText().toString(), secondSelectedButton.getText().toString(), Float.parseFloat(debtNum.getText().toString()), ts);
            }
        });

        DCswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (DCswitch.isChecked()) {
                    debtNum.setTextColor(Color.parseColor("#00ff00"));
                    debtNum.setHintTextColor(Color.parseColor("#00ff00"));
                } else {
                    debtNum.setTextColor(Color.parseColor("#ff0000"));
                    debtNum.setHintTextColor(Color.parseColor("#ff0000"));
                }
            }
        });
    }

    public static CharSequence createDate(String timestamp) {
        long ts = Long.parseLong(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(d);
    }

    private void addNewTransaction(String user, String receiver, Float debtAmount, String timeStamp) {
        View footerView =  ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.debts_list_item, transactionLV, false);
        transactionLV.addFooterView(footerView);
        RelativeLayout footerColorLayout1 = (RelativeLayout) footerView.findViewById(R.id.colorLayout1);
        TextView footerUserTV = (TextView) footerView.findViewById(R.id.userTextView);
        TextView footerReceiverTV = (TextView) footerView.findViewById(R.id.receiverTextView);
        TextView footerDebtAmountTV = (TextView) footerView.findViewById(R.id.debtAmountTextView);
        TextView footerEuroTV = (TextView) footerView.findViewById(R.id.euroTV);
        TextView footerTimeStamp = (TextView) footerView.findViewById(R.id.timestampTV);
        footerTimeStamp.setText(createDate(timeStamp));
        footerUserTV.setText(user);
        footerReceiverTV.setText(receiver);
        footerDebtAmountTV.setText(Float.toString(Math.abs(debtAmount)));
        if (debtAmount > 0) { // Deve avvenire dopo popolamento debtAmountTV!
            footerColorLayout1.setBackgroundResource(android.R.color.holo_red_light);
            footerUserTV.setTextColor(Color.parseColor("#FF0000"));
            footerReceiverTV.setTextColor(Color.parseColor("#00ff00"));
            footerEuroTV.setTextColor(Color.parseColor("#ff0000"));
            footerDebtAmountTV.setTextColor(Color.parseColor("#ff0000"));
        } else {
            footerUserTV.setTextColor(Color.parseColor("#00ff00"));
            footerReceiverTV.setTextColor(Color.parseColor("#ff0000"));
            footerColorLayout1.setBackgroundResource(android.R.color.holo_green_light);
            footerEuroTV.setTextColor(Color.parseColor("#00ff00"));
            footerDebtAmountTV.setTextColor(Color.parseColor("#00ff00"));
        }
        //transactionAdapter.notifyDataSetChanged();
    }


    private void updateTransactionsData() throws JSONException {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://steeve.altervista.org/VecchioSito/pages/php/update_transactions.php");
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("user", firstSelectedButton.getTextOn());
            json.put("debt", debtNum.getText());
            json.put("receiver", secondSelectedButton.getTextOn());

            long time = System.currentTimeMillis();
            Timestamp tsTemp = new Timestamp(time);
            String ts =  tsTemp.toString();
            json.put("transactiontime", ts);

            JSONArray postjson=new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost", postjson);

            // Execute HTTP Post Request
            Log.v(LOG_TAG, "Created JSON: "+ json);
            HttpResponse response = httpclient.execute(httppost);

            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.v(LOG_TAG, line);
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

        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    private class AsyncConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                updateTransactionsData();
                updateSummaryData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //Resetto la pagina per permettere un'altra transazione immediata
            RelativeLayout editDebtsLayout = (RelativeLayout) findViewById(R.id.editDebtsLayout);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editDebtsLayout.getWindowToken(), 0);
            Toast.makeText(getApplicationContext(), "Transaction recorded!", Toast.LENGTH_SHORT).show();
            debtNum.setText(null);
            if (DCswitch.isChecked()) { DCswitch.setChecked(false); }
            firstSelectedButton.setChecked(false);
            secondSelectedButton.setChecked(false);
            new AsyncTransactionLoad().execute();
        }
    }

    private void updateSummaryData() throws JSONException {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://steeve.altervista.org/VecchioSito/pages/php/update_summary.php");
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("user", firstSelectedButton.getTextOn());
            json.put("debt", debtNum.getText());
            json.put("receiver", secondSelectedButton.getTextOn());

            JSONArray postjson=new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost", postjson);

            // Execute HTTP Post Request
            Log.v(LOG_TAG, "Created Summary JSON: "+ json);
            HttpResponse response = httpclient.execute(httppost);

            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.v(LOG_TAG, line);
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

        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }


    private class AsyncTransactionLoad extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
                readDbTransactions();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBarEditDebt, "progress", 35, 100);
            animation.setDuration(500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarEditDebt.setVisibility(View.GONE);
                    try {
                        Log.v(LOG_TAG, "Setting up transactions list: ");
                        setupTransactionsList();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 700);
        }
    }



    public String readDbTransactions() {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://steeve.altervista.org/VecchioSito/pages/php/get_transations.php");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                Log.v(LOG_TAG, "I came here");
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
        Log.v(LOG_TAG, builder.toString());
        transactionData = builder.toString();
        return builder.toString();
    }

    private void setupTransactionsList() throws ParseException, JSONException {
        usersArray = JSONDecoder.getTransactionUsersData(transactionData);
        receiversArray = JSONDecoder.getTransactionReceiversData(transactionData);
        debtAmountsArray = JSONDecoder.getTransactionDebtAmountsData(transactionData);
        timeStampsArray = JSONDecoder.getTransactionDateStampsData(transactionData);
        transactionLV = (ListView) findViewById(R.id.transactionsListView);
        transactionAdapter = new DebtListAdapter(this.getApplicationContext(), usersArray, receiversArray, debtAmountsArray, timeStampsArray);
        transactionLV.setAdapter(transactionAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        Runtime.getRuntime().gc();
    }
}
