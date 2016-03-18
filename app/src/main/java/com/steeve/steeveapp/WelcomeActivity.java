package com.steeve.steeveapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class WelcomeActivity extends AppCompatActivity {
    private String LOG_TAG= "WelcomeActivity";
    private SharedPreferences sharedPreferences;
    private String userName, token;
    private Integer userID;
    private GCMBroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isReceiverRegistered = false;
        setContentView(R.layout.welcome_screen);
        setButtonListener();
        setupUserPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setupUserPreferences() {
        sharedPreferences = getSharedPreferences("userDataPreferences", Context.MODE_PRIVATE);
        Integer retrievedCounter = sharedPreferences.getInt("loginCounter", 1);
        SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
        editor.putInt("loginCounter", retrievedCounter++);
        editor.apply();
        userName = sharedPreferences.getString("userName", null);
        userID = sharedPreferences.getInt("userID", -1);
        if (userName == null && userID == -1) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(WelcomeActivity.this);
            //builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Who are you?");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WelcomeActivity.this, android.R.layout.select_dialog_singlechoice);
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
                    SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                    editor.putString("userName", userName);
                    editor.apply();
                    if (userName.equals("Pando")) {
                        userID = 0;
                    } else if (userName.equals("Rimo")) {
                        userID = 1;
                    } else if (userName.equals("Neri")) {
                        userID = 2;
                    } else {
                        userID = 3;
                    }
                    editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
                    editor.putInt("userID", userID);
                    editor.apply();
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(WelcomeActivity.this);
                    builderInner.setTitle("Ciao, " + userName + "!");
                    builderInner.setPositiveButton("Fuck you", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            registerGCMUser();
                        }
                    });
                    builderInner.show();
                }
            });
            builderSingle.show();
        } else { registerGCMUser(); }

        token = sharedPreferences.getString("token", null);
        Log.v(LOG_TAG, "Registered TOKEN for this client: "+token);
        String allPreferences = sharedPreferences.getAll().toString();
        Log.v(LOG_TAG, "SharedPreferences data: " + allPreferences);
    }

    private void registerGCMUser() {
        mRegistrationBroadcastReceiver = new GCMBroadcastReceiver();
        registerReceiver();

        if (checkPlayServices()) {             // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void setButtonListener() {
        Button welcomeButton = (Button) findViewById(R.id.welcomeButton);
        welcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Click received!");
                Intent openMainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(openMainIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
