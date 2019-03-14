package it_geeks.info.gawla_app.repository.SocketConnection;

import com.crashlytics.android.Crashlytics;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketConnection {

    private static final String GAWLA_SERVER_URL = "http://134.209.0.250:8888";
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(GAWLA_SERVER_URL);
        } catch (URISyntaxException e) {
            Crashlytics.logException(e);
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
