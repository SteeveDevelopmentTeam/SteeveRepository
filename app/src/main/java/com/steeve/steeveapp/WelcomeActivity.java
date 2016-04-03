package com.steeve.steeveapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class WelcomeActivity extends AppCompatActivity {
    private String LOG_TAG= "WelcomeActivity";
    private SharedPreferences sharedPreferences;
    private String userName, token;
    private Integer userID;
    private GCMBroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered, updateInitiated;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private StringBuilder builder = new StringBuilder();
    private String remoteApkVersion;
    private int localVersionCode;
    private String path, remoteApkPath, downloadCounterString;
    private ImageView welcomeButton;
    private RelativeLayout changeLogLayout;
    private ProgressBar downloadProgressBar;
    private TextView downloadProgressPercentageTV, downloadInformationsTV, downloadSpeedTV;
    private long total, downloadSpeedCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isReceiverRegistered = false;
        updateInitiated = false;
        setContentView(R.layout.welcome_screen);
        if (getIntent().getAction().equals("updateAction")) {
            updateInitiated = true;
            checkForUpdates();
        } else { checkForUpdates(); }
        setButtonListener();
        setupUserPreferences();
    }


    private void checkForUpdates() {
        new CheckForUpdatesAsync().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setupUserPreferences() {
        sharedPreferences = getSharedPreferences("userDataPreferences", Context.MODE_PRIVATE);
        Integer retrievedCounter = sharedPreferences.getInt("loginCounter", 0);
        SharedPreferences.Editor editor = getSharedPreferences("userDataPreferences", MODE_PRIVATE).edit();
        editor.putInt("loginCounter", retrievedCounter+1);
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
            arrayAdapter.add("Angie");

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
                    } else if (userName.equals("Roman")) {
                        userID = 3;
                    } else { userID = 4; }
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
        downloadProgressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        downloadProgressPercentageTV = (TextView) findViewById(R.id.downloadProgressPercentageTV);
        downloadInformationsTV = (TextView) findViewById(R.id.downloadInformationsTV);
        changeLogLayout = (RelativeLayout) findViewById(R.id.changeLogLayout);
        downloadSpeedTV = (TextView) findViewById(R.id.downloadSpeedTV);
        welcomeButton = (ImageView) findViewById(R.id.welcomeButton);
        welcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Click received!");
                Intent openMainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(openMainIntent);
            }
        });
    }


    public class CheckForUpdatesAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            getDbApkVersion();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    private void getDbApkVersion() {
        builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://steeve.altervista.org/AutoUpdateAPK/get_apk_version.php");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
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
        remoteApkVersion = builder.toString();
        checkLocalVersion();
    }

    private void checkLocalVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        localVersionCode = packageInfo.versionCode;
        Log.v(LOG_TAG, "Local apk version: " + localVersionCode);
        getApkUpdate();
    }

    private void getApkUpdate() {
        try {
            if (!Integer.toString(localVersionCode).equals(JSONDecoder.getRemoteApkVersion(remoteApkVersion)[0])) {
                Log.v(LOG_TAG, "Found updated version of APK!");
                if (updateInitiated) {
                    new UpdateAppAsync().execute();
                    Log.v(LOG_TAG, "Update procedure started from notification");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeLogLayout.setVisibility(View.GONE);
                            downloadProgressBar.setVisibility(View.VISIBLE);
                            downloadInformationsTV.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    final RelativeLayout updateDialogLayout = (RelativeLayout) findViewById(R.id.updateDialogLayout);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDialogLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    Button yesButton = (Button) findViewById(R.id.updateYesButton);
                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UpdateAppAsync().execute();
                            Log.v(LOG_TAG, "Update procedure started");
                            updateDialogLayout.setVisibility(View.GONE);
                            changeLogLayout.setVisibility(View.GONE);
                            downloadProgressBar.setVisibility(View.VISIBLE);
                            downloadInformationsTV.setVisibility(View.VISIBLE);
                        }
                    });

                    Button noButton = (Button) findViewById(R.id.updateNoButton);
                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateDialogLayout.setVisibility(View.GONE);
                            changeLogLayout.setVisibility(View.GONE);
                        }
                    });
                }
                File previousApkPath = new File( Environment.getExternalStorageDirectory().getPath()+"/steeveapp-updateapk-v"+Integer.toString(Integer.parseInt(JSONDecoder.getRemoteApkVersion(remoteApkVersion)[0]) - 1)+".apk");
                if (previousApkPath.exists()) {
                    previousApkPath.delete();
                }
            }

            ImageView changeLogIV = (ImageView) findViewById(R.id.changeLogIV);
            changeLogIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (welcomeButton.getVisibility() == View.VISIBLE) {
                        welcomeButton.setVisibility(View.GONE);
                        changeLogLayout.setVisibility(View.VISIBLE);
                        TextView changeLogTV = (TextView) findViewById(R.id.changeLogTV);
                        TextView apkSizeTV = (TextView) findViewById(R.id.apkSizeTV);
                        try {
                            changeLogTV.setText(JSONDecoder.getRemoteApkVersion(remoteApkVersion)[1]);
                            int apkSize = (Integer.parseInt(JSONDecoder.getRemoteApkVersion(remoteApkVersion)[2]));
                            String apkSizeString;
                            DecimalFormat dec = new DecimalFormat("0.00");
                            double m = apkSize/1024000.0;
                            double g = apkSize/1048576000.0;
                            double t = apkSize/1073741824000.0;
                            if (t > 1) {
                                apkSizeString = dec.format(t).concat("TB");
                            } else if (g > 1) {
                                apkSizeString = dec.format(g).concat("GB");
                            } else if (m > 1) {
                                apkSizeString = dec.format(m).concat("MB");
                            } else {
                                apkSizeString = dec.format(apkSize).concat("KB");
                            }
                            apkSizeTV.setText(apkSizeString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        welcomeButton.setVisibility(View.VISIBLE);
                        changeLogLayout.setVisibility(View.GONE);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class UpdateAppAsync extends AsyncTask {

        @Override
        protected String doInBackground(Object[] params) {
            try {
                path = Environment.getExternalStorageDirectory().getPath()+"/steeveapp-updateapk-v"+(Integer.toString(Integer.parseInt(JSONDecoder.getRemoteApkVersion(remoteApkVersion)[0])+1))+".apk";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            remoteApkPath = "http://steeve.altervista.org/AutoUpdateAPK/app-debug.apk";
            try {
                URL url = new URL(remoteApkPath);
                URLConnection connection = url.openConnection();
                connection.connect();

                long fileLength = Long.parseLong(JSONDecoder.getRemoteApkVersion(remoteApkVersion)[2]);
                Log.v(LOG_TAG, "File lenght: "+ fileLength);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);
                Log.v(LOG_TAG, "APK name: " + path);

                byte data[] = new byte[1024];
                total = 0;
                downloadSpeedCounter = 0;
                int count;

                Looper l = Looper.getMainLooper();
                final Handler h = new Handler(l);
                final int delay = 1000;

                Runnable downloadRunnable = new Runnable(){
                    public void run(){
                        DecimalFormat dec = new DecimalFormat("0.00");
                        double k = (total-downloadSpeedCounter)/1024.0;
                        double m = (total-downloadSpeedCounter)/1024000.0;

                        if (m > 1) {
                            downloadCounterString = dec.format(m).concat("MB/s");
                        } else {
                            downloadCounterString = dec.format(k).concat("KB/s");
                        }
                        Log.v(LOG_TAG, "Download info:    Total: " +total+ "   DownloadSpeedCounter: " +downloadSpeedCounter+ "    Subtranction: "+(total-downloadSpeedCounter));
                        downloadSpeedTV.setText(downloadCounterString);
                        downloadSpeedCounter = total;
                        h.postDelayed(this, delay);
                    }
                };
                h.postDelayed(downloadRunnable, delay);

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                h.removeCallbacks(downloadRunnable);
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Well that didn't work out so well...");
                Log.e(LOG_TAG, e.getMessage());
            }
            return path;
        }

        @Override
        protected void onProgressUpdate(Object[] progress) {
            downloadProgressBar.setProgress((int) progress[0]);
            if ((int)progress[0]<=100) {
                downloadProgressPercentageTV.setText(progress[0] + "%");
            }
            super.onProgressUpdate(progress);
        }


        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(Object o) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            Log.v(LOG_TAG, "About to install new .apk");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(i);
        }
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
