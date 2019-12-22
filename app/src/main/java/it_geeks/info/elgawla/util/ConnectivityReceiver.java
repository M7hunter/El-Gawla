package it_geeks.info.elgawla.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.Interfaces.ConnectionInteface;

public class ConnectivityReceiver extends BroadcastReceiver {

    private ConnectionInteface connectionInteface;

    public ConnectivityReceiver(Context context, ConnectionInteface connectionInteface) {
        context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.connectionInteface = connectionInteface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (RetrofitClient.isConnected(context))
        {
            connectionInteface.onConnected();
        }
        else
        {
            connectionInteface.onDisconnected();
        }
    }
}
