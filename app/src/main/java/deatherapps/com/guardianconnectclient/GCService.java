package deatherapps.com.guardianconnectclient;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import br.com.goncalves.pugnotification.notification.PugNotification;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static deatherapps.com.guardianconnectclient.R.raw.tekli;


/**
 * Created by Proje-Kemaleddin on 22.02.2018.
 */

public class GCService extends Service {
    public GCService(){}

    private static final String CHANNEL_ID = "Diyabetliyiz.biz";
    public static String URL_JSON = "https://carelink.minimed.eu/patient/connect/ConnectViewerServlet";
    public static String BASEURL = "https://carelink.minimed.eu";
    public static String FISTNAME;
    public static String LASTNAME;
    public static String SG;
    public static String DATETIME;
    public static String SGS;
    public static String SGSDATETIME;
    public static String BATTERY;
    public static String SENSOR;
    public static String SENSORDAYTIME;
    public static String CALIBRE;
    public static String CALIBRESTATUS;
    public static String CALIBRESTATE;
    public static String TRENT;
    String jsonString = null;
    public  String USERNAME;
    public  String PASSWORD;
    public  int OLCUM;
    public  int UST_LIMIT;
    public  int ALT_LIMIT;
    public String SES;
    public String TITRE;
    PendingIntent pIntent;
    SharedPreferences preferences;
    public static JSONArray sgsArray;
    public static List<String> SGSList;
    public static List<String> SGSDATETIMEList;
    public static long[] pattern;
    AudioManager mobilemode;
    public Timer timer;
    private Uri TEKLI,IKILI,UCLU,SESSIZ;
    public NotificationManager manager;
    Intent intent;
    public Notification notification;
    public String NOTIFICATION_ID = String.valueOf(06);
    public String CHANNEL_NAME = "Ölçümler";
    public NotificationChannel channel;

    Handler handler = new Handler();
    @SuppressLint({"NewApi", "WrongConstant"})
    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        intent = new Intent(this, SgsListesi.class);
        pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        SES = String.valueOf(preferences.getBoolean("sbtn", false));
        TITRE = String.valueOf(preferences.getBoolean("tbtn", false));
        OLCUM=preferences.getInt("olcum", 5);
        mobilemode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        TEKLI=(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.tekli));
        IKILI=(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.ikili));
        UCLU=(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.uclu));
        SESSIZ=(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.bos));

        channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setSound(null, null);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(pattern);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Baslat();

                    }
                });
            }
        };

        timer = new Timer();

        timer.schedule(timerTask,1000,OLCUM);

//BUTONLAR
        switch (SES) {
            case "true":

                break;
            case "false":
                channel.setSound(SESSIZ,null);
                break;
            default:

                break;
        }
        switch (TITRE) {
            case "true":
                pattern = new long[]{500, 1000, 500, 1000, 500, 1000};
                channel.enableVibration(true);
                channel.setVibrationPattern(pattern);

                break;
            case "false":
                pattern = new long[]{0, 0};
                channel.enableVibration(false);
                channel.setVibrationPattern(pattern);
                break;
            default:

                break;
        }

        SGSList = new ArrayList<>();
        SGSDATETIMEList = new ArrayList<>();
    }
    @Nullable
    @Override
    public IBinder onBind (Intent ıntent){

        throw new UnsupportedOperationException("");
    }

    @Override

    public int onStartCommand (Intent intent,int flags, int startId){


        return Service.START_STICKY;
//                return super.onStartCommand(intent, flags, startId);
    }



    //JSON İŞLEMLERİ
    public JSONArray Baslat () {
        SGSDATETIMEList.clear();
        SGSList.clear();
        USERNAME=preferences.getString("user", "");
        PASSWORD= preferences.getString("pass", "");
        OLCUM=preferences.getInt("olcum", 5);
        UST_LIMIT=preferences.getInt("ust",180);
        ALT_LIMIT=preferences.getInt("alt", 90);
        try {

            jsonString = new VerileriCek().execute().get();
            String sonString = "[" + jsonString + "]";
            JSONArray jsonArr = new JSONArray(sonString);

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                FISTNAME = (jsonObj.getString("firstName"));
                Log.e("FISTNAME: ", FISTNAME);
                LASTNAME = (jsonObj.getString("lastName"));
                Log.e("LASTNAME: ", LASTNAME);
                BATTERY = (jsonObj.getString("medicalDeviceBatteryLevelPercent"));
                Log.e("BATTERY: ", BATTERY);
                SENSOR = (jsonObj.getString("sensorDurationHours"));
                Log.e("SENSÖR: ", SENSOR);

                if (Integer.parseInt(SENSOR) < 24) {
                    SENSOR = SENSOR;
                    SENSORDAYTIME = "Saat";
                } else {
                    SENSOR = Integer.parseInt(SENSOR) / 24 + "";
                    SENSORDAYTIME = "Gün";
                }

                CALIBRE = (jsonObj.getString("timeToNextCalibHours"));
                Log.e("CALIBRE: ", CALIBRE);
                CALIBRESTATE = (jsonObj.getString("sensorState"));
                Log.e("CALIBRE STATE: ", CALIBRESTATE);
                CALIBRESTATUS = (jsonObj.getString("calibStatus"));
                Log.e("CALIBRE STATUS: ", CALIBRESTATUS);
                TRENT = (jsonObj.getString("lastSGTrend"));
                Log.e("TRENT: ", TRENT);
                String lastSG = jsonObj.getString("lastSG");
                JSONArray lastSGArray = new JSONArray("[" + lastSG + "]");
                for (int j = 0; j < lastSGArray.length(); j++) {
                    JSONObject sgObject = lastSGArray.getJSONObject(j);
                    SG = (sgObject.getString("sg"));
                    Log.e("SG: ", SG);
                    DATETIME = (sgObject.getString("datetime"));
                    Log.e("DATETIME: ", DATETIME.substring(11, 16));
                    DATETIME = DATETIME.substring(11, 16);
                }
                String sgs = jsonObj.getString("sgs");
                sgsArray = new JSONArray(sgs);
                for (int s = 0; s < sgsArray.length(); s++) {
                    JSONObject sgsObject = sgsArray.getJSONObject(s);
                    SGS = (sgsObject.getString("sg"));
                    SGSDATETIME = (sgsObject.getString("datetime").substring(11, 16));
                    SGSList.add(SGS);
                    SGSDATETIMEList.add(SGSDATETIME);
                }

                Log.e("SGSLER: ", String.valueOf(SGSList));
//
//                    Log.e("SGSDATETIMELER: ", String.valueOf(SGSDATETIMEList));
            }
        } catch (JSONException e) {

        } catch (InterruptedException e) {

        } catch (ExecutionException e) {

        }

        Uyar();
        WidgetUpdate();
        return null;
    }

    //BAĞLANTI KURMAK
    private class VerileriCek extends AsyncTask<Void, Void, String> {

        String URL_CONNECT = "https://carelink.minimed.eu/patient/j_security_check?j_username="+USERNAME+"&j_password="+PASSWORD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            Connection connection = null;
            Document document = null;

            try {
                connection = Jsoup.connect(URL_CONNECT).timeout(15000);
                if (connection !=null) {
                    document = connection.get();
                }else{
                    new VerileriCek().execute();
                }
            } catch(IOException e){


            }

            Connection connection2 = null;
            Document document2 = null;


            connection2 = Jsoup.connect(URL_JSON).timeout(15000);
            if (connection2 !=null) {
                connection2.cookies(connection.response().cookies());
            }else{
                new VerileriCek().execute();
            }
            try {
                document2 = connection2.ignoreContentType(true).get();
            } catch (IOException e) {


            }
//                System.out.println();
            return document2.body().childNodes().get(0).toString();


        }
    }
//UYARILAR

    @SuppressLint("NewApi")
    public void Uyar () {

        switch (TRENT) {
            case "NONE":

                break;

            case "UP":

                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " + SG)
                        .setContentText("( ↑ ) Yükseliş")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(TEKLI)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - ( ↑ ) Yükseliş", Toast.LENGTH_LONG).show();
                break;

            case "UP_DOUBLE":

                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                        .setContentText("( ↑ ↑ ) Yükseliş")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(IKILI)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - ( ↑ ↑ ) Yükseliş", Toast.LENGTH_LONG).show();

                break;

            case "UP_TRIPLE":

                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                        .setContentText("( ↑ ↑ ↑ ) Yükseliş")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(UCLU)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - ( ↑ ↑ ↑ ) Yükseliş", Toast.LENGTH_LONG).show();

                break;

            case "DOWN":
                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                        .setContentText("( ↓ ) Düşüş")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(TEKLI)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - ( ↓ ) Düşüş", Toast.LENGTH_LONG).show();

                break;

            case "DOWN_DOUBLE":

                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                        .setContentText("( ↓ ↓ ) Düşüş")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(IKILI)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - ( ↓ ↓ ) Düşüş", Toast.LENGTH_LONG).show();

                break;

            case "DOWN_TRIPLE":
                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                        .setContentText("( ↓ ↓ ↓ ) Düşüş")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(UCLU)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);
                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - ( ↓ ↓ ↓ ) Düşüş", Toast.LENGTH_LONG).show();

            default:

                break;
        }
        switch (SG) {
            case "0":
                if (CALIBRESTATE == "SENSOR_WARM_UP") {
                    notification = new Notification.Builder(this, CHANNEL_ID)
                            .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                            .setContentText("Sensör Isınıyor")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(TEKLI)
                            .setAutoCancel(false)
                            .build();
                    manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);
                    Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - Sensör Isınıyor", Toast.LENGTH_LONG).show();

                } else {

                    notification = new Notification.Builder(this, CHANNEL_ID)
                            .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                            .setContentText("Ölçüm Alınmıyor")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(TEKLI)
                            .setAutoCancel(false)
                            .build();
                    manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

                    Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - Ölçüm Alınmıyor", Toast.LENGTH_LONG).show();

                }
                break;

            default:

                break;
        }
        switch (CALIBRESTATUS) {
            case "DUENOW":
                CALIBRESTATUS = "KALİBRE İSTİYOR";
                CALIBRE = "?";
                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                        .setContentText("Sensör Isınıyor")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(TEKLI)
                        .setAutoCancel(false)
                        .build();
                manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);
                Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - Sensör Isınıyor", Toast.LENGTH_LONG).show();

                break;
            case "NORMAL":
                CALIBRESTATUS = "";
                CALIBRE = "?";
                break;
            case "LESS_THAN_SIX_HRS":
                CALIBRESTATUS = "";
                break;
            default:
                CALIBRESTATUS = "";
                break;
        }
        switch (CALIBRESTATE) {
            case "UNKNOWN":
                CALIBRESTATUS = "BİLGİ ALINAMIYOR";
                if (BATTERY == "0") {
                    CALIBRESTATUS = "SENSÖR BEKLENİYOR";
                    CALIBRE = "?";
                }
                break;
            case "SENSOR_UNPLUGGED":
                CALIBRESTATUS = "SENSÖR ÇIKARILDI";
                break;
            case "SENSOR_CONFIGURATION_REQUIRED":
                CALIBRESTATUS = "SENSÖR TAKILIYOR";
                CALIBRE = "?";
                break;
            case "SENSOR_WARM_UP":
                CALIBRESTATUS = "SENSÖR ISINIYOR";
                CALIBRE = "?";
                break;

            default:

                break;
        }

        if (Integer.parseInt(SG) >= UST_LIMIT) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                    .setContentText("Üst Limit'ten Yüksekte")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(TEKLI)
                    .setAutoCancel(false)
                    .build();
            manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);

            Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - Üst Limit'ten Yüksekte", Toast.LENGTH_LONG).show();

        }
        if (Integer.parseInt(SG) <= ALT_LIMIT) {

            if (CALIBRESTATE.matches("SENSOR_WARM_UP")) {


                Toast.makeText(this, "Sensör Isınıyor", Toast.LENGTH_LONG).show();

            } else {
                if (CALIBRESTATE.matches("UNKNOWN")) {
                    Toast.makeText(this, "Sensör Bekleniyor", Toast.LENGTH_LONG).show();

                } else {
                    notification = new Notification.Builder(this, CHANNEL_ID)
                            .setContentTitle("ÖLÇÜMÜNÜZ = " +SG)
                            .setContentText("Alt Limit'ten Aşağıda")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(TEKLI)
                            .setAutoCancel(false)
                            .build();
                    manager.notify(Integer.parseInt(NOTIFICATION_ID), notification);
                    Toast.makeText(this, "Diyabetliyiz.biz\n◄" + SG + "►" + " - Alt Limit'ten Aşağıda", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    //WİDGET UPDATE
    public void WidgetUpdate () {
        Intent intent = new Intent(this, GCCWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), GCCWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }




}

