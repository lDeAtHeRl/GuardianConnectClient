package deatherapps.com.guardianconnectclient;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import static deatherapps.com.guardianconnectclient.GCService.BATTERY;
import static deatherapps.com.guardianconnectclient.GCService.CALIBRE;
import static deatherapps.com.guardianconnectclient.GCService.CALIBRESTATUS;
import static deatherapps.com.guardianconnectclient.GCService.DATETIME;
import static deatherapps.com.guardianconnectclient.GCService.FISTNAME;
import static deatherapps.com.guardianconnectclient.GCService.LASTNAME;
import static deatherapps.com.guardianconnectclient.GCService.SENSOR;
import static deatherapps.com.guardianconnectclient.GCService.SENSORDAYTIME;
import static deatherapps.com.guardianconnectclient.GCService.SG;
import static deatherapps.com.guardianconnectclient.GCService.TRENT;


/**
 * Implementation of App Widget functionality.
 */
public class GCCWidget extends AppWidgetProvider {
    public  int ALT_LIMIT;
    public  int UST_LIMIT;
    SharedPreferences preferences;
    @SuppressLint("ResourceAsColor")
    @Override

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {for (int appWidgetId : appWidgetIds) {
                super.onUpdate(context, appWidgetManager, appWidgetIds);

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.gccwidget);
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                UST_LIMIT=preferences.getInt("ust",0);
                ALT_LIMIT=preferences.getInt("alt", 0);

                Uri uri = Uri.parse("http://www.diyabetliyiz.biz");
                Intent dbintent = new Intent(Intent.ACTION_VIEW, uri);
                PendingIntent dbwIntent = PendingIntent.getActivity(context,0, dbintent, 0);
                remoteViews.setOnClickPendingIntent(R.id.DB, dbwIntent);

                Intent Aintent = new Intent(context, Ayarlar.class);
                PendingIntent Ayrintent = PendingIntent.getActivity(context, 0, Aintent, 0);
                remoteViews.setOnClickPendingIntent(R.id.Settings_btn, Ayrintent);

                Intent Sgsintent = new Intent(context, SgsListesi.class);
                PendingIntent Sgslintent = PendingIntent.getActivity(context, 0, Sgsintent, 0);
                remoteViews.setOnClickPendingIntent(R.id.AllSg_btn, Sgslintent);


                PendingIntent Sgsl = PendingIntent.getActivity(context, 0, Sgsintent, 0);
                remoteViews.setOnClickPendingIntent(R.id.Sg_tv, Sgsl);


                remoteViews.setTextViewText(R.id.Name_tv,FISTNAME+" "+LASTNAME);
                remoteViews.setTextViewText(R.id.Battery_tv,BATTERY);
                remoteViews.setTextViewText(R.id.SensorDay_tv, SENSOR);
                remoteViews.setTextViewText(R.id.GunSaat_tv, SENSORDAYTIME);

                remoteViews.setTextViewText(R.id.Calibre_tv,CALIBRE);
                remoteViews.setTextViewText(R.id.CalibreStatus_tv,CALIBRESTATUS);
                remoteViews.setTextViewText(R.id.Sg_tv,SG);
                remoteViews.setTextViewText(R.id.Date_tv,DATETIME);

                if(Integer.parseInt(SG)>=UST_LIMIT) {
                    remoteViews.setTextColor(R.id.Sg_tv, Color.RED);

                }else  if(Integer.parseInt(SG)<=ALT_LIMIT){
                    remoteViews.setTextColor(R.id.Sg_tv, Color.RED);

                }else{
                    remoteViews.setTextColor(R.id.Sg_tv, Color.CYAN);
                }

                if(Integer.parseInt(BATTERY)<100) {
                    remoteViews.setImageViewResource(R.id.Battery_img, R.mipmap.full);
                }
                if (Integer.parseInt(BATTERY)<90){
                    remoteViews.setImageViewResource(R.id.Battery_img, R.mipmap.high);
                }
                if (Integer.parseInt(BATTERY)<70){
                    remoteViews.setImageViewResource(R.id.Battery_img, R.mipmap.middle);
                }
                if (Integer.parseInt(BATTERY)<40){
                    remoteViews.setImageViewResource(R.id.Battery_img, R.mipmap.low);
                }
                if (Integer.parseInt(BATTERY)==0){
                    remoteViews.setImageViewResource(R.id.Battery_img, R.mipmap.charge);
                }

                remoteViews.setImageViewResource(R.id.Calibre_img, R.mipmap.kalibre);
                remoteViews.setImageViewResource(R.id.SensorTime_img, R.mipmap.takvim);
                remoteViews.setImageViewResource(R.id.Settings_btn, R.mipmap.ayarlar);
                remoteViews.setImageViewResource(R.id.AllSg_btn, R.mipmap.olcumler);
                remoteViews.setImageViewResource(R.id.Refresh_btn, R.mipmap.sync);
                remoteViews.setImageViewResource(R.id.DB, R.mipmap.dblogo);


            switch (TRENT) {
                case "NONE" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.normal);

                    break;

                case "UP" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.cikis);

                    break;

                case "UP_DOUBLE" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.ikilicikis);

                    break;

                case "UP_TRIPLE" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.uclucikis);

                    break;

                case "DOWN" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.dusus);

                    break;

                case "DOWN_DOUBLE" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.ikilidusus);

                    break;

                case "DOWN_TRIPLE" :
                    remoteViews.setImageViewResource(R.id.Trent_img,R.mipmap.ucludusus);

                    break;

                default :

                    break;
            }

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

