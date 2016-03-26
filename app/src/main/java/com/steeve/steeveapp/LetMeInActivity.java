package com.steeve.steeveapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Roman on 20/03/2016.
 */
public class LetMeInActivity extends Activity {
    private ImageView doorIV;
    private SharedPreferences sharedPreferences;
    private String userName;
    private int userID;
    private StringBuilder builder = new StringBuilder();
    private String LOG_TAG = "LetMeInActivity";
    private String messageTarget;
    private TextView letMeInTitleTV;
    private boolean fromWidget = false;
    boolean widgetClicked;


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
        setupListeners();

    }


    private void setupListeners() {
        doorIV = (ImageView) findViewById(R.id.doorIV);
        doorIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorIV.setImageResource(R.drawable.ringbell_on);
                SendOpenMessage();
            }
        });

        final ToggleButton pOpenButton = (ToggleButton) findViewById(R.id.pOpenButton);
        pOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pOpenButton.isChecked()) {
                    messageTarget = "Pando";
                } else { messageTarget = null; }
            }
        });

        final ToggleButton riOpenButton = (ToggleButton) findViewById(R.id.riOpenButton);
        riOpenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (riOpenButton.isChecked()) {
                    messageTarget = "Rimo";
                } else { messageTarget = null; }
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
    }

    private void SendOpenMessage (){
            Intent i= new Intent(getApplicationContext(), NotificationService.class);
            i.setAction("sendOpenMessage");
            i.putExtra("messageTarget", messageTarget);
            getApplicationContext().startService(i);
    }
}
