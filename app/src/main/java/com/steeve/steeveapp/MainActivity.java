package com.steeve.steeveapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Roman on 01/03/2016.
 */
public class MainActivity extends Activity{

    private static Integer loginCount;
    public SharedPreferences sharedPreferences;
    private String LOG_TAG = "MainActivityLog";
    private ListView actionList;
    public static int [] listImages;
    public static String [] listTexts;

    @Override
         protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        sharedPreferences = getSharedPreferences("loginCounterPreference", MODE_PRIVATE);
        Integer retrievedCounter = sharedPreferences.getInt("loginCounter", 0);
        loginCount = retrievedCounter;
        updateCounter();
        setupList();
    }

    private void setupList() {
        listImages = new int[]{android.R.drawable.btn_dialog, android.R.drawable.btn_radio, android.R.drawable.btn_radio};
        listTexts = new String[]{"Gimme money", "Shopping", "ProvaGCM"};
        actionList= (ListView) findViewById(R.id.mainList);
        actionList.setAdapter(new ListAdapter(this, listTexts, listImages));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("loginCounterPreference", MODE_PRIVATE).edit();
        editor.putInt("loginCounter", loginCount);
        editor.commit();
        Runtime.getRuntime().gc();
    }

    private void updateCounter() {
        loginCount++;
        TextView counterTextView = (TextView) findViewById(R.id.loginCounterText);
        counterTextView.setText(loginCount.toString());
    }

}
