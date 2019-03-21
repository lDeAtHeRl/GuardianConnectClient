package deatherapps.com.guardianconnectclient;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class Ayarlar extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText USER, PASS, OLCUM, UST_LIMIT, ALT_LIMIT;
    ToggleButton KAYDET, SES, TITRESIM;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        USER = findViewById(R.id.USERNAME_TV);
        PASS = findViewById(R.id.PASSWORD_TV);
        OLCUM = findViewById(R.id.OLCUM_TV);
        UST_LIMIT = findViewById(R.id.UST_LIMIT_TV);
        ALT_LIMIT = findViewById(R.id.ALT_LIMIT_TV);
        KAYDET = findViewById(R.id.KAYDET_BTN);
        SES = findViewById(R.id.SES_BTN);
        TITRESIM = findViewById(R.id.TITRESIM_BTN);

        getSupportActionBar().setTitle("Ayarlar");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        KAYDET.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = preferences.edit();
                boolean on = ((ToggleButton) v).isChecked();

                if (on) {
                    Integer OLCUMS = Integer.valueOf(OLCUM.getText().toString().trim());
                    Integer UST = Integer.valueOf(UST_LIMIT.getText().toString().trim());
                    Integer ALT = Integer.valueOf(ALT_LIMIT.getText().toString().trim());

                    String USERNAME = USER.getText().toString().trim();
                    String PASSWORD = PASS.getText().toString().trim();
                    if (USERNAME.matches("") || PASSWORD.matches("") || OLCUMS.toString().matches("") || UST.toString().matches("") || ALT.toString().matches("")) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Ayarlar.this);
                        alertDialog.setTitle("Uyarı");
                        alertDialog.setMessage("Eksiksiz Doldurunuz!");
                        alertDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                    }
                    editor.putString("user", USERNAME);
                    editor.putString("pass", PASSWORD);
                    editor.putInt("olcum", OLCUMS * 1000 * 60);
                    editor.putInt("ust", UST);
                    editor.putInt("alt", ALT);
                    editor.putBoolean("kbtn", true);
                    editor.commit();
                    alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), GCService.class);
                    alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1 * 60 * 1000 , alarmIntent);
                    startService(intent);
                    Ayarlar.this.finish();
                } else {

                }

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    this.finishAffinity();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}