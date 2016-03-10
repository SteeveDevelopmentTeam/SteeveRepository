package com.steeve.steeveapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
    public String dbDataSummary;
    private ProgressDialog progress;
    private Boolean isPopupShowing = false;
    private TextView debtActivityTitleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debt_activity_layout);
        setupListeners();
        progress = ProgressDialog.show(this, "Loading", "", true);
        debtActivityTitleTV = (TextView) findViewById(R.id.debtActivityTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Righteous-Regular.ttf");
        debtActivityTitleTV.setTypeface(myTypeface);
        }

    @Override
    protected void onResume () {
        super.onResume();
        new Connection().execute();
    }

    private void setupListeners() {
        final RelativeLayout pandoCDcontainer = (RelativeLayout) findViewById(R.id.pandoDCcont);
        final RelativeLayout rimoCDcontainer = (RelativeLayout) findViewById(R.id.rimoDCcont);
        final RelativeLayout neriCDcontainer = (RelativeLayout) findViewById(R.id.neriDCcont);
        final RelativeLayout romanCDcontainer = (RelativeLayout) findViewById(R.id.romanDCcont);
        ImageButton editDebtsButton = (ImageButton) findViewById(R.id.debtsEditButton);
        editDebtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMainIntent = new Intent(DebtActivity.this, EditDebtsActivity.class);
                startActivity(openMainIntent);
            }
        });

        pandoCDcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPopupShowing) {
                    Log.v(LOG_TAG, "Popup already showing!");
                } else {
                    Log.v(LOG_TAG, "Click received!");
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAsDropDown(pandoCDcontainer, 50, -30);
                    isPopupShowing = true;

                    try {
                        populatePandoDataSummary(0, popupView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RelativeLayout debtMainLayout = (RelativeLayout) findViewById(R.id.debtMainLayout); //Per chiudere il popup quando si clicca fuori di esso
                    debtMainLayout.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                            isPopupShowing = false;
                        }
                    });
                }
            }
        });

        rimoCDcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPopupShowing) {
                    Log.v(LOG_TAG, "Popup already showing!");
                } else {
                    Log.v(LOG_TAG, "Click received!");
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAsDropDown(rimoCDcontainer, 50, -30);
                    isPopupShowing = true;

                    try {
                        populateRimoDataSummary(1, popupView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RelativeLayout debtMainLayout = (RelativeLayout) findViewById(R.id.debtMainLayout); //Per chiudere il popup quando si clicca fuori di esso
                    debtMainLayout.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                            isPopupShowing = false;
                        }
                    });
                }
            }
        });

        neriCDcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPopupShowing) {
                    Log.v(LOG_TAG, "Popup already showing!");
                } else {
                    Log.v(LOG_TAG, "Click received!");
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAsDropDown(neriCDcontainer, 50, -30);
                    isPopupShowing = true;

                    try {
                        populateNeriDataSummary(2, popupView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RelativeLayout debtMainLayout = (RelativeLayout) findViewById(R.id.debtMainLayout); //Per chiudere il popup quando si clicca fuori di esso
                    debtMainLayout.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                            isPopupShowing = false;
                        }
                    });
                }
            }
        });

        romanCDcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPopupShowing) {
                    Log.v(LOG_TAG, "Popup already showing!");
                } else {
                    Log.v(LOG_TAG, "Click received!");
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAsDropDown(romanCDcontainer, 50, -30);
                    isPopupShowing = true;

                    try {
                        populateRomanDataSummary(3, popupView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RelativeLayout debtMainLayout = (RelativeLayout) findViewById(R.id.debtMainLayout); //Per chiudere il popup quando si clicca fuori di esso
                    debtMainLayout.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                            isPopupShowing = false;
                        }
                    });
                }
            }
        });
    }

    private void populatePandoDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);

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
    }


    private void populateRimoDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);

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
    }


    private void populateNeriDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);

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
    }


    private void populateRomanDataSummary(Integer userID, View popupWindow) throws JSONException {
        TextView sumUserId = (TextView) popupWindow.findViewById(R.id.sumUserId);
        TextView sumTVDuser1 = (TextView) popupWindow.findViewById(R.id.sumTVDuser1);
        TextView sumTVDuser2 = (TextView) popupWindow.findViewById(R.id.sumTVDuser2);
        TextView sumTVDuser3 = (TextView) popupWindow.findViewById(R.id.sumTVDuser3);
        TextView sumTVDdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt1);
        TextView sumTVDdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt2);
        TextView sumTVDdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVDdebt3);

        TextView sumTVCuser1 = (TextView) popupWindow.findViewById(R.id.sumTVCuser1);
        TextView sumTVCuser2 = (TextView) popupWindow.findViewById(R.id.sumTVCuser2);
        TextView sumTVCuser3 = (TextView) popupWindow.findViewById(R.id.sumTVCuser3);
        TextView sumTVCdebt1 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt1);
        TextView sumTVCdebt2 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt2);
        TextView sumTVCdebt3 = (TextView) popupWindow.findViewById(R.id.sumTVCdebt3);

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
            try {
                populateUserData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.dismiss();
        }
    }

    private void populateUserData () throws  JSONException {
        //Carico dati Pando
        TextView pandoCTV = (TextView) findViewById(R.id.pandoCtext);
        TextView pandoDTV = (TextView) findViewById(R.id.pandoDtext);
        Float [] pandoData = JSONDecoder.getUserSummary(0, dbDataSummary);
        Float pandoCreditTotal = 0f;
        Float pandoDebitTotal =  0f;
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
        pandoCTV.setText(Float.toString(Math.abs(pandoCreditTotal)));
        pandoDTV.setText(Float.toString(Math.abs(pandoDebitTotal)));
        
        

        //Carico dati Rimo
        TextView rimoCTV = (TextView) findViewById(R.id.rimoCtext);
        TextView rimoDTV = (TextView) findViewById(R.id.rimoDtext);
        Float [] rimoData = JSONDecoder.getUserSummary(1, dbDataSummary);
        Float rimoCreditTotal = 0f;
        Float rimoDebitTotal =  0f;
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
        rimoCTV.setText(Float.toString(Math.abs(rimoCreditTotal)));
        rimoDTV.setText(Float.toString(Math.abs(rimoDebitTotal)));


        //Carico dati Neri
        TextView neriCTV = (TextView) findViewById(R.id.neriCtext);
        TextView neriDTV = (TextView) findViewById(R.id.neriDtext);
        Float [] neriData = JSONDecoder.getUserSummary(2, dbDataSummary);
        Float neriCreditTotal = 0f;
        Float neriDebitTotal =  0f;
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
        neriCTV.setText(Float.toString(Math.abs(neriCreditTotal)));
        neriDTV.setText(Float.toString(Math.abs(neriDebitTotal)));


        //Carico dati Roman
        TextView romanCTV = (TextView) findViewById(R.id.romanCtext);
        TextView romanDTV = (TextView) findViewById(R.id.romanDtext);
        Float [] romanData = JSONDecoder.getUserSummary(3, dbDataSummary);
        Float romanCreditTotal = 0f;
        Float romanDebitTotal =  0f;
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
        romanCTV.setText(Float.toString(Math.abs(romanCreditTotal)));
        romanDTV.setText(Float.toString(Math.abs(romanDebitTotal)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
