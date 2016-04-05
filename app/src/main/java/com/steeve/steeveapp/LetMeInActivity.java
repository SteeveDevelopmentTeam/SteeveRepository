package com.steeve.steeveapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by Roman on 20/03/2016.
 */
public class LetMeInActivity extends Activity {
    private ImageView ringbellIV;
    private String LOG_TAG = "LetMeInActivity";
    private String messageTarget;
    private TextView letMeInTitleTV;
    private RelativeLayout customNotificationButtonLayout, customNotificationMessageLayout, letMeInLayout;
    private EditText customNotificationMessageET;
    private BroadcastReceiver receiver;
    private ToggleButton pOpenButton, riOpenButton, nOpenButton, roOpenButton, aOpenButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.let_me_in_layout);
        letMeInTitleTV = (TextView) findViewById(R.id.letMeInTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "AllertaStencil-Regular.ttf");
        letMeInTitleTV.setTypeface(myTypeface);
        setupListeners();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                resetDefaultRingbellColor();
            }
        };
        SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
        editor.putBoolean("isLetMeInActivityRunning", true);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(MyGcmListenerService.resetDoorbellUIIntent)
        );
    }


    private void setupListeners() {
        ringbellIV = (ImageView) findViewById(R.id.doorIV);
        ringbellIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringbellIV.setImageResource(R.drawable.ringbell_on);
                SendOpenMessage();
            }
        });

        pOpenButton = (ToggleButton) findViewById(R.id.pOpenButton);
        pOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pOpenButton.isChecked()) {
                    messageTarget = "Pando";
                    riOpenButton.setChecked(false);
                    nOpenButton.setChecked(false);
                    roOpenButton.setChecked(false);
                    aOpenButton.setChecked(false);
                } else {
                    messageTarget = null;
                }
            }
        });

        riOpenButton = (ToggleButton) findViewById(R.id.riOpenButton);
        riOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (riOpenButton.isChecked()) {
                    messageTarget = "Rimo";
                    pOpenButton.setChecked(false);
                    nOpenButton.setChecked(false);
                    roOpenButton.setChecked(false);
                    aOpenButton.setChecked(false);
                } else {
                    messageTarget = null;
                }
            }
        });

        nOpenButton = (ToggleButton) findViewById(R.id.nOpenButton);
        nOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nOpenButton.isChecked()) {
                    messageTarget = "Neri";
                    pOpenButton.setChecked(false);
                    riOpenButton.setChecked(false);
                    roOpenButton.setChecked(false);
                    aOpenButton.setChecked(false);
                } else { messageTarget = null; }
            }
        });

        roOpenButton = (ToggleButton) findViewById(R.id.roOpenButton);
        roOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (roOpenButton.isChecked()) {
                    messageTarget = "Roman";
                    pOpenButton.setChecked(false);
                    riOpenButton.setChecked(false);
                    nOpenButton.setChecked(false);
                    aOpenButton.setChecked(false);
                } else { messageTarget = null; }
            }
        });

        aOpenButton = (ToggleButton) findViewById(R.id.aOpenButton);
        aOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aOpenButton.isChecked()) {
                    messageTarget = "Angie";
                    pOpenButton.setChecked(false);
                    riOpenButton.setChecked(false);
                    nOpenButton.setChecked(false);
                    roOpenButton.setChecked(false);
                } else {
                    messageTarget = null;
                    Toast.makeText(getApplicationContext(), "Please select a target", Toast.LENGTH_SHORT);
                }
            }
        });

        customNotificationMessageLayout = (RelativeLayout) findViewById(R.id.customNotificationMessageLayout);
        customNotificationButtonLayout = (RelativeLayout) findViewById(R.id.editNotificationMessageLayout);
        customNotificationMessageET = (EditText) findViewById(R.id.notificationCustomMessageET);
        letMeInLayout = (RelativeLayout) findViewById(R.id.letMeInLayout);
        customNotificationButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customNotificationMessageLayout.getVisibility() == View.GONE) {
                    customNotificationMessageLayout.setVisibility(View.VISIBLE);
                    customNotificationMessageET.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(customNotificationMessageET, 0);
                } else {
                    customNotificationMessageLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    private void SendOpenMessage (){
        Intent i= new Intent(getApplicationContext(), NotificationService.class);
        i.setAction("sendOpenMessage");
        if (customNotificationMessageET.getText() != null) {
            i.putExtra("customTextToSpeech", customNotificationMessageET.getText().toString());
        }
        i.putExtra("messageTarget", messageTarget);
        getApplicationContext().startService(i);
        if (customNotificationMessageET.getText() != null) {
            customNotificationMessageET.setText("");
            customNotificationMessageLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
        editor.putBoolean("isLetMeInActivityRunning", false);
        editor.apply();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void resetDefaultRingbellColor() {
        ringbellIV.setImageResource(R.drawable.ringbell_off);
    }
}
