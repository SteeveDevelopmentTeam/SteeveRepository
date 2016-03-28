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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Roman on 20/03/2016.
 */
public class LetMeInActivity extends Activity {
    private ImageView ringbellIV;
    private String LOG_TAG = "LetMeInActivity";
    private String messageTarget;
    private TextView letMeInTitleTV;
    private RelativeLayout customNotificationButtonLayout;
    private RelativeLayout customNotificationMessageLayout;
    private EditText customNotificationMessageET;
    private BroadcastReceiver receiver;


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

        final ToggleButton pOpenButton = (ToggleButton) findViewById(R.id.pOpenButton);
        pOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pOpenButton.isChecked()) {
                    messageTarget = "Pando";
                } else {
                    messageTarget = null;
                }
            }
        });

        final ToggleButton riOpenButton = (ToggleButton) findViewById(R.id.riOpenButton);
        riOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (riOpenButton.isChecked()) {
                    messageTarget = "Rimo";
                } else {
                    messageTarget = null;
                }
            }
        });

        final ToggleButton nOpenButton = (ToggleButton) findViewById(R.id.nOpenButton);
        nOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nOpenButton.isChecked()) {
                    messageTarget = "Neri";
                } else { messageTarget = null; }
            }
        });

        final ToggleButton roOpenButton = (ToggleButton) findViewById(R.id.roOpenButton);
        roOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (roOpenButton.isChecked()) {
                    messageTarget = "Roman";
                } else { messageTarget = null; }
            }
        });

        customNotificationMessageLayout = (RelativeLayout) findViewById(R.id.customNotificationMessageLayout);
        customNotificationButtonLayout = (RelativeLayout) findViewById(R.id.editNotificationMessageLayout);
        customNotificationMessageET = (EditText) findViewById(R.id.notificationCustomMessageET);
        customNotificationButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customNotificationMessageLayout.getVisibility() == View.GONE) {
                    customNotificationMessageLayout.setVisibility(View.VISIBLE);
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
