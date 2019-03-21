package deatherapps.com.guardianconnectclient;

import android.annotation.SuppressLint;
import android.app.ActivityManager;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText USER, PASS, OLCUM, UST_LIMIT, ALT_LIMIT;
    ToggleButton KAYDET, SES,TITRESIM;
    ConnectionDetector mConnectionDetector;
    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (servisCalisiyormu()) {
            MainActivity.this.finish();
    } else {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        mConnectionDetector = new ConnectionDetector(getApplicationContext());
        LayoutInflater inflater = getLayoutInflater();
        view = inflater.inflate(R.layout.toast_layout, null);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setTitle("◄Diyabetliyiz.biz►");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        USER = findViewById(R.id.USERNAME_TV);
        PASS = findViewById(R.id.PASSWORD_TV);
        OLCUM = findViewById(R.id.OLCUM_TV);
        UST_LIMIT = findViewById(R.id.UST_LIMIT_TV);
        ALT_LIMIT = findViewById(R.id.ALT_LIMIT_TV);
        KAYDET = findViewById(R.id.KAYDET_BTN);
        SES = findViewById(R.id.SES_BTN);
        TITRESIM = findViewById(R.id.TITRESIM_BTN);
        KAYDET.setChecked(false);
        SES.setChecked(false);
        TITRESIM.setChecked(false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        USER.setText(preferences.getString("user", ""));
        PASS.setText(preferences.getString("pass", ""));
        OLCUM.setText(String.valueOf(preferences.getInt("olcum", 5) / 1000 / 60));
        UST_LIMIT.setText(String.valueOf(preferences.getInt("ust", 180)));
        ALT_LIMIT.setText(String.valueOf(preferences.getInt("alt", 90)));
        SES.setChecked(preferences.getBoolean("sbtn", false));
        TITRESIM.setChecked(preferences.getBoolean("tbtn", false));


            SES.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = preferences.edit();
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    editor.putBoolean("sbtn", true);
                    editor.commit();
                } else {
                    editor.putBoolean("sbtn", false);
                    editor.commit();
                }
            }
        });

        TITRESIM.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = preferences.edit();
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    editor.putBoolean("tbtn", true);
                    editor.commit();
                } else {
                    editor.putBoolean("tbtn", false);
                    editor.commit();
                }
            }
        });



            if (mConnectionDetector.isConnectingToInternet() != false) {
                KAYDET.setOnClickListener(new View.OnClickListener() {

                    @Override

                    public void onClick(View v) {
                        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        editor = preferences.edit();
                        boolean on = ((ToggleButton) v).isChecked();

                        if (on) {

                            String USERNAME = USER.getText().toString().trim();
                            String PASSWORD = PASS.getText().toString().trim();

                            Integer OLCUMS = Integer.valueOf(OLCUM.getText().toString().trim());
                            Integer UST = Integer.valueOf(UST_LIMIT.getText().toString().trim());
                            Integer ALT = Integer.valueOf(ALT_LIMIT.getText().toString().trim());
                            if (USERNAME.matches("") || PASSWORD.matches("") || OLCUMS.toString().matches("") || UST.toString().matches("") || ALT.toString().matches("")) {

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle("Uyarı");
                                alertDialog.setMessage("Eksiksiz Doldurunuz!");
                                alertDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alertDialog.show();
                            }


                            editor.putInt("olcum", OLCUMS * 1000 * 60);
                            editor.putInt("ust", UST);
                            editor.putInt("alt", ALT);
                            editor.putBoolean("login", true);
                            editor.putString("user", USERNAME);
                            editor.putString("pass", PASSWORD);
                            editor.putBoolean("kbtn", true);

                            editor.commit();


                        }
                        internetebak();
                    }

                });


            } else {
                internetebidahabak();
            }
        }
    }


    public void internetebidahabak() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                KAYDET.setChecked(false);
                KAYDET.setText("Tekrar Dene");

                internetebak();
            }
        }, 1000);
    }
    public void internetebak() {

        ConnectionDetector mConnectionDetector;
        mConnectionDetector = new ConnectionDetector(getApplicationContext());
        if (mConnectionDetector.isConnectingToInternet() != false) {
            servisCalisiyormu();
            startService(new Intent(this, ServisKontrol.class));
            startService(new Intent(this, GCService.class));
            MainActivity.this.finish();
        } else {
            internetebidahabak();
        }
    }

    @SuppressLint("NewApi")
    public boolean servisCalisiyormu() {

        ActivityManager Amanager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Amanager.getRunningServices(Integer.MAX_VALUE)) {
            if (GCService.class.getName().equals(service.service.getClassName())) {

                return true;
            }
        }
        return false;
    }

}