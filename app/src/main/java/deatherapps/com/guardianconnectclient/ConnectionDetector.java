package deatherapps.com.guardianconnectclient;

/**
 * Created by Proje-Kemaleddin on 24.02.2018.
 */

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionDetector {

    private static Context mContext;

    public ConnectionDetector(Context context){
        this.mContext = context;
    }

    public static boolean isConnectingToInternet(){

        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() == true)
        {
            return true;
        }

        return false;

    }
}