package it_geeks.info.elgawla.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import it_geeks.info.elgawla.util.Interfaces.ConnectionInteface;

public class ConnectivityReceiver extends BroadcastReceiver {

    private ConnectionInteface connectionInteface;
    private static boolean connected = false;

    public ConnectivityReceiver(Context context, ConnectionInteface connectionInteface) {
        context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.connectionInteface = connectionInteface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnected(context))
        {
            connectionInteface.onConnected();
        }
        else
        {
            connectionInteface.onDisconnected();
        }
    }

    // connected ?
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        Log.d("ConnectivityReceiver", "isConnected: " + connected);
        return connected;
    }
}
