package com.steeve.steeveapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.text.ParseException;

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
    public static Context context;
    public DebtListAdapter transactionAdapter;
    private TextView editDebtsTitleTV;

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
        new AsyncTransactionLoad().execute();
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
                addNewTransaction(firstSelectedButton.getText().toString(), secondSelectedButton.getText().toString(), Float.parseFloat(debtNum.getText().toString()));
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

    private void addNewTransaction(String user, String receiver, Float debtAmount) {
        View footerView =  ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.debts_list_item, transactionLV, false);
        transactionLV.addFooterView(footerView);
        RelativeLayout footerColorLayout1 = (RelativeLayout) footerView.findViewById(R.id.colorLayout1);
        TextView footerUserTV = (TextView) footerView.findViewById(R.id.userTextView);
        TextView footerReceiverTV = (TextView) footerView.findViewById(R.id.receiverTextView);
        TextView footerDebtAmountTV = (TextView) footerView.findViewById(R.id.debtAmountTextView);
        TextView footerEuroTV = (TextView) footerView.findViewById(R.id.euroTV);
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
            try {
                Log.v(LOG_TAG, "Setting up transactions list: ");
                setupTransactionsList();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        transactionLV = (ListView) findViewById(R.id.transactionsListView);
        transactionAdapter = new DebtListAdapter(this.getApplicationContext(), usersArray, receiversArray, debtAmountsArray);
        transactionLV.setAdapter(transactionAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
