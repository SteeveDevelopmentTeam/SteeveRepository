package com.steeve.steeveapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
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
        setupDrawer();
    }

    private void setupDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView drawerButton = (ImageView) findViewById(R.id.drawerButton);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(actionList);
            }
        });
    }

    private void setupList() {
        listImages = new int[]{R.drawable.dollar_icon, R.drawable.shopping};
        listTexts = new String[]{"Gimme money", "Shopping"};
        actionList= (ListView) findViewById(R.id.drawer_list);
        View listHeaderLogo = getLayoutInflater().inflate(R.layout.list_header_logo_layout, null);
        actionList.addHeaderView(listHeaderLogo);
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
