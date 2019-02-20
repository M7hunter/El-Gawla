package it_geeks.info.gawla_app.Repositry.SocketConnection;

import android.app.Application;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketConnection extends Application {

    private Socket mSocket;
    {

        try {
            mSocket = IO.socket(Constants.GAWLA_SERVER_URL);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

    public Socket getSocket() {
        return mSocket;
    }
}
