package com.steeve.steeveapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Roman on 01/03/2016.
 */
public class MainActivity extends Activity {

    private static Integer loginCount;
    public SharedPreferences sharedPreferences;
    private String LOG_TAG = "MainActivityLog";
    private ListView actionList;
    public static int [] listImages;
    public static String [] listTexts;
    private String userName, userDebt, userCredit, need, needAmount;
    private Integer userID;
    private static LayoutInflater inflater = null;
    private Context context;
    private DrawerLayout drawer;
    private TextView adminPanelTV;

    @Override
         protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences("userDataPreferences", MODE_PRIVATE);
        Log.v(LOG_TAG, "MainActivity preferences: " + sharedPreferences.getAll().toString());
        setupStats();
        setupListeners();
        setupList();
        setupDrawer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setupStats();
    }



    private void setupStats() {
        userName = sharedPreferences.getString("userName", null);
        userID = sharedPreferences.getInt("userID", -1);
        userDebt = sharedPreferences.getString("debt", null);
        userCredit = sharedPreferences.getString("credit", null);
        need = sharedPreferences.getString("need", null);
        needAmount = sharedPreferences.getString("needAmount", null);
        loginCount = sharedPreferences.getInt("loginCounter", 1);
        TextView loginCountTV = (TextView) findViewById(R.id.loginCounterText);
        loginCountTV.setText(loginCount.toString());
        TextView statsUserNameTV = (TextView) findViewById(R.id.statsUserName);
        statsUserNameTV.setText(userName);
        ImageView statsProfilePic = (ImageView) findViewById(R.id.statsUserProfilePic);
        TextView statsDebtTV = (TextView) findViewById(R.id.statsUserDebt);
        TextView statsCreditTV = (TextView) findViewById(R.id.statsUserCredit);
        ImageView statsStatusIV = (ImageView) findViewById(R.id.statsStatusIV);
        Log.v(LOG_TAG, "MainActivity data: " + "userID: "+userID+"  Debit: "+userDebt+"  Credit: "+userCredit);
        if (userID.equals(0)) {
            statsProfilePic.setImageResource(R.drawable.pando_circle);
        } else if (userID.equals(1)) {
            statsProfilePic.setImageResource(R.drawable.rimo_circle);
        } else if (userID.equals(2)) {
            statsProfilePic.setImageResource(R.drawable.neri_circle);
        }  else if (userID.equals(3)) {
        statsProfilePic.setImageResource(R.drawable.roman_circle);
        } else {
            statsProfilePic.setImageResource(R.drawable.angie_circle);
        }

        if (userDebt == null) {
            statsDebtTV.setText("ND");
        } else {
            statsDebtTV.setText(Float.toString(Math.abs(Float.parseFloat(userDebt)))+"€");
        }

        if (userCredit == null) {
            statsCreditTV.setText("ND");
        } else {
            statsCreditTV.setText(Float.toString(Math.abs(Float.parseFloat(userCredit)))+"€");
        }

        try { //Try/catch necessario per evitare NPE al primo avvio
            if (!need.equals("0")) {
                switch (needAmount) {
                    case "1":
                        statsStatusIV.setImageResource(R.drawable.alert_1_star);
                        break;
                    case "2":
                        statsStatusIV.setImageResource(R.drawable.alert_2_star);
                        break;
                    case "3":
                        statsStatusIV.setImageResource(R.drawable.alert_3_star);
                        break;
                }
            }
        } catch (Exception e) {  }
    }

    private void setupDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView drawerButton = (ImageView) findViewById(R.id.drawerButton);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(actionList);
            }
        });
    }

    private void setupList() {
        listImages = new int[]{R.drawable.dollar_icon2, R.drawable.shopping_icon2, R.drawable.door_icon, R.drawable.settings_icon};
        listTexts = new String[]{"Gimme money", "Shopping", "Doorbell", "Settings"};
        actionList= (ListView) findViewById(R.id.drawer_list);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeaderLogo = getLayoutInflater().inflate(R.layout.list_header_logo_layout, null);
        actionList.addHeaderView(listHeaderLogo);
        actionList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return listTexts.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            class Holder {
                TextView tv;
                ImageView img;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Holder holder = new Holder();
                View rowView;
                rowView = inflater.inflate(R.layout.list_item, null);
                holder.tv = (TextView) rowView.findViewById(R.id.tileText);
                holder.img = (ImageView) rowView.findViewById(R.id.tileImage);
                holder.tv.setText(listTexts[position]);
                Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Lobster-Regular.ttf");
                holder.tv.setTypeface(myTypeface);
                holder.img.setImageResource(listImages[position]);
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
                        Log.v(LOG_TAG, "Position: " + position);
                        switch (position) { // Using 1 and 2 index because there is a header view which occupies position 0
                            case 0:
                                Intent debtIntent = new Intent(context, DebtActivity.class);
                                startActivityForResult(debtIntent, 1);
                                break;
                            case 1:
                                Intent shoppingIntent = new Intent(context, ShoppingActivity.class);
                                startActivityForResult(shoppingIntent, 1);
                                break;
                            case 2:
                                Intent letMeInIntent = new Intent(context, LetMeInActivity.class);
                                startActivityForResult(letMeInIntent, 1);
                                break;
                            case 3:
                                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                                startActivity(settingsIntent);
                                break;
                        }
                    }
                });
                return rowView;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            Log.v(LOG_TAG, "Entered onActivityResult");
        }
    }

    private void setupListeners() {
        ImageView mainLogo = (ImageView) findViewById(R.id.mainLogo);
        mainLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducePando();
            }
        });
        adminPanelTV = (TextView) findViewById(R.id.adminPanelTV);
        if (userID == 3) {
            adminPanelTV.setVisibility(View.VISIBLE);
            adminPanelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openAdminPanelIntent = new Intent(getApplicationContext(), AdminPanelActivity.class);
                    startActivity(openAdminPanelIntent);
                }
            });
        }
    }

    private void reproducePando() {
        MediaPlayer mp;
        mp = MediaPlayer.create(this, R.raw.pando);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else { super.onBackPressed(); }
    }

    @Override
    protected void onPause() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }
}
